/*
    FreeRTOS V9.0.0 - Copyright (C) 2016 Real Time Engineers Ltd.
    All rights reserved

    VISIT http://www.FreeRTOS.org TO ENSURE YOU ARE USING THE LATEST VERSION.

    This file is part of the FreeRTOS distribution.

    FreeRTOS is free software; you can redistribute it and/or modify it under
    the terms of the GNU General Public License (version 2) as published by the
    Free Software Foundation >>>> AND MODIFIED BY <<<< the FreeRTOS exception.

    ***************************************************************************
    >>!   NOTE: The modification to the GPL is included to allow you to     !<<
    >>!   distribute a combined work that includes FreeRTOS without being   !<<
    >>!   obliged to provide the source code for proprietary components     !<<
    >>!   outside of the FreeRTOS kernel.                                   !<<
    ***************************************************************************

    FreeRTOS is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
    FOR A PARTICULAR PURPOSE.  Full license text is available on the following
    link: http://www.freertos.org/a00114.html

    ***************************************************************************
     *                                                                       *
     *    FreeRTOS provides completely free yet professionally developed,    *
     *    robust, strictly quality controlled, supported, and cross          *
     *    platform software that is more than just the market leader, it     *
     *    is the industry's de facto standard.                               *
     *                                                                       *
     *    Help yourself get started quickly while simultaneously helping     *
     *    to support the FreeRTOS project by purchasing a FreeRTOS           *
     *    tutorial book, reference manual, or both:                          *
     *    http://www.FreeRTOS.org/Documentation                              *
     *                                                                       *
    ***************************************************************************

    http://www.FreeRTOS.org/FAQHelp.html - Having a problem?  Start by reading
    the FAQ page "My application does not run, what could be wrong?".  Have you
    defined configASSERT()?

    http://www.FreeRTOS.org/support - In return for receiving this top quality
    embedded software for free we request you assist our global community by
    participating in the support forum.

    http://www.FreeRTOS.org/training - Investing in training allows your team to
    be as productive as possible as early as possible.  Now you can receive
    FreeRTOS training directly from Richard Barry, CEO of Real Time Engineers
    Ltd, and the world's leading authority on the world's leading RTOS.

    http://www.FreeRTOS.org/plus - A selection of FreeRTOS ecosystem products,
    including FreeRTOS+Trace - an indispensable productivity tool, a DOS
    compatible FAT file system, and our tiny thread aware UDP/IP stack.

    http://www.FreeRTOS.org/labs - Where new FreeRTOS products go to incubate.
    Come and try FreeRTOS+TCP, our new open source TCP/IP stack for FreeRTOS.

    http://www.OpenRTOS.com - Real Time Engineers ltd. license FreeRTOS to High
    Integrity Systems ltd. to sell under the OpenRTOS brand.  Low cost OpenRTOS
    licenses offer ticketed support, indemnification and commercial middleware.

    http://www.SafeRTOS.com - High Integrity Systems also provide a safety
    engineered and independently SIL3 certified version for use in safety and
    mission critical applications that require provable dependability.

    1 tab == 4 spaces!
*/

/* Kernel includes. */
#include "FreeRTOS.h" /* Must come first. */
#include "task.h"     /* RTOS task related API prototypes. */
#include "queue.h"    /* RTOS queue related API prototypes. */
#include "timers.h"   /* Software timer related API prototypes. */
#include "semphr.h"   /* Semaphore related API prototypes. */

/* TODO Add any manufacture supplied header files can be included
here. */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "n200/drivers/n200_func.h"
#include "soc/drivers/soc.h"
#include "soc/drivers/board.h"
#include "n200/drivers/riscv_encoding.h"
#include "n200/drivers/n200_timer.h"

#define mainQUEUE_RECEIVE_TASK_PRIORITY     ( tskIDLE_PRIORITY + 2 )
#define mainQUEUE_SEND_TASK_PRIORITY        ( tskIDLE_PRIORITY + 1 )
#define mainEVENT_SEMAPHORE_TASK_PRIORITY   ( configMAX_PRIORITIES - 1 )

#define NBIT_DEFAULT  3
#define REG_LEN       8


/* The period of the example software timer, specified in milliseconds, and
converted to ticks using the pdMS_TO_TICKS() macro. */
#define mainSOFTWARE_TIMER_PERIOD_MS        pdMS_TO_TICKS( 1000 )

#define BUTTON_1_GPIO_OFFSET 30
#define BUTTON_2_GPIO_OFFSET 31

#define ECLIC_INT_DEVICE_BUTTON_1 (SOC_ECLIC_INT_GPIO_BASE + BUTTON_1_GPIO_OFFSET)
#define ECLIC_INT_DEVICE_BUTTON_2 (SOC_ECLIC_INT_GPIO_BASE + BUTTON_2_GPIO_OFFSET)
// The real value is:
//#define ECLIC_INT_DEVICE_BUTTON_1 49 // 30+19
//#define ECLIC_INT_DEVICE_BUTTON_2 50 // 31+19

