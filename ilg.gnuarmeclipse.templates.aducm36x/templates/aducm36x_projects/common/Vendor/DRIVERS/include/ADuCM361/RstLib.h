/**
 *****************************************************************************
   @file     RstLib.h
   @brief    Reads the reset status bits and allows them to be reset.  
   @version  V0.2
   @author   ADI
   @date     September 2012
   @par Revision History:
   - V0.1, December 2010: initial version. 
   - V0.2, September 2012: Functions now CMSIS compliant
   

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/


extern int ReadRstSta(void);
extern int ClearRstSta(int iStaClr );

//Reset status.
#define RST_NONE	0	


