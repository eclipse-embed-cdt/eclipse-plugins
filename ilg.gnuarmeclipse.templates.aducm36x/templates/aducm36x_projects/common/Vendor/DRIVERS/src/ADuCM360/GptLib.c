/**
 *****************************************************************************
   @addtogroup gpt 
   @{
   @file     GptLib.c
   @brief Set of Timer peripheral functions.
   - Example:
 
   @version  V0.3
   @author   ADI
   @date     April 2013 

   @par Revision History:
   - V0.1, May 2012: initial version. 
   - V0.2, February 2013:   Fixed GptBsy().   
   - V0.3, April 2013: fixed capture event list in GptCapSrc()
   
All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/

#include "GptLib.h"
#include <ADuCM360.h>



/**
	@brief int GptCfg(ADI_TIMER_TypeDef *pTMR, int iClkSrc, int iScale, int iMode)
			==========Configures timer GPTx if not busy. 
	@param pTMR :{pADI_TM0,pADI_TM1}
		- pADI_TM0 for timer 0.
		- pADI_TM1 for timer 1.
	@param iClkSrc :{TCON_CLK_UCLK,TCON_CLK_PCLK,TCON_CLK_LFOSC,TCON_CLK_LFXTAL}
		- TxCON.5,6
		- TCON_CLK_UCLK, the timer is clocked by system clock.
		- TCON_CLK_PCLK, the timer is clocked by peripheral clock.
		- TCON_CLK_LFOSC, the timer is clocked by 32kHz clock.
		- TCON_CLK_LFXTAL, the timer is clocked by external 32kHz crystal.
	@param iScale :{TCON_PRE_DIV1,TCON_PRE_DIV16,TCON_PRE_DIV256,TCON_PRE_DIV32768}
		- TxCON.0,1
		- TCON_PRE_DIV1 for prescale of 1 (or 4 if UCLK chosen by iSrc).
		- TCON_PRE_DIV16 for prescale of 16.
		- TCON_PRE_DIV256 for prescale of 256.
		- TCON_PRE_DIV32768 for prescale of 32768.
	@param iMode :{TCON_MOD_PERIODIC|TCON_UP|TCON_RLD|TCON_ENABLE|TCON_EVENTEN}	
		- TxCON.2-4,7,12	
		- TCON_MOD_PERIODIC = 1  for the timer periodic mode. TCON_MOD_FREERUN or 0 by default.
		- TCON_UP = 1 to count down. 0 to count up. .
		- TCON_RLD = TCON_RLD_DIS or TCON_RLD_EN for reload on everflow. TCON_RLD_DIS by default.
		- TCON_ENABLE = TCON_ENABLE_DIS or TCON_ENABLE_EN to enable timer. TCON_ENABLE_DIS by default.
		- TCON_EVENT = TCON_EVENT_DIS or TCON_EVENTEN_EN to enable capture mode. TCON_EVENT_DIS by default.
	@return 0 if timer interface busy or 1 if successfull.
	
**/

int GptCfg(ADI_TIMER_TypeDef *pTMR, int iClkSrc, int iScale, int iMode)
{
  int	i1;
  if(pTMR->STA & TSTA_CON) return 0;
  i1 = pTMR->CON & TCON_EVENT_MSK; // to keep the selected event
  i1 |= iClkSrc;
  i1 |= iScale;
  i1 |= iMode;
  pTMR->CON = i1;
  return 1;	
}

/**
	@brief int GptLd(ADI_TIMER_TypeDef *pTMR, int iTLd);
			==========Sets timer reload value.
	@param pTMR :{pADI_TM0,pADI_TM1}
		- pADI_TM0 for timer 0.
		- pADI_TM1 for timer 1.
	@param iTLd :{0-65535}
		- Sets reload value TxLD to iTLd.
	@return 1.
**/

int GptLd(ADI_TIMER_TypeDef *pTMR, int iTLd)
{      
  pTMR->LD= iTLd;
  return 1;	
}


/**
	@brief int GptVal(ADI_TIMER_TypeDef *pTMR);
			==========Reads timer value.
	@param pTMR :{pADI_TM0,pADI_TM1}
		- pADI_TM0 for timer 0.
		- pADI_TM1 for timer 1.
	@return timer value TxVAL.
**/

int GptVal(ADI_TIMER_TypeDef *pTMR)
{
  return pTMR->VAL;
}

/**
	@brief int GptCapRd(ADI_TIMER_TypeDef *pTMR);
			==========Reads capture value. Allows capture of a new value.
	@param pTMR :{pADI_TM0,pADI_TM1}
		- pADI_TM0 for timer 0.
		- pADI_TM1 for timer 1.
	@return capture value TxCAP.
**/

int GptCapRd(ADI_TIMER_TypeDef *pTMR)
{
  return pTMR->CAP;
}

