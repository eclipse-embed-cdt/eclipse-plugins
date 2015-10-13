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
   @file     DmaLib.h
   @brief   Set of DMA peripheral functions.
   - DmaBase(), DmaSet(), DmaClr(), DmaSta() and DmaErr() apply to all DMA channels together.
   - DmaOn() apply for each channel separately.

   @version    V0.3
   @author     ADI
   @date       October 2015
   @par Revision History:
   - V0.1, October 2012: initial version.
   - V0.2, May 2014: Added #define for iALTERNATE, bPrimary and bPrimary
   - V0.3, October 2015: Coding style cleanup - no functional changes.

**/
#include <ADuCM360.h>

typedef struct {
   unsigned int cycle_ctrl    : 3;
   unsigned int next_useburst : 1;
   unsigned int n_minus_1     : 10;
   unsigned int r_power       : 4;
   unsigned int src_prot_ctrl : 3;
   unsigned int dst_prot_ctrl : 3;
   unsigned int src_size      : 2;
   unsigned int src_inc       : 2;
   unsigned int dst_size      : 2;
   unsigned int dst_inc       : 2;
} CtrlCfgBits;

// Define the structure of a DMA descriptor.
typedef struct dmaDesc {
   unsigned int srcEndPtr;
   unsigned int destEndPtr;
   union {
      unsigned int   ctrlCfgVal;
      CtrlCfgBits    Bits;
   } ctrlCfg ;
   unsigned int reserved4Bytes;
} DmaDesc;
//typedef enum {false = 0, true = !false} boolean;
// Suitable aLignment for the DMA descriptors
#define DMACHAN_DSC_ALIGN 0x200
/* CCD allocation must be an integral power of two, i.e., 12 channels is allocated as 16 */
#define CCD_SIZE                       (16)

extern int DmaBase(void);
extern DmaDesc *Dma_GetDescriptor(unsigned int iChan, int iAlternate);
extern int DmaSet(int iMask, int iEnable, int iAlt, int iPriority);
extern int DmaClr(int iMask, int iEnable, int iAlt, int iPriority);
extern int DmaSta(void);
extern int DmaErr(int iErrClr);

extern int AdcDmaReadSetup(int iType, int iCfg, int iNumVals, int *pucRX_DMA);
extern int AdcDmaWriteSetup(int iType, int iCfg, int iNumVals, int *pucTX_DMA);
extern int DacDmaWriteSetup(int iType, int iCfg, int iNumVals, int *pucTX_DMA);

extern int DmaPeripheralStructSetup(int iChan, int iCfg);
extern int DmaStructPtrOutSetup(int iChan, int iNumVals, unsigned char *pucTX_DMA);
extern int DmaStructPtrInSetup(int iChan, int iNumVals, unsigned char *pucRX_DMA);
extern int DmaCycleCntCtrl(unsigned int iChan, int iNumx, int iCfg);
//DMA channel numbers.
#define  SPI1TX_C 1
#define  SPI1RX_C 2
#define  UARTTX_C 3
#define  UARTRX_C 4
#define  I2CSTX_C 5
#define  I2CSRX_C 6
#define  I2CMTX_C 7
#define  I2CMRX_C 8
#define  DAC_C    9
#define  ADC0_C   10
#define  ADC1_C   11
#define  SINC2_C  12

//DMA channel bit value.
#define  NONE     0
#define  SPI1TX_B 1
#define  SPI1RX_B 2
#define  UARTTX_B 4
#define  UARTRX_B 8
#define  I2CSTX_B 0x10
#define  I2CSRX_B 0x20
#define  I2CMTX_B 0x40
#define  I2CMRX_B 0x80
#define  DAC_B    0x100
#define  ADC0_B      0x200
#define  ADC1_B      0x400
#define  SYNC2_B     0x800

#define ADC0DMAWRITE 0
#define ADC0DMAREAD  1
#define ADC1DMAWRITE 2
#define ADC1DMAREAD  3
#define SINC2DMAREAD 4

//DMA error bit.
#define  DMA_ERR_RD     0
#define  DMA_ERR_CLR    1

//DMA type (Cycl_ctrl)
#define  DMA_STOP    0
#define  DMA_BASIC      1
#define  DMA_AUTO    2
#define  DMA_PING    3
#define  DMA_MSG_PRI    4
#define  DMA_MSG_ALT    5
#define  DMA_PSG_PRI    6
#define  DMA_PSG_ALT    7

//Bytes per transfer.
#define  DMA_SIZE_BYTE      0
#define  DMA_SIZE_HWORD  0x1000000
#define  DMA_SIZE_WORD      0x2000000


//Destination Increment per transfer.
#define  DMA_DSTINC_BYTE      0
#define  DMA_DSTINC_HWORD  0x40000000
#define  DMA_DSTINC_WORD      0x80000000
#define  DMA_DSTINC_NO       0xC0000000

//Source Increment per transfer.
#define  DMA_SRCINC_BYTE      0
#define  DMA_SRCINC_HWORD  0x4000000
#define  DMA_SRCINC_WORD      0x8000000
#define  DMA_SRCINC_NO       0xC000000

#define iPrimary        0
#define bPrimary        0
#define bAlternate      1
#define ALTERNATE      CCD_SIZE
#define iALTERNATE     (12)



