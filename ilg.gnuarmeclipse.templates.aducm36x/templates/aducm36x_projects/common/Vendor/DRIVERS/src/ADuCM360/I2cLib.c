/**
 *****************************************************************************
   @addtogroup i2c 
   @{
   @file     I2cLib.c
   @brief    Set of I2C peripheral functions.
    
   @version  V0.2
   @author   ADI
   @date     May 2013 

   @par Revision History:
   - V0.1, April 2012: initial version. 
   - V0.2, May 2013:   corrected comments in I2cBaud().                 

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/

#include	"I2cLib.h"
#include <ADuCM360.h>


/**
	@brief int I2cMCfg(int iDMACfg, int iIntSources, int iConfig)
			========== Configure the I2C master channel.
	@param iDMACfg :{0|I2CMCON_TXDMA|I2CMCON_RXDMA} \n
		- 0 by default to disable all DMA operation.
		- I2CMCON_TXDMA to enable I2C Master Tx DMA requests.
		- I2CMCON_RXDMA to enable I2C Master Rx DMA requests.
	@param iIntSources :{0|I2CMCON_IENCMP_EN|I2CMCON_IENNACK_EN|I2CMCON_IENALOST_EN|I2CMCON_IENTX_EN|I2CMCON_IENRX_EN} \n		 
		- 0 by default to disable all interrupt sources.
		- I2CMCON_IENCMP_EN to enable interrupts when a Stop condition is detected. I2CMCON_IENCMP_DIS by default.
		- I2CMCON_IENNACK_EN to enable interrupts when a NACK is detected. I2CMCON_IENNACK_DIS by default.
		- I2CMCON_IENALOST_EN to enable interrupts when bus arbitration is lost. I2CMCON_IENALOST_DIS by default.
		- I2CMCON_IENTX_EN to enable Transmit interrupts. I2CMCON_IENTX_DIS by default.
		- I2CMCON_IENRX_EN to enable Receive interrupts. I2CMCON_IENRX_DIS by default.
	@param iConfig :{0|I2CMCON_LOOPBACK_EN|I2CMCON_COMPETE_EN|I2CMCON_MAS_EN}	\n
		- 0 by default.
		- I2CMCON_LOOPBACK_EN to enable I2C loopback mode. I2CMCON_LOOPBACK_DIS by default.
		- I2CMCON_COMPETE_EN to enable I2C Master to compete for control of bus. I2CMCON_COMPETE_DIS by default.
		- I2CMCON_MAS_EN to enable I2C Master mode. I2CMCON_MAS_DIS by default.
**/

int I2cMCfg(int iDMACfg, int iIntSources, int iConfig)
{
  int i1;
	  
  i1 = iDMACfg;
  i1 |= iIntSources;
  i1 |= iConfig;
  pADI_I2C->I2CMCON = i1; 

  return 1;
}
/**
	@brief int I2cStretch(int iMode, int iStretch)
			========== Configure the I2C Clock stretching.
	@param iMode :{MASTER, SLAVE} \n
		- 0 or MASTER  to control I2C Master clock stretch.
		- 1 or SLAVE  to control I2C Slave clock stretch.
	@param iStretch :{STRETCH_DIS,STRETCH_EN}	\n
		- 0 or STRETCH_DIS to disable clock stretching.
		- 1 or STRETCH_EN to enable clock stretching.
**/
int I2cStretch(int iMode, int iStretch)
{
   if (iMode == MASTER)
   {
	  if (iStretch == STRETCH_EN)		// Enable master clock stretching
	  	pADI_I2C->I2CMCON |= I2CMCON_STRETCH;
	  else                              // Disable master clock stretching
	    pADI_I2C->I2CMCON &= 0xFFF7;
   }
   else	// Slave clock stretch control
   {
	   if (iStretch == STRETCH_EN)		// Enable Slave clock stretching
	  	pADI_I2C->I2CSCON |= I2CSCON_STRETCH;
	  else                              // Disable Slave clock stretching
	    pADI_I2C->I2CSCON &= 0xFFBF;
   }
   return 1;
}

/**
	@brief int I2cFifoFlush(int iMode, int iFlush)
			========== Flush Master or Slave Tx FIFO
	@param iMode :{MASTER, SLAVE}
		- 0 or MASTER for I2C Master operation.
		- 1 or SLAVE for I2C Slave operation.
	@param iFlush :{DISABLE, ENABLE}
		- 0 or DISABLE to disable flush of FIFO.
		- 1 or ENABLE to enable flush of FIFO.
	@return 1 if successful 
**/

int I2cFifoFlush(int iMode, int iFlush)
{
  if (iMode == 1) // slave
  {
    if (iFlush == 1)
      pADI_I2C->I2CFSTA |= 0x100;
    else 
      pADI_I2C->I2CFSTA &= 0xEFF;
  }
  else           // master
  {
    if (iFlush == 1)
      pADI_I2C->I2CFSTA |= 0x200;
    else 
      pADI_I2C->I2CFSTA &= 0xDFF; 
  }
  return 1;
}

