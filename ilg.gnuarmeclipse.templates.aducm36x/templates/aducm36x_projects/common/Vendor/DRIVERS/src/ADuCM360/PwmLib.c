/*******************************************************************************
* Copyright 2015(c) Analog Devices, Inc.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification,
* are permitted provided that the following conditions are met:
*  - Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*  - Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*  - Neither the name of Analog Devices, Inc. nor the names of its
*    contributors may be used to endorse or promote products derived
*    from this software without specific prior written permission.
*  - The use of this software may or may not infringe the patent rights
*    of one or more patent holders.  This license does not release you
*    from the requirement that you obtain separate licenses from these
*    patent holders to use this software.
*  - Use of the software either in source or binary form, must be run
*    on or directly connected to an Analog Devices Inc. component.
*
* THIS SOFTWARE IS PROVIDED BY ANALOG DEVICES "AS IS" AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, NON-INFRINGEMENT, MERCHANTABILITY
* AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
* IN NO EVENT SHALL ANALOG DEVICES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* INTELLECTUAL PROPERTY RIGHTS, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*******************************************************************************/

/**
 *****************************************************************************
   @addtogroup pwm
   @{
   @file     PwmLib.c
   @brief    Set of PWM peripheral functions.
   @version  V0.5
   @author   ADI
   @date     October 2015

   @par Revision History:
   - V0.2, September 2012: initial version.
   - V0.3, April 2013: corrected comment on PWM period calculation
   - V0.4, August 2013: corrected  PwmHBCfg() function
   - V0.5, October 2015: Coding style cleanup - no functional changes.

**/

#include "PwmLib.h"
#include <ADuCM360.h>


/**
      @brief int PwmInit(unsigned int iPWMCP, unsigned int iPWMIEN, unsigned int iSYNC, unsigned int iTRIP)
         ========== Configure the PWM peripheral clock, interrupt, synchronisation and trip.

      @param iPWMCP :{UCLK_2,UCLK_4,UCLK_8,UCLK_16,UCLK_32,UCLK_64,UCLK_128,UCLK_256}
      - PWMCON0.8-6. Divides UCLK to the PWM peripheral
      - UCLK_2 or 0 = UCLK/2
      - UCLK_4 or 0x40 = UCLK/4
      - UCLK_8 or 0x80 = UCLK/8
      - UCLK_16 or 0xC0 = UCLK/16
      - UCLK_32 or 0x100 = UCLK/32
      - UCLK_64 or 0x140 = UCLK/64
      - UCLK_128 or 0x180 = UCLK/128
      - UCLK_256 or 0x1C0 = UCLK/256

      @param iPWMIEN :{PWMCON0_PWMIEN_DIS,PWMCON0_PWMIEN_EN}
      - PWMCON0.10
      - PWMCON0_PWMIEN_DIS or 0 for PWM Interupt disable
      - PWMCON0_PWMIEN_EN or 0x400 for PWM Interupt enable

      @param iSYNC :{PWMCON0_SYNC_DIS,PWMCON0_SYNC_EN}
      - PWMCON0.15
      - PWMCON0_SYNC_DIS or 0 to disable PWM Synchronization
      - PWMCON0_SYNC_EN or 0x8000 to enable all PWM counters to reset on the next clock
           edge after the detection of a falling edge on the SYNC pin.

      @param iTRIP :{PWMCON1_TRIPEN_DIS,PWMCON1_TRIPEN_EN}
      - PWMCON1.6
      - PWMCON1_TRIPEN_DIS or 0 to Disable PWM trip functionality
      - PWMCON1_TRIPEN_EN or 0x40 to enable PWM trip function

      @return 1
      @note
        This function disables HBridge mode and other configurations.
        It should be called before any other PWM functions.
**/
int PwmInit(unsigned int iPWMCP, unsigned int iPWMIEN, unsigned int iSYNC, unsigned int iTRIP)
{
   unsigned int i1;
   i1 = iPWMCP;
   i1 |= iPWMIEN;
   i1 |= iSYNC;
   pADI_PWM->PWMCON0 = i1;
   pADI_PWM->PWMCON1 = iTRIP;

   return 1;
}


