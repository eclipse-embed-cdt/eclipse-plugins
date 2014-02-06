/******************************************************************************
* File:    vectors.c
*
* Purpose: Configure interrupt vector table for Kinetis.
******************************************************************************/

#include "vectors.h"
#include "isr.h"
#include "common.h"

/******************************************************************************
* Vector Table
******************************************************************************/
typedef void (*vector_entry)(void);

#if (defined(KEIL))
const vector_entry  __vector_table[] = //@ ".intvec" =
#elif (defined(IAR))
#pragma location = ".intvec"
const vector_entry  __vector_table[] = //@ ".intvec" =
#elif (defined(CW))
const vector_entry __attribute__ ((section(".vectortable"))) __vector_table[] = //@ ".intvec" =
#endif
{
   VECTOR_000,           /* Initial SP           */
   VECTOR_001,           /* Initial PC           */
   VECTOR_002,
   VECTOR_003, 
   VECTOR_004,
   VECTOR_005,
   VECTOR_006,
   VECTOR_007,
   VECTOR_008,
   VECTOR_009,
   VECTOR_010,
   VECTOR_011,
   VECTOR_012,
   VECTOR_013,
   VECTOR_014,
   VECTOR_015,
   VECTOR_016,
   VECTOR_017,
   VECTOR_018,
   VECTOR_019,
   VECTOR_020,
   VECTOR_021,
   VECTOR_022,
   VECTOR_023,
   VECTOR_024,
   VECTOR_025,
   VECTOR_026,
   VECTOR_027,
   VECTOR_028,
   VECTOR_029,
   VECTOR_030,
   VECTOR_031,
   VECTOR_032,
   VECTOR_033,
   VECTOR_034,
   VECTOR_035,
   VECTOR_036,
   VECTOR_037,
   VECTOR_038,
   VECTOR_039,
   VECTOR_040,
   VECTOR_041,
   VECTOR_042,
   VECTOR_043,
   VECTOR_044,
   VECTOR_045,
   VECTOR_046,
   VECTOR_047,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
   VECTOR_PADDING,
#ifndef CW
   CONFIG_1,
   CONFIG_2,
   CONFIG_3,
   CONFIG_4,
#endif

};

#if (defined(CW))
const vector_entry __attribute__ ((section(".cfmconfig"))) flash_config[] = //@ ".intvec" =
{
	   CONFIG_1,
	   CONFIG_2,
	   CONFIG_3,
	   CONFIG_4,
	
};
#endif
// VECTOR_TABLE end
/******************************************************************************
* default_isr(void)
*
* Default ISR definition.
*
* In:  n/a
* Out: n/a
******************************************************************************/
void default_isr(void)
{
   #define VECTORNUM                     (*(volatile uint32_t*)(0xE000ED04))

   printf("\n****default_isr entered on vector %d*****\r\n\n",VECTORNUM);
   return;
}



/******************************************************************************
* default_isr(void)
*
* Default ISR definition.
*
* In:  n/a
* Out: n/a
******************************************************************************/
void NMI_isr(void)
{
  (void)SCB_ICSR;
   
             SIM_SCGC5 |= (SIM_SCGC5_PORTA_MASK
                      | SIM_SCGC5_PORTB_MASK
                      | SIM_SCGC5_PORTC_MASK
                      | SIM_SCGC5_PORTD_MASK
                      | SIM_SCGC5_PORTE_MASK );
  
 //  printf("\n****NMI_isr entered ");
    /* Configure the PTA4 pin for its GPIO function */
    PORTA_PCR4 = PORT_PCR_MUX(0x1) | PORT_PCR_PE_MASK | PORT_PCR_PS_MASK; // GPIO is alt1 function for this pin
   
  
}






/******************************************************************************/
/* Generic interrupt handler for the PTA4 GPIO interrupt connected to an 
 * abort switch. 
 * NOTE: For true abort operation this handler should be modified
 * to jump to the main process instead of executing a return.
 */
