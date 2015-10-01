/**
 *****************************************************************************
   @file     PwrLib.h
   @brief    Functions for controling power modes
   @version  V0.1
   @author   ADI
   @date     September 2012 


All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.
		
**/
#include <ADuCM360.h>

extern int PwrCfg(int iMode);
extern int PwrRead(void);

