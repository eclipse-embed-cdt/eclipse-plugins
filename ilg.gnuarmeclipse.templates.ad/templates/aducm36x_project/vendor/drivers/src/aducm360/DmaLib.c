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
   @addtogroup dma
   @{
   @file    DmaLib.c
   @brief   Set of DMA peripheral functions.
   - DmaBase(), DmaSet(), DmaClr(), DmaSta() and DmaErr() apply to all DMA channels together.
   - The rest of the functions apply for each channel separately.

   @version    V0.5
   @author     ADI
   @date       October 2015
   @par Revision History:
   - V0.1, October 2012: initial version.
   - V0.2, September 2013: Fixed doxygen comments for DmaCycleCntCtrl()
   - V0.3, May 2014: Fixed AdcDmaWriteSetup() when ADC0DMAWRITE+iALTERNATE is passed in;
                     Changed handling of unused values passed into AdcDmaReadSetup()
                     and DacDmaWriteSetup();
                     Fixed numerous doxygen parameter comments.
   - V0.4, August 2015: Initialise every new instance of DmaDesc
   - V0.5, October 2015: Coding style cleanup - no functional changes.

**/
#include <stdio.h>
#include <string.h>
#include "DmaLib.h"

// Define dmaChanDesc as an array of descriptors aligned to the required
// boundary for all supported compilers. It was not possible to do something
// in a macro here because the #pragma for IAR cannot be put in a #define.
//
// Support primary and alternate here
#define DMACHAN_NUM ((INT_NUM_DMA_LAST - INT_NUM_DMA_FIRST) + 1)

#if defined(__ARMCC_VERSION) || defined(__GNUC__)
DmaDesc dmaChanDesc     [CCD_SIZE * 2] __attribute__ ((aligned (DMACHAN_DSC_ALIGN)));
#endif

#ifdef __ICCARM__
#pragma data_alignment=(DMACHAN_DSC_ALIGN)
DmaDesc dmaChanDesc     [CCD_SIZE * 2];
#endif



/**
   @brief DmaDesc * Dma_GetDescriptor(unsigned int iChan,int iAlternate);
         ========== Returns the Primary or Alternate structure descriptor of the
         specified channel.
   @param iChan :{SPI1TX_C,SPI1RX_C,UARTTX_C,UARTRX_C,I2CSTX_C,I2CSRX_C,I2CMTX_C,
                  I2CMRX_C,DAC_C,ADC0_C,ADC1_C,SINC2_C}
   - Set to peripheral required.
   - 0 or SPI1TX_C for SPI1 transmit
   - 1 or SPI1RX_C for SPI1 Receive
   - 2 or UARTTX_C for UART Transmit
   - 3 or UARTRX_C for UART Receive
   - 4 or I2CSTX_C for I2C Slave transmit
   - 5 or I2CSRX_C for I2C Slave Receive
   - 6 or I2CMTX_C for I2C Master Transmit
   - 7 or I2CMRX_C for I2C Master Receive
   - 8 or DAC_C for DAC DMA output
   - 9 or ADC0_C for ADC0
   - 10 or ADC1_C for ADC0
   - 11 or SINC2_C for SINC2 filter output
   Address for DMA primary data base.
   @param iAlternate :{bPrimary, bAlternate}
   - 0 or bPrimary for Primary
   - 1 or bAlternate for Alternate structure
   @return 1.

**/

DmaDesc *Dma_GetDescriptor(unsigned int iChan, int iAlternate)
{
   if (iChan < CCD_SIZE) {
      if (iAlternate != 0) {
         return &(dmaChanDesc[iChan + CCD_SIZE]);

      } else {
         return &(dmaChanDesc[iChan ]);
      }

   } else {
      return 0x0;
   }
}

/**
   @brief int DmaBase(void)
         ========== Sets the Address of DMA Data base pointer.
   @return 1.
**/
int DmaBase(void)
{
   volatile unsigned int uiBasPtr = 0;

   memset(dmaChanDesc, 0x0, sizeof(dmaChanDesc));    // Clear all the DMA descriptors (individual blocks will update their  descriptors
   uiBasPtr = (unsigned int)&dmaChanDesc; // Setup the DMA base pointer.
   pADI_DMA->DMAPDBPTR = uiBasPtr;
   pADI_DMA->DMACFG = 1;                             // Enable DMA controller
   return 1;
}
/**
   @brief int DmaSet(int iMask, int iEnable, int iAlt, int iPriority)
         ========== Controls Mask, Primary Enable, Alternate enable and priority enable bits
   @param iMask :{0|DMARMSKSET_SPI1TX|DMARMSKSET_SPI1RX|DMARMSKSET_UARTTX|DMARMSKSET_UARTRX
                  |DMARMSKSET_I2CSTX|DMARMSKSET_I2CSRX|DMARMSKSET_I2CMTX|DMARMSKSET_I2CMRX
                  |DMARMSKSET_DAC|DMARMSKSET_ADC0|DMARMSKSET_ADC1|DMARMSKSET_SINC2}
   - Select a combination of peripheral mask bits to be set.
   - 0 = to Mask no DMA source
   - 0x1 or DMARMSKSET_SPI1TX to mask SPI1 Transmit
   - 0x2 or DMARMSKSET_SPI1RX to mask SPI1 receive
   - 0x4 or DMARMSKSET_UARTTX to mask UART Transmit
   - 0x8 or DMARMSKSET_UARTRX to mask UART receive
   - 0x10 or DMARMSKSET_I2CSTX to mask I2C Slave Transmit
   - 0x20 or DMARMSKSET_I2CSRX to mask I2C Slave receive
   - 0x40 or DMARMSKSET_I2CMTX to mask I2C Master Transmit
   - 0x80 or DMARMSKSET_I2CMRX to mask I2C Master receive
   - 0x100 or DMARMSKSET_DAC to mask DAC output
   - 0x200 or DMARMSKSET_ADC0 to Mask ADC0
   - 0x400 or DMARMSKSET_ADC1 to Mask ADC1
   - 0x800 or DMARMSKSET_SINC2 to Mask SINC2
   @param iEnable :{0|DMAENSET_SPI1TX|DMAENSET_SPI1RX|DMAENSET_UARTTX|DMAENSET_UARTRX
                     |DMAENSET_I2CSTX|DMAENSET_I2CSRX|DMAENSET_I2CMTX|DMAENSET_I2CMRX
                     |DMAENSET_DAC|DMAENSET_ADC0|DMAENSET_ADC1|DMAENSET_SINC2}
   - Select a combination of peripheral enable bits to be set.
   - 0 = to enable no DMA source
   - 0x1 or DMAENSET_SPI1TX to enable SPI1 Transmit
   - 0x2 or DMAENSET_SPI1RX to enable SPI1 receive
   - 0x4 or DMAENSET_UARTTX to enable UART Transmit
   - 0x8 or DMAENSET_UARTRX to enable UART receive
   - 0x10 or DMAENSET_I2CSTX to enable I2C Slave Transmit
   - 0x20 or DMAENSET_I2CSRX to enable I2C Slave receive
   - 0x40 or DMAENSET_I2CMTX to enable I2C Master Transmit
   - 0x80 or DMAENSET_I2CMRX to enable I2C Master receive
   - 0x100 or DMAENSET_DAC to enable DAC output
   - 0x200 or DMAENSET_ADC0 to enable ADC0
   - 0x400 or DMAENSET_ADC1 to enable ADC1
   - 0x800 or DMAENSET_SINC2 to enable SINC2
   @param iAlt :{0|DMAALTSET_SPI1TX|DMAALTSET_SPI1RX|DMAALTSET_UARTTX|DMAALTSET_UARTRX
                  |DMAALTSET_I2CSTX|DMAALTSET_I2CSRX|DMAALTSET_I2CMTX|DMAALTSET_I2CMRX
                  |DMAALTSET_DAC|DMAALTSET_ADC0|DMAALTSET_ADC1|DMAALTSET_SINC2}
   - Select a combination of peripheral alternate bits to be set.
   - 0 = to enable no DMA source
   - 0x1 or DMAALTSET_SPI1TX to set alternate SPI1 Transmit
   - 0x2 or DMAALTSET_SPI1RX to set alternate  SPI1 receive
   - 0x4 or DMAALTSET_UARTTX to set alternate  UART Transmit
   - 0x8 or DMAALTSET_UARTRX to set alternate  UART receive
   - 0x10 or DMAALTSET_I2CSTX to set alternate  I2C Slave Transmit
   - 0x20 or DMAALTSET_I2CSRX to set alternate  I2C Slave receive
   - 0x40 or DMAALTSET_I2CMTX to set alternate  I2C Master Transmit
   - 0x80 or DMAALTSET_I2CMRX to set alternate  I2C Master receive
   - 0x100 or DMAALTSET_DAC to set alternate  DAC output
   - 0x200 or DMAALTSET_ADC0 to set alternate  ADC0
   - 0x400 or DMAALTSET_ADC1 to set alternate ADC1
   - 0x800 or DMAALTSET_SINC2 to set alternate SINC2
   @param iPriority :{0|DMAPRISET_SPI1TX|DMAPRISET_SPI1RX|DMAPRISET_UARTTX|DMAPRISET_UARTRX
                     |DMAPRISET_I2CSTX|DMAPRISET_I2CSRX|DMAPRISET_I2CMTX|DMAPRISET_I2CMRX
                     |DMAPRISET_DAC|DMAPRISET_ADC0|DMAPRISET_ADC1|DMAPRISET_SINC2}
   - Select a combination of peripheral priority bits to be set.
   - 0 to leave priority level of all channels as they are
   - 0x1 or DMAPRISET_SPI1TX to high prioritize SPI1 Transmit
   - 0x2 or DMAPRISET_SPI1RX to high prioritize SPI1 receive
   - 0x4 or DMAPRISET_UARTTX to high prioritize  UART Transmit
   - 0x8 or DMAPRISET_UARTRX to high prioritize  UART receive
   - 0x10 or DMAPRISET_I2CSTX to high prioritize  I2C Slave Transmit
   - 0x20 or DMAPRISET_I2CSRX to high prioritize  I2C Slave receive
   - 0x40 or DMAPRISET_I2CMTX to high prioritize  I2C Master Transmit
   - 0x80 or DMAPRISET_I2CMRX to high prioritize  I2C Master receive
   - 0x100 or DMAPRISET_DAC to high prioritize  DAC output
   - 0x200 or DMAPRISET_ADC0 to high prioritize ADC0
   - 0x400 or DMAPRISET_ADC1 to high prioritize ADC1
   - 0x800 or DMAPRISET_SINC2 to high prioritize SINC2
   @return 1.
**/
int DmaSet(int iMask, int iEnable, int iAlt, int iPriority)
{
   pADI_DMA->DMARMSKSET = iMask;
   pADI_DMA->DMAENSET = iEnable;
   pADI_DMA->DMAALTSET = iAlt;
   pADI_DMA->DMAPRISET = iPriority;
   return 1;
}

/**
   @brief int DmaClr(int iMask, int iEnable, int iAlt, int iPriority)
         ========== Controls Mask, Primary Enable, Alternate enable and priority Clear bits
   @param iMask :{0|DMARMSKCLR_SPI1TX|DMARMSKCLR_SPI1RX|DMARMSKCLR_UARTTX|DMARMSKCLR_UARTRX
                     |DMARMSKCLR_I2CSTX|DMARMSKCLR_I2CSRX|DMARMSKCLR_I2CMTX|DMARMSKCLR_I2CMRX
                     |DMARMSKCLR_DAC|DMARMSKCLR_ADC0|DMARMSKCLR_ADC1|DMARMSKCLR_SINC2}
      - Select a combination of peripheral mask bits to be cleared.
      - 0 = to disable no Mask bit
      - 0x1 or DMARMSKCLR_SPI1TX to disable mask SPI1 Transmit
      - 0x2 or DMARMSKCLR_SPI1RX to disable mask SPI1 receive
      - 0x4 or DMARMSKCLR_UARTTX to disable mask UART Transmit
      - 0x8 or DMARMSKCLR_UARTRX to disable mask UART receive
      - 0x10 or DMARMSKCLR_I2CSTX to disable mask I2C Slave Transmit
      - 0x20 or DMARMSKCLR_I2CSRX to disable mask I2C Slave receive
      - 0x40 or DMARMSKCLR_I2CMTX to disable mask I2C Master Transmit
      - 0x80 or DMARMSKCLR_I2CMRX to disable mask I2C Master receive
      - 0x100 or DMARMSKCLR_DAC to disable mask DAC output
      - 0x200 or DMARMSKCLR_ADC0 to disable mask ADC0
      - 0x400 or DMARMSKCLR_ADC1 to disable Mask ADC1
      - 0x800 or DMARMSKCLR_SINC2 to disable Mask SINC2
   @param iEnable :{0|DMAENCLR_SPI1TX|DMAENCLR_SPI1RX|DMAENCLR_UARTTX|DMAENCLR_UARTRX
                     |DMAENCLR_I2CSTX|DMAENCLR_I2CSRX|DMAENCLR_I2CMTX|DMAENCLR_I2CMRX
                     |DMAENCLR_DAC|DMAENCLR_ADC0|DMAENCLR_ADC1|DMAENCLR_SINC2}
      - Select a combination of peripheral enable bits to be cleared.
      - 0 = to enable no DMA source
      - 0x1 or DMAENCLR_SPI1TX to disable SPI1 Transmit
      - 0x2 or DMAENCLR_SPI1RX to disableSPI1 receive
      - 0x4 or DMAENCLR_UARTTX to disable UART Transmit
      - 0x8 or DMAENCLR_UARTRX to disable UART receive
      - 0x10 or DMAENCLR_I2CSTX to disable I2C Slave Transmit
      - 0x20 or DMAENCLR_I2CSRX to disable I2C Slave receive
      - 0x40 or DMAENCLR_I2CMTX to disable I2C Master Transmit
      - 0x80 or DMAENCLR_I2CMRX to disable I2C Master receive
      - 0x100 or DMAENCLR_DAC to disable DAC output
      - 0x200 or DMAENCLR_ADC0 to disable ADC0
      - 0x400 or DMAENCLR_ADC1 to disable ADC1
      - 0x800 or DMAENCLR_SINC2 to disable SINC2
   @param iAlt :{0|DMAALTCLR_SPI1TX|DMAALTCLR_SPI1RX|DMAALTCLR_UARTTX|DMAALTCLR_UARTRX
                  |DMAALTCLR_I2CSTX|DMAALTCLR_I2CSRX|DMAALTCLR_I2CMTX|DMAALTCLR_I2CMRX
                  |DMAALTCLR_DAC|DMAALTCLR_ADC0|DMAALTCLR_ADC1|DMAALTCLR_SINC2}
      - Select a combination of peripheral alternate bits to be cleared.
      - 0 = to enable no DMA source
      - 0x1 or DMAALTCLR_SPI1TX to clear alternate SPI1 Transmit
      - 0x2 or DMAALTCLR_SPI1RX to clear alternate  SPI1 receive
      - 0x4 or DMAALTCLR_UARTTX to clear alternate  UART Transmit
      - 0x8 or DMAALTCLR_UARTRX to clear alternate  UART receive
      - 0x10 or DMAALTCLR_I2CSTX to clear alternate  I2C Slave Transmit
      - 0x20 or DMAALTCLR_I2CSRX to clear alternate  I2C Slave receive
      - 0x40 or DMAALTCLR_I2CMTX to clear alternate  I2C Master Transmit
      - 0x80 or DMAALTCLR_I2CMRX to clear alternate  I2C Master receive
      - 0x100 or DMAALTCLR_DAC to clear alternate  DAC output
      - 0x200 or DMAALTCLR_ADC0 to clear alternate  ADC0
      - 0x400 or DMAALTCLR_ADC1 to clear alternate ADC1
      - 0x800 or DMAALTCLR_SINC2 to clear alternate SINC2
   @param iPriority :{0|DMAPRICLR_SPI1TX|DMAPRICLR_SPI1RX|DMAPRICLR_UARTTX|DMAPRICLR_UARTRX
                     |DMAPRICLR_I2CSTX|DMAPRICLR_I2CSRX|DMAPRICLR_I2CMTX|DMAPRICLR_I2CMRX
                     |DMAPRICLR_DAC|DMAPRICLR_ADC0|DMAPRICLR_ADC1|DMAPRICLR_SINC2}
      - Select a combination of peripheral priority bits to default level.
      - 0 to leave priority level of all channels as they are
      - 0x1 or DMAPRICLR_SPI1TX to default prioritize SPI1 Transmit
      - 0x2 or DMAPRICLR_SPI1RX to default prioritize SPI1 receive
      - 0x4 or DMAPRICLR_UARTTX to default prioritize  UART Transmit
      - 0x8 or DMAPRICLR_UARTRX to default prioritize  UART receive
      - 0x10 or DMAPRICLR_I2CSTX to default prioritize  I2C Slave Transmit
      - 0x20 or DMAPRICLR_I2CSRX to default prioritize  I2C Slave receive
      - 0x40 or DDMAPRICLR_I2CMTX to default prioritize  I2C Master Transmit
      - 0x80 or DMAPRICLR_I2CMRX to default prioritize  I2C Master receive
      - 0x100 or DMAPRICLR_DAC to default prioritize  DAC output
      - 0x200 or DMAPRICLR_ADC0 to default prioritize ADC0
      - 0x400 or DMAPRICLR_ADC1 to default prioritize ADC1
      - 0x800 or DMAPRICLR_SINC2 to default prioritize SINC2
   @return 1.
**/
int DmaClr(int iMask, int iEnable, int iAlt, int iPriority)
{
   pADI_DMA->DMARMSKCLR = iMask;
   pADI_DMA->DMAENCLR = iEnable;
   pADI_DMA->DMAALTCLR = iAlt;
   pADI_DMA->DMAPRICLR = iPriority;
   return 1;
}


/**
   @brief int DmaSta(void)
      ========== Reads the status of the DMA controller.
   @return value of DMASTA /n
      bit 0 - enabled   /n
      bits [7:4]
      - 0 = Idle
      - 1 = Reading channel controller data
      - 2 = Reading source data end pointer
      - 3 = Reading destination end pointer
      - 4 = Reading source data
      - 5 = Writing destination data
      - 6 = Waiting for DMArequest to clear
      - 7 = Writing channel controller data
      - 8 = Stalled
      - 9 = Done
      - 10 = Scatter gather transition.
      bits [20:16]
      - number of DMA channels available -1

**/
int DmaSta(void)
{
   return pADI_DMA->DMASTA;
}

/**
   @brief int DmaErr(int iErrClr)
         ========== Reads and optionally clears DMA error bit.
   @param iErrClr :{DMA_ERR_RD,DMA_ERR_CLR}
      - 0 or DMA_ERR_RD only reads error bit.
      - 1 or DMA_ERR_CLR reads then clears error bit.
   @return value of DMAERRCLR
      - 0 - flags no error
      - 1 - flags error.
**/
int DmaErr(int iErrClr)
{
   if(iErrClr) {
      pADI_DMA->DMAERRCLR = 1;
   }

   return pADI_DMA->DMAERRCLR;
}


/**
   @brief int DmaPeripheralStructSetup(int iChan, int iCfg)
         ==========Sets up DMA config structure for the required channel

   @param iChan :{0,SPI1TX_C,SPI1RX_C,UARTTX_C,UARTRX_C,I2CSTX_C,I2CSRX_C,I2CMTX_C,I2CMRX_C,
         DAC_C,ADC0_C,ADC1_C,SINC2_C,
         SPI1TX_C+ALTERNATE,SPI1RX_C+ALTERNATE,UARTTX_C+ALTERNATE,UARTRX_C+ALTERNATE,
         I2CSTX_C+ALTERNATE,I2CSRX_C+ALTERNATE,I2CMTX_C+ALTERNATE,I2CMRX_C+ALTERNATE,
         DAC_C+ALTERNATE,ADC0_C+ALTERNATE,ADC1_C+ALTERNATE,SINC2_C+ALTERNATE}
   - 0 to select no channel
   - 1 or SPI1TX_C for the SPI1 Transmit channel - primary structure
   - 2 or SPI1RX_C for the SPI1 receive channel - primary structure
   - 3 or UARTTX_C for the UART Transmit channel - primary structure
   - 4 or UARTRX_C for the UART receive channel - primary structure
   - 5 or I2CSTX_C for the I2C Slave transmit channel - primary structure
   - 6 or I2CSRX_C for the I2C Slave receive channel - primary structure
   - 7 or I2CMTX_C for the I2C Master transmit channel - primary structure
   - 8 or I2CMRX_C for the I2C Master receive channel - primary structure
   - 9 or DAC_C for the DAC output channel - primary structure
   - 10 or ADC0_C for the ADC0 output channel - primary structure
   - 11 or ADC1_C for the ADC1 output channel - primary structure
   - 12 or SINC2_C for the SINC2 output channel - primary structure
   - 17 or SPI1TX_C+ALTERNATE for the SPI1 Transmit channel - ALTERNATE structure
   - 18 or SPI1RX_C+ALTERNATE for the SPI1 receive channel - ALTERNATE structure
   - 19 or UARTTX_C+ALTERNATE for the UART Transmit channel - ALTERNATE structure
   - 20 or UARTRX_C+ALTERNATE for the UART receive channel - ALTERNATE structure
   - 21 or I2CSTX_C+ALTERNATE for the I2C Slave transmit channel - ALTERNATE structure
   - 22 or I2CSRX_C+ALTERNATE for the I2C Slave receive channel - ALTERNATE structure
   - 23 or I2CMTX_C+ALTERNATE for the I2C Master transmit channel - ALTERNATE structure
   - 24 or I2CMRX_C+ALTERNATE for the I2C Master receive channel - ALTERNATE structure
   - 25 or DAC_C+ALTERNATE for the DAC output channel - ALTERNATE structure
   - 26 or ADC0_C+ALTERNATE for the ADC0 output channel - ALTERNATE structure
   - 27 or ADC1_C+ALTERNATE for the ADC1 output channel - ALTERNATE structure
   - 28 or SINC2_C+ALTERNATE for the SINC2 output channel - ALTERNATE structure
   @param iCfg :{DMA_DSTINC_BYTE|DMA_DSTINC_HWORD|DMA_DSTINC_WORD|DMA_DSTINC_NO|
                 DMA_SRCINC_BYTE|DMA_SRCINC_HWORD|DMA_SRCINC_WORD|DMA_SRCINC_NO|
                 DMA_SIZE_BYTE|DMA_SIZE_HWORD|DMA_SIZE_WORD}
   - Choose one of DMA_DSTINC_BYTE, DMA_DSTINC_HWORD,DMA_DSTINC_WORD,DMA_DSTINC_NO for destination address increment
   - Choose one of DMA_SRCINC_BYTE,DMA_SRCINC_HWORD,DMA_SRCINC_WORD,DMA_SRCINC_NO for source address increment
   - Choose one of DMA_SIZE_BYTE (byte),DMA_SIZE_HWORD (half-word),DMA_SIZE_WORD(word) for source data size

   @return 1:

**/
int DmaPeripheralStructSetup(int iChan, int iCfg)
{
   int iChanSel = 0;
   DmaDesc Desc;
   Desc.ctrlCfg.ctrlCfgVal = 0;
   Desc.destEndPtr = 0;
   Desc.reserved4Bytes = 0;
   Desc.srcEndPtr = 0;

   if (iChan > CCD_SIZE) {
      iChanSel = 1;  // Alternate
      iChan = iChan - CCD_SIZE;

   } else {
      iChanSel = 0;
   }

   // Common configuration of all the descriptors used here
   //  Desc.ctrlCfg.Bits.cycle_ctrl       = (iCfg & 0x7);
   Desc.ctrlCfg.Bits.next_useburst    = 0x0;
   Desc.ctrlCfg.Bits.r_power          = 0;
   Desc.ctrlCfg.Bits.src_prot_ctrl    = 0x0;
   Desc.ctrlCfg.Bits.dst_prot_ctrl    = 0x0;
   Desc.ctrlCfg.Bits.src_size         = ((iCfg & 0x3000000) >> 24);
   Desc.ctrlCfg.Bits.dst_size         = ((iCfg & 0x3000000) >> 24);

   Desc.ctrlCfg.Bits.n_minus_1        = 16;
   Desc.ctrlCfg.Bits.src_inc          = ((iCfg & 0xC000000) >> 26);
   Desc.ctrlCfg.Bits.dst_inc          = ((iCfg & 0xC0000000) >> 30);
   iChan--;

   if (iChanSel == 0) {
      *Dma_GetDescriptor(iChan, 0) = Desc;   // primary structure

   } else {
      *Dma_GetDescriptor(iChan, 1) = Desc;   // alternate structure
   }


   return 1;
}
/**
   @brief int DmaStructPtrOutSetup(int iChan, int iNumVals, unsigned char *pucTX_DMA)
            ==========For DMA operations where the destination is fixed (peripheral register is fixed)
   @param iChan :{0,SPI1TX_C,UARTTX_C,I2CSTX_C,I2CMTX_C,DAC_C,ADC0_C,ADC1_C,
         SPI1TX_C+ALTERNATE,UARTTX_C+ALTERNATE,I2CSTX_C+ALTERNATE,I2CMTX_C+ALTERNATE,
         DAC_C+ALTERNATE,ADC0_C+ALTERNATE,ADC1_C+ALTERNATE}
   - 0 to select no channel
   - 1 or SPI1TX_C for the SPI1 Transmit channel - primary structure
   - 3 or UARTTX_C for the UART Transmit channel - primary structure
   - 5 or I2CSTX_C for the I2C Slave transmit channel - primary structure
   - 7 or I2CMTX_C for the I2C Master transmit channel - primary structure
   - 9 or DAC_C for the DAC output channel - primary structure
   - 10 or ADC0_C for the ADC0 output channel - primary structure
   - 11 or ADC1_C for the ADC1 output channel - primary structure
   - 17 or SPI1TX_C+ALTERNATE for the SPI1 Transmit channel - ALTERNATE structure
   - 19 or UARTTX_C+ALTERNATE for the UART Transmit channel - ALTERNATE structure
   - 21 or I2CSTX_C+ALTERNATE for the I2C Slave transmit channel - ALTERNATE structure
   - 23 or I2CMTX_C+ALTERNATE for the I2C Master transmit channel - ALTERNATE structure
   - 25 or DAC_C+ALTERNATE for the DAC output channel - ALTERNATE structure
   - 26 or ADC0_C+ALTERNATE for the ADC0 output channel - ALTERNATE structure
   - 27 or ADC1_C+ALTERNATE for the ADC1 output channel - ALTERNATE structure

   @param iNumVals :{1-1024}
   - 1 to 1024. Number of values to transfer
   @param *pucTX_DMA :{0-0xFFFFFFFF}
   - Pass Source pointer  address for DMA transfers
   @return 1:

**/

int DmaStructPtrOutSetup(int iChan, int iNumVals, unsigned char *pucTX_DMA)
{
   int iChanSel = 0;

   if (iChan > CCD_SIZE) {
      iChanSel = 1;  // Alternalte
      iChan = iChan - CCD_SIZE;

   } else {
      iChanSel = 0;
   }

   switch (iChan) {
   case 1:  // SPI1 Tx
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].destEndPtr   = (unsigned int)(&pADI_SPI1->SPITX);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr   = (unsigned int)(&pADI_SPI1->SPITX);
      }

      break;

   case 3:  // UART Tx
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].destEndPtr   = (unsigned int)(&pADI_UART->COMTX);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr   = (unsigned int)(&pADI_UART->COMTX);
      }

      break;

   case 5:  // I2C Slave Tx
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].destEndPtr   = (unsigned int)(&pADI_I2C->I2CSTX);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr   = (unsigned int)(&pADI_I2C->I2CSTX);
      }

      break;

   case 7:  // I2C Master Tx
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].destEndPtr   = (unsigned int)(&pADI_I2C->I2CMTX);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr   = (unsigned int)(&pADI_I2C->I2CMTX);
      }

      break;

   case 9:  // DAC out
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].destEndPtr   = (unsigned int)(&pADI_DAC->DACDAT);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr   = (unsigned int)(&pADI_DAC->DACDAT);
      }

      break;

   case 10: // ADC0 write to control registers
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].destEndPtr   = (unsigned int)(&pADI_ADC0->MSKI + iNumVals - 0x1);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr   = (unsigned int)(&pADI_ADC0->MSKI + iNumVals - 0x1);
      }

      break;

   case 11: // ADC1 write to control registers
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].destEndPtr   = (unsigned int)(&pADI_ADC1->MSKI + iNumVals - 0x1);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr    = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr   = (unsigned int)(&pADI_ADC1->MSKI + iNumVals - 0x1);
      }

      break;

   default:
      break;
   }

   return 1;
}
/**
   @brief int DmaStructPtrInSetup(int iChan, int iNumVals, unsigned char *pucRX_DMA);
            ==========For DMA operations where the destination is fixed (peripheral register is fixed)
   @param iChan :{0,SPI1RX_C,UARTRX_C,I2CSRX_C,I2CMRX_C,
                  SPI1RX_C+ALTERNATE,UARTRX_C+ALTERNATE,I2CSRX_C+ALTERNATE,
                  I2CMRX_C+ALTERNATE}
    - 0 to select no channel
    - 2 or SPI1RX_C for the SPI1 receive channel - primary structure
    - 4 or UARTRX_C for the UART receive channel - primary structure
    - 6 or I2CSRX_C for the I2C Slave receive channel - primary structure
    - 8 or I2CMRX_C for the I2C Master receive channel - primary structure
    - 18 or SPI1RX_C+ALTERNATE for the SPI1 receive channel - ALTERNATE structure
    - 20 or UARTRX_C+ALTERNATE for the UART receive channel - ALTERNATE structure
    - 22 or I2CSRX_C+ALTERNATE for the I2C Slave receive channel - ALTERNATE structure
    - 24 or I2CMRX_C+ALTERNATE for the I2C Master receive channel - ALTERNATE structure
   @param iNumVals :{1-1024}
    - 1 to 1024. Number of values to transfer
   @param *pucRX_DMA :{0-0xFFFFFFFF}
    - Pass pointer to destination address for DMA transfers
   @return 1:

**/
int DmaStructPtrInSetup(int iChan, int iNumVals, unsigned char *pucRX_DMA)
{
   int iChanSel = 0;

   if (iChan > CCD_SIZE) {
      iChanSel = 1;  // Alternalte
      iChan = iChan - CCD_SIZE;

   } else {
      iChanSel = 0;
   }

   switch (iChan) {
   case 2:  // SPI1 Rx
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].destEndPtr    = (unsigned int)(pucRX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].srcEndPtr   = (unsigned int)(&pADI_SPI1->SPIRX);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr    = (unsigned int)(pucRX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr   = (unsigned int)(&pADI_SPI1->SPIRX);
      }

      break;

   case 4:  // UART Rx
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].destEndPtr    = (unsigned int)(pucRX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].srcEndPtr   = (unsigned int)(&pADI_UART->COMRX);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr    = (unsigned int)(pucRX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr   = (unsigned int)(&pADI_UART->COMRX);
      }

      break;

   case 6:  // I2C Slave Rx
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].destEndPtr    = (unsigned int)(pucRX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].srcEndPtr   = (unsigned int)(&pADI_I2C->I2CSRX);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr    = (unsigned int)(pucRX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr   = (unsigned int)(&pADI_I2C->I2CSRX);
      }

      break;

   case 8:  // I2C Master Rx
      iChan--;

      if (iChanSel == 0) { // - primary structure
         dmaChanDesc[iChan].destEndPtr    = (unsigned int)(pucRX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan].srcEndPtr   = (unsigned int)(&pADI_I2C->I2CMRX);

      } else { // Alternate structure used
         dmaChanDesc[iChan + CCD_SIZE].destEndPtr    = (unsigned int)(pucRX_DMA + iNumVals - 0x1);
         dmaChanDesc[iChan + CCD_SIZE].srcEndPtr   = (unsigned int)(&pADI_I2C->I2CMRX);
      }

      break;

   default:
      break;
   }

   return 1;
}
/**
   @brief int DmaCycleCntCtrl(unsigned int iChan, int iNumx, int iCfg)
            ==========Used to re-enable DMA config structure when moving ADC results direct to memory
   @param iChan :{0,SPI1TX_C,SPI1RX_C,UARTTX_C,UARTRX_C,I2CSTX_C,I2CSRX_C,I2CMTX_C,I2CMRX_C,
         DAC_C,ADC0_C,ADC1_C,SINC2_C,
         SPI1TX_C+ALTERNATE,SPI1RX_C+ALTERNATE,UARTTX_C+ALTERNATE,UARTRX_C+ALTERNATE,
         I2CSTX_C+ALTERNATE,I2CSRX_C+ALTERNATE,I2CMTX_C+ALTERNATE,I2CMRX_C+ALTERNATE,
         DAC_C+ALTERNATE,ADC0_C+ALTERNATE,ADC1_C+ALTERNATE,SINC2_C+ALTERNATE}
    - 0 to select no channel
    - 1 or SPI1TX_C for the SPI1 Transmit channel - primary structure
    - 2 or SPI1RX_C for the SPI1 receive channel - primary structure
    - 3 or UARTTX_C for the UART Transmit channel - primary structure
    - 4 or UARTRX_C for the UART receive channel - primary structure
    - 5 or I2CSTX_C for the I2C Slave transmit channel - primary structure
    - 6 or I2CSRX_C for the I2C Slave receive channel - primary structure
    - 7 or I2CMTX_C for the I2C Master transmit channel - primary structure
    - 8 or I2CMRX_C for the I2C Master receive channel - primary structure
    - 9 or DAC_C for the DAC output channel - primary structure
    - 10 or ADC0_C for the ADC0 output channel - primary structure
    - 11 or ADC1_C for the ADC1 output channel - primary structure
    - 12 or SINC2_C for the SINC2 output channel - primary structure
    - 17 or SPI1TX_C+ALTERNATE for the SPI1 Transmit channel - ALTERNATE structure
    - 18 or SPI1RX_C+ALTERNATE for the SPI1 receive channel - ALTERNATE structure
    - 19 or UARTTX_C+ALTERNATE for the UART Transmit channel - ALTERNATE structure
    - 20 or UARTRX_C+ALTERNATE for the UART receive channel - ALTERNATE structure
    - 21 or I2CSTX_C+ALTERNATE for the I2C Slave transmit channel - ALTERNATE structure
    - 22 or I2CSRX_C+ALTERNATE for the I2C Slave receive channel - ALTERNATE structure
    - 23 or I2CMTX_C+ALTERNATE for the I2C Master transmit channel - ALTERNATE structure
    - 24 or I2CMRX_C+ALTERNATE for the I2C Master receive channel - ALTERNATE structure
    - 25 or DAC_C+ALTERNATE for the DAC output channel - ALTERNATE structure
    - 26 or ADC0_C+ALTERNATE for the ADC0 output channel - ALTERNATE structure
    - 27 or ADC1_C+ALTERNATE for the ADC1 output channel - ALTERNATE structure
    - 28 or SINC2_C+ALTERNATE for the SINC2 output channel - ALTERNATE structure
   @param iCfg :{DMA_STOP|DMA_BASIC|DMA_AUTO|DMA_PING|DMA_PSG_PRI|DMA_PSG_ALT}
    - Choose one of DMA_STOP,DMA_BASIC,DMA_AUTO,DMA_PING,DMA_PSG_PRI,DMA_PSG_ALT
   @param iNumx :{1-1024}
   - 1 to 1024. Number of values to transfer
 @return 1:

**/
int DmaCycleCntCtrl(unsigned int iChan, int iNumx, int iCfg)
{
   iChan--;
   dmaChanDesc[iChan].ctrlCfg.Bits.cycle_ctrl       = (iCfg & 0x7);
   dmaChanDesc[iChan].ctrlCfg.Bits.n_minus_1        = iNumx - 0x1;
   return 1;
}



