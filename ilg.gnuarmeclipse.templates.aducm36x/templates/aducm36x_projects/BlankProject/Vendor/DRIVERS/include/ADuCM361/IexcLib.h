/**
 *****************************************************************************
   @file     IexcLib.h
   @brief    Set of Excitation Current Source functions.
   - Configure Excitation Currents with IexcCfg().
   - Select output current with IexcDat()

   @version  V0.2
   @author   ADI
   @date     January 2013
   @par Revision History:
   - V0.1, March 2011: initial version. 
   - V0.2, January 2013: Fixed IexcDat() - current outputs are all correct.

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/

int IexcCfg(int iPd, int iRefsel, int iPinsel1, int iPinsel0);
int IexcDat(int iIDAT, int iIDAT0);
		
#define IDAT0En   1
#define IDAT0Dis  0


