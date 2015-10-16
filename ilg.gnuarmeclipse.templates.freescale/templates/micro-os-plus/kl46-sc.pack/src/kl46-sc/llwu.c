/*!
 * \file    llwu.c
 * \brief   common LLWU routines
 *
 * This file defines the functions/interrupt handlers/macros used for LLWU to be used as wakeup source.
 * And some common initializations.
 *
 * \version $Revision: 1.0 $
 * \author  Philip Drake(rxaa60)
 ***/

#include "common.h"
#include "llwu.h"
#include "mcg.h"
#include "vectors.h"

extern int re_init_clk;
extern int clock_freq_hz;

// [ILG]
#if defined ( __GNUC__ )
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wconversion"
#endif

/* function: llwu_configure

   description: Set up the LLWU for wakeup the MCU from LLS and VLLSx modes 
   from the selected pin or module.
   
   inputs:
   pin_en - unsigned integer, bit position indicates the pin is enabled.  
            More than one bit can be set to enable more than one pin at a time.  
   
   rise_fall - 0x00 = External input disabled as wakeup
               0x01 - External input enabled as rising edge detection
               0x02 - External input enabled as falling edge detection
               0x03 - External input enablge as any edge detection
   module_en - unsigned char, bit position indicates the module is enabled.  
               More than one bit can be set to enabled more than one module                   
   
   for example:  if bit 0 and 1 need to be enabled as rising edge detect call this  routine with
   pin_en = 0x0003 and rise_fall = 0x02
   
   Note: to set up one set of pins for rising and another for falling, 2 calls to this 
         function are required, 1st for rising then the second for falling.
   
*/

void llwu_configure(unsigned int pin_en, unsigned char rise_fall, unsigned char module_en ) {
    uint8 temp;
    
    temp = LLWU_PE1;
    if( pin_en & 0x0001)
    {      
        printf("\n Invalid LLWU configured pin PTE1/SCI1_RX/I2C1_SCL /SPI1_SIN");      
    }
    if( pin_en & 0x0002)
    {      
        printf("\n Invalid LLWU configured pin PTE2/SPI1_SCK/SDHC0_DCLK");       
    }
    if( pin_en & 0x0004)
    {
        printf("\n Invalid LLWU configured pin PTE4/SPI1_PCS0/SDHC0_D3");
    }
    if( pin_en & 0x0008)
    {
        printf("\n Invalid LLWU configured pin PTA4/FTM0_CH1/NMI/EZP_CS");
    }
    LLWU_PE1 = temp;

    temp = LLWU_PE2;
    if( pin_en & 0x0010)
    {
        printf("\n Invalid LLWU configured pin PTA13/FTM1_CH1 /FTM1_QD_PHB");
    }
    if( pin_en & 0x0020)
    {
        temp |= LLWU_PE2_WUPE5(rise_fall);
        printf("\n LLWU configured pins PTB0/I2C0_SCL/FTM1_CH0/FTM1_QD_PHA is LLWU wakeup source ");
        LLWU_F1 |= LLWU_F1_WUF5_MASK;   // write one to clear the flag
    }
    if( pin_en & 0x0040)
    {
        temp |= LLWU_PE2_WUPE6(rise_fall);
        printf("\n LLWU configured pins PTC1/SCI1_RTS/FTM0_CH0 is LLWU wakeup source ");
        LLWU_F1 |= LLWU_F1_WUF6_MASK;   // write one to clear the flag
    }
    if( pin_en & 0x0080)
    {
        temp |= LLWU_PE2_WUPE7(rise_fall);
        printf("\n LLWU configured pins PTC3/SCI1_RX/FTM0_CH2 is LLWU wakeup source ");
        LLWU_F1 |= LLWU_F1_WUF7_MASK;   // write one to clear the flag
    }
    LLWU_PE2 = temp;

    temp = LLWU_PE3;
    if( pin_en & 0x0100)
    {
        temp |= LLWU_PE3_WUPE8(rise_fall);
        printf("\n LLWU configured pins PTC4/SPI0_PCS0/FTM0_CH3 is LLWU wakeup source ");
        LLWU_F2 |= LLWU_F2_WUF8_MASK;   // write one to clear the flag
    }
    if( pin_en & 0x0200)
    {
        temp |= LLWU_PE3_WUPE9(rise_fall);
        printf("\n LLWU configured pins PTC5/SPI0_SCK/I2S0_RXD0 is LLWU wakeup source ");
        LLWU_F2 |= LLWU_F2_WUF9_MASK;   // write one to clear the flag
    }
    if( pin_en & 0x0400)
    {
        temp |= LLWU_PE3_WUPE10(rise_fall);
        printf("\n LLWU configured pins PTC6/PDB0_EXTRG to be an LLWU wakeup source ");
        LLWU_F2 |= LLWU_F2_WUF10_MASK;   // write one to clear the flag
    }
    if( pin_en & 0x0800)
    {
        temp |= LLWU_PE3_WUPE11(rise_fall);
        printf("\n LLWU configured pins PTC11/I2S0_RXD1 to be an LLWU wakeup source ");
        LLWU_F2 |= LLWU_F2_WUF11_MASK;   // write one to clear the flag
    }
    LLWU_PE3 = temp;

    temp = LLWU_PE4;
    if( pin_en & 0x1000)
    {
        temp |= LLWU_PE4_WUPE12(rise_fall);
        printf("\n LLWU configured pins PTD0/SPI0_PCS0/SCI2_RTS to be an LLWU wakeup source ");
        LLWU_F2 |= LLWU_F2_WUF12_MASK;   // write one to clear the flag
    }
    if( pin_en & 0x2000)
    {
        temp |= LLWU_PE4_WUPE13(rise_fall);
        printf("\n LLWU configured pins PTD2/SCI2_RX to be an LLWU wakeup source ");
        LLWU_F2 |= LLWU_F2_WUF13_MASK;   // write one to clear the flag
    }
    if( pin_en & 0x4000)
    {
        temp |= LLWU_PE4_WUPE14(rise_fall);
        printf("\n LLWU configured pins PTD4/SCI0_RTS/FTM0_CH4/EWM_IN is LLWU wakeup source ");
        LLWU_F2 |= LLWU_F2_WUF14_MASK;   // write one to clear the flag
    }
    if( pin_en & 0x8000)
    {
        temp |= LLWU_PE4_WUPE15(rise_fall);
        printf("\n LLWU configured pins PTD6/SCI0_RX/FTM0_CH6/FTM0_FLT0 is LLWU wakeup source ");
        LLWU_F2 |= LLWU_F2_WUF15_MASK;   // write one to clear the flag
    }
    LLWU_PE4 = temp;
    if (module_en == 0){
      LLWU_ME = 0;
    }else  {
    LLWU_ME |= module_en;  //Set up more modules to wakeup up
    printf("\n LLWU configured modules as LLWU wakeup sources = 0x%02X,",(LLWU_ME));

    }
} // End LLWU Configuration


