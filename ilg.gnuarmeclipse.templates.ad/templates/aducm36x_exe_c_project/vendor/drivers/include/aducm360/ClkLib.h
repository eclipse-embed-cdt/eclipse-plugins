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
   @file       ClkLib.h
   @brief      Set of Timer peripheral functions.
   - Example:
   - ClkCfg(CLK_CD3,CLK_HF,CLK_HFO,CLK_OFF);
   - ClkSel(CLK_OFF,CLK_OFF,CLK_CD0,CLK_OFF);

   @version    V0.6
   @author     ADI
   @date    October 2015
   @par Revision History:
   - V0.1, December 2010: initial version.
   - V0.2, January 2012: Changed Clkcfg() - removed write to XOSCCON.
   - V0.3, January 2013: corrected comments.
   - V0.4, February 2013: corrected parameters in ClkDis()
   - V0.5, October 2015: Coding style cleanup - no functional changes.
   - V0.6, October 2015:   Use Standard Integer Types, prefer unsigned types, add include and C++ guards.

**/

#ifndef __ADUCM36X_CLKLIB_H
#define __ADUCM36X_CLKLIB_H

#ifdef __cplusplus
 extern "C" {
#endif

#include <ADuCM360.h>

extern uint32_t ClkCfg(uint32_t iCd, uint32_t iClkSrc, uint32_t iSysClockDiv, uint32_t iClkOut);
extern uint32_t ClkSel(uint32_t iSpiCd, uint32_t iI2cCd, uint32_t iUrtCd, uint32_t iPwmCd);
extern uint32_t ClkDis(uint32_t iClkDis);
extern uint32_t XOSCCfg(uint32_t iXosc);

#define  CLK_OFF     -1
#define  CLK_CD0     0
#define  CLK_CD1     1
#define  CLK_CD2     2
#define  CLK_CD3     3
#define  CLK_CD4     4
#define  CLK_CD5     5
#define  CLK_CD6     6
#define  CLK_CD7     7

#define  CLK_HF      0
#define  CLK_LFX     1
#define  CLK_LF      2
#define  CLK_P4      3

#define  CLK_UCLKCG  0
#define  CLK_UCLK 1
#define  CLK_UDIV 2
#define  CLK_HFO     5
#define  CLK_LFO     6
#define  CLK_LFXO 7

#define  CLK_XOFF 0
#define  CLK_XON     1
#define  CLK_XON2 5

#ifdef __cplusplus
}
#endif

#endif /* __ADUCM36X_CLKLIB_H */
