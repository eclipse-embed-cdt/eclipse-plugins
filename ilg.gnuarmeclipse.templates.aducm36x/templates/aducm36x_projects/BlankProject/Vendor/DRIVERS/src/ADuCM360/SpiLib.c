/**
 *****************************************************************************
   @addtogroup spi 
   @{
   @file     SpiLib.c
   @brief    Set of SPI peripheral functions.
   @version  V0.3
   @author   ADI
   @date     November 2012
   @par Revision History:
   - V0.1, May 2012: initial version. 
   - V0.2, October 2012: Added SPI DMA support
   - V0.3, November 2012: Moved SPI DMA functionality to DmaLib

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/

#include	"SpiLib.h"
#include <ADuCM360.h>
#include "DmaLib.h"

/**
      @brief int SpiCfg(ADI_SPI_TypeDef *pSPI, int iFifoSize, int iMasterEn, int iConfig);
			========== Configure the SPI channel.
      @param pSPI :{pADI_SPI0 , pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
      @param iFifoSize :{SPICON_MOD_TX1RX1,SPICON_MOD_TX2RX2,SPICON_MOD_TX3RX3,SPICON_MOD_TX4RX4}
                - SPICON_MOD_TX1RX1 for single byte Rx and Tx FIFO sizes.
		- SPICON_MOD_TX2RX2 for two bytes Rx and Tx FIFO sizes.
		- SPICON_MOD_TX3RX3 for three bytes Rx and Tx FIFO sizes.
		- SPICON_MOD_TX4RX4 for four bytes Rx and Tx FIFO sizes.
      @param iMasterEn :{SPICON_MASEN_DIS,SPICON_MASEN_EN}
		- SPICON_MASEN_DIS to enable SPI in Slave mode.
		- SPICON_MASEN_EN to enable SPI in Master mode.
      @param iConfig :{SPICON_CON_EN|SPICON_LOOPBACK_EN|SPICON_SOEN_EN|SPICON_RXOF_EN|SPICON_ZEN_EN|SPICON_TIM_TXWR|
                       SPICON_LSB_EN|SPICON_WOM_EN|SPICON_CPOL_HIGH|SPICON_CPHA_SAMPLETRAILING|SPICON_ENABLE_EN}
                - SPICON_CON_EN to enable continuous transfer. SPICON_CON_DIS by default. 
                - SPICON_LOOPBACK_EN to enable loopback mode (connect MISO to MOSI). SPICON_LOOPBACK_DIS by default.
                - SPICON_SOEN_EN for MISO to operate as normal. 
                  SPICON_SOEN_DIS disable the output driver on the MISO pin, by default.
                - SPICON_RXOF_EN to overwrite the valid data in the Rx register with the new serial byte received.
                  SPICON_RXOF_DIS discard the new serial byte received, by default.
                - SPICON_ZEN_EN to transmit 0x00 when there is no valid data in the Tx FIFO. 
                  SPICON_ZEN_DIS transmit the last transmitted value, by default.
                - SPICON_TIM_TXWR for Initiate transfer with a write to the SPI TX register. SPICON_TIM_RXRD by default.
                - SPICON_LSB_EN for LSB transmitted first. SPICON_LSB_DIS by default.
                - SPICON_WOM_EN for Open Circuit Data Output Enable. SPICON_WOM_DIS by default.
                - SPICON_CPOL_HIGH for Serial clock idles high. SPICON_CPOL_LOW by default.
                - SPICON_CPHA_SAMPLETRAILING for Serial clock pulses at the  start of the first data bit transfer. 
                  SPICON_CPHA_SAMPLELEADING by default.
                - SPICON_ENABLE_EN to enable of the SPI peripheral. SPICON_ENABLE_DIS by default.
	@return 1.  
**/

int SpiCfg(ADI_SPI_TypeDef *pSPI, int iFifoSize, int iMasterEn, int iConfig)
{    
  int i1;      
  i1  = iFifoSize;    
  i1 |= iMasterEn;    
  i1 |= iConfig;    
  pSPI->SPICON = i1;     
  return 1;
}