void llwu_configure_filter(unsigned int wu_pin_num, unsigned char filter_en, unsigned char rise_fall ) 
{
   //wu_pin_num is the pin number to be written to FILTSEL.  wu_pin_num is not the same as pin_en. 
    uint8 temp;

    printf("\nEnabling Filter %x on WU Pin %x for WU sense %x \n",filter_en, wu_pin_num, rise_fall);
   
     temp = 0;
     //first clear filter values and clear flag by writing a 1
     LLWU_FILT1 = LLWU_FILT1_FILTF_MASK;
     LLWU_FILT2 = LLWU_FILT2_FILTF_MASK;
     
     if(filter_en == 1)
     {
    	 //clear the flag bit and set the others
         temp |= (LLWU_FILT1_FILTF_MASK) | (LLWU_FILT1_FILTE(rise_fall) | LLWU_FILT1_FILTSEL(wu_pin_num));         
         LLWU_FILT1 = temp;
         
     }else if (filter_en == 2)
     {
    	 //clear the flag bit and set the others
    	 temp |= (LLWU_FILT2_FILTF_MASK) | (LLWU_FILT2_FILTE(rise_fall) | LLWU_FILT2_FILTSEL(wu_pin_num));         
         LLWU_FILT2 = temp;
     }else
     {
    	 printf("\nError - invalid filter number\n"); 
     }
}    

// [ILG]
#if defined ( __GNUC__ )
#pragma GCC diagnostic pop
#endif

// [ILG]
#if defined ( __GNUC__ )
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wmissing-prototypes"
#endif