// Since the BUTTON_1 ECLIC IRQ number is 49, and BUTTON_2 is 50, we need to overriede the irq49/50 handler 
#define BUTTON_1_HANDLER eclic_irq49_handler
#define BUTTON_2_HANDLER eclic_irq50_handler


#define mainQUEUE_LENGTH                    ( 1 )
static void prvSetupHardware( void );
extern void idle_task(void);


/* The queue used by the queue send and queue receive tasks. */
static QueueHandle_t xQueue = NULL;

void wait_seconds(size_t n)
{
  unsigned long start_mtime, delta_mtime;

  // Don't start measuruing until we see an mtime tick
  unsigned long tmp = mtime_lo();
  do {
    start_mtime = mtime_lo();
  } while (start_mtime == tmp);

  do {
    delta_mtime = mtime_lo() - start_mtime;
  } while (delta_mtime < (n * TIMER_FREQ));

 // printf("-----------------Waited %d seconds.\n", n);
}
static void vExampleTimerCallback( TimerHandle_t xTimer );

void config_eclic_irqs (){
  //time_init   in port.c

  eclic_enable_interrupt (ECLIC_INT_DEVICE_BUTTON_1);
  eclic_enable_interrupt (ECLIC_INT_DEVICE_BUTTON_2);


  eclic_set_nlbits(3);
  //  The button have higher level

  eclic_set_irq_lvl_abs(ECLIC_INT_DEVICE_BUTTON_1, 2);
  eclic_set_irq_lvl_abs(ECLIC_INT_DEVICE_BUTTON_2, 3);

  //  The MTIME using Vector-Mode
  eclic_set_vmode(ECLIC_INT_MTIP);

 } 


void gpio_init(){
  GPIO_REG(GPIO_INPUT_EN)    &= ~((0x1<< RED_LED_GPIO_OFFSET) | (0x1<< GREEN_LED_GPIO_OFFSET) | (0x1 << BLUE_LED_GPIO_OFFSET)) ;
  GPIO_REG(GPIO_OUTPUT_EN)   |=  ((0x1<< RED_LED_GPIO_OFFSET)| (0x1<< GREEN_LED_GPIO_OFFSET) | (0x1 << BLUE_LED_GPIO_OFFSET)) ;

  GPIO_REG(GPIO_OUTPUT_VAL)  |=   (0x1 << RED_LED_GPIO_OFFSET) ;
  GPIO_REG(GPIO_OUTPUT_VAL)  &=  ~((0x1<< BLUE_LED_GPIO_OFFSET) | (0x1<< GREEN_LED_GPIO_OFFSET)) ;
}


void button_init(){

    GPIO_REG(GPIO_OUTPUT_EN)  &= ~((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));
    GPIO_REG(GPIO_PULLUP_EN)  &= ~((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));
    GPIO_REG(GPIO_INPUT_EN)   |=  ((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));
    
    GPIO_REG(GPIO_RISE_IE) |= ((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));
    GPIO_REG(GPIO_FALL_IE) &= ~((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));
    GPIO_REG(GPIO_HIGH_IE) &= ~((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));
    GPIO_REG(GPIO_LOW_IE) &= ~((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));

}


void prvSetupHardware( void )
{
  
    button_init();

    gpio_init();
    
    config_eclic_irqs();
    
  
}
TaskHandle_t StartTask_Handler;
TaskHandle_t StartTask2_Handler;

void start_task(void *pvParameters);
void start_task2(void *pvParameters);

int main(void)
{

	TimerHandle_t xExampleSoftwareTimer = NULL;
 
    /* Configure the system ready to run the demo.  The clock configuration
    can be done here if it was not done before main() was called. */
    prvSetupHardware();
    
    xQueue = xQueueCreate(     /* The number of items the queue can hold. */
                            mainQUEUE_LENGTH,
                            /* The size of each item the queue holds. */
                            sizeof( uint32_t ) );

    if(xQueue == NULL)	{
    	for(;;);
    }
    xTaskCreate((TaskFunction_t )start_task,            //ÈÎÎñº¯Êý
                (const char*    )"start_task",          //ÈÎÎñÃû³Æ
                (uint16_t       )521,        //ÈÎÎñ¶ÑÕ»´óÐ¡
                (void*          )NULL,                  //´«µÝ¸øÈÎÎñº¯ÊýµÄ²ÎÊý
                (UBaseType_t    )2,       //ÈÎÎñÓÅÏÈ¼¶
                (TaskHandle_t*  )&StartTask_Handler);   //ÈÎÎñ¾ä±ú        


     xTaskCreate((TaskFunction_t )start_task2,            //ÈÎÎñº¯Êý
                (const char*    )"start_task2",          //ÈÎÎñÃû³Æ
                (uint16_t       )521,        //ÈÎÎñ¶ÑÕ»´óÐ¡
                (void*          )NULL,                  //´«µÝ¸øÈÎÎñº¯ÊýµÄ²ÎÊý
                (UBaseType_t    )1,       //ÈÎÎñÓÅÏÈ¼¶
                (TaskHandle_t*  )&StartTask2_Handler); 
 

 
   xExampleSoftwareTimer = xTimerCreate(     
                                            ( const char * ) "LEDTimer",
                                            
                                            mainSOFTWARE_TIMER_PERIOD_MS,
                                            
                                            pdTRUE,
                                            
                                            ( void * ) 0,
                                            
                                            vExampleTimerCallback
                                        );
    xTimerStart( xExampleSoftwareTimer, 0 );  
    printf("Before StartScheduler\n");//Bob: I added it to here to easy analysis
    
    vTaskStartScheduler();
   
    printf("post   ok \n");
  
    for( ;; )
    {
       ;
    };
}

