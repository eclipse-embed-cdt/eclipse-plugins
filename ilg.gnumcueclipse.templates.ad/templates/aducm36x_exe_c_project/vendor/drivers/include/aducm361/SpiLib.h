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
   @file     SpiLib.h
   @brief    Set of SPI peripheral functions.
   @version  V0.5
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.1, May 2012: initial version.
   - V0.2, October 2012: Added SPI DMA support
   - V0.3, November 2012: Moved SPI DMA functionality to DmaLib
   - V0.4, October 2015: Coding style cleanup - no functional changes.
   - V0.5, October 2015: Use Standard Integer Types, prefer unsigned types, add include and C++ guards.

**/
#ifndef __ADUCM36X_SPILIB_H
#define __ADUCM36X_SPILIB_H

#ifdef __cplusplus
 extern "C" {
#endif

#include <ADuCM361.h>

extern uint32_t SpiCfg(ADI_SPI_TypeDef *pSPI, uint32_t iFifoSize, uint32_t iMasterEn, uint32_t iConfig);
extern uint32_t SpiRx(ADI_SPI_TypeDef *pSPI);
extern uint32_t SpiTx(ADI_SPI_TypeDef *pSPI, uint32_t iTx);
extern uint32_t SpiSta(ADI_SPI_TypeDef *pSPI);
extern uint32_t SpiBaud(ADI_SPI_TypeDef *pSPI, uint32_t iClkDiv, uint32_t iCserr);
extern uint32_t SpiFifoFlush(ADI_SPI_TypeDef *pSPI, uint32_t iTxFlush, uint32_t iRxFlush);
extern uint32_t SpiTxFifoFlush(ADI_SPI_TypeDef *pSPI, uint32_t iTxFlush);
extern uint32_t SpiRxFifoFlush(ADI_SPI_TypeDef *pSPI, uint32_t iRxFlush);
extern uint32_t SpiDma(ADI_SPI_TypeDef *pSPI, uint32_t iDmaRxSel, uint32_t iDmaTxSel, uint32_t iDmaEn);
extern uint32_t SpiCountRd(ADI_SPI_TypeDef *pSPI);

#ifdef __cplusplus
}
#endif

#endif /* __ADUCM36X_SPILIB_H */
