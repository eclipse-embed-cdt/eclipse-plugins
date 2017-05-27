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
   @file       AdcLib.h
   @brief      Set of ADC peripheral functions.
   - Start by setting ADC in idle mode with AdcGo().
   - Set up ADC with AdcRng(), AdcFlt(), AdcMski() and AdcPin().
   - Optionally use AdcBuf() AdcDiag() and AdcBias().
   - Start conversion with AdcGo().
   - Check with AdcSta() that result is available.
   - Read result with AdcRd().
   - Example:

   @version    V0.6
   @author     ADI
   @date       October 2015
   @par Revision History:
   - V0.1, November 2010: initial version.
   - V0.2, September 2012: Fixed AdcDiag() - correct bits are modified now.
                           Fixed AdcRng().
   - V0.3, January 2013:   Fixed AdcFlt()
                           Changed AdcDetCon(), AdcDetSta(), AdcStpRd() to use
                           the pPort passed in.
   - V0.4, April 2013:     Added parameters definitions for AdcBias function
   - V0.5, October 2015:   Coding style cleanup - no functional changes.
   - V0.6, October 2015:   Use Standard Integer Types, prefer unsigned types, add include and C++ guards.

**/
#ifndef __ADUCM36X_ADCLIB_H
#define __ADUCM36X_ADCLIB_H

#ifdef __cplusplus
 extern "C" {
#endif

#include <ADuCM361.h>

extern uint32_t AdcRng(ADI_ADC_TypeDef *pPort, uint32_t iRef, uint32_t iGain, uint32_t iCode);
extern uint32_t AdcGo(ADI_ADC_TypeDef *pPort, uint32_t iStart);
extern int32_t AdcRd(ADI_ADC_TypeDef *pPort);
extern uint32_t AdcBuf(ADI_ADC_TypeDef *pPort, uint32_t iRBufCfg, uint32_t iBufCfg);
extern uint32_t AdcDiag(ADI_ADC_TypeDef *pPort, uint32_t iDiag);
extern uint32_t AdcBias(ADI_ADC_TypeDef *pPort, uint32_t iBiasPin, uint32_t iBiasBoost, uint32_t iGndSw);
extern uint32_t AdcPin(ADI_ADC_TypeDef *pPort, uint32_t iInN, uint32_t iInP);
extern uint32_t AdcFlt(ADI_ADC_TypeDef *pPort, uint32_t iSF, uint32_t iAF, uint32_t iFltCfg);
extern uint32_t AdcMski(ADI_ADC_TypeDef *pPort, uint32_t iMski, uint32_t iWr);
extern uint32_t AdcSta(ADI_ADC_TypeDef *pPort);
//extern uint32_t AdcDma(ADI_ADC_TypeDef *pPort, uint32_t iDmaRdWr);
extern uint32_t AdcPGAErr(ADI_ADC_TypeDef *pPort, uint32_t iAdcSta);
extern uint32_t AdcDetSta(ADI_ADCSTEP_TypeDef *pPort);
extern uint32_t AdcDetCon(ADI_ADCSTEP_TypeDef *pPort, uint32_t iCtrl, uint32_t iAdcSel, uint32_t iRate );
extern uint32_t AdcStpRd(ADI_ADCSTEP_TypeDef *pPort);
extern uint32_t AdcDmaCon(uint32_t iChan, uint32_t iEnable);


//ADC filter extra features disabled
#define  FLT_NORMAL     0
//VBias boost.
#define  ADC_BIAS_X1    0
#define  ADC_BUF_ON     0
#define  ADC_GND_OFF     0
#define  ADC_BIAS_OFF    0


#define  DETCON_RATE_2ms 0
#define  DETCON_RATE_4ms 1
#define  DETCON_RATE_6ms 2
#define  DETCON_RATE_8ms 3

#ifdef __cplusplus
}
#endif

#endif /* __ADUCM36X_ADCLIB_H */