/**
	@brief int SpiBaud(ADI_SPI_TypeDef *pSPI, int iClkDiv, int iCserr)
			========== Set the SPI clock rate in Master mode.
	@param pSPI :{pADI_SPI0 , pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
	@param iClkDiv :{0 - 63}
		-  iClkDiv to set the SPI baud rate - baud rate is SPI UCLK/2x(iCLKDiv+1).
	@param iCserr :{SPIDIV_BCRST_DIS,SPIDIV_BCRST_EN}
		- SPIDIV_BCRST_DIS to disable CS error detection.
		- SPIDIV_BCRST_EN to Enable CS error detection.
	@return 1. 
**/
int SpiBaud(ADI_SPI_TypeDef *pSPI, int iClkDiv, int iCserr)
{
  int i1;
  i1 = iCserr;
  i1 |= iClkDiv;
  pSPI->SPIDIV = i1;
  return 1;
}

/**
	@brief int SpiRx(ADI_SPI_TypeDef *pSPI)
			========== Write 8 bits of iRx to SPIxRX.
	@param pSPI :{pADI_SPI0 , pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
	@return SPIRX value.
**/
int SpiRx(ADI_SPI_TypeDef *pSPI)	
{
  return pSPI->SPIRX; 	
}

/**
	@brief int SpiTx(ADI_SPI_TypeDef *pSPI, int iTx);
			========== Write 8 bits of iTx to SPIxTX.
	@param pSPI :{pADI_SPI0 , pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
        @param iTx :{0-255}	
		- Byte to transmit.
	@return 1. 
**/
int SpiTx(ADI_SPI_TypeDef *pSPI, int iTx)	
{
  pSPI->SPITX = iTx; 	
  return 1;
}

/**
	@brief int SpiSta(ADI_SPI_TypeDef *pSPI)
			========== Read the status register for the SPI.
	@param pSPI :{pADI_SPI0, pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
        @return value of SPIxSTA:
                - SPISTA_IRQ, Global SPI IRQ.
                - SPISTA_TXFSTA_EMPTY, SPISTA_TXFSTA_ONEBYTE, SPISTA_TXFSTA_TWOBYTES, SPISTA_TXFSTA_THREEBYTES or
                  SPISTA_TXFSTA_FOURBYTES, SPI Tx FIFO status, number of bytes in FIFO.
                - SPISTA_TXUR, SPI Tx FIFO under-run.
                - SPISTA_TX, SPI Tx IRQ.
                - SPISTA_RX, SPI Rx IRQ.
                - SPISTA_RXOF, SPI Rx Overflow IRQ.
                - SPISTA_RXFSTA_EMPTY, SPISTA_RXFSTA_ONEBYTE, SPISTA_RXFSTA_TWOBYTES, SPISTA_RXFSTA_THREEBYTES or
                  SPISTA_RXFSTA_FOURBYTES, SPI Rx FIFO status, number of bytes in FIFO.
                - SPISTA_RXS, SPI Rx FIFO excess bytes in FIFO.
                - SPISTA_CSERR, Chip select SPI error.
**/
int SpiSta(ADI_SPI_TypeDef *pSPI)
{      
  return pSPI->SPISTA;	
}


/**
	@brief int SpiFifoFlush(ADI_SPI_TypeDef *pSPI, int iTxFlush, int iRxFlush);
			========== Function to flush Rx or Tx FIFOs.
	@param pSPI :{pADI_SPI0 , pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
	@param iTxFlush :{SPICON_TFLUSH_DIS,SPICON_TFLUSH_EN}	
		- Set to Flush the Tx FIFO.
		- SPICON_TFLUSH_DIS to do nothing.
		- SPICON_TFLUSH_EN to flush Tx FIFO.
	@param iRxFlush :{SPICON_RFLUSH_DIS,SPICON_RFLUSH_EN}
		- Set to Flush the Tx FIFO.
		- SPICON_RFLUSH_DIS to do nothing.
		- SPICON_RFLUSH_EN to flush Rx FIFO.
	@return 1. 
**/
int SpiFifoFlush(ADI_SPI_TypeDef *pSPI, int iTxFlush, int iRxFlush)
{
  if (iTxFlush == SPICON_TFLUSH_EN)
  {
    pSPI->SPICON	|= 0x2000;
    pSPI->SPICON	&= 0xDFFF;
  }
  if (iRxFlush == SPICON_RFLUSH_EN)
  {
    pSPI->SPICON	|= 0x1000;
    pSPI->SPICON	&= 0xEFFF;
  }		
  return 1;
}

