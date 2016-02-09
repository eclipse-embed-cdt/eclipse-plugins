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
   @file     WutLib.h
   @brief    Set of wake up Timer peripheral functions.
   @version  V0.3
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.1, May 2012: Initial release.
   - V0.2, October 2015: Coding style cleanup - no functional changes.
   - V0.3, October 2015: Use Standard Integer Types, prefer unsigned types, add include and C++ guards.

**/
#ifndef __ADUCM36X_WUTLIB_H
#define __ADUCM36X_WUTLIB_H

#ifdef __cplusplus
 extern "C" {
#endif

#include <ADuCM361.h>

extern uint32_t WutCfg(uint32_t iMode, uint32_t iWake, uint32_t iPre, uint32_t iClkSrc);
extern uint32_t WutLdWr(uint32_t iField, uint32_t lTld);
extern uint32_t WutLdRd(uint32_t iField);
extern uint32_t WutInc(uint32_t iInc);
extern uint32_t WutClrInt(uint32_t iSource);
extern uint32_t WutCfgInt(uint32_t iSource, uint32_t iEnable);
extern uint32_t WutVal(void);
extern uint32_t WutSta(void);
extern uint32_t WutGo(uint32_t iEnable);

#ifdef __cplusplus
}
#endif

#endif /* __ADUCM36X_WUTLIB_H */
