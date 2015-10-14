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
   @addtogroup int
   @{
   @file       IntLib.c
   @brief      Set of interrupt functions.
   - configure external interrupts with EiCfg()
   - clear external interrupt flag with EiClr()
   - Example: Library used in Timers project

   @version    V0.2
   @author     ADI
   @date       October 2015
   @par Revision History:
   - V0.1, August 2011: Initial release.
   - V0.2, October 2015: Coding style cleanup - no functional changes.

**/

#include "IntLib.h"
#include <ADuCM360.h>

/**
   @brief int EiClr(int iEiNr)
         ==========clear external interrupt flag
   @param iEiNr :{EICLR_IRQ0, EICLR_IRQ1, EXTINT2, EXTINT3, EXTINT4, EXTINT5, EXTINT6, EXTINT7}
      - 0x0: EXTINT0, External Interrupt 0
      - 0x1: EXTINT1, External Interrupt 1
      - 0x2: EXTINT2, External Interrupt 2
      - 0x3: EXTINT3, External Interrupt 3
      - 0x4: EXTINT4, External Interrupt 4
      - 0x5: EXTINT5, External Interrupt 5
      - 0x6: EXTINT6, External Interrupt 6
      - 0x7: EXTINT7, External Interrupt 7
   @return 1
   @warning

**/
int EiClr(int iEiNr)
{
   pADI_INTERRUPT->EICLR = 0x1UL << iEiNr;
   return 1;
}

/**
   @brief int EiCfg(int iEiNr, int iEnable, int iMode)
         ==========configures external interrupt
   @param iEiNr :{EXTINT0, EXTINT1, EXTINT2, EXTINT3, EXTINT4, EXTINT5, EXTINT6, EXTINT7}
      - 0x0: EXTINT0, External Interrupt 0
      - 0x1: EXTINT1, External Interrupt 1
      - 0x2: EXTINT2, External Interrupt 2
      - 0x3: EXTINT3, External Interrupt 3
      - 0x4: EXTINT4, External Interrupt 4
      - 0x5: EXTINT5, External Interrupt 5
      - 0x6: EXTINT6, External Interrupt 6
      - 0x7: EXTINT7, External Interrupt 7
        @param iEnable :{INT_DIS,INT_EN}
                - 0x0: INT_DIS for disabled.
                - 0x1: INT_EN for enabled.
        @param iMode :{INT_RISE, INT_FALL, INT_EDGES, INT_HIGH, INT_LOW}
                - 0x0: INT_RISE Rising edge
                - 0x1: INT_FALL Falling edge
                - 0x2: INT_EDGES Rising or falling edge
                - 0x3: INT_HIGH High level
                - 0x4: INT_LOW Low level
   @return 1
   @warning
      the NVIC also needs to be configured
                external interrupts are available regardless of the GPIO configuration
                only ext int 0, 1 and 2 are available in SHUTDOWN mode
**/

int EiCfg(int iEiNr, int iEnable, int iMode)
{
   volatile unsigned long *pEIxCFG;
   unsigned long EIxCFG_A, EI0CFG_A, ulOffset, ulContent, ulMask;
   EI0CFG_A = (unsigned long)& pADI_INTERRUPT->EI0CFG;

   EIxCFG_A = EI0CFG_A + ((iEiNr / 4) * 4); // determine correct EIxCFG register
   pEIxCFG = (volatile unsigned long *)EIxCFG_A;
   ulOffset = (iEiNr % 4) * 4;  // determine correct offset in register

   if (iEnable == INT_DIS) {
      ulMask = 0xFUL << ulOffset;
      *pEIxCFG = *pEIxCFG & ~ulMask ; // clear the appropriate bit in the correct EIxCFG register

   } else {
      pADI_INTERRUPT->EICLR = 0x1UL << iEiNr; // clears flag first
      ulContent = (0x8UL | iMode) << ulOffset;  // calculate the value to write
      ulMask = 0xFUL << ulOffset;
      *pEIxCFG = (*pEIxCFG & ~ulMask) | ulContent; // set the appropriate bits in the correct EIxCFG register
   }

   return 1;
}


/**@}*/

