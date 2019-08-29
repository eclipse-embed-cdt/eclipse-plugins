/*
 * IntTest.c
 *
 *  Created on: 2018Äê10ÔÂ17ÈÕ
 *      Author: danialxie
 *
 *        This is an PIC interrupt nesting test for N200 SOC, NUCLEI, Inc.
 *      Attention:
 *         This test need hardware board supporting.
 * 	       A GPIO interrupt is stimulated by another GPIO using wire connection.
 * 	       Wire connection:
 * 	          Source  --> Target
 * 	          GPIO 0  --> GPIO 8
 * 	          GPIO 1  --> GPIO 9
 * 	          GPIO 2  --> GPIO 10
 * 	          GPIO 3  --> GPIO 11
 * 	          GPIO 4  --> GPIO 12
 * 	          GPIO 5  --> GPIO 13
 * 	          GPIO 6  --> GPIO 14
 * 	          GPIO 7  --> GPIO 15
 */

/* Kernel includes. */
#include "FreeRTOS.h" /* Must come first. */
#include "task.h"     /* RTOS task related API prototypes. */
#include "queue.h"    /* RTOS queue related API prototypes. */
#include "timers.h"   /* Software timer related API prototypes. */
#include "semphr.h"   /* Semaphore related API prototypes. */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "n200/drivers/n200_pic_tmr.h"
#include "n200/drivers/n200_func.h"

#define INT_TEST_NEST_DEPTH  6
#define INT_TEST_GPIO_NUM  6
#define INT_TEST_TASK_DELAY  50 // ms
#define TASK_TEST_STACK_DEPTH  200

#define TASK_TEST_QUEUE_NUM  2
#define TASK_TEST_QUEUE_LENGTH  3

#define GPIO_INT_SOURCE(x) (SOC_PIC_INT_GPIO_BASE + x)

/* Configure board type:
 *   Board under test :        SIGNAL_BOARD_ENABLE     0
 *   Signal generation board : SIGNAL_BOARD_ENABLE     1
 */
#define SIGNAL_BOARD_ENABLE       0

#define INT_TEST_INT_WAVE_ENABLE  1

#if INT_TEST_INT_WAVE_ENABLE
    #define INT_TEST_TIMER_PERIOD  500    // ms
    #define INT_TEST_INT_DELAY    10    // ms
#else
    #define INT_TEST_TIMER_PERIOD  500    // ms
    #define INT_TEST_INT_DELAY    0x3ff    // ms
#endif

#define INT_TEST_MAX_TIMER_PERIOD	100 // ms
#define INT_TEST_MIN_TIMER_PERIOD	50 // ms
#define INT_TEST_MUTE_TIMER_PERIOD	200 // ms

/* Interrupt handler */
void DefaultInterruptHandler(void);
void GPIOInterruptHandler(uint32_t num, uint32_t priority);
void GPIOInterruptHandler8(void);
void GPIOInterruptHandler9(void);
void GPIOInterruptHandler10(void);
void GPIOInterruptHandler11(void);
void GPIOInterruptHandler12(void);
void GPIOInterruptHandler13(void);
void GPIOInterruptHandler14(void);
void GPIOInterruptHandler15(void);

/* Binary Semaphore */
QueueHandle_t xGPIOSemaphore[INT_TEST_NEST_DEPTH];
QueueHandle_t xMessageQueue[TASK_TEST_QUEUE_NUM];

/* Timer handle */
TimerHandle_t xSoftTimer = NULL;

static void vPrintString(const char* msg)
{
	taskENTER_CRITICAL();
	{
      write(1, msg, strlen(msg));
	}
	taskEXIT_CRITICAL();
}

