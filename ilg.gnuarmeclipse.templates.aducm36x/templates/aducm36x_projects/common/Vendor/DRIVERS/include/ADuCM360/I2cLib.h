/**
 *****************************************************************************
   @file     I2cLib.h
   @brief    Set of I2C peripheral functions.
   @version  V0.1
   @author   ADI
   @date     April 2012 
              

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/

#include <ADuCM360.h>


extern int I2cMCfg(int iDMACfg, int iIntSources, int iConfig);
extern int I2cStretch(int iMode, int iStretch);
extern int I2cFifoFlush(int iMode, int iFlush);
extern int I2cSCfg(int iDMACfg, int iIntSources, int iConfig);
extern int I2cRx(int iMode);
extern int I2cTx(int iMode, int iTx);
extern int I2cBaud(int iHighPeriod, int iLowPeriod);
extern int I2cMWrCfg(unsigned int uiDevAddr);
extern int I2cMRdCfg(unsigned int uiDevAddr, int iNumBytes, int iExt);
extern int I2cSta(int iMode);
extern int I2cMRdCnt(void);		
extern int I2cSGCallCfg(int iHWGCallAddr);
extern int I2cSIDCfg(int iSlaveID0, int iSlaveID1, int iSlaveID2, int iSlaveID3);	

#define MASTER 0
#define SLAVE 1
#define DISABLE 0
#define ENABLE 1

#define STRETCH_DIS 0
#define STRETCH_EN 1
