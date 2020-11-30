/*******************************************************************************
* Copyright 2015-2018(c) Analog Devices, Inc.
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
   @file     PwmLib.h
   @brief    Set of PWM peripheral functions.
   @version  V0.4
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.2, September 2012: Initial release.
   - V0.3, October 2015: Coding style cleanup - no functional changes.
   - V0.4, October 2015: Use Standard Integer Types, prefer unsigned types, add include and C++ guards.

*****************************************************************************/
#ifndef __ADUCM36X_PWMLIB_H
#define __ADUCM36X_PWMLIB_H

#ifdef __cplusplus
 extern "C" {
#endif

#include <sys/platform.h>

extern uint32_t PwmInit(uint32_t iPWMCP, uint32_t iPWMIEN, uint32_t iSYNC, uint32_t iTRIP);
extern uint32_t PwmClrInt(uint32_t iSource);
extern uint32_t PwmTime(uint32_t iPair, uint32_t uiFreq, uint32_t uiPWMH_High, uint32_t uiPWML_High);
extern uint32_t PwmGo(uint32_t iPWMEN, uint32_t iHMODE);
extern uint32_t PwmHBCfg(uint32_t iENA, uint32_t iPOINV, uint32_t iHOFF, uint32_t iDIR);
extern uint32_t PwmInvert(uint32_t iInv1, uint32_t iInv3, uint32_t iInv5);
extern uint32_t PwmLoad(uint32_t iLoad);

#define  UCLK_2 0
#define  UCLK_4 0x40
#define  UCLK_8 0x80
#define  UCLK_16 0xC0
#define  UCLK_32 0x100
#define  UCLK_64 0x140
#define  UCLK_128 0x180
#define  UCLK_256 0x1C0

#define  PWM0_1 0
#define  PWM2_3 1
#define  PWM4_5 2

#ifdef __cplusplus
}
#endif

#endif /* __ADUCM36X_PWMLIB_H */
