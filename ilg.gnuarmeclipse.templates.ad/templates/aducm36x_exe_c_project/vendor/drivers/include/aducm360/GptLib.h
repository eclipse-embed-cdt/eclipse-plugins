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
   @file     GptLib.h
   @brief Set of Timer peripheral functions.
   - Example:

   @version  V0.3
   @author   ADI
   @date     October 2015

   @par Revision History:
   - V0.1, May 2012: Initial release.
   - V0.2, October 2015: Coding style cleanup - no functional changes.
   - V0.3, October 2015: Use Standard Integer Types, prefer unsigned types, add include and C++ guards.
**/
#ifndef __ADUCM36X_GPTLIB_H
#define __ADUCM36X_GPTLIB_H

#ifdef __cplusplus
 extern "C" {
#endif

#include <ADuCM360.h>

extern uint32_t GptCfg(ADI_TIMER_TypeDef *pTMR, uint32_t iClkSrc, uint32_t iScale, uint32_t iMode);
extern uint32_t GptLd(ADI_TIMER_TypeDef *pTMR, uint32_t iTLd);
extern uint32_t GptVal(ADI_TIMER_TypeDef *pTMR);
extern uint32_t GptCapRd(ADI_TIMER_TypeDef *pTMR);
extern uint32_t GptCapSrc(ADI_TIMER_TypeDef *pTMR, uint32_t iTCapSrc);
extern uint32_t GptSta(ADI_TIMER_TypeDef *pTMR);
extern uint32_t GptClrInt(ADI_TIMER_TypeDef *pTMR, uint32_t iSource);
extern uint32_t GptBsy(ADI_TIMER_TypeDef *pTMR);

#ifdef __cplusplus
}
#endif

#endif /* __ADUCM36X_GPTLIB_H */