/**
	@brief int I2cSCfg(int iDMACfg, int iIntSources, int iConfig)
			========== Configure the SPI channel.
	@param iDMACfg :{0|I2CSCON_TXDMA|I2CSCON_RXDMA} \n
		- 0 by default to disable all DMA operation.
		- I2CSCON_TXDMA to enable I2C Master Tx DMA requests.
		- I2CSCON_RXDMA to enable I2C Master Rx DMA requests.
	@param iIntSources :{0|I2CSCON_IENREPST_EN|I2CSCON_IENTX_EN|I2CSCON_IENRX_EN|I2CSCON_IENSTOP_EN} \n
		- 0 by default to disable all interrupt sources.
		- I2CSCON_IENREPST_EN to enable interrupts when a repeated Start condition is detected. I2CSCON_IENREPST_DIS by default.
		- I2CSCON_IENTX_EN to enable Transmit interrupts. I2CSCON_IENTX_DIS by default.
		- I2CSCON_IENRX_EN to enable Receive interrupts. I2CSCON_IENRX_DIS by default.
		- I2CSCON_IENSTOP_EN to enable interrupts when Stop condition is detected. I2CSCON_IENSTOP_DIS by default.
	@param iConfig :{0|I2CSCON_NACK_EN|I2CSCON_EARLYTXR_EN|I2CSCON_GCSB_CLR|I2CSCON_HGC_EN|I2CSCON_GC_EN|I2CSCON_ADR10_EN|I2CSCON_SLV_EN}	\n
		- 0 by default.
		- I2CSCON_NACK_EN to force NACK after next byte. I2CSCON_NACK_DIS by default.
		- I2CSCON_EARLYTXR_EN to enable early transmit request. I2CSCON_EARLYTXR_DIS by default.               
		- I2CSCON_GCSB_CLR to clear general call status register.
		- I2CSCON_HGC_EN to enable Hardware general calls. I2CSCON_HGCEN_DIS by default.
		- I2CSCON_GC_EN to enable general calls. I2CSCON_GCEN_DIS by default.
		- I2CSCON_ADR10_EN to enable 10-bit addresses. I2CSCON_ADR10EN_DIS by default.
		- I2CSCON_SLV_EN to enable Slave mode. I2CSCON_SLVEN_DIS by default.
**/

int I2cSCfg(int iDMACfg, int iIntSources, int iConfig)
{
  int i1;
	  
  i1 = iDMACfg;
  i1 |= iIntSources;
  i1 |= iConfig;
  pADI_I2C->I2CSCON = i1; 
	 
  return 1;
}


/**
	@brief int I2cRx(int iMode)
			========== Reads 8 bits of I2CMRX or I2CSRX
	@param iMode :{MASTER, SLAVE}
		- 0 or MASTER for I2C Master operation.
		- 1 or SLAVE for I2C Slave operation.
	@return I2CSRX or I2CMRX 
**/

int I2cRx(int iMode)
{
  int i1;

  i1 = iMode;
  if (i1 == 1)
  {
    return pADI_I2C->I2CSRX; 
  }
  else
  {
    return pADI_I2C->I2CMRX; 
  }
}

 

/**
	@brief int I2cTx(int iMode, int iTx)
			========== Write 8 bits of iTx to I2CMTX ro I2CSTX
	@param iMode :{MASTER, SLAVE}
		- 0 or MASTER for I2C Master operation.
		- 1 or SLAVE for I2C Slave operation.
	@param iTx :{0-255}	\n
		Byte to transmit.
	@return 1 if successful 
**/

int I2cTx(int iMode, int iTx)
{
  unsigned int i1;

  i1 = iTx;
  if (iMode == 1)
    pADI_I2C->I2CSTX = i1; 
  else
    pADI_I2C->I2CMTX = i1; 
  return 1;
}


/**
	@brief int I2cBaud(int iHighPeriod, int iLowPeriod)
	========== Set the I2C clock rate in Master mode - CLKCON1 setting not accounted for

	@brief iHighPeriod configures the Master Clock high period               \n
	@brief iLowPeriod configures the Master Clock low period            \n
	@param iHighPeriod :{0-255}                                       \n
		- 0x12 for 400kHz operation.
		- 0x4E for 100kHz operation.
	@param iLowPeriod :{0-255}	                             \n
		- 0x13 for 400kHz operation.
		- 0x4F for 100kHz operation.
	@return 1 
**/

int I2cBaud(int iHighPeriod, int iLowPeriod)
{
  unsigned int i1;

  i1 =  iLowPeriod;
  i1 |= (iHighPeriod << 8);
  pADI_I2C->I2CDIV = i1;
  return 1;
}


/**
	@brief int I2cMWrCfg(unsigned int uiDevAddr)
			========== Configure I2CADR0/I2CADR1 - Device address register.
	
	@param uiDevAddr :{0-1023}     \n
	@ This function write uiDevAddr to I2CADR0 address, lsb =0
	@return 1
**/

