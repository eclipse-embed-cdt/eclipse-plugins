/*
 * File:        pmc.c
 * Purpose:     Provides routines for entering low power modes.
 *
 * Notes:	Since the wakeup mechanism for low power modes
 *              will be application specific, these routines
 *              do not include code to setup interrupts to exit
 *              from the low power modes. The desired means of
 *              low power mode exit should be configured before
 *              calling any of these functions.
 *
 *              These routines do not include protection to
 *              prevent illegal state transitions in the mode
 *              controller, and all routines that write to the
 *              PMPROT register write a value to allow all
 *              possible low power modes (it is write once, so
 *              if only the currently requested mode is enabled
 *              a different mode couldn't be enabled later on).
 *              
 */

#include "common.h"
#include "pmc.h"
#include "mcg.h"
#include "uart.h"
int i;

// [ILG]
#if defined ( __GNUC__ )
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wconversion"
#endif

void LVD_Initalize(unsigned char lvd_select, 
                   unsigned char lvd_reset_enable,
                   unsigned char lvd_int_enable, 
                   unsigned char lvw_select,
                   unsigned char lvw_int_enable){
    PMC_LVDSC1 =  PMC_LVDSC1_LVDACK_MASK | 
                 (lvd_reset_enable) |   //enable LVD Reset ?
	          lvd_int_enable |      //LVD Interrupt ?
	          PMC_LVDSC1_LVDV(lvd_select)   ;        //select high or low LVD
    PMC_LVDSC2  = PMC_LVDSC2_LVWACK_MASK | 
                 (lvw_int_enable) |    //LVW interrupt?
                  PMC_LVDSC2_LVWV(lvw_select);  // select LVW level 1,2,3 or 4
}

// [ILG]
#if defined ( __GNUC__ )
#pragma GCC diagnostic pop
#endif

//******************************************************************************
void LVD_Init(void)
{
/* setup LVD
Low-Voltage Detect Voltage Select
Selects the LVD trip point voltage (VLVD).
00 Low trip point selected (VLVD = VLVDL)
01 High trip point selected (VLVD = VLVDH)
10 Reserved
11 Reserved
  */
	PMC_LVDSC1 =  PMC_LVDSC1_LVDRE_MASK |   //enable LVD Reset
	               PMC_LVDSC1_LVDV(1);   //Enable LVD Reset High level

        
PMC_LVDSC2  = PMC_LVDSC2_LVWACK_MASK | 
              PMC_LVDSC2_LVWV(3) ;


// ack to clear initial flags
PMC_LVDSC1 |= PMC_LVDSC1_LVDACK_MASK; 
PMC_LVDSC2 |= PMC_LVDSC2_LVWACK_MASK;

/* 
LVWV if LVD high range selected
2.621
2.72
2.82
2.92
LVDV if LVD low range selected
1.74
1.84
1.94


*/
}
void pmc_lvd_isr(void)
{
 
  if (PMC_LVDSC1 &PMC_LVDSC1_LVDF_MASK){
   printf("[LVD_isr]LV DETECT interrupt occurred");
  }
  if (PMC_LVDSC2 &PMC_LVDSC2_LVWF_MASK){
   printf("[LVD_isr]LV WARNING interrupt occurred");
  }
 
 // ack to clear initial flags
 PMC_LVDSC1 |= PMC_LVDSC1_LVDACK_MASK; 
 PMC_LVDSC2 |= PMC_LVDSC2_LVWACK_MASK;

}




