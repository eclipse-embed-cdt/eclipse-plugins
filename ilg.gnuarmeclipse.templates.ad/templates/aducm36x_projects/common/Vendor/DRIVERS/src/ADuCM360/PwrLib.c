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
   @addtogroup pwr
   @{
   @file     PwrLib.c
   @brief    Functions for controling power modes
   @version  V0.2
   @author   PAD CSE group
   @date     October 2015
   @par Revision History:
   - V0.1, September 2012: Initial release.
   - V0.2, October 2015: Coding style cleanup - no functional changes.

**/
#include "PwrLib.h"
#include "ADuCM360.h"

/**
   @brief int PwrCfg(int iMode)
         ========== Sets MCU power mode.
   @param iMode :{PWRMOD_MOD_FULLACTIVE,PWRMOD_MOD_MCUHALT,PWRMOD_MOD_PERHALT,PWRMOD_MOD_SYSHALT,PWRMOD_MOD_TOTALHALT,PWRMOD_MOD_HIBERNATE }
      - 0 or PWRMOD_MOD_FULLACTIVE for fully active mode.
      - 1 or PPWRMOD_MOD_MCUHALT to halt the MCU.
      - 2 or PWRMOD_MOD_PERHALT to halt the MCU and clock to peripherals.
      - 3 or PWRMOD_MOD_SYSHALT to halt the MCU, and clock to memory and DMA.
      - 4 or PWRMOD_MOD_TOTALHALT For DEEPSLEEP mode
      - 5 or PWRMOD_MOD_HIBERNATE For DEEPSLEEP mode
   @note
   @return 1.
**/

int PwrCfg(int iMode)
{
   int index = 0;

   if (iMode > 5) { // Check for invalid sleep mode value
      iMode = 0;
   }

   if ((iMode == 4) || (iMode == 5)) {
      SCB->SCR = 0x04;    // sleepdeep mode - write to the Cortex-m3 System Control register bit2
   }

   pADI_PWRCTL->PWRKEY = 0x4859; // key1
   pADI_PWRCTL->PWRKEY = 0xF27B; // key2
   pADI_PWRCTL->PWRMOD = iMode;

   for (index = 0; index < 2; index++);

   __WFI();

   for (index = 0; index < 2; index++);

   return 1;
}
/**
   @brief int PwrRead(void)
         ========== reads MCU control register including WCENACK bit.

   @note
   @return 1.
**/
int PwrRead(void)
{
   return pADI_PWRCTL->PWRMOD;
}

/**@}*/
