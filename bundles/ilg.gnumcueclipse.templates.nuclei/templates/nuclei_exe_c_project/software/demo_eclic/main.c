// See LICENSE for license details.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "stdatomic.h"

#include "n200/drivers/n200_func.h"
#include "soc/drivers/soc.h"
#include "soc/drivers/board.h"
#include "n200/drivers/riscv_encoding.h"
#include "n200/drivers/n200_timer.h"
#include "n200/drivers/n200_eclic.h"
#include "n200/drivers/riscv_bits.h"

#define BUTTON_1_GPIO_OFFSET 30
#define BUTTON_2_GPIO_OFFSET 31

#define ECLIC_INT_DEVICE_BUTTON_1 (SOC_ECLIC_INT_GPIO_BASE + BUTTON_1_GPIO_OFFSET)
#define ECLIC_INT_DEVICE_BUTTON_2 (SOC_ECLIC_INT_GPIO_BASE + BUTTON_2_GPIO_OFFSET)
// The real value is:
//#define ECLIC_INT_DEVICE_BUTTON_1 49 // 30+19
//#define ECLIC_INT_DEVICE_BUTTON_2 50 // 31+19


// Since the BUTTON_1 ECLIC IRQ number is 49, and BUTTON_2 is 50, we need to overriede the irq49/50 handler 
//#define BUTTON_1_HANDLER eclic_irq49_handler
#define BUTTON_2_HANDLER eclic_irq50_handler
#define MTIME_HANDLER   eclic_mtip_handler

#define SAVE_STATUS_IRQ_VECTOR \
  uint32_t mcause = read_csr(mcause);\
  uint32_t mepc = read_csr(mepc);\
  uint32_t msubm = read_csr(0x7c4);\
  set_csr(mstatus, MSTATUS_MIE); \


#define RESTORE_STATUS_IRQ_VECTOR  \
  clear_csr(mstatus, MSTATUS_MIE);\
  write_csr(0x7c4, msubm);\
  write_csr(mepc, mepc);\
  write_csr(mcause, mcause); \


void reset_demo (void);



void no_interrupt_handler (void) {};

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

  printf("-----------------Waited %d seconds.\n", n);
}


/*Entry Point for Machine Timer Interrupt Handler*/
  //  Can only enable this attribute if the toolchain support it
//void __attribute__ ((interrupt)) MTIME_HANDLER(){
void MTIME_HANDLER(){

  #ifdef CFG_SIMULATION
    // Use write functions instead of printf because it will be much faster in simulation
  write (STDOUT_FILENO, "Begin mtime handler\n", strlen("Begin mtime handler\n"));
  #else
  printf ("%s","Begin mtime handler----NonVector mode\n");
  #endif
  GPIO_REG(GPIO_OUTPUT_VAL) ^= (0x1 << RED_LED_GPIO_OFFSET);

  volatile uint64_t * mtime       = (uint64_t*) (TIMER_CTRL_ADDR + TIMER_MTIME);
  volatile uint64_t * mtimecmp    = (uint64_t*) (TIMER_CTRL_ADDR + TIMER_MTIMECMP);
  uint64_t now = *mtime;
  #ifdef CFG_SIMULATION
    // In simulation, we need to make it more quick
  uint64_t then = now + 0.01 * TIMER_FREQ;
  #else
  uint64_t then = now + 0.5 * TIMER_FREQ;// Here we set 1 second, but we need to wait 5 cycles to get out from this handler, so the blink will not toggle as 1 cycle
  #endif
  *mtimecmp = then;

  #ifdef CFG_SIMULATION
  write (STDOUT_FILENO, "End mtime handler\n", strlen("End mtime handler\n"));
  #else
  wait_seconds(5);// Wait for a while
  
  printf ("%s","End mtime handler\n");
  #endif

}



static void _putc(char c) {
  while ((int32_t) UART0_REG(UART_REG_TXFIFO) < 0);
  UART0_REG(UART_REG_TXFIFO) = c;
}

int _getc(char * c){
  int32_t val = (int32_t) UART0_REG(UART_REG_RXFIFO);
  if (val > 0) {
    *c =  val & 0xFF;
    return 1;
  }
  return 0;
}

char * read_instructions_msg= " \
\n\
 ";


const char * printf_instructions_msg= " \
\n\
\n\
\n\
\n\
This is printf function printed:  \n\
\n\
             !! Here We Go, Nuclei-N200 !! \n\
\n\
     ######    ###    #####   #####          #     #\n\
     #     #    #    #     # #     #         #     #\n\
     #     #    #    #       #               #     #\n\
     ######     #     #####  #        #####  #     #\n\
     #   #      #          # #                #   #\n\
     #    #     #    #     # #     #           # #\n\
     #     #   ###    #####   #####             #\n\
\n\
 ";


//



void __attribute__ ((interrupt)) BUTTON_1_HANDLER(void) {
   //save mepc,mcause,msubm enable interrupts
  SAVE_STATUS_IRQ_VECTOR;
 
  #ifdef CFG_SIMULATION
  write (STDOUT_FILENO, "----Begin button1 handler\n", strlen("----Begin button1 handler\n"));
  #else
  printf ("%s","----Begin button1 handler----Vector mode\n");
  #endif

  // Green LED On
  GPIO_REG(GPIO_OUTPUT_VAL) |= (1 << GREEN_LED_GPIO_OFFSET);

  // Clear the GPIO Pending interrupt by writing 1.
  GPIO_REG(GPIO_RISE_IP) = (0x1 << BUTTON_1_GPIO_OFFSET);

  #ifdef CFG_SIMULATION
  write (STDOUT_FILENO, "----End button1 handler\n", strlen("----End button1 handler\n"));
  #else
  wait_seconds(5);// Wait for a while

  printf ("%s","----End button1 handler\n");

  #endif
  //disable interrupts,restore mepc,mcause,msubm 
  RESTORE_STATUS_IRQ_VECTOR;
  
};


