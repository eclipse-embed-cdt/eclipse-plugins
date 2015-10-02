/**
 *****************************************************************************
   @file       AdcLib.h
   @brief      Set of ADC peripheral functions.
   - Start by setting ADC in idle mode with AdcGo().
   - Set up ADC with AdcRng(), AdcFlt(), AdcMski() and AdcPin().
   - Optionally use AdcBuf() AdcDiag() and AdcBias().
   - Start conversion with AdcGo().
   - Check with AdcSta() that result is available.
   - Read result with AdcRd().
   - Example:

   @version    V0.4
   @author     ADI
   @date       April 2013
   @par Revision History:
   - V0.1, November 2010: initial version. 
   - V0.2, September 2012: Fixed AdcDiag() - correct bits are modified now.
                           Fixed AdcRng().
   - V0.3, January 2013:   Fixed AdcFlt()
                           Changed AdcDetCon(), AdcDetSta(), AdcStpRd() to use
                           the pPort passed in.
   - V0.4, April 2013:     Added parameters definitions for AdcBias function

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/
#include <ADuCM360.h>

extern int AdcRng(ADI_ADC_TypeDef *pPort, int iRef, int iGain, int iCode);
extern int AdcGo(ADI_ADC_TypeDef *pPort, int iStart);
extern int AdcRd(ADI_ADC_TypeDef *pPort);
extern int AdcBuf(ADI_ADC_TypeDef *pPort, int iRBufCfg, int iBufCfg);
extern int AdcDiag(ADI_ADC_TypeDef *pPort, int iDiag);
extern int AdcBias(ADI_ADC_TypeDef *pPort, int iBiasPin, int iBiasBoost, int iGndSw);
extern int AdcPin(ADI_ADC_TypeDef *pPort, int iInN, int iInP);
extern int AdcFlt(ADI_ADC_TypeDef *pPort, int iSF, int iAF, int iFltCfg);
extern int AdcMski(ADI_ADC_TypeDef *pPort, int iMski, int iWr);
extern int AdcSta(ADI_ADC_TypeDef *pPort);
//extern int AdcDma(ADI_ADC_TypeDef *pPort, int iDmaRdWr);
extern int AdcPGAErr(ADI_ADC_TypeDef *pPort, int iAdcSta);
extern int AdcDetSta(ADI_ADCSTEP_TypeDef *pPort);
extern int AdcDetCon(ADI_ADCSTEP_TypeDef *pPort, int iCtrl, int iAdcSel, int iRate );
extern int AdcStpRd(ADI_ADCSTEP_TypeDef *pPort);
extern int AdcDmaCon(int iChan, int iEnable);


	
//ADC filter extra features disabled
#define	FLT_NORMAL		0
//VBias boost.
#define	ADC_BIAS_X1		0
#define	ADC_BUF_ON		0
#define	ADC_GND_OFF     0
#define	ADC_BIAS_OFF    0


#define	DETCON_RATE_2ms 0
#define	DETCON_RATE_4ms 1
#define	DETCON_RATE_6ms 2
#define	DETCON_RATE_8ms 3