/**

      @brief int PwmTime(int iPair, unsigned int uiFreq, unsigned int uiPWMH_High, unsigned int uiPWML_High)
         ========== Configure period and duty cycle of a pair.

      @param iPair :{PWM0_1, PWM2_3, PWM4_5}
         - PWM0_1 or 0 for PWM Pair0 (PWM0/PWM1 outputs)
         - PWM2_3 or 1 for PWM Pair1 (PWM2/PWM3 outputs)
         - PWM4_5 or 2 for PWM Pair2 (PWM4/PWM5 outputs)

      @param uiFreq :{0-0xFFFF}                    \n
               The PWM Period is calculated as follows:  \n
               tUCLK/DIV * (PWMxLEN+1) * Nprescale
                    where tUCLK/DIV is the PWM clock frequency selected by CLKCON1[14:12] and CLKSYSDIV[0], Nprescale is PWMCON0[8:6]

      @param uiPWMH_High :{0-0xFFFF}
              - Pass values between 0-0xFFFF for duty cycle of high and low side
                   - High Side High period must be greater or equal to Low side High period (iPWM0High>=iPWM1High)
         - High Side high period must be less than uiFreq
         - uiPWMH_High/uiFreq gives the duty cycle for ratio for PWMH high      \n
           For example, uiFreq = 0x50 and uiPWMH_High = 0x30; then duty cycle is 60:40 for high:low on PWMH pin

      @param uiPWML_High :{0-0xFFFF}
         - Pass values between 0-0xFFFF for duty cycle of high and low side
                   - Low output High period must be less or equal to High side High period (iPWM0High>=iPWM1High)
         - Low Side high period must be less than uiFreq
         - uiPWML_High/uiFreq gives the duty cycle for ratio for PWML high    \n
           For example, uiFreq = 0x50 and uiPWML_High = 0x10; then duty cycle is 20:80 for high:low on PWML pin

      @return 0     Failure: uiPWMH_High < uiPWML_High
      @return 1     Success
      @return 2     Failure: uiPWML_High > pADI_PWM->PWM0COM1 - this will result in lower than expected duty cycle on PWML output
**/

int PwmTime(int iPair, unsigned int uiFreq, unsigned int uiPWMH_High, unsigned int uiPWML_High)
{
   if  (uiPWMH_High < uiPWML_High) {
      return 0x0;   // Error: PWM0 High period must be >= PWM1 High period; PWM2 High period must be >= PWM3 High period; PWM4 High period must be >= PWM5 High period
   }

   switch (iPair) {
   case 0:
      pADI_PWM->PWM0LEN = uiFreq;
      pADI_PWM->PWM0COM0 = pADI_PWM->PWM0LEN;
      pADI_PWM->PWM0COM1 = uiPWMH_High;

      if ( uiPWML_High < pADI_PWM->PWM0COM1) {
         pADI_PWM->PWM0COM2 = uiPWML_High;

      } else {       // PWML output is dictated by PWMH. PWML high period will be PWMLEN-PWMH_HIGH
         return 2;
      }

      break;

   case 1:
      pADI_PWM->PWM1LEN = uiFreq;
      pADI_PWM->PWM1COM0 = pADI_PWM->PWM1LEN;
      pADI_PWM->PWM1COM1 = uiPWMH_High;

      if ( uiPWML_High < pADI_PWM->PWM1COM1) {
         pADI_PWM->PWM1COM2 = uiPWML_High;

      } else {       // PWML output is dictated by PWMH. PWML high period will be PWMLEN-PWMH_HIGH
         return 2;
      }

      break;

   case 2:
      pADI_PWM->PWM2LEN = uiFreq;
      pADI_PWM->PWM2COM0 = pADI_PWM->PWM2LEN;
      pADI_PWM->PWM2COM1 = uiPWMH_High;

      if ( uiPWML_High < pADI_PWM->PWM2COM1) {
         pADI_PWM->PWM2COM2 = uiPWML_High;

      } else {       // PWML output is dictated by PWMH. PWML high period will be PWMLEN-PWMH_HIGH
         return 2;
      }

      break;

   default:
      break;
   }

   return 1;
}


/**
      @brief int PwmClrInt(unsigned int iSource)
         ========== Clear PWMs interrupt flags.

      @param iSource :{PWMCLRI_TRIP|PWMCLRI_PWM2|PWMCLRI_PWM1|PWMCLRI_PWM0}
      - PWMCLRI.0 - 4
      - NULL do nothing
      - PWMCLRI_x for : clear the latched IRQPWMx interrupt.

      @return 1
**/
int PwmClrInt(unsigned int iSource)
{
   pADI_PWM->PWMCLRI |= iSource;
   return 1;
}
/**
      @brief int PwmLoad(int iLoad)
         ========== Controls how PWM compare registers are loaded.

      @param iLoad :{PWMCON0_LCOMP_DIS,PWMCON0_LCOMP_EN}
      - PWMCON0_LCOMP_DIS or 0 to use previous compare register values
      - PWMCON0_LCOMP_EN or 0x8 to load compare registers on the next PWM timer transition (low to high edge)

      @return 1
**/
int PwmLoad(int iLoad)
{
   unsigned int i1;

   i1 = (pADI_PWM->PWMCON0 & 0xFFF7); // mask off bit 3
   i1 |= iLoad;
   pADI_PWM->PWMCON0 = i1;
   return 1;
}