void abort_isr(void)
{
   /* Write 1 to the PTA4 interrupt flag bit to clear the interrupt */
   PORTA_PCR4 |= PORT_PCR_ISF_MASK;    
  
   printf("\n****Abort button interrupt****\n\n");
   return;
}
/******************************************************************************/
/* Exception frame without floating-point storage 
 * hard fault handler in C,
 * with stack frame location as input parameter
 */
void 
hard_fault_handler_c(unsigned int * hardfault_args)
{
    unsigned int stacked_r0;
    unsigned int stacked_r1;
    unsigned int stacked_r2;
    unsigned int stacked_r3;
    unsigned int stacked_r12;
    unsigned int stacked_lr;
    unsigned int stacked_pc;
    unsigned int stacked_psr;
    
    //Exception stack frame
    stacked_r0 = ((unsigned long) hardfault_args[0]);
    stacked_r1 = ((unsigned long) hardfault_args[1]);
    stacked_r2 = ((unsigned long) hardfault_args[2]);
    stacked_r3 = ((unsigned long) hardfault_args[3]);
    
    stacked_r12 = ((unsigned long) hardfault_args[4]);
    stacked_lr = ((unsigned long) hardfault_args[5]);
    stacked_pc = ((unsigned long) hardfault_args[6]);
    stacked_psr = ((unsigned long) hardfault_args[7]);
    
    printf ("[Hard fault handler]\n");
    printf ("R0 = %x\n", stacked_r0);
    printf ("R1 = %x\n", stacked_r1);
    printf ("R2 = %x\n", stacked_r2);
    printf ("R3 = %x\n", stacked_r3);
    printf ("R12 = %x\n", stacked_r12);
    printf ("LR = %x\n", stacked_lr);
    printf ("PC = %x\n", stacked_pc);
    printf ("PSR = %x\n", stacked_psr);
#ifndef CW
    printf ("BFAR = %x\n", (*((volatile unsigned long *)(0xE000ED38))));
    printf ("CFSR = %x\n", (*((volatile unsigned long *)(0xE000ED28))));
    printf ("HFSR = %x\n", (*((volatile unsigned long *)(0xE000ED2C))));
    printf ("DFSR = %x\n", (*((volatile unsigned long *)(0xE000ED30))));
    printf ("AFSR = %x\n", (*((volatile unsigned long *)(0xE000ED3C))));
#else
    printf ("BFAR = %x\n", (*((volatile unsigned int *)(0xE000ED38))));
    printf ("CFSR = %x\n", (*((volatile unsigned int *)(0xE000ED28))));
    printf ("HFSR = %x\n", (*((volatile unsigned int *)(0xE000ED2C))));
    printf ("DFSR = %x\n", (*((volatile unsigned int *)(0xE000ED30))));
    printf ("AFSR = %x\n", (*((volatile unsigned int *)(0xE000ED3C))));
#endif
    for(;;)
    {}
} 
void SRTC_ISR(void) 
{
  
  volatile uint32 temp;
  
   printf("SRTC_ISR entered\r\n");
   
   temp = RTC_SR;
   temp++;
   
   if((RTC_SR & 0x01)== 0x01)
     {
       printf("SRTC time invalid interrupt entered...\r\n");
   	   RTC_SR &= 0x07;  //clear TCE, or SRTC_TSR can  not be written 
   	   RTC_TSR = 0x00000005;  //clear TIF by writing to the seconds register
     }	
   else if((RTC_SR & 0x02) == 0x02)
   {
   	   printf("SRTC time overflow interrupt entered...\r\n");
   	   RTC_SR &= 0x07;  //clear TCE, or SRTC_STSR can  not be written
   	   RTC_TSR = 0x00000005;  //clear TOF
           RTC_SR = 0x10; //start time again to exit wait loop in application code.
   }	 	
   else if((RTC_SR & 0x04) == 0x04)
   {
   	   printf("SRTC alarm interrupt entered...\r\n");
   	   RTC_TAR = 0x0;// Write 0 to disable
   	   //SPIOC_PTC_PDOR = 0x0001 ^ GPIOC_PTC_PDOR;
   }	
   else
   {
           printf("No valid Flag was set!\n");
   }
   return;
}

/* End of "vectors.c" */
