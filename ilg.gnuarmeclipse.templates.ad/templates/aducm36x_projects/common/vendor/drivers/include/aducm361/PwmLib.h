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
   @file     PwmLib.h
   @brief    Set of PWM peripheral functions.
   @version  V0.3
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.2, September 2012: Initial release.
   - V0.3, October 2015: Coding style cleanup - no functional changes.

*****************************************************************************/

extern int PwmInit(unsigned int iPWMCP, unsigned int iPWMIEN, unsigned int iSYNC, unsigned int iTRIP);
extern int PwmClrInt(unsigned int iSource);
extern int PwmTime(int iPair, unsigned int uiFreq, unsigned int uiPWMH_High, unsigned int uiPWML_High);
extern int PwmGo(unsigned int iPWMEN, unsigned int iHMODE);
extern int PwmHBCfg(unsigned int iENA, unsigned int iPOINV, unsigned int iHOFF, unsigned int iDIR);
extern int PwmInvert(int iInv1, int iInv3, int iInv5);
extern int PwmLoad(int iLoad);

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
