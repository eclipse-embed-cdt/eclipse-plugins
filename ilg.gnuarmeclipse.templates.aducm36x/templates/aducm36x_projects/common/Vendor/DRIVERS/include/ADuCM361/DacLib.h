/**
 *****************************************************************************  
   @file       DacLib.h
   @brief      Set of DAC peripheral functions.
   - First configure DAC with DacCfg().
   - Set sync mode with DacSyn() if desired.
   - Output DAC value with DacWr().
   - Example:
      DacCfg(0,DAC_REF_INT,DAC_VOUT,DAC_12BIT);
      for(i1 = 0; i1<0x10000000; i1 += 0x1000000)
         DacWr(0,i1);

   @version    V0.1
	@author     ADI
	@date       November 2010

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/
extern int DacCfg(int iDisable, int iRef, int iDrv, int iMode);
extern int DacWr(int iChan, int iData);
extern int DacSync(int iChan, int iSync, int iTime);
extern int DacDma(int iChan, int iDmaSel);

