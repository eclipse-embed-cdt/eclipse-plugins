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
   @file     I2cLib.h
   @brief    Set of I2C peripheral functions.
   @version  V0.2
   @author   ADI
   @date     October 2015

   @par Revision History:
   - V0.1, April 2012: Initial release.
   - V0.2, October 2015: Coding style cleanup - no functional changes.

**/

#include <ADuCM360.h>


extern int I2cMCfg(int iDMACfg, int iIntSources, int iConfig);
extern int I2cStretch(int iMode, int iStretch);
extern int I2cFifoFlush(int iMode, int iFlush);
extern int I2cSCfg(int iDMACfg, int iIntSources, int iConfig);
extern int I2cRx(int iMode);
extern int I2cTx(int iMode, int iTx);
extern int I2cBaud(int iHighPeriod, int iLowPeriod);
extern int I2cMWrCfg(unsigned int uiDevAddr);
extern int I2cMRdCfg(unsigned int uiDevAddr, int iNumBytes, int iExt);
extern int I2cSta(int iMode);
extern int I2cMRdCnt(void);
extern int I2cSGCallCfg(int iHWGCallAddr);
extern int I2cSIDCfg(int iSlaveID0, int iSlaveID1, int iSlaveID2, int iSlaveID3);

#define MASTER 0
#define SLAVE 1
#define DISABLE 0
#define ENABLE 1

#define STRETCH_DIS 0
#define STRETCH_EN 1
