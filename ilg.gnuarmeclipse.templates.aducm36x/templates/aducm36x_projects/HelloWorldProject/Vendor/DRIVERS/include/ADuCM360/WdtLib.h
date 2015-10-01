/**
 *****************************************************************************
   @file     WdtLib.h
   @brief    Set of watchdog Timer peripheral functions.
   @version  V0.2
   @author   ADI
   @date     September 2012
   @par Revision History:
   - V0.1, May 2012: initial version. 
   - V0.2, September 2012: WdtClrInt now checks if it is safe to write to T3CLRI and
            this is now a function with no inputs


All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/

extern int WdtCfg(int iPre, int iInt, int iPd);
extern int WdtGo(int iEnable);
extern int WdtLd(int iTld);
extern int WdtVal(void);
extern int WdtSta(void);
extern int WdtClrInt(void);