void start_task(void *pvParameters)
{
    TickType_t xNextWakeTime;
    int x;
    printf("task_1\n");
    while(1)
    {
        printf("task1_running..... \n");
      
        vTaskDelay(200);
         
    }
}   

void start_task2(void *pvParameters)
{
    uint32_t ulReceivedValue;
    printf("task_2\n");
    /* Initialise xNextWakeTime - this only needs to be done once. */

    while(1)
    {
        
        printf("task2_running..... \n");
        
        vTaskDelay(200);
    }
}   


static void vExampleTimerCallback( TimerHandle_t xTimer )
{
    /* The timer has expired.  Count the number of times this happens.  The
    timer that calls this function is an auto re-load timer, so it will
    execute periodically. */
   
   
   printf("timers Callback\n");
   

}


void BUTTON_1_HANDLER(void) {   

    printf ("%s","----Begin button1 handler\n");
    GPIO_REG(GPIO_OUTPUT_VAL) ^= (0x1 << RED_LED_GPIO_OFFSET);
    printf ("%s","----red LED off or on\n");
    wait_seconds(5);
    printf ("%s","----End button1 handler\n");

    GPIO_REG(GPIO_RISE_IP) = (0x1 << BUTTON_1_GPIO_OFFSET);  
};
void BUTTON_2_HANDLER(void) {
 
    printf ("%s","--------Begin button2 handler\n");
    printf ("%s","--------Higher level\n");
    wait_seconds(5);
    printf ("%s","--------End button2 handler\n");    

    GPIO_REG(GPIO_RISE_IP) = (0x1 << BUTTON_2_GPIO_OFFSET);

};



void vApplicationTickHook( void )
{
BaseType_t xHigherPriorityTaskWoken = pdFALSE;
static uint32_t ulCount = 0;

    /* The RTOS tick hook function is enabled by setting configUSE_TICK_HOOK to
    1 in FreeRTOSConfig.h.

    "Give" the semaphore on every 500th tick interrupt. */
    // xSemaphoreGiveFromISR( xEventSemaphore, &xHigherPriorityTaskWoken );
    //    ulCount = 0UL;

   // 	GPIO_REG(GPIO_OUTPUT_VAL)  ^=   (0x1 << GREEN_LED_GPIO_OFFSET) ;
    	

    /* If xHigherPriorityTaskWoken is pdTRUE then a context switch should
    normally be performed before leaving the interrupt (because during the
    execution of the interrupt a task of equal or higher priority than the
    running task was unblocked).  The syntax required to context switch from
    an interrupt is port dependent, so check the documentation of the port you
    are using.

    In this case, the function is running in the context of the tick interrupt,
    which will automatically check for the higher priority task to run anyway,
    so no further action is required. */
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
  //  ( void ) pcTaskName;
   // ( void ) xTask;

    /* Run time stack overflow checking is performed if
    configconfigCHECK_FOR_STACK_OVERFLOW is defined to 1 or 2.  This hook
    function is called if a stack overflow is detected.  pxCurrentTCB can be
    inspected in the debugger if the task name passed into this function is
    corrupt. */
    write(1, "Stack Overflow\n", 15);
    for( ;; );
}
/*-----------------------------------------------------------*/

extern UBaseType_t uxCriticalNesting;
void vApplicationIdleHook( void )
{
volatile size_t xFreeStackSpace;
// wait_seconds(2);
 //printf(", the mstatus is 0x%x \n", read_csr(mstatus));
// printf(", the mepc is 0x%x\n", read_csr(mepc));
   // idle_task();//printf("In trap handler, the mstatus is 0x%x \n", read_csr(mstatus));
    /* The idle task hook is enabled by setting configUSE_IDLE_HOOK to 1 in
    FreeRTOSConfig.h.

    This function is called on each cycle of the idle task.  In this case it
    does nothing useful, other than report the amount of FreeRTOS heap that
    remains unallocated. */
  //  xFreeStackSpace = xPortGetFreeHeapSize();
 // printf(" the mie is 0x%x\n",(read_csr(mstatus)&MSTATUS_MIE));
 //s printf( "idle_task\n");
  //      eclic_enable_interrupt (CLIC_INT_TMR);
  //  set_csr(mstatus, MSTATUS_MIE);
   // if( xFreeStackSpace > 100 )
   // {
        /* By now, the kernel has allocated everything it is going to, so
        if there is a lot of heap remaining unallocated then
        the value of configTOTAL_HEAP_SIZE in FreeRTOSConfig.h can be
        reduced accordingly. */
  //  }
}