/**
      @brief int PwmGo(unsigned int iPWMEN, unsigned int iHMODE)
         ========== Starts/Stops the PWM in standard or H-Bridge mode.

      @param iPWMEN :{PWMCON0_ENABLE_DIS,PWMCON0_ENABLE_EN}
      - PWMCON0_ENABLE_DIS or 0x0  to disable the PWM peripheral.
      - PWMCON0_ENABLE_EN or 0x1 to enable/start the PWM peripheral.
      @param iHMODE :{PWMCON0_MOD_DIS,PWMCON0_MOD_EN}
      - PWMCON0_MOD_DIS or 0 to operate in standard mode.
      - PWMCON0_MOD_EN or 0x2 to operate in H-Bridge mode.

      @return 1

**/
int PwmGo(unsigned int iPWMEN, unsigned int iHMODE)
{
   if (iHMODE == PWMCON0_MOD_DIS) {
      PWMCON0_MOD_BBA = 0;   // Standard mode

   } else {
      PWMCON0_MOD_BBA = 1;   // H-Bridge mode
   }

   if (iPWMEN == PWMCON0_ENABLE_DIS) {
      PWMCON0_ENABLE_BBA = 0;   // Disable PWM

   } else {
      PWMCON0_ENABLE_BBA = 1;   // Enable PWM
   }

   return 1;
}

/**
      @brief int PwmInvert(int iInv1, int iInv3, int iInv5)
         ========== Selects PWM outputs for inversion.

      @param iInv1 :{PWMCON0_PWM1INV_DIS, PWMCON0_PWM1INV_EN}
      - PWMCON0_PWM1INV_DIS or 0 to Disable PWM1 output inversion.
      - PWMCON0_PWM1INV_EN or 0x800 to Enable PWM1 output inversion.
     @param iInv3 :{PWMCON0_PWM3INV_DIS,PWMCON0_PWM3INV_EN}
      - PWMCON0_PWM3INV_DIS or 0 to Disable PWM3 output inversion.
      - PWMCON0_PWM3INV_EN or 0x1000 to Enable PWM3 output inversion.
     @param iInv5 :{PWMCON0_PWM5INV_DIS,PWMCON0_PWM5INV_EN}
      - PWMCON0_PWM5INV_DIS or 0 to Disable PWM5 output inversion.
      - PWMCON0_PWM5INV_EN or 0x2000 to Enable PWM5 output inversion.
      @return 1

**/
int PwmInvert(int iInv1, int iInv3, int iInv5)
{
   unsigned int i1;

   i1 = (pADI_PWM->PWMCON0 & 0x87FF); // mask off bits  11, 12, 13
   i1 |= (iInv1 + iInv3 + iInv5);
   pADI_PWM->PWMCON0 = i1;
   return 1;
}

/**
      @brief int PwmHBCfg(unsigned int iENA, unsigned int iPOINV, unsigned int iHOFF, unsigned int iDIR)
         ========== HBridge mode - configure PWMs.

      @param iENA :{PWMCON0_ENA_DIS,PWMCON0_ENA_EN}
      - PWMCON0_ENA_DIS or 0 to use the values previously stored in the internal compare registers.
      - PWMCON0_ENA_EN or 0x200 enable HBridge outputs

      @param iPOINV :{PWMCON0_POINV_DIS,PWMCON0_POINV_EN}
      - PWMCON0_POINV_DIS or 0 for PWM outputs as normal in H-Bridge mode
      - PWMCON0_POINV_EN for invert all PWM outputs in H-bridge mode

      @param iHOFF :{PWMCON0_HOFF_DIS,PWMCON0_HOFF_EN}
      - PWMCON0_HOFF_DIS 0 for PWM outputs as normal in H-Bridge mode
      - PWMCON0_HOFF_EN for Forces PWM0 and PWM2 outputs high and PWM1 and PWM3 low (default).

      @param iDIR :{PWMCON0_DIR_DIS,PWMCON0_DIR_EN}
      - PWMCON0_DIR_DIS for Enable PWM2 and PWM3 as the output signals while PWM0 and PWM1 are held low.
      - PWMCON0_DIR_EN for Enables PWM0 and PWM1 as the output signals while PWM2 and PWM3 are held low.

      @return 1
**/
int PwmHBCfg(unsigned int iENA, unsigned int iPOINV, unsigned int iHOFF, unsigned int iDIR)
{
   unsigned int i1;

   i1 = (pADI_PWM->PWMCON0 & 0xFDCB);   // Mask bits 9,5,4,2
   i1 |= iENA;
   i1 |= iPOINV;
   i1 |= iHOFF;
   i1 |= iDIR;
   pADI_PWM->PWMCON0 = i1;
   return 1;
}

/**@}*/