/* function: vGPIOInit */
void vGPIOInit(void){

	uint32_t input_gpios = 0x0000FF00;  // GPIO8- 15
	uint32_t output_gpios = 0x000000FF; // GPIO0- 7
	uint32_t led_gpios = 0x00680000;    // GPIO19(R), 21(G), 22(B)

	// Set GPIO 0-7 as output pins
	GPIO_REG(GPIO_INPUT_EN)  &= ~output_gpios;
	GPIO_REG(GPIO_OUTPUT_EN)  |= output_gpios;
	GPIO_REG(GPIO_OUTPUT_EN)  |= led_gpios;

	// Set GPIO 8-15 as input pins
	GPIO_REG(GPIO_OUTPUT_EN)  &= ~input_gpios;
	GPIO_REG(GPIO_INPUT_EN)  |= input_gpios;
	GPIO_REG(GPIO_PULLUP_EN)  |= input_gpios;

	// Enable GPIO interrupt
	GPIO_REG(GPIO_FALL_IE) |= input_gpios;

#if !(SIGNAL_BOARD_ENABLE)
	// Print GPIO status for debugging
	{
		char gpio_status = 'H';
      char num_str[16];
		write(1, "Input GPIO init status: ", 24);
      for(int i = 0; i < 32; ++ i) {
			if((0x1 << i) & input_gpios) { // Check input GPIOs
				gpio_status = (GPIO_REG(GPIO_INPUT_VAL) &= (0x1 << i) & input_gpios)? 'H':'L';

				write(1, "[", 1);
				itoa(i, num_str, 10);
				write(1, num_str, strlen(num_str));
				write(1, "]", 1);
				write(1, &gpio_status, 1);
				write(1, " ", 1);
			}
		}
      write(1, "\r\n", 2);
	}
#endif
}

//enables interrupt and assigns handler
void enable_interrupt(uint32_t int_num, uint32_t int_priority, function_ptr_t handler) {
    pic_interrupt_handlers[int_num] = handler;
    pic_set_priority(int_num, int_priority);
    pic_enable_interrupt (int_num);
}

/* function: vPICInit */
static void vPICInit(void) {

	// Disable global interrupter
	clear_csr(mstatus, MSTATUS_MIE);

	// Initialize interrupter handler
	for (int i = 0; i < PIC_NUM_INTERRUPTS; i ++){
		pic_interrupt_handlers[i] = DefaultInterruptHandler;
	}

#if !(SIGNAL_BOARD_ENABLE)

   // Enable GPIO interrupter
	enable_interrupt(GPIO_INT_SOURCE(8),  1, GPIOInterruptHandler8);
	enable_interrupt(GPIO_INT_SOURCE(9),  2, GPIOInterruptHandler9);
	enable_interrupt(GPIO_INT_SOURCE(10),  3, GPIOInterruptHandler10);
	enable_interrupt(GPIO_INT_SOURCE(11),  4, GPIOInterruptHandler11);
	enable_interrupt(GPIO_INT_SOURCE(12),  5, GPIOInterruptHandler12);
	enable_interrupt(GPIO_INT_SOURCE(13),  6, GPIOInterruptHandler13);
	//enable_interrupt(GPIO_INT_SOURCE(14),  7, GPIOInterruptHandler14);
	//enable_interrupt(GPIO_INT_SOURCE(15),  7, GPIOInterruptHandler15);

#endif

	// Enable global interrupt
	set_csr(mstatus, MSTATUS_MIE);
}

/* function: vGenerateSimulateInterrupt
 * Attention:
 *  This function need hardware board supporting.
 * 	One GPIO interrupt is stimulated by another in pair.
 * 	See also vGPIOInit().
 */
static void vGenerateSimulateInterrupt(uint32_t gpio) {
	GPIO_REG(GPIO_OUTPUT_VAL)  &=  ~(0x1 << gpio);
	vTaskDelay(5);
	GPIO_REG(GPIO_OUTPUT_VAL)  |=   (0x1 << gpio);
}

void DefaultInterruptHandler(void){}

/* GPIO interrupt handler */
void GPIOInterruptHandler(uint32_t num, uint32_t priority) {

	BaseType_t xHigherPriorityTaskWoken = pdFALSE;

	char num_str[64];

	int uxSavedStatus = taskENTER_CRITICAL_FROM_ISR();
	write(1, ">Enter Interrupt ", 17);
	utoa(num, num_str, 10);
	write(1, num_str, strlen(num_str));
	write(1, " (priority ", 11);
	utoa(priority, num_str, 10);
	write(1, num_str, strlen(num_str));
	write(1, ") ...\r\n", 7);
	taskEXIT_CRITICAL_FROM_ISR(uxSavedStatus);

	//for(uint32_t i = 0; i < INT_TEST_INT_DELAY; ++i);

	int smph_index = num - 8; // Depend on wire connection
	xSemaphoreGiveFromISR(xGPIOSemaphore[smph_index], &xHigherPriorityTaskWoken);

	uxSavedStatus = taskENTER_CRITICAL_FROM_ISR();
	write(1, ">Exit Interrupt ", 16);
	utoa(num, num_str, 10);
	write(1, num_str, strlen(num_str));
	write(1, " (priority ", 11);
	utoa(priority, num_str, 10);
	write(1, num_str, strlen(num_str));
	write(1, ") ...\r\n", 7);
	taskEXIT_CRITICAL_FROM_ISR(uxSavedStatus);

	// Clear interrupt pending bit

	GPIO_REG(GPIO_FALL_IP) |= (0x1 << num);

	//portYIELD_FROM_ISR(xHigherPriorityTaskWoken);

}

