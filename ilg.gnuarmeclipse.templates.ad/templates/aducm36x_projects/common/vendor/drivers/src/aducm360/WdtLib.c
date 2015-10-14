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
   @addtogroup wdt
   @{
   @file     WdtLib.c
   @brief    Set of watchdog Timer peripheral functions.
   @version  V0.3
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.1, May 2012: initial version.
   - V0.2, September 2012: WdtClrInt now checks if it is safe to write to T3CLRI and
            this is now a function with no inputs
   - V0.3, October 2015: Coding style cleanup - no functional changes.

**/
#include "WdtLib.h"
#include <ADuCM360.h>


/**
   @brief int WdtCfg(int iPre, int iInt, int iPd);
         ========== Configures the watchdog timer.
   @param iPre :{T3CON_PRE_DIV1,T3CON_PRE_DIV16,T3CON_PRE_DIV256,T3CON_PRE_DIV4096}
      - T3CON_PRE_DIV1, for a prescaler of 1.
      - T3CON_PRE_DIV16, for a prescaler of 16.
                - T3CON_PRE_DIV256, for a prescaler of 256.
                - T3CON_PRE_DIV4096, for a prescaler of 4096.
   @param iInt :{T3CON_IRQ_DIS,T3CON_IRQ_EN}
      - T3CON_IRQ_DIS, to generate a reset on a timer timeout.
      - T3CON_IRQ_EN, to generate an interrupt on a timer timeout.
        @param iPd :{T3CON_PD_DIS,T3CON_PD_EN}
      - T3CON_PD_DIS, the timer continues to count when in hibernate mode.
      - T3CON_PD_EN, the timer stops counting when in hibernate mode.
   @return 1.

**/

int WdtCfg(int iPre, int iInt, int iPd)
{
   int i1;
   i1 = pADI_WDT->T3CON & 0x20;  // keep bit 5
   i1 |= 0x40;                   // reserved bit that should be set
   i1 |= iPre;
   i1 |= iInt;
   i1 |= iPd;
   pADI_WDT->T3CON = i1;
   return 1;
}



/**
   @brief int WdtGo(int iEnable);
         ========== Enable or reset the watchdog timer.
   @param iEnable :{T3CON_ENABLE_DIS,T3CON_ENABLE_EN}
      - T3CON_ENABLE_DIS, to reset the timer.
      - T3CON_ENABLE_EN, to enable the timer.
   @return 1.
**/
int WdtGo(int iEnable)
{
   if (iEnable == T3CON_ENABLE_DIS) {
      T3CON_ENABLE_BBA = 0;

   } else {
      T3CON_ENABLE_BBA = 1;
   }

   return 1;
}


/**
   @brief int WdtLd(int iTld);
         ========== Sets timer reload value.
   @param iTld :{0-0xFFFF}
      - Sets reload value T3LD to iTld.
   @return 1.
**/
int WdtLd(int iTld)
{
   pADI_WDT->T3LD = iTld;
   return 1;
}


/**
   @brief int WdtVal(void);
         ========== Reads timer value.
   @return Timer value T3VAL.
**/

int WdtVal(void)
{
   return pADI_WDT->T3VAL;
}


/**
   @brief int WdtSta(void);
         ========== Returns timer Status.
   @return T3STA.
**/

int WdtSta(void)
{
   return pADI_WDT->T3STA;
}



/**
   @brief int WdtClrInt(void);
         ========== Clear timer interrupts.
   @return 1 if the interrupt was cleared successfully, 0 otherwise.
**/
int WdtClrInt(void)
{
   if(T3STA_CLRI_BBA == 0) {
      pADI_WDT->T3CLRI = 0xCCCC;
      return 1;

   } else {
      return 0;
   }
}



/**@}*/