void BUTTON_2_HANDLER(void) {

  #ifdef CFG_SIMULATION
  write (STDOUT_FILENO, "----------Begin button2 handler\n", strlen("----------Begin button2 handler\n"));
  #else
  printf ("%s","--------Begin button2 handler----NonVector mode\n");
  #endif

  // Blue LED On
  GPIO_REG(GPIO_OUTPUT_VAL) |= (1 << BLUE_LED_GPIO_OFFSET);

  GPIO_REG(GPIO_RISE_IP) = (0x1 << BUTTON_2_GPIO_OFFSET);

  #ifdef CFG_SIMULATION
  write (STDOUT_FILENO, "----------End button2 handler\n", strlen("----------End button2 handler\n"));
  #else
  wait_seconds(5);// Wait for a while

  printf ("%s","--------End button2 handler\n");
  #endif

};

void config_eclic_irqs (){

  // Have to enable the interrupt both at the GPIO level,
  // and at the ECLIC level.
  eclic_enable_interrupt (ECLIC_INT_MTIP);
  eclic_enable_interrupt (ECLIC_INT_DEVICE_BUTTON_1);
  eclic_enable_interrupt (ECLIC_INT_DEVICE_BUTTON_2);


  eclic_set_nlbits(3);
  //  The button have higher level
 eclic_set_irq_lvl_abs(ECLIC_INT_MTIP, 1);
  eclic_set_irq_lvl_abs(ECLIC_INT_DEVICE_BUTTON_1, 2);
  eclic_set_irq_lvl_abs(ECLIC_INT_DEVICE_BUTTON_2, 3);

  //  The BUTTON1 using Vector-Mode
  //  Can only enable this mode if the toolchain support it
  eclic_set_vmode(ECLIC_INT_DEVICE_BUTTON_1);
 } 


void setup_mtime (){

    // Set the machine timer to go off in 2 seconds.
    volatile uint64_t * mtime    = (uint64_t*) (TIMER_CTRL_ADDR + TIMER_MTIME);
    volatile uint64_t * mtimecmp = (uint64_t*) (TIMER_CTRL_ADDR + TIMER_MTIMECMP);
    uint64_t now = *mtime;
  #ifdef CFG_SIMULATION
    // Bob: In simulation, we need to make it more quick
    uint64_t then = now + 0.001 * TIMER_FREQ;
  #else
    uint64_t then = now + 2 * TIMER_FREQ;
  #endif
    *mtimecmp = then;

}

int main(int argc, char **argv)
{
  // Set up the GPIOs such that the LED GPIO
  // can be used as both Inputs and Outputs.
  

  GPIO_REG(GPIO_OUTPUT_EN)  &= ~((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));
  GPIO_REG(GPIO_PULLUP_EN)  &= ~((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));
  GPIO_REG(GPIO_INPUT_EN)   |=  ((0x1 << BUTTON_1_GPIO_OFFSET) | (0x1 << BUTTON_2_GPIO_OFFSET));

  GPIO_REG(GPIO_RISE_IE) |= (1 << BUTTON_1_GPIO_OFFSET);
  GPIO_REG(GPIO_RISE_IE) |= (1 << BUTTON_2_GPIO_OFFSET);


  GPIO_REG(GPIO_INPUT_EN)    &= ~((0x1<< RED_LED_GPIO_OFFSET) | (0x1<< GREEN_LED_GPIO_OFFSET) | (0x1 << BLUE_LED_GPIO_OFFSET)) ;
  GPIO_REG(GPIO_OUTPUT_EN)   |=  ((0x1<< RED_LED_GPIO_OFFSET)| (0x1<< GREEN_LED_GPIO_OFFSET) | (0x1 << BLUE_LED_GPIO_OFFSET)) ;

  GPIO_REG(GPIO_OUTPUT_VAL)  |=   (0x1 << RED_LED_GPIO_OFFSET) ;
  GPIO_REG(GPIO_OUTPUT_VAL)  &=  ~((0x1<< BLUE_LED_GPIO_OFFSET) | (0x1<< GREEN_LED_GPIO_OFFSET)) ;


  
  // Print the message
  printf ("%s",printf_instructions_msg);

  //printf("\nIn main function, the mstatus is 0x%x\n", read_csr(mstatus));


  printf ("%s","\nPlease enter any letter from keyboard to continue!\n");

  #ifdef CFG_SIMULATION
  //Bob: for simulation, we comment it off
  char c = 0xff;
  printf ("%s","I got an input, it is\n\r");
  #else
  char c;
  // Check for user input
  while(1){
    if (_getc(&c) != 0){
       printf ("%s","I got an input, it is\n\r");
       break;
    }
  }
  #endif
  _putc(c);
  printf ("\n\r");
  printf ("%s","\nThank you for supporting RISC-V, you will see the blink soon on the board!\n");

 
  config_eclic_irqs();

  setup_mtime();

  // Enable interrupts in general.
  set_csr(mstatus, MSTATUS_MIE);


  // For Bit-banging 
  
  uint32_t bitbang_mask = 0;
  bitbang_mask = (1 << 13);

  GPIO_REG(GPIO_OUTPUT_EN) |= bitbang_mask;

  
  while (1){
    GPIO_REG(GPIO_OUTPUT_VAL) ^= bitbang_mask;
    // For Bit-banging with Atomics demo if the A extension is supported.
    //atomic_fetch_xor_explicit(&GPIO_REG(GPIO_OUTPUT_VAL), bitbang_mask, memory_order_relaxed);
  }

  return 0;

}
