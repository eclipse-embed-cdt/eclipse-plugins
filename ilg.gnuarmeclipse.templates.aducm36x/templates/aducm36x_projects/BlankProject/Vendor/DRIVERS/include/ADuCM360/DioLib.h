/**
 *****************************************************************************
   @file     DioLib.h
   @brief    Set of Digital IO peripheral functions.
   @version  V0.3
   @author   ADI
   @date     November 2012 
          
   @par Revision History:
   - V0.1, May 2012: initial version. 
   - V0.2, October 2012: comment correction, 
                         addition of pin configuration functions,
                         addition of Tristate functions. 
   - V0.3, November 2012: several comment corrections.

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

*****************************************************************************/
#include "ADuCM360.h"

#define PIN0 0x0 
#define PIN1 0x1 
#define PIN2 0x2 
#define PIN3 0x3 
#define PIN4 0x4 
#define PIN5 0x5 
#define PIN6 0x6 
#define PIN7 0x7

// port configuration
extern int DioCfg(ADI_GPIO_TypeDef *pPort, int iMpx);
extern int DioDrv(ADI_GPIO_TypeDef *pPort, int iOen, int iPul, int iOce);
extern int DioOen(ADI_GPIO_TypeDef *pPort, int iOen);
extern int DioPul(ADI_GPIO_TypeDef *pPort, int iPul);
extern int DioOce(ADI_GPIO_TypeDef *pPort, int iOce);
extern int DioTriState(ADI_GPIO_TypeDef *pPort); // new 

// pin configuration
extern int DioCfgPin(ADI_GPIO_TypeDef *pPort, int iPin, int iMode);  // new
extern int DioOenPin(ADI_GPIO_TypeDef *pPort, int iPin, int iOen);   // new
extern int DioPulPin(ADI_GPIO_TypeDef *pPort, int iPin, int iPul);   // new
extern int DioOcePin(ADI_GPIO_TypeDef *pPort, int iPin, int iOce);   // new
extern int DioTriStatePin(ADI_GPIO_TypeDef *pPort, int iPin);        // new

extern int DioRd(ADI_GPIO_TypeDef *pPort);
extern int DioWr(ADI_GPIO_TypeDef *pPort, int iVal);
extern int DioSet(ADI_GPIO_TypeDef *pPort, int iVal);
extern int DioClr(ADI_GPIO_TypeDef *pPort, int iVal);
extern int DioTgl(ADI_GPIO_TypeDef *pPort, int iVal);