/**
	@brief int GptCapSrc(ADI_TIMER_TypeDef *pTMR, int iTCapSrc);
			==========Sets capture source.
	@param pTMR :{pADI_TM0,pADI_TM1}
		- pADI_TM0 for timer 0.
		- pADI_TM1 for timer 1.
	@param iTCapSrc :{T0CON_EVENT_T2, T0CON_EVENT_EXT0, T0CON_EVENT_EXT1, T0CON_EVENT_EXT2, 
                        T0CON_EVENT_EXT3, T0CON_EVENT_EXT4, T0CON_EVENT_EXT5, T0CON_EVENT_EXT6,      
                        T0CON_EVENT_EXT7, T0CON_EVENT_T3, T0CON_EVENT_T1,
                        T0CON_EVENT_ADC0, T0CON_EVENT_ADC1, T0CON_EVENT_STEP, T0CON_EVENT_FEE,
			T1CON_EVENT_COM, T1CON_EVENT_T0, T1CON_EVENT_SPI0, T1CON_EVENT_SPI1,
			T1CON_EVENT_I2CS, T1CON_EVENT_I2CM, T1CON_EVENT_DMAERR, T1CON_EVENT_DMADONE,
                        T1CON_EVENT_EXT1, T1CON_EVENT_EXT2, T1CON_EVENT_EXT3, T1CON_EVENT_PWMTRIP,
                        T1CON_EVENT_PWM0, T1CON_EVENT_PWM1, T1CON_EVENT_PWM2}
		- TxCON.8-11
                - for timer 0 capture event, select one of the following event:
			T0CON_EVENT_T2, T0CON_EVENT_EXT0, T0CON_EVENT_EXT1, T0CON_EVENT_EXT2, 
                        T0CON_EVENT_EXT3, T0CON_EVENT_EXT4, T0CON_EVENT_EXT5, T0CON_EVENT_EXT6,      
                        T0CON_EVENT_EXT7, T0CON_EVENT_T3, T0CON_EVENT_T1,
                        T0CON_EVENT_ADC0, T0CON_EVENT_ADC1, T0CON_EVENT_STEP, T0CON_EVENT_FEE         
                - for timer 1  capture event, select one of the following event: 
			T1CON_EVENT_COM, T1CON_EVENT_T0, T1CON_EVENT_SPI0, T1CON_EVENT_SPI1,
			T1CON_EVENT_I2CS, T1CON_EVENT_I2CM, T1CON_EVENT_DMAERR, T1CON_EVENT_DMADONE,
                        T1CON_EVENT_EXT1, T1CON_EVENT_EXT2, T1CON_EVENT_EXT3, T1CON_EVENT_PWMTRIP,
                        T1CON_EVENT_PWM0, T1CON_EVENT_PWM1, T1CON_EVENT_PWM2
	@return 1.
**/

int GptCapSrc(ADI_TIMER_TypeDef *pTMR, int iTCapSrc)
{
  int	i1;
  if(pTMR->STA & TSTA_CON) return 0;
  i1 = pTMR->CON & (~TCON_EVENT_MSK);
  i1 |= iTCapSrc;
  pTMR->CON = i1;
  return 1;
}

	
/**
	@brief int GptSta(ADI_TIMER_TypeDef *pTMR);
			==========Reads timer status register.
	@param pTMR :{pADI_TM0,pADI_TM1}
		- pADI_TM0 for timer 0.
		- pADI_TM1 for timer 1.
	@return TxSTA.
**/

int GptSta(ADI_TIMER_TypeDef *pTMR)
{
  return pTMR->STA;
}


/**
	@brief int GptClrInt(ADI_TIMER_TypeDef *pTMR, int iSource);
			==========clears current Timer interrupt by writing to TxCLRI.
	@param pTMR :{pADI_TM0,pADI_TM1}
		- pADI_TM0 for timer 0.
		- pADI_TM1 for timer 1.
	@param iSource :{TSTA_TMOUT,TSTA_CAP}
		- TSTA_TMOUT for time out.
		- TSTA_CAP for capture event.
	@return 1.
**/

int GptClrInt(ADI_TIMER_TypeDef *pTMR, int iSource)
{
  pTMR->CLRI = iSource;
  return 1;
}
               
/**
	@brief int GptBsy(ADI_TIMER_TypeDef *pTMR);
			==========Checks the busy bit.
	@param pTMR :{pADI_TM0,pADI_TM1}
		- pADI_TM0 for timer 0.
		- pADI_TM1 for timer 1.
@return busy bit: 0 is not busy, 1 is busy.
**/
int GptBsy(ADI_TIMER_TypeDef *pTMR)
{
  if (pTMR == pADI_TM0) return T0STA_CON_BBA;
  else return T1STA_CON_BBA;
}

/**@}*/