/**
   @brief int AdcDmaReadSetup(int iType, int iCfg, int iNumVals, int *pucRX_DMA)
         ==========Sets up DMA config structure when moving ADC results direct to memory
   @param iType :{ADC0DMAREAD,ADC1DMAREAD,SINC2DMAREAD,
                 ADC0DMAREAD+iALTERNATE,
                 ADC1DMAREAD+iALTERNATE,SINC2DMAREAD+iALTERNATE}
   Choose one of:
    - 1 or ADC0DMAREAD for ADC0 Primary DMA reading of ADC0 data outputs
    - 3 or ADC1DMAREAD for ADC1 Primary DMA reading of ADC1 data outputs
    - 4 or SINC2DMAREAD for SINC2 Primary DMA reading of SINC2 data outputs
    - 13 or ADC0DMAREAD+iALTERNATE for ADC0 Alternate DMA reading of ADC0 data outputs
    - 15 or ADC1DMAREAD+iALTERNATE for ADC1 Alternate DMA reading of ADC1 data outputs
    - 16 or SINC2DMAREAD+iALTERNATE for SINC2 Alternate DMA reading of SINC2 data outputs
   @param iCfg :{DMA_DSTINC_BYTE|DMA_DSTINC_HWORD|DMA_DSTINC_WORD|DMA_DSTINC_NO|
                  DMA_SRCINC_BYTE|DMA_SRCINC_HWORD|DMA_SRCINC_WORD|DMA_SRCINC_NO|
                  DMA_SIZE_BYTE|DMA_SIZE_HWORD|DMA_SIZE_WORD|
                  DMA_STOP|DMA_BASIC|DMA_AUTO|DMA_PING|DMA_PSG_PRI|DMA_PSG_ALT}
    - Choose one of DMA_DSTINC_BYTE, DMA_DSTINC_HWORD,DMA_DSTINC_WORD,DMA_DSTINC_NO for destination address increment
    - Choose one of DMA_SRCINC_BYTE,DMA_SRCINC_HWORD,DMA_SRCINC_WORD,DMA_SRCINC_NO for source address increment
    - Choose one of DMA_SIZE_BYTE (byte),DMA_SIZE_HWORD (half-word),DMA_SIZE_WORD(word) for source data size
    - Choose one of DMA_STOP,DMA_BASIC,DMA_AUTO,DMA_PING,DMA_PSG_PRI,DMA_PSG_ALT
   @param iNumVals :{1-1024}
    - Number of values to be transferred
   @param *pucRX_DMA :{0-0xFFFFFFFF}
    - Pass pointer to destination address for DMA transfers
   @return 1:

**/
int AdcDmaReadSetup(int iType, int iCfg, int iNumVals, int *pucRX_DMA)
{
   int iChanSel = 0;
   DmaDesc Desc;
   Desc.ctrlCfg.ctrlCfgVal = 0;
   Desc.destEndPtr = 0;
   Desc.reserved4Bytes = 0;
   Desc.srcEndPtr = 0;

   if (iType >= iALTERNATE) {
      iChanSel = 1;  // Alternate
      iType = iType - iALTERNATE;

   } else {
      iChanSel = 0;
   }

   // Common configuration of all the descriptors used here
   Desc.ctrlCfg.Bits.cycle_ctrl       = (iCfg & 0x7);
   Desc.ctrlCfg.Bits.next_useburst    = 0x0;
   Desc.ctrlCfg.Bits.r_power          = 0;
   Desc.ctrlCfg.Bits.src_prot_ctrl    = 0x0;
   Desc.ctrlCfg.Bits.dst_prot_ctrl    = 0x0;
   Desc.ctrlCfg.Bits.src_size         = ((iCfg & 0x3000000) >> 24);
   Desc.ctrlCfg.Bits.dst_size         = ((iCfg & 0x3000000) >> 24);

   Desc.ctrlCfg.Bits.n_minus_1        = iNumVals - 0x1;
   Desc.ctrlCfg.Bits.src_inc          = ((iCfg & 0xC000000) >> 26);
   Desc.ctrlCfg.Bits.dst_inc          = ((iCfg & 0xC0000000) >> 30);


   switch (iType) {
   case ADC0DMAREAD:
      Desc.srcEndPtr                  = (unsigned int)&pADI_ADC0->DAT;
      Desc.destEndPtr                 = (unsigned int)(pucRX_DMA + iNumVals - 0x1);

      if (iChanSel == 0) {
         *Dma_GetDescriptor(ADC0_C - 1, 0) = Desc;   // primary structure

      } else {
         *Dma_GetDescriptor(ADC0_C - 1, 1) = Desc;   // alternate structure
      }

      break;

   case ADC1DMAREAD:
      Desc.srcEndPtr                  = (unsigned int)&pADI_ADC1->DAT;
      Desc.destEndPtr                 = (unsigned int)(pucRX_DMA + iNumVals - 0x1);

      if (iChanSel == 0) {
         *Dma_GetDescriptor(ADC1_C - 1, 0) = Desc;   // primary structure

      } else {
         *Dma_GetDescriptor(ADC1_C - 1, 1) = Desc;   // alternate structure
      }

      break;

   case SINC2DMAREAD:
      Desc.srcEndPtr                 = (unsigned int)&pADI_ADCSTEP->STEPDAT;
      Desc.destEndPtr                = (unsigned int)(pucRX_DMA + iNumVals - 0x1); //

      if (iChanSel == 0) {
         *Dma_GetDescriptor(SINC2_C - 1, 0) = Desc;   // primary structure

      } else {
         *Dma_GetDescriptor(SINC2_C - 1, 1) = Desc;   // alternate structure
      }

      break;

   default:
      break;
   }

   return 1;
}
/**
   @brief int AdcDmaWriteSetup(int iType, int iCfg, int iNumVals, int *pucTX_DMA)
         ==========Sets up DMA config structure when moving values from memory to the ADC control registers
       Source address always starts with ADCxMSKI register.
   @param iType :{ADC0DMAWRITE,ADC1DMAWRITE,
                 ADC0DMAWRITE+iALTERNATE,
                 ADC1DMAWRITE+iALTERNATE}
      - 0 or ADC0DMAWRITE for ADC0 Primary DMA write to control registers.
      - 2 or ADC1DMAWRITE for ADC1 Primary DMA write to control registers.
      - 12 or ADC0DMAWRITE+iALTERNATE for ADC0 Alternate DMA write to control registers.
      - 14 or ADC1DMAWRITE+iALTERNATE for ADC1 Alternate DMA write to control registers.
   @param iCfg :{DMA_DSTINC_BYTE|DMA_DSTINC_HWORD|DMA_DSTINC_WORD|DMA_DSTINC_NO|
                  DMA_SRCINC_BYTE|DMA_SRCINC_HWORD|DMA_SRCINC_WORD|DMA_SRCINC_NO|
                  DMA_SIZE_BYTE|DMA_SIZE_HWORD|DMA_SIZE_WORD|
                  DMA_STOP|DMA_BASIC|DMA_AUTO|DMA_PING|DMA_PSG_PRI|DMA_PSG_ALT}
    - Choose one of DMA_DSTINC_BYTE, DMA_DSTINC_HWORD,DMA_DSTINC_WORD,DMA_DSTINC_NO for destination address increment
    - Choose one of DMA_SRCINC_BYTE,DMA_SRCINC_HWORD,DMA_SRCINC_WORD,DMA_SRCINC_NO for source address increment
    - Choose one of DMA_SIZE_BYTE (byte),DMA_SIZE_HWORD (half-word),DMA_SIZE_WORD(word) for source data size
    - Choose one of DMA_STOP,DMA_BASIC,DMA_AUTO,DMA_PING,DMA_PSG_PRI,DMA_PSG_ALT
   @param iNumVals :{1-1024}
    - Number of values to be transferred
   @param *pucTX_DMA :{0-0xFFFFFFFF}
    - Pass Source pointer  address for DMA transfers
   @return 1:

**/
int AdcDmaWriteSetup(int iType, int iCfg, int iNumVals, int *pucTX_DMA)
{
   int iChanSel = 0;
   DmaDesc Desc;
   Desc.ctrlCfg.ctrlCfgVal = 0;
   Desc.destEndPtr = 0;
   Desc.reserved4Bytes = 0;
   Desc.srcEndPtr = 0;

   if (iType >= iALTERNATE) {
      iChanSel = 1;  // Alternate
      iType = iType - iALTERNATE;

   } else {
      iChanSel = 0;
   }

   // Common configuration of all the descriptors used here
   Desc.ctrlCfg.Bits.cycle_ctrl       = (iCfg & 0x7);
   Desc.ctrlCfg.Bits.next_useburst    = 0x0;
   Desc.ctrlCfg.Bits.r_power          = 0;
   Desc.ctrlCfg.Bits.src_prot_ctrl    = 0x0;
   Desc.ctrlCfg.Bits.dst_prot_ctrl    = 0x0;
   Desc.ctrlCfg.Bits.src_size         = ((iCfg & 0x3000000) >> 24);
   Desc.ctrlCfg.Bits.dst_size         = ((iCfg & 0x3000000) >> 24);

   Desc.ctrlCfg.Bits.n_minus_1        = iNumVals - 0x1;
   Desc.ctrlCfg.Bits.src_inc          = ((iCfg & 0xC000000) >> 26);
   Desc.ctrlCfg.Bits.dst_inc          = ((iCfg & 0xC0000000) >> 30);

   switch (iType) {
   case ADC0DMAWRITE:
      Desc.srcEndPtr                  = (unsigned int)&pucTX_DMA;
      Desc.destEndPtr                 = (unsigned int)(pADI_ADC0->MSKI + iNumVals - 0x1);

      if (iChanSel == 0) {
         *Dma_GetDescriptor(ADC0_C - 1, 0) = Desc;   // primary structure

      } else {
         *Dma_GetDescriptor(ADC0_C - 1, 1) = Desc;   // alternate structure
      }

      break;

   case ADC1DMAWRITE:
      Desc.srcEndPtr                  = (unsigned int)&pucTX_DMA;
      Desc.destEndPtr                 = (unsigned int)(pADI_ADC1->MSKI + iNumVals - 0x1);

      if (iChanSel == 0) {
         *Dma_GetDescriptor(ADC1_C - 1, 0) = Desc;   // primary structure

      } else {
         *Dma_GetDescriptor(ADC1_C - 1, 1) = Desc;   // alternate structure
      }

      break;

   default:
      break;
   }

   return 1;
}
/**
   @brief int DacDmaWriteSetup(int iType, int iCfg, int iNumVals, int *pucTX_DMA)
            ==========Specific function to setup DAC DMA control structure
       Source address always starts with ADCxMSKI register.
   @param iType :{DAC_C,DAC_C+iALTERNATE}
      - 9 or DAC_C for DAC Primary DMA control
      - 21 or DAC_C+iALTERNATE for DAC Alternate DMA control
   @param iCfg :{DMA_DSTINC_BYTE|DMA_DSTINC_HWORD|DMA_DSTINC_WORD|DMA_DSTINC_NO|
                  DMA_SRCINC_BYTE|DMA_SRCINC_HWORD|DMA_SRCINC_WORD|DMA_SRCINC_NO|
                  DMA_SIZE_BYTE|DMA_SIZE_HWORD|DMA_SIZE_WORD|
                  DMA_STOP|DMA_BASIC|DMA_AUTO|DMA_PING|DMA_PSG_PRI|DMA_PSG_ALT}
    - Choose one of DMA_DSTINC_BYTE, DMA_DSTINC_HWORD,DMA_DSTINC_WORD,DMA_DSTINC_NO for destination address increment
    - Choose one of DMA_SRCINC_BYTE,DMA_SRCINC_HWORD,DMA_SRCINC_WORD,DMA_SRCINC_NO for source address increment
    - Choose one of DMA_SIZE_BYTE (byte),DMA_SIZE_HWORD (half-word),DMA_SIZE_WORD(word) for source data size
    - Choose one of DMA_STOP,DMA_BASIC,DMA_AUTO,DMA_PING,DMA_PSG_PRI,DMA_PSG_ALT
   @param iNumVals :{1-1024}
    - Number of values to be transferred
   @param *pucTX_DMA :{0-0xFFFFFFFF}
    - Pass Source pointer  address for DMA transfers
   @return 1:

**/
int DacDmaWriteSetup(int iType, int iCfg, int iNumVals, int *pucTX_DMA)
{
   int iChanSel = 0;
   DmaDesc Desc;
   Desc.ctrlCfg.ctrlCfgVal = 0;
   Desc.destEndPtr = 0;
   Desc.reserved4Bytes = 0;
   Desc.srcEndPtr = 0;

   if (iType >= iALTERNATE) {
      iChanSel = 1;  // Alternate

   } else {
      iChanSel = 0;
   }

   // Common configuration of all the descriptors used here
   Desc.ctrlCfg.Bits.cycle_ctrl       = (iCfg & 0x7);
   Desc.ctrlCfg.Bits.next_useburst    = 0x0;
   Desc.ctrlCfg.Bits.r_power          = 0;
   Desc.ctrlCfg.Bits.src_prot_ctrl    = 0x0;
   Desc.ctrlCfg.Bits.dst_prot_ctrl    = 0x0;
   Desc.ctrlCfg.Bits.src_size         = ((iCfg & 0x3000000) >> 24);
   Desc.ctrlCfg.Bits.dst_size         = ((iCfg & 0x3000000) >> 24);

   Desc.ctrlCfg.Bits.n_minus_1        = iNumVals - 0x1;
   Desc.ctrlCfg.Bits.src_inc          = ((iCfg & 0xC000000) >> 26);
   Desc.ctrlCfg.Bits.dst_inc          = ((iCfg & 0xC0000000) >> 30);

   Desc.srcEndPtr                  = (unsigned int)(pucTX_DMA + iNumVals - 0x1);
   Desc.destEndPtr                 = (unsigned int)(&pADI_DAC->DACDAT);

   if (iChanSel == 0) {
      *Dma_GetDescriptor(DAC_C - 1, 0) = Desc;   // primary structure

   } else {
      *Dma_GetDescriptor(DAC_C - 1, 1) = Desc;   // alternate structure
   }

   return 1;
}

/**@}*/