/**
	@brief int SpiTxFifoFlush(ADI_SPI_TypeDef *pSPI, int iTxFlush)
			========== Function to flush Tx FIFO.
	@param pSPI :{pADI_SPI0 , pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
	@param iTxFlush :{SPICON_TFLUSH_DIS,SPICON_TFLUSH_EN}	
		- Set to Flush the Tx FIFO.
		- SPICON_TFLUSH_DIS to do nothing.
		- SPICON_TFLUSH_EN to flush Tx FIFO.
	@warning 
                The flush bit stays set or cleared.
	@return 1. 
**/
int SpiTxFifoFlush(ADI_SPI_TypeDef *pSPI, int iTxFlush)
{
  if (iTxFlush == SPICON_TFLUSH_EN)
  {
    pSPI->SPICON	|= 0x2000;
  }
  else                      
  {		
    pSPI->SPICON	&= 0xDFFF;
  }
  return 1;
}

/**
	@brief int SpiRxFifoFlush(ADI_SPI_TypeDef *pSPI, int iRxFlush)
			========== Function to flush Rx FIFO.
	@param pSPI :{pADI_SPI0 , pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
	@param iRxFlush :{SPICON_RFLUSH_DIS,SPICON_RFLUSH_EN}
		- Set to Flush the Tx FIFO.
		- SPICON_RFLUSH_DIS to do nothing.
		- SPICON_RFLUSH_EN to flush Rx FIFO
	@warning
                The flush bit stays set or cleared.
	@return 1. 
**/
int SpiRxFifoFlush(ADI_SPI_TypeDef *pSPI, int iRxFlush)
{
  if (iRxFlush == SPICON_RFLUSH_EN)
  {
    pSPI->SPICON	|= 0x1000;
  }
  else                     
  {		
    pSPI->SPICON	&= 0xEFFF;
  }	
  return 1;
}

/**
	@brief int SpiDma(ADI_SPI_TypeDef *pSPI, int iDmaRxSel, int iDmaTxSel, int iDmaEn);
			========== Enables/Disables DMA channel.
	@param pSPI :{0,pADI_SPI1}
		- pADI_SPI1 for SPI1.
	@param iDmaRxSel :{SPIDMA_IENRXDMA_DIS,SPIDMA_IENRXDMA_EN}
		- SPIDMA_IENRXDMA_DIS to disable SPI Rx DMA channel.
		- SPIDMA_IENRXDMA_EN to enable SPI Rx DMA channel.
	@param iDmaTxSel :{SPIDMA_IENTXDMA_DIS,SPIDMA_IENTXDMA_EN}
		- SPIDMA_IENTXDMA_DIS to disable SPI Tx DMA channel.
		- SPIDMA_IENTXDMA_EN to enable SPI Tx DMA channel.
	@param iDmaEn :{SPIDMA_ENABLE_DIS,SPIDMA_ENABLE_EN}
		- SPIDMA_ENABLE_DIS to disable SPI DMA mode.
		- SPIDMA_ENABLE_EN to enable SPI DMA mode.
	@return 1.
*/
int SpiDma(ADI_SPI_TypeDef *pSPI, int iDmaRxSel, int iDmaTxSel, int iDmaEn)
{
  int	i1;
  i1 = iDmaRxSel;
  i1 |= iDmaTxSel;
  i1 |= iDmaEn;
  pSPI->SPIDMA = i1;
  return 1;
}

/**
	@brief int SpiCountRd(ADI_SPI_TypeDef *pSPI);
			========== Read the SPIxCNT register for the SPI - number of bytes received.
	@param pSPI :{pADI_SPI0 , pADI_SPI1}
		- pADI_SPI0 for SPI0.
		- pADI_SPI1 for SPI1.
	@return 
		SPICNT, Number of bytes received.
**/

int SpiCountRd(ADI_SPI_TypeDef *pSPI)
{
  return	pSPI->SPICNT;
}

/**@}*/
