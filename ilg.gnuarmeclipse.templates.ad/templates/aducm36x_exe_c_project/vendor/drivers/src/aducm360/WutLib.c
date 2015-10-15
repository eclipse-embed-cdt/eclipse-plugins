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
   @addtogroup wut
   @{
   @file     WutLib.c
   @brief    Set of wake up Timer peripheral functions.
   @version  V0.3
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.1, May 2012: initial version.
   - V0.2, May 2013: corrected WutLdRd() - correct values returned.
   - V0.3, October 2015: Coding style cleanup - no functional changes.

**/

#include "WutLib.h"
#include <ADuCM360.h>


/**
   @brief int WutCfg(int iMode, int iWake, int iPre, int iClkSrc)

   @param iMode :{T2CON_MOD_PERIODIC,T2CON_MOD_FREERUN}
      - 0 or T2CON_MOD_PERIODIC for periodic mode
      - 1 or T2CON_MOD_FREERUN for free running mode
   @param iWake :{T2CON_WUEN_DIS,T2CON_WUEN_EN}
      - 0 or T2CON_WUEN_DIS to use the wake up timer as general purpose timer
      - 1 or T2CON_WUEN_EN to allow the wake up timer to wake up the part
   @param iPre :{T2CON_PRE_DIV1,T2CON_PRE_DIV16,T2CON_PRE_DIV256,T2CON_PRE_DIV32768}
      - 0 or T2CON_PRE_DIV1
      - 1 or T2CON_PRE_DIV16
                - 2 or T2CON_PRE_DIV256
                - 3 or T2CON_PRE_DIV32768
   @param iClkSrc :{T2CON_CLK_PCLK,T2CON_CLK_LFXTAL,T2CON_CLK_LFOSC,T2CON_CLK_EXTCLK}
      - 0 or T2CON_CLK_PCLK => Clocked by system clock.
      - 0x200 or T2CON_CLK_LFXTAL =>  Clocked by external 32kHz crystal.
      - 0x400 or T2CON_CLK_LFOSC =>  Clocked by 32kHz internal oscillator.
      - 0x600 or T2CON_CLK_EXTCLK =>  Clocked by external signal on GPIO.
   @return 0 if timer interface busy or 1 if successfull.

**/

int WutCfg(int iMode, int iWake, int iPre, int iClkSrc)
{
   int i1 = 0;

   i1 |= iMode;
   i1 |= iWake;
   i1 |= iClkSrc;
   i1 |= iPre;
   i1 |= T2CON_WUEN; // the wake up timer interrupt if enabled is allowed to wake up the device
   pADI_WUT->T2CON = i1;
   return 1;
}


/**
   @brief int WutInc(int iInc);
         ==========Sets timer increment value.
   @param iInc :{0-0xFFF}
      - Sets increment value TxLD to iInc.
                - if the timer is already running, this function stops WUA during update of increment register
   @return 1
**/
int WutInc(int iInc)
{
   if (T2CON_ENABLE_BBA == 1) {
      T2CON_STOPINC_BBA = 1;
   }

   pADI_WUT->T2INC = iInc;
   T2CON_STOPINC_BBA = 0;
   return 1;
}


/**
   @brief int WutLdWr(int iField, unsigned long lTld);
         ==========Sets timer reload value.
   @param iField :{0, 1, 2, 3}
      - 0 for field A
      - 1 for field B
      - 2 for field C
        - 3 for field D. In periodic mode field D is the timeout.
   @param lTld :{0-0xFFFFFFFF}
      - Sets reload value TxLD to lTLd.
   @return 0 if invalid field or 1 if field is valid
   @note If the timer is already running the corresponding interrupt should be diabled.
**/
int WutLdWr(int iField, unsigned long lTld)
{
   switch (iField) {
   case 0: {
      if (T2CON_ENABLE_BBA == 1) {
         T2CON_STOPINC_BBA = 1;
      }

      pADI_WUT->T2WUFA0 = (short)(lTld & 0x0000FFFF);
      pADI_WUT->T2WUFA1 = (short)(lTld >> 16);
      break;
   }

   case 1: {
      pADI_WUT->T2WUFB0 = (short)(lTld & 0x0000FFFF);
      pADI_WUT->T2WUFB1 = (short)(lTld >> 16);
      break;
   }

   case 2: {
      pADI_WUT->T2WUFC0 = (short)(lTld & 0x0000FFFF);
      pADI_WUT->T2WUFC1 = (short)(lTld >> 16);
      break;
   }

   case 3: {
      pADI_WUT->T2WUFD0 = (short)(lTld & 0x0000FFFF);
      pADI_WUT->T2WUFD1 = (short)(lTld >> 16);
      break;
   }

   default:
      return 0;
   }

   return 1;
}

/**
   @brief unsigned long WutLdRd(int iField);
         ==========Sets timer reload value.
   @param iField :{0, 1, 2, 3}
      - 0 for field A
      - 1 for field B
      - 2 for field C
      - 3 for field D.
   @return TxLD corresponding to iField or 0 if iField is not valid
**/

unsigned long WutLdRd(int iField)
{
   unsigned long uL;

   switch (iField) {
   case 0: {
      uL = pADI_WUT->T2WUFA1;
      uL = uL << 16;
      uL |= pADI_WUT->T2WUFA0;
      break;
   }

   case 1: {
      uL = pADI_WUT->T2WUFB1;
      uL = uL << 16;
      uL |= pADI_WUT->T2WUFB0;
      break;
   }

   case 2: {
      uL = pADI_WUT->T2WUFC1;
      uL = uL << 16;
      uL |= pADI_WUT->T2WUFC0;
      break;
   }

   case 3: {
      uL = pADI_WUT->T2WUFC1;
      uL = uL << 16;
      uL |= pADI_WUT->T2WUFC0;
      break;
   }

   default: {
      uL = 0;
      break;
   }
   }

   return uL;
}

/**
   @brief long WutVal(void);
         ==========Reads timer value.

   @return 0 if freeze has not worked...
                 or timer value T2VAL.
**/

long WutVal(void)
{
   long lVal;

   if (T2CON_FREEZE_BBA == 0) {
      T2CON_FREEZE_BBA = 1;
   }

   lVal = pADI_WUT->T2VAL0;

   if (T2STA_FREEZE_BBA == 0) {
      return 0;   // T2VAL is not frozen
   }

   lVal |= (pADI_WUT->T2VAL1 << 16);
   return (lVal);
}



/**
   @brief int WutSta(void);
         ==========Returns Timer Status.
   @return T2STA.
**/

int WutSta(void)
{
   return (pADI_WUT->T2STA);
}



/**
   @brief int WutClrInt(int iSource);
         ==========Clear timer interrupts.
   @param iSource :{T2CLRI_WUFA,T2CLRI_WUFB,T2CLRI_WUFC,T2CLRI_WUFD,T2CLRI_ROLL}
      - 0 or T2CLRI_WUFA for wake up field A interrupt
      - 1 or T2CLRI_WUFB for wake up field B interrupt
                - 2 or T2CLRI_WUFC for wake up field C interrupt
                - 3 or T2CLRI_WUFD for wake up field D interrupt
                - 4 or T2CLRI_ROLL for timer overflow interrupt
   @return 1.
**/
int WutClrInt(int iSource)
{
   (pADI_WUT->T2CLRI) = iSource;
   return 1;
}


/**
   @brief int WutCfgInt(int iSource, iEnable);
         ==========Clear timer interrupts.
   @param iSource :{T2IEN_WUFA| T2IEN_WUFB| T2IEN_WUFC| T2IEN_WUFD| T2IEN_ROLL}
      - 0x1 or T2IEN_WUFA for wake up field A interrupt
      - 0x2 or T2IEN_WUFB for wake up field B interrupt
      - 0x4 or T2IEN_WUFC for wake up field C interrupt
      - 0x8 or T2IEN_WUFD for wake up field D interrupt
      - 0x10 or T2IEN_ROLL for timer overflow interrupt
   @param iEnable :{0,1}
      - 0 disable interrupt
      - 1 enable interrupt
   @return 1.
**/
int WutCfgInt(int iSource, int iEnable)
{
   int iInterrupt = 0;

   if (iEnable == 1) {           // Enabling interrupts
      iInterrupt = (iSource & 0x1F);
      pADI_WUT->T2IEN |= iInterrupt;

   } else {
      iInterrupt = pADI_WUT->T2IEN;
      iInterrupt = (iSource ^ iInterrupt);
      pADI_WUT->T2IEN = (iInterrupt & 0x1F);
   }

   return 1;
}


/**
   @brief int WutGo(int iEnable);
         ==========Enable or reset the wake up timer
   @param iEnable :{T2CON_ENABLE_DIS,T2CON_ENABLE_EN}
      - 0 or T2CON_ENABLE_DIS to reset the timer
      - 0x80 or T2CON_ENABLE_EN to enable the timer
   @return 1.
**/
int WutGo(int iEnable)
{
   if (iEnable == T2CON_ENABLE_DIS) {
      pADI_WUT->T2CON = T2CON_ENABLE_DIS;

   } else {
      T2CON_ENABLE_BBA = 1;
   }

   return 1;
}


/**@}*/
