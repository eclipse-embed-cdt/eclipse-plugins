/**
 *****************************************************************************
   @file     GptLib.h
   @brief Set of Timer peripheral functions.
   - Example:

   @version  V0.1
   @author   ADI
   @date     May 2012 

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/
#include <ADuCM360.h>

extern int GptCfg(ADI_TIMER_TypeDef *pTMR, int iClkSrc, int iScale, int iMode);
extern int GptLd(ADI_TIMER_TypeDef *pTMR, int iTLd);
extern int GptVal(ADI_TIMER_TypeDef *pTMR);
extern int GptCapRd(ADI_TIMER_TypeDef *pTMR);
extern int GptCapSrc(ADI_TIMER_TypeDef *pTMR, int iTCapSrc);
extern int GptSta(ADI_TIMER_TypeDef *pTMR);
extern int GptClrInt(ADI_TIMER_TypeDef *pTMR, int iSource);
extern int GptBsy(ADI_TIMER_TypeDef *pTMR);


