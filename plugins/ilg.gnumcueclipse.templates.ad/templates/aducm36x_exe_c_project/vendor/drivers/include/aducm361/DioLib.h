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
   @file     DioLib.h
   @brief    Set of Digital IO peripheral functions.
   @version  V0.5
   @author   ADI
   @date     October 2015

   @par Revision History:
   - V0.1, May 2012: initial version.
   - V0.2, October 2012: comment correction,
                         addition of pin configuration functions,
                         addition of Tristate functions.
   - V0.3, November 2012: several comment corrections.
   - V0.4, October 2015: Coding style cleanup - no functional changes.
   - V0.5, October 2015: Use Standard Integer Types, prefer unsigned types, add include and C++ guards.

*****************************************************************************/
#ifndef __ADUCM36X_DIOLIB_H
#define __ADUCM36X_DIOLIB_H

#ifdef __cplusplus
 extern "C" {
#endif

#include <ADuCM361.h>

#define PIN0 0x0
#define PIN1 0x1
#define PIN2 0x2
#define PIN3 0x3
#define PIN4 0x4
#define PIN5 0x5
#define PIN6 0x6
#define PIN7 0x7

// port configuration
extern uint32_t DioCfg(ADI_GPIO_TypeDef *pPort, uint32_t iMpx);
extern uint32_t DioDrv(ADI_GPIO_TypeDef *pPort, uint32_t iOen, uint32_t iPul, uint32_t iOce);
extern uint32_t DioOen(ADI_GPIO_TypeDef *pPort, uint32_t iOen);
extern uint32_t DioPul(ADI_GPIO_TypeDef *pPort, uint32_t iPul);
extern uint32_t DioOce(ADI_GPIO_TypeDef *pPort, uint32_t iOce);
extern uint32_t DioTriState(ADI_GPIO_TypeDef *pPort); // new

// pin configuration
extern uint32_t DioCfgPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iMode);  // new
extern uint32_t DioOenPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iOen);   // new
extern uint32_t DioPulPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iPul);   // new
extern uint32_t DioOcePin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iOce);   // new
extern uint32_t DioTriStatePin(ADI_GPIO_TypeDef *pPort, uint32_t iPin);        // new

extern uint32_t DioRd(ADI_GPIO_TypeDef *pPort);
extern uint32_t DioWr(ADI_GPIO_TypeDef *pPort, uint32_t iVal);
extern uint32_t DioSet(ADI_GPIO_TypeDef *pPort, uint32_t iVal);
extern uint32_t DioClr(ADI_GPIO_TypeDef *pPort, uint32_t iVal);
extern uint32_t DioTgl(ADI_GPIO_TypeDef *pPort, uint32_t iVal);

#ifdef __cplusplus
}
#endif

#endif /* __ADUCM36X_DIOLIB_H */
