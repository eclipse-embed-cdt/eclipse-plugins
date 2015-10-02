/**
 *****************************************************************************
   @file     SpiLib.h
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

#include <ADuCM360.h>

extern int SpiCfg(ADI_SPI_TypeDef *pSPI, int iFifoSize, int iMasterEn, int iConfig);
extern int SpiRx(ADI_SPI_TypeDef *pSPI);
extern int SpiTx(ADI_SPI_TypeDef *pSPI, int iTx);
extern int SpiSta(ADI_SPI_TypeDef *pSPI);
extern int SpiBaud(ADI_SPI_TypeDef *pSPI, int iClkDiv, int iCserr);
extern int SpiFifoFlush(ADI_SPI_TypeDef *pSPI, int iTxFlush, int iRxFlush);
extern int SpiTxFifoFlush(ADI_SPI_TypeDef *pSPI, int iTxFlush);
extern int SpiRxFifoFlush(ADI_SPI_TypeDef *pSPI, int iRxFlush);
extern int SpiDma(ADI_SPI_TypeDef *pSPI, int iDmaRxSel, int iDmaTxSel, int iDmaEn);
extern int SpiCountRd(ADI_SPI_TypeDef *pSPI);