/* GPIO 8 interrupt handler */
void GPIOInterruptHandler8(void) {
   GPIOInterruptHandler(8, 1);
}

/* GPIO 9 interrupt handler */
void GPIOInterruptHandler9(void) {
   GPIOInterruptHandler(9, 2);
}

/* GPIO 10 interrupt handler */
void GPIOInterruptHandler10(void) {
   GPIOInterruptHandler(10, 3);
}

/* GPIO 11 interrupt handler */
void GPIOInterruptHandler11(void) {
   GPIOInterruptHandler(11, 4);
}

/* GPIO 12 interrupt handler */
void GPIOInterruptHandler12(void) {
   GPIOInterruptHandler(12, 5);
}

/* GPIO 13 interrupt handler */
void GPIOInterruptHandler13(void) {
   GPIOInterruptHandler(13, 6);
}

/* GPIO 14 interrupt handler */
void GPIOInterruptHandler14(void) {
   GPIOInterruptHandler(14, 7);
}

/* GPIO 15 interrupt handler */
void GPIOInterruptHandler15(void) {
   GPIOInterruptHandler(15, 7);
}

void vQueueSendTask(void *pvParameters) {
	TickType_t xLastWakeTime;

   if(pvParameters == NULL) return;
	
	uint32_t queue_index = *(uint32_t*)pvParameters; 
	uint32_t data;

   char num_str[16];
		
	for(;;) {
		vTaskDelayUntil(&xLastWakeTime, pdMS_TO_TICKS(INT_TEST_TASK_DELAY));
	
      data = xTaskGetTickCount();
		xQueueSend(xMessageQueue[queue_index], &data, 5);

		taskENTER_CRITICAL();
		{
			// using indention as prompt
			for(int i = 0; i < queue_index; ++ i)write(1, "   ", 3);
			char *name = pcTaskGetName(NULL);
			write(1, name, strlen(name));
			write(1, ":send data ", 11);
			utoa(data, num_str, 10);
			write(1, num_str, strlen(num_str));
			write(1, "\r\n", 2);
         //fflush(stdout);
		}
		taskEXIT_CRITICAL();
	}
}

void vQueueReceiveTask(void *pvParameters) {
   if(pvParameters == NULL) return;
	
	uint32_t queue_index = *(uint32_t*)pvParameters; 
	uint32_t data;

   char num_str[16];
	
	for(;;) {
		vTaskDelay(pdMS_TO_TICKS(INT_TEST_TASK_DELAY));

		xQueueReceive(xMessageQueue[queue_index], &data, 5);

		taskENTER_CRITICAL();
		{
			// using indention as prompt
			for(int i = 0; i < queue_index; ++ i)write(1, "   ", 3);

			char *name = pcTaskGetName(NULL);
			write(1, name, strlen(name));
			write(1, ":receive data ", 14);
			utoa(data, num_str, 10);
			write(1, num_str, strlen(num_str));
			write(1, "\r\n", 2);
         //fflush(stdout);
		}
		taskEXIT_CRITICAL();
	}
}


