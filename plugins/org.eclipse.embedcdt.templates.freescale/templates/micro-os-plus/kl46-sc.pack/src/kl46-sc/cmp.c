/*
 * File:        cmp.c
 * Purpose:     Provide common CMP routines
 *
 * Notes:       
 *              
 */


#include "common.h"
#include "cmp.h"

/********************************************************************/
/*
 * Initialize the CMP for operation, interrupts, clock select, input select
 * 
 *
 * NOTE: Since the Comparators are pinned out in multiple locations on most
 *       Kinetis devices, this driver does not enable CMP pin functions.
 *       The desired pins should be enabled before calling this init function.
 *
 * Parameters:
 *  cmpch           - CMP channel to initialize
 *  rising_falling  - equal to bit mask in the CMP_SCR Register
 *                    CMP_SCR_IER_MASK or CMP_SCR_IEF_MASK
 
 
 * the reset of these inputs  are not yet implemented
 *  clk             - CMP module Clock
 *  inp         cmp pos input selection
 *  inn         cmp neg input selection
 *  outen       cmp output enable
 *  dacval      cmp dac input setting.
 */
void cmp_init ( unsigned char rising_falling ) 
{
               // Filter and digital hysteresis disabled
             CMP0_CR0 = 0x00;
             // no sample(continuous), no window, -, HS mode, 
             // no invert, COUTA(unfiltered), output pin enable, moduledisable
             CMP0_CR1 = CMP_CR1_PMODE_MASK | CMP_CR1_COS_MASK | CMP_CR1_OPE_MASK; //0x16;
             // Filter disabled
             CMP0_FPR = 0x00;
             // Enable rising or falling edge interrupts, clear flags
             CMP0_SCR = //CMP_SCR_IER_MASK |
                                  rising_falling |
                                  //CMP_SCR_IEF_MASK | 
                                  CMP_SCR_CFR_MASK |
                                  CMP_SCR_CFF_MASK;   //0x1E
             // 6-bit DAC enabled, output set to 1/2-scale, Vdd reference
             CMP0_DACCR = CMP_DACCR_DACEN_MASK |
                                    CMP_DACCR_VRSEL_MASK |
                                    CMP_DACCR_VOSEL(0x20);   //0xE0;
             // P-input as external channel 0, M-input as 6b DAC
             CMP0_MUXCR = CMP_MUXCR_PSEL(0) |
                                    CMP_MUXCR_MSEL(7);   //0x07;

             // enable CMP
             CMP0_CR1 |= CMP_CR1_EN_MASK;
}


void cmp0_isr(void)
{
  GPIOB_PSOR = 0x00080000;           // set Port B 19 indicate wakeup

  printf("\n[CMP0_ISR]****ISR entered*****\r\n\n");
  printf("[CMP0_ISR]CMP0_SCR   = %#02X ",    (CMP0_SCR ))  ;
  if (CMP0_SCR & CMP_SCR_CFR_MASK) {
     CMP0_SCR |= CMP_SCR_CFR_MASK;  
     printf(" CMP0_CFR set and is now clear \n");
     printf("[CMP0_ISR]CMP0_SCR   = %#02X ",    (CMP0_SCR ))  ;
  }
  if (CMP0_SCR & CMP_SCR_CFF_MASK) {
     CMP0_SCR |= CMP_SCR_CFF_MASK;  
     printf(" CMP0_CFF set and is now clear \n");
     printf("[CMP0_ISR]CMP0_SCR   = %#02X ",    (CMP0_SCR ))  ;
  }
  return;  
}