//Interrupt handler for LLWU 
#ifdef CMSIS
void LLW_IRQHandler(void) {
#else
void llwu_isr(void){
#endif
   //printf("\n [LLWU ISR] "); 
   if (LLWU_F1 & LLWU_F1_WUF5_MASK) {
   //    printf("****WUF5 was set *****\r\n"); 
       LLWU_F1 |= LLWU_F1_WUF5_MASK;   // write one to clear the flag
   }
   if (LLWU_F1 & LLWU_F1_WUF6_MASK) {
   //    printf("****WUF6 was set *****\r\n"); 
       LLWU_F1 |= LLWU_F1_WUF6_MASK;   // write one to clear the flag
    }
   if (LLWU_F1 & LLWU_F1_WUF7_MASK) {
   //    printf("****WUF7 was set from PTC3 input  *****\r\n"); 
       LLWU_F1 |= LLWU_F1_WUF7_MASK;   // write one to clear the flag
   }
   if (LLWU_F2 & LLWU_F2_WUF8_MASK) {
   //    printf("****WUF8 was set *****\r\n"); 
       LLWU_F2 |= LLWU_F2_WUF8_MASK;   // write one to clear the flag
   }
   if (LLWU_F2 & LLWU_F2_WUF9_MASK) {
   //    printf("****WUF9 was set *****\r\n"); 
       LLWU_F2 |= LLWU_F2_WUF9_MASK;   // write one to clear the flag
   }
   if (LLWU_F2 & LLWU_F2_WUF10_MASK) {
   //    printf("****WUF10 was set *****\r\n"); 
       LLWU_F2 |= LLWU_F2_WUF10_MASK;   // write one to clear the flag
   }
   if (LLWU_F2 & LLWU_F2_WUF11_MASK) {
   //    printf("****WUF11 was set *****\r\n"); 
       LLWU_F2 |= LLWU_F2_WUF11_MASK;   // write one to clear the flag
   }
   if (LLWU_F2 & LLWU_F2_WUF12_MASK) {
   //    printf("****WUF12 was set *****\r\n"); 
       LLWU_F2 |= LLWU_F2_WUF12_MASK;   // write one to clear the flag
   }
   if (LLWU_F2 & LLWU_F2_WUF13_MASK) {
   //    printf("****WUF13 was set *****\r\n"); 
       LLWU_F2 |= LLWU_F2_WUF13_MASK;   // write one to clear the flag
   }
   if (LLWU_F2 & LLWU_F2_WUF14_MASK) {
   //    printf("****WUF14 was set *****\r\n"); 
       LLWU_F2 |= LLWU_F2_WUF14_MASK;   // write one to clear the flag
   }
   if (LLWU_F2 & LLWU_F2_WUF15_MASK) {
   //    printf("****WUF15 was set *****\r\n"); 
       LLWU_F2 |= LLWU_F2_WUF15_MASK;   // write one to clear the flag
   }
   
   /************************************************************************
    * Note: This ISR does not write to the LLWU_F3 register because these
    * are peripheral module wakeups.  The flags contained in the LLWU_F3 
    * register should be cleared through the associated module interrupt 
    * and not through the LLWU_F3 per the Kinetis L Family Reference
    * Manual (LLWU Chapter)
    **********************************************************************/
  if (LLWU_F3 & LLWU_F3_MWUF0_MASK) {
    //   printf("****WUF3_MWUF0 IF  LPTMR  *****\r\n"); 
         SIM_SCGC5 |= SIM_SCGC5_LPTMR_MASK;
         LPTMR0_CSR |=  LPTMR_CSR_TCF_MASK;   // write 1 to TCF to clear the LPT timer compare flag
         LPTMR0_CSR = ( LPTMR_CSR_TEN_MASK | LPTMR_CSR_TIE_MASK | LPTMR_CSR_TCF_MASK  );
   }
   if(LLWU_FILT1 & LLWU_FILT1_FILTF_MASK){
	   
	   LLWU_FILT1 |= LLWU_FILT1_FILTF_MASK;
   }
   if(LLWU_FILT2 & LLWU_FILT2_FILTF_MASK){
	   
	   LLWU_FILT2 |= LLWU_FILT2_FILTF_MASK;
   }
   NVIC_ICPR |= 1 << (LLWU_irq_no%32);
}

// [ILG]
#if defined ( __GNUC__ )
#pragma GCC diagnostic pop
#endif