/* GPIO interrupt deferred processing task */
void vGPIOHandlerTask(void *pvParameters) {
	TickType_t xLastWakeTime;

	if(pvParameters == NULL) return;

	uint32_t gpio= *(uint32_t*)pvParameters;

	uint32_t index = gpio - 8; // Depend on wire relation.

   char num_str[16];

	xLastWakeTime = xTaskGetTickCount();
	for(;;)
	{
		xSemaphoreTake(xGPIOSemaphore[index], portMAX_DELAY);
		GPIO_REG(GPIO_OUTPUT_VAL) |= (0x1 << RED_LED_GPIO_OFFSET);

		/*Print Task information*/
		taskENTER_CRITICAL();
		{
         // using indention as priority prompt
			for(int i = 0; i < index; ++ i)write(1, "   ", 3);

			char *name = pcTaskGetName(NULL);
			write(1, name, strlen(name));
			write(1, " running GPIO", 13);
			utoa(gpio, num_str, 10);
			write(1, num_str, strlen(num_str));
			write(1, " interrupt processing \r\n", 24);
		}
		taskEXIT_CRITICAL();

		vTaskDelayUntil(&xLastWakeTime, pdMS_TO_TICKS(INT_TEST_TASK_DELAY));

		taskENTER_CRITICAL();
		{
         // using indention as priority prompt
			for(int i = 0; i < index; ++ i)write(1, "   ", 3);

			char *name = pcTaskGetName(NULL);
			write(1, name, strlen(name));
			write(1, " exiting GPIO", 13);
			utoa(gpio, num_str, 10);
			write(1, num_str, strlen(num_str));
			write(1, " interrupt processing \r\n", 24);
		}
		taskEXIT_CRITICAL();

		GPIO_REG(GPIO_OUTPUT_VAL) &= ~(0x1 << RED_LED_GPIO_OFFSET);
	}
}

/* Timer task */
static void vTimerCallback(TimerHandle_t xTimer) {
	TickType_t xLastWakeTime;

	uint32_t gpio_out = 0;

   char num_str[16];

	GPIO_REG(GPIO_OUTPUT_VAL) |= (0x1 << GREEN_LED_GPIO_OFFSET);

	/*Print task information*/
	taskENTER_CRITICAL();
	{
      write(1, "===== Timer task start (peroid: ", 32);
		utoa(xTimerGetPeriod(xSoftTimer), num_str, 10);
		write(1, num_str, strlen(num_str));
		write(1, ") ====== \r\n", 11);
	}
	taskEXIT_CRITICAL();

#if INT_TEST_INT_WAVE_ENABLE
	// Init interrupt sequence
	int32_t  int_gpio[INT_TEST_NEST_DEPTH];

	int32_t int_len = rand() % INT_TEST_NEST_DEPTH;
	for (int i = 0; i < int_len; ++i) {
		int_gpio[i] = rand() % INT_TEST_GPIO_NUM;
		gpio_out |= 0x1 <<int_gpio[i];
	}

#else

	gpio_out = 0x1 << (rand() % INT_TEST_GPIO_NUM);

#endif

	GPIO_REG(GPIO_OUTPUT_VAL) = gpio_out;

	vTaskDelayUntil(&xLastWakeTime, pdMS_TO_TICKS(200));

	// Change timer peroid
	TickType_t xNewPeriod = rand() % INT_TEST_MAX_TIMER_PERIOD;	
	if (xNewPeriod < INT_TEST_MIN_TIMER_PERIOD) xNewPeriod = INT_TEST_MUTE_TIMER_PERIOD;

	xTimerChangePeriod(xSoftTimer, pdMS_TO_TICKS(xNewPeriod), pdMS_TO_TICKS(50));

	GPIO_REG(GPIO_OUTPUT_VAL) &= ~(0x1 << GREEN_LED_GPIO_OFFSET);
}

static void vPrintSystemStatus(TimerHandle_t xTimer) {
	 taskENTER_CRITICAL();
    printf("%0d: mstatus = 0x%x, mepc = 0x%x, mcause = 0x%x, mtval = 0x%x\n", 
            xTaskGetTickCount(), 
            read_csr(mstatus),
            read_csr(mepc),
            read_csr(mcause),
            read_csr(mbadaddr)
          );

    printf("       istatus = 0x%x, nstatus = 0x%x, msubmode = 0x%x\n", 
            read_csr_istatus,
            read_csr_nstatus,
            read_csr_msubmode
          );
	taskEXIT_CRITICAL();
}