int I2cMWrCfg(unsigned int uiDevAddr)
{
  uiDevAddr &= 0xFFFE;		// Ensure write bit is configured
  if (uiDevAddr > 0xFF)  	// Check for 10-bit address
  {
    pADI_I2C->I2CADR1 = (uiDevAddr & 0xFF);
    pADI_I2C->I2CADR0	= ((uiDevAddr >> 7) & 0x6) | 0xF0;
  }
  else
  {
    pADI_I2C->I2CADR0 = uiDevAddr & 0xFF;
    pADI_I2C->I2CADR1 = 0x00;
  }
  return 1;
}


/**
	@brief int I2cMRdCfg(unsigned int uiDevAddr, int iNumBytes, int iExt)
			========== Configure I2CMRXCNT - Master read control register.
	@param uiDevAddr :{0-1023}     \n
	@param iNumBytes :{0-65535}     \n
	@param iExt :{DISABLE,ENABLE}
		- DISABLE for Master to Read 1 to 256 bytes
		- ENABLE for Master to read more than 256 bytes
	@return 1
**/

int I2cMRdCfg(unsigned int uiDevAddr, int iNumBytes, int iExt)
{
  pADI_I2C->I2CMRXCNT = (iNumBytes - 1);
  pADI_I2C->I2CMRXCNT |= iExt;
  if (uiDevAddr > 0xFF)  		// Check for 10-bit address
  {
    pADI_I2C->I2CADR1	= (uiDevAddr & 0xFF);
    pADI_I2C->I2CADR0	= ((uiDevAddr >> 7) & 0x6) | 0xF0;
  }
  else
  {
    pADI_I2C->I2CADR0 = uiDevAddr & 0xFF;
    pADI_I2C->I2CADR1 = 0x00;
  }
  pADI_I2C->I2CADR0 |= 0x1;			// Ensure read bit is set to 1
  return 1;
}

/**
	@brief int I2cSta(int iMode)
			========== Read the status register for the I2C Master or Slave.
@param iMode :{MASTER, SLAVE}
		- 0 or MASTER for I2C Master operation.
		- 1 or SLAVE for I2C Slave operation.
	@return value of I2CSSTA:
		- I2CSSTA.0 = Tx FIFO status
		- I2CSSTA.1 = I2C Tx FIFO under-run 
		- I2CSSTA.2 = I2C Tx Request
		- I2CSSTA.3 = I2C Rx Request
		- I2CSSTA.4 = I2C Rx FIFO overflow
		- I2CSSTA.5 = I2C Slave NACK of address byte
		- I2CSSTA.6 = I2C Slave busy
		- I2CSSTA.7 = I2C general call interrupt
		- I2CSSTA.9:8 = I2C general call ID
		- I2CSSTA.10 = I2C Stop condition detected
		- I2CSSTA.12:11 = I2C ID Match status
		- I2CSSTA.13 = I2C repeated Start condition detected
		- I2CSSTA.14 = I2C Start + matching address condition detected
	@return value of I2CMSTA:
		- I2CMSTA.1:0 = Tx FIFO status
		- I2CMSTA.2 = I2C Tx Request
		- I2CMSTA.3 = I2C Rx Request
		- I2CMSTA.4 = I2C NACK of address byte from Slave
		- I2CMSTA.5 = I2C Arbitration lost
		- I2CMSTA.6 = I2C Master busy
		- I2CMSTA.7 = I2C NACK of data byte from Slave
		- I2CMSTA.8 = I2C Stop condition detected
		- I2CMSTA.9 = Rx overflow
		- I2CMSTA.10 = I2C Bus busy
		- I2CMSTA.11 = I2C Stop detected
		- I2CMSTA.12 = I2C Tx Under-run condition
**/

int I2cSta(int iMode)
{
  if (iMode == 1)
    return pADI_I2C->I2CSSTA;
  else
    return pADI_I2C->I2CMSTA;
}


/**
	@brief int I2cMRdCnt(void)
			========== Master Read count register - read by master to keep track of number of bytes received.

	@return value of I2CMCRXCNT.
**/

int I2cMRdCnt(void)
{
  return pADI_I2C->I2CMCRXCNT;
}


/**
	@brief int I2cSGCallCfg(int iHWGCallAddr)
			========== Configure ID value for Hardware General Calls.

	@param iHWGCallAddr :{0-255}
	@return 1
**/

int I2cSGCallCfg(int iHWGCallAddr)
{
  pADI_I2C->I2CALT = iHWGCallAddr;
  return 1;
}


/**
	@brief int I2cSIDCfg(int iSlaveID0, int iSlaveID1,int iSlaveID2,int iSlaveID3)
			========== Configure ID value for Slave address - value betweeen 0-0xFF.

	@param iSlaveID0 :{0-255}
	@param iSlaveID1 :{0-255}
	@param iSlaveID2 :{0-255}
	@param iSlaveID3 :{0-255}
	@return 1
**/

int I2cSIDCfg(int iSlaveID0, int iSlaveID1,int iSlaveID2,int iSlaveID3)
{
  pADI_I2C->I2CID0 = iSlaveID0;
  pADI_I2C->I2CID1 = iSlaveID1;
  pADI_I2C->I2CID2 = iSlaveID2;
  pADI_I2C->I2CID3 = iSlaveID3;	
  return 1;	
}

/**@}*/