#if SIGNAL_BOARD_ENABLE
int main(void)
{
	uint32_t gpio_index[] = {8, 9, 10, 11, 12, 13, 14, 15};

	// Initialize GPIOs, PIC and timer
	vGPIOInit();
	vPICInit();

	// Create tasks - GPIO interrupt task
	for(uint32_t i = 0; i < INT_TEST_NEST_DEPTH; ++i) {
		// Create semaphores
		xGPIOSemaphore[i] = xSemaphoreCreateBinary();

		// Create tasks
		xTaskCreate(vGPIOHandlerTask, "GPIO Task", TASK_TEST_STACK_DEPTH, &gpio_index[i], 1, NULL);
	}

	// Create timer
	xSoftTimer = xTimerCreate("Timer", pdMS_TO_TICKS(INT_TEST_TIMER_PERIOD), pdTRUE, NULL, vTimerCallback);

	vPrintString("Starting timer ...\r\n");
	xTimerStart(xSoftTimer, 0);

	vPrintString("Starting task scheduler ...\r\n");
	vTaskStartScheduler();

	for(;;);

	return 0;
}

#else
// Test target board
int main(void)
{
	uint32_t gpio_index[] = {8, 9, 10, 11, 12, 13, 14, 15};
	uint32_t queue_index[] = {0, 1, 2, 3};

	// Initialize GPIOs, PIC and timer
	vGPIOInit();
	vPICInit();

	// Delay
	for(uint32_t i = 0; i < 0xffff; ++i);

	// Create tasks -- interrupt task
	for(uint32_t i = 0; i < INT_TEST_NEST_DEPTH; ++i) {
		// Create semaphores
		xGPIOSemaphore[i] = xSemaphoreCreateBinary();

		// Create tasks
		//xTaskCreate(vGPIOHandlerTask, "GPIO Task H", TASK_TEST_STACK_DEPTH, &gpio_index[i], 3, NULL);
		xTaskCreate(vGPIOHandlerTask, "GPIO Task L", TASK_TEST_STACK_DEPTH, &gpio_index[i], i+1, NULL);
	}

	// Create tasks -- queue task
	for(uint32_t i = 0; i < TASK_TEST_QUEUE_NUM; ++i) {
		// Create semaphores
		xMessageQueue[i] = xQueueCreate(TASK_TEST_QUEUE_LENGTH, sizeof(uint32_t));

		// Create tasks
		xTaskCreate(vQueueSendTask, "Queue Send Task:", TASK_TEST_STACK_DEPTH, &queue_index[i], 1, NULL);
		xTaskCreate(vQueueReceiveTask, "Queue Receive Task:", TASK_TEST_STACK_DEPTH, &queue_index[i], 1, NULL);
	}

	// Create timer
	xSoftTimer = xTimerCreate("Timer", pdMS_TO_TICKS(INT_TEST_TIMER_PERIOD), pdTRUE, NULL, vPrintSystemStatus);

	vPrintString("Starting timer ...\r\n");
	xTimerStart(xSoftTimer, 0);

	vPrintString("Starting task scheduler ...\r\n");
	vTaskStartScheduler();

	for(;;)

	return 0;
}

#endif

void vApplicationIdleHook( void )
{
   //vPrintString("enter idle task\n");

   //write_csr(mie, 1); // open mstatue.mie
   //asm volatile ("wfi"); // enter low power mode
}
/*-----------------------------------------------------------*/

void vApplicationMallocFailedHook( void )
{
    /* The malloc failed hook is enabled by setting
    configUSE_MALLOC_FAILED_HOOK to 1 in FreeRTOSConfig.h.

    Called if a call to pvPortMalloc() fails because there is insufficient
    free memory available in the FreeRTOS heap.  pvPortMalloc() is called
    internally by FreeRTOS API functions that create tasks, queues, software
    timers, and semaphores.  The size of the FreeRTOS heap is set by the
    configTOTAL_HEAP_SIZE configuration constant in FreeRTOSConfig.h. */
	write(1,"malloc failed\n", 14);
    for( ;; );
}
/*-----------------------------------------------------------*/

void vApplicationStackOverflowHook( TaskHandle_t xTask, signed char *pcTaskName )
{
    ( void ) pcTaskName;
    ( void ) xTask;

    /* Run time stack overflow checking is performed if
    configconfigCHECK_FOR_STACK_OVERFLOW is defined to 1 or 2.  This hook
    function is called if a stack overflow is detected.  pxCurrentTCB can be
    inspected in the debugger if the task name passed into this function is
    corrupt. */
    write(1, "Stack Overflow\n", 15);
    for( ;; );
}
/*-----------------------------------------------------------*/
