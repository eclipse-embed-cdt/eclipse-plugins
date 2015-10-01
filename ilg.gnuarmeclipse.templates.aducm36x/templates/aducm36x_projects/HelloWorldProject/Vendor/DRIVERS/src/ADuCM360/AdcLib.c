/**
 *****************************************************************************
   @addtogroup adc 
   @{
   @file       AdcLib.c
   @brief      Set of ADC peripheral functions.
   - Start by setting ADC in idle mode with AdcGo().
   - Set up ADC with AdcRng(), AdcFlt(), AdcMski() and AdcPin().
   - Optionally use AdcBuf() AdcDiag() and AdcBias().
   - Start conversion with AdcGo().
   - Check with AdcSta() that result is available.
   - Read result with AdcRd().
   - Example:

   @version    V0.5
   @author     ADI
   @date       April 2013
   @par Revision History:
   - V0.1, November 2010: initial version. 
   - V0.2, September 2012: Fixed AdcDiag() - correct bits are modified now.
                           Fixed AdcRng().
   - V0.3, January 2013:   Fixed AdcFlt()
                           Changed AdcDetCon(), AdcDetSta(), AdcStpRd() to use
                           the pPort passed in.
   - V0.4, April 2013:   Changed AdcDetCon(). 
                         Removed BOOST15 option.
                         Fixed doxygen comments

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/


#include	"AdcLib.h"
#include <ADuCM360.h>
#include "DmaLib.h"

/**
	@brief int AdcRng(ADI_ADC_TypeDef *pPort, int iRef, int iGain, int iCode)
			==========Sets ADC measurement range.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iRef :{ADCCON_ADCREF_INTREF, ADCCON_ADCREF_EXTREF, ADCCON_ADCREF_EXTREF2, ADCCON_ADCREF_AVDDREF}
		- ADCxCON.12,13
		- 0 or ADCCON_ADCREF_INTREF for internal reference.
		- 1 or ADCCON_ADCREF_EXTREF for external reference.
		- 2 or ADCCON_ADCREF_EXTREF2 for external reference.
		- 3 or ADCCON_ADCREF_AVDDREF for AVDD reference.
	@param iGain :{ADCMDE_PGA_G1,ADCMDE_PGA_G1+ADCMDE_ADCMOD2,ADCMDE_PGA_G2,ADCMDE_PGA_G2+ADCMDE_ADCMOD2,ADCMDE_PGA_G4,ADCMDE_PGA_G4+ADCMDE_ADCMOD2,
	ADCMDE_PGA_G8,ADCMDE_PGA_G8+ADCMDE_ADCMOD2,ADCMDE_PGA_G16,ADCMDE_PGA_G16+ADCMDE_ADCMOD2,ADCMDE_PGA_G32,ADCMDE_PGA_G32+ADCMDE_ADCMOD2,
	ADCMDE_PGA_G64,ADCMDE_PGA_G64+ADCMDE_ADCMOD2,ADCMDE_PGA_G128,ADCMDE_PGA_G128+ADCMDE_ADCMOD2}
		- ADCxMDE.4-7
		- 0 or ADCMDE_PGA_G1 for gain of 1.
		- 1 or ADCMDE_PGA_G2 for gain of 2.
		- 2 or ADCMDE_PGA_G4 for gain of 4.
		- 3 or ADCMDE_PGA_G8 for gain of 8.
		- 4 or ADCMDE_PGA_G16 for gain of 16.
		- 5 or ADCMDE_PGA_G32 for gain of 32.
		- 6 or ADCMDE_PGA_G64 for gain of 64.
		- 7 or ADCMDE_PGA_G128 for gain of 128.
		- 8 or ADCMDE_ADCMOD2_MOD2ON for extra Modulator gain of 2
	@param iCode :{ADCCON_ADCCODE_INT,ADCCON_ADCCODE_UINT}
		- ADCxCON.18
		- 0 or ADCCON_ADCCODE_INT for bipolar (C type int) result.
		- 1 or ADCCON_ADCCODE_UINT to truncate negative values to 0.
	@return 1.
	@warning None
**/
int AdcRng(ADI_ADC_TypeDef *pPort, int iRef, int iGain, int iCode)
	{
	int	i1 = 0;
		
  i1 = pPort->MDE&0xff07;
	pPort->MDE = i1|((iGain&0xf8));
	i1 = pPort->CON&0xfffcfff;
	if(iCode)		i1 |= 1<<18;
	pPort->CON = i1|((iRef&0x3000));
	return 1;
	}

/**
	@brief int AdcFlt(ADI_ADC_TypeDef *pPort, int iSF, int iAF, int iFltCfg)
			==========Sets filter details.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iSF :{0-127}
		- Set iSF to desired Sinc Decimation factor:
	@param iAF :{0-15}	ADCxFLT.8-11.
		- Set iAF to desired averaging factor:
	@param iFltCfg :{FLT_NORMAL|ADCFLT_NOTCH2|ADCFLT_SINC4EN|ADCFLT_RAVG2|ADCFLT_CHOP}
	- ADCxFLT.7,12,14,15
	- Combination of the following features :
		- 0 or FLT_NORMAL for no extra filter settings.
		- 0x80 or ADCFLT_NOTCH2 for second notch.
		- 0x1000 or ADCFLT_SINC4EN for second notch.
		- 0x4000 or ADCFLT_RAVG2 for an average by 2.
		- 0x8000 or ADCFLT_CHOP to enable choping.
	@return ADCxFLT MMR.  Not normally of interest.
	@note See the Digital Filter Response Model on the product page if required.
**/
int AdcFlt(ADI_ADC_TypeDef *pPort, int iSF, int iAF, int iFltCfg)
	{
	int	i1 = 0;
		
	i1 = (iSF&0x7f)|((iAF << 8) & 0xf00)|(iFltCfg&0xD080);
	pPort->FLT = i1;
	return pPort->FLT;
	}

/**
	@brief int AdcGo(ADI_ADC_TypeDef *pPort, int iStart)
			==========Start ADC conversion.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iStart :{ADCMDE_ADCMD_OFF,ADCMDE_ADCMD_CONT,ADCMDE_ADCMD_SINGLE,ADCMDE_ADCMD_IDLE,ADCMDE_ADCMD_INTOCAL,ADCMDE_ADCMD_INTGCAL,ADCMDE_ADCMD_SYSOCAL,ADCMDE_ADCMD_SYSGCAL}
		- ADCxMDE.0-2,
		- 0 or ADCMDE_ADCMD_OFF for power down.
		- 1 or ADCMDE_ADCMD_CONT for continuous conversion.
		- 2 or ADCMDE_ADCMD_SINGLE for Single conversion.
		- 3 or ADCMDE_ADCMD_IDLE for idle mode.
		- 4 or ADCMDE_ADCMD_INTOCAL for offset calibration.
		- 5 or ADCMDE_ADCMD_INTGCAL for gain calibration.
		- 6 or ADCMDE_ADCMD_SYSOCAL for system offset calibration.
		- 7 or ADCMDE_ADCMD_SYSGCAL for system gain calibration.
	@return 1.
	@warning  Up to 3 conversion results after the ADC had been powered
		up are not accurate.
**/

int AdcGo(ADI_ADC_TypeDef *pPort, int iStart)
	{
	int	i1 = 0;
	i1 = pPort->CON;
	if(iStart)		i1 |= ADCCON_ADCEN;
	else		i1 &= ~ADCCON_ADCEN; 	
	pPort->CON |= i1;
	i1 = pPort->MDE&0xFFFF8;
	pPort->MDE = i1|(iStart&7);
	return 1;
	}

/**
	@brief int AdcSta(ADI_ADC_TypeDef  *pPort)
			==========Reads the ADC status.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@return ADCxSTA:
		- bit0 or ADCxSTA_RDY => New ADCn data available.
		- bit1 or ADCxSTA_OVR => Overrange.
		- bit2 or ADCxSTA_THEX => Threshold exceeded.
		- bit3 or ADCxSTA_ATHEX => Accumulator threshold exceeded.
		- bit4 or ADCxSTA_ERR => Conversion error.
		- bit4 or ADCxSTA_CAL => Calibration data available.
**/

int AdcSta(ADI_ADC_TypeDef *pPort)
	{
	   return pPort->STA;
	}
/**
	@brief int AdcDetSta(ADI_ADCSTEP_TypeDef  *pPort)
			==========Reads the ADC status.
	@param pPort :{pADI_ADCSTEP,}
		- pADI_ADCSTEP.
	@return DETSTA:
		- bit0 or DETSTA_STEPDATRDY => New STEPDAT data available.
		- bit1 or DETSTA_STEPFLAG => Step threshold exceeded.
		- bit2 or DETSTA_STEPERR => Overrange detected.
		- bit3 or DETSTA_DATOF => Sinc2 data output result overwrite.
		- bit4 or DETSTA_REFSTA => External reference detection error.
**/

int AdcDetSta(ADI_ADCSTEP_TypeDef *pPort)
	{
		return pPort->DETSTA;
	}



/**
	@brief int AdcRd(ADI_ADC_TypeDef *pPort)
			==========Reads the ADC status.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@return ADC data (ADCxDAT).  MSb of data is bit 27.  Bits 28 to 31
		give extended sign.	\n
		Value of 0x0fffffff => VRef.	\n
		Value of 0x00000000 => 0V.		\n
		Value of 0xF0000000 => -Vref.	\n
	@warning	Returns ADCxDAT even if it does not contain new data.
**/


int AdcRd(ADI_ADC_TypeDef *pPort)
	{
	   return pPort->DAT;
	}
 
/**
	@brief int AdcStpRd(ADI_ADCSTEP_TypeDef *pPort)
			==========Reads the ADC status.
	@param pPort :{pADI_ADCSTEP,}
		Set to pADI_ADCSTEP. Only one channel available.
	@return STEPDAT
	@warning	Returns STEPDAT even if it does not contain new data.
**/

int AdcStpRd(ADI_ADCSTEP_TypeDef *pPort)
	{
	return pPort->STEPDAT;
	}
/**
	@brief int AdcBuf(ADI_ADC_TypeDef *pPort, int iBufCfg, int iRBufCfg)
			==========Configures ADC buffers.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iRBufCfg :{ADCCFG_EXTBUF_OFF| ADCCFG_EXTBUF_VREFPN| ADCCFG_EXTBUF_VREFP_VREF2P}
	- ADCxCFG.0-1
	- Combination of the following features :
		- 0 or ADCCFG_EXTBUF_OFF - Both external reference buffers powered down.
		- 1 or ADCCFG_EXTBUF_VREFPN. VREF+ and VREF- inputs selected as buffer inputs
		- 2 or ADCCFG_EXTBUF_VREFP_VREF2P. VREF+ and EXT2_REFIN+ inputs selected as buffer inputs
	@param iBufCfg :{ADC_BUF_ON| ADCCON_BUFBYPN| ADCCON_BUFBYPP| ADCCON_BUFPOWP| ADCCON_BUFPOWN}
	- ADCxCON.14-17
	- Combination of the following features :
		- 0 or ADC_BUF_ON for both reference buffers on.
		- 1 or ADCCON_BUFBYPN to bypass negative reference buffer.
		- 2 or ADCCON_BUFBYPP to bypass positive reference buffer.
		- 4 or ADCCON_BUFPOWP to power down positive reference buffer.
		- 8 or ADCCON_BUFPOWN to power down negative reference buffer.
	@return 1.
**/

int AdcBuf(ADI_ADC_TypeDef *pPort, int iRBufCfg, int iBufCfg)
	{
	int	i1 = 0;

    i1 = pPort->CON&0x000C3fff;		// Mask off bits 17:14
	i1 |= (iBufCfg&0x3C000);
    pPort->CON = i1;
    i1 = (pPort->ADCCFG&0x3FC0);
	i1 |= (iRBufCfg&0xf);
    pPort->ADCCFG = i1;
 	return 1;
	}

/**
	@brief int AdcDiag(ADI_ADC_TypeDef *pPort, int iDiag)
			==========Applies diagnostic currents on ADC pins.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iDiag :{ADCCON_ADCDIAG_DIAG_OFF, ADCCON_ADCDIAG_DIAG_POS, ADCCON_ADCDIAG_DIAG_NEG, ADCCON_ADCDIAG_DIAG_ALL}
		- 0 or ADCCON_ADCDIAG_DIAG_OFF to switch off both diagnostic currents.
		- 1 or ADCCON_ADCDIAG_DIAG_POS to switch of on positive diagnostic current.
		- 2 or ADCCON_ADCDIAG_DIAG_NEG to switch of on positive diagnostic current.
		- 3 or ADCCON_ADCDIAG_DIAG_ALL to switch both diagnostic currents on
	@return 1.
**/
int AdcDiag(ADI_ADC_TypeDef *pPort, int iDiag)
	{
	int	i1 = 0;
	
        i1 = pPort->CON;
        i1 &= 0xFF3FF;
	pPort->CON = i1|(iDiag&0xC00);
	return 1;
	}

/**
	@brief int AdcPin(ADI_ADC_TypeDef *pPort, int iInN, int iInP)
			==========Reads ADC data.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iInN :{ADCCON_ADCCN_AIN0,ADCCON_ADCCN_AIN1,ADCCON_ADCCN_AIN2,ADCCON_ADCCN_AIN3,ADCCON_ADCCN_AIN4,ADCCON_ADCCN_AIN5,ADCCON_ADCCN_AIN6,ADCCON_ADCCN_AIN7,
				ADCCON_ADCCN_AIN8,ADCCON_ADCCN_AIN9,ADCCON_ADCCN_AIN10,ADCCON_ADCCN_AIN11,ADCCON_ADCCN_AGND,ADCCON_ADCCN_TEMP}
		- ADCxCON.0-4
			- 0 to 11 or ADCCON_ADCCN_AIN0 to ADCCON_ADCCN_AIN11 for input pins AIN0 to AIN11
			- or 17 or ADCCON_ADCCN_TEMP for temp sensor.	\n
		For ADC0 additional option is:
			- 28 or AIN0OFF for off.	\n
		For ADC1 additional options are:
			- 12 or ADCCON_ADCCP_DAC for DAC output
			- 13 or ADCCON_ADCCP_AVDD4 for AVdd/4
			- 14 or ADCCON_ADCCP_IOVDD4 for IODD/4
			- 15 or ADCCON_ADCCP_AGND for AGND
			- 16 or ADCCON_ADCCP_TEMP for temp sensor.
	@param iInP :{ADCCON_ADCCP_AIN0,ADCCON_ADCCP_AIN1,ADCCON_ADCCP_AIN2,ADCCON_ADCCP_AIN3,ADCCON_ADCCP_AIN4,ADCCON_ADCCP_AIN5,ADCCON_ADCCP_AIN6,ADCCON_ADCCP_AIN7,
				ADCCON_ADCCP_AIN8,ADCCON_ADCCP_AIN9,ADCCON_ADCCP_AIN10,ADCCON_ADCCP_AIN11,ADCCON_ADCCP_DAC, ADCCON_ADCCP_AVDD4, ADCCON_ADCCP_IOVDD4,
				ADCCON_ADCCN_AGND,ADCCON_ADCCP_TEMP}
		- ADCxCON.5-9
			- Choices are the same as for iInN.
			- Ensure parameter << 5 to populate ADCxCON[9:5] bits
	@return 1.
**/

int AdcPin(ADI_ADC_TypeDef *pPort, int iInN, int iInP)
	{
	int	i1 = 0;

	i1 = pPort->CON&0x000fffc00;
	i1 |= ((iInP)&0x3E0);
	i1 |= iInN&0x1f;
	pPort->CON = i1;
	return 1;
	}

/**
	@brief int AdcMski(ADI_ADC_TypeDef *pPort, int iMski, int iWr)
			==========Masks in dividual ADC interupt flags.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iMski :{ADCMSKI_RDY|ADCMSKI_OVR|ADCMSKI_THEX|ADCMSKI_ATHEX}
	- ADCxMSKI.0-3
	- Choose bitwise or combination of the following.
		- 0 or ADC_M_NONE => To mask no ADC interupts.
		- 1 or ADCMSKI_RDY => Mask ready interupt.
		- 2 or ADCMSKI_OVR => Mask overrange interupt.
		- 4 or ADCMSKI_THEX => Mask threshold exceeded interupt.
		- 8 or ADCMSKI_ATHEX => Mask accumulator threshold exceeded interupt.
	@param iWr :{0,1}
		- 0 to read ADCnMSKI only.
		- 1 to write masks then read ADCnMSKI.
	@return ADCnMSKI:
		- bit0 or ADCMSKI_RDY => Mask ready interupt.
		- bit1 or ADCMSKI_OVR => Mask overrange interupt.
		- bit2 or ADCMSKI_THEX => Mask threshold exceeded interupt.
		- bit3 or ADCMSKI_ATHEX => Mask accumulator threshold exceeded interupt.
**/

int AdcMski(ADI_ADC_TypeDef *pPort, int iMski, int iWr)
	{
	if(iWr)	  	pPort->MSKI = iMski;
	return 	pPort->MSKI;
	}

/**
	@brief int AdcBias(ADI_ADC_TypeDef *pPort, int iBiasPin, int iBiasBoost, int iGndSw)
			==========Adds bias and ground switch to ADC input.
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iBiasPin :{ADC_BIAS_OFF, ADCCFG_PINSEL_AIN7, ADCCFG_PINSEL_AIN11}
	- ADC0CFG.8-10
		- 0 or ADC_BIAS_OFF to switch off bias.
		- 1024 or ADCCFG_PINSEL_AIN7 to switch bias current to AIN7.
		- 1536 or ADCCFG_PINSEL_AIN11 to switch bias current to AIN11.
	@param iBiasBoost :{ADC_BIAS_X1, ADCCFG_BOOST30}
	- ADC0CFG.12
		- 0 or ADC_BIAS_X1 for normal Vbias.
		- 8192 or ADCCFG_BOOST30 to boost Vbias by 30 times.
	@param iGndSw :{ADC_GND_OFF| ADCCFG_GNDSWON| ADCCFG_GNDSWRESEN}
	- ADC0CFG.4-7
		- 0 or ADC_GND_OFF to open ground switch.
		- 128 or ADCXCFG_GNDSWON to close ground switch.
		- 64 or ADCXCFG_GNDSWRESEN to close ground switch in series with 20k Ohm.
	@return 1.
**/

int AdcBias(ADI_ADC_TypeDef *pPort, int iBiasPin, int iBiasBoost, int iGndSw)
	{
	int	i1 = 0;
	i1 = pPort->ADCCFG&0x000001f;
	i1 += (iBiasPin&0x700);
	i1 += (iBiasBoost&0x3000);
	pPort->ADCCFG = i1|(iGndSw&0xC0);
	return 1;
	}
/**
	@brief int AdcPGAErr(ADI_ADC_TypeDef *pPort, int iAdcSta)
			==========Handles PGA threshold exceeded Error
	@param pPort :{pADI_ADC0, pADI_ADC1}
		- pADI_ADC0 for ADC0.
		- pADI_ADC1 for ADC1.
	@param iAdcSta :{ADC0STA_OVR, ADC0STA_THEX, ADC0STA_ATHEX, ADC0STA_ERR}
	- ADCxSTA.1-4
		- 2 or ADC0STA_OVR. ADC Gross overrange error bit. Too coarse to be used
		- 4 or ADC0STA_THEX. ADC Comparator threshold exceeded status
		- 8 or ADC0STA_ATHEX. ADC Accumulator Comparator threshold exceeded status
		- 16 or AD0CSTA_ERR. ADC Error status
	@return 1.
**/

int AdcPGAErr(ADI_ADC_TypeDef *pPort, int iAdcSta)
	{
	if((iAdcSta&0x4) == 0x4)
		{
	 	pPort->MSKI |= 0x4;
		pPort->PRO = 4;
		}
	if((iAdcSta&0x8) == 0x8)
		{
	 	pPort->MSKI |= 0x8;
		pPort->PRO = 0x30;				// Enable 1V Comparator + Accumulator
		}
	return 1;
	}


   
/**
	@brief int AdcDetCon(ADI_ADCSTEP_TypeDef *pPort, int iCtrl, int iAdcSel, int iRate )	
	 			==========Setup DETCON register.	
	@param pPort :{pADI_ADCSTEP,}	
			- pADI_ADCSTEP.
	@param iCtrl :{0|DETCON_SINC2_EN|DETCON_REFDET}
		- 0 or SINC2 and Reference detection off
		- 0x80 or DETCON_SINC2_EN enabled.
		- 0x100 or DETCON_REFDET. Reference detection enabled
	@param iAdcSel :{0,DETCON_ADCSEL}
		- 0. ADC0 is the SINC2 filter source
		- 0x4 or DETCON_ADCSEL. ADC1 is the SINC2 filter source
	@param iRate :{DETCON_RATE_2ms, DETCON_RATE_4ms, DETCON_RATE_6ms, DETCON_RATE_8ms}
		- 0 or DETCON_RATE_2ms. SINC2 filter update rate is 2mS
		- 0x1 or DETCON_RATE_4ms. SINC2 filter update rate is 4mS		
		- 0x2 or DETCON_RATE_6ms. SINC2 filter update rate is 6mS
		- 0x3 or DETCON_RATE_8ms. SINC2 filter update rate is 8mS
	@return 1:

**/
int AdcDetCon(ADI_ADCSTEP_TypeDef *pPort, int iCtrl, int iAdcSel, int iRate )
{
   int	i1 = 0;

   i1 |= (iCtrl|iAdcSel|iRate); 
   pPort->DETCON = i1;  
   return 1;
}
/**
	@brief int AdcDmaCon(int iChan, int iEnable)	
	 			==========First part of setting up ADC DMA channel structure	
	@param iChan :{ADC0DMAWRITE,ADC0DMAREAD,ADC1DMAWRITE,ADC1DMAREAD,SINC2DMAREAD}	
			- 0 or ADC0DMAWRITE for ADC0 DMA write to control registers
      - 1 or ADC0DMAREAD for ADC0 DMA reading of ADC0 data outputs
      - 2 or ADC1DMAWRITE for ADC1 DMA write to control registers
      - 3 or ADC1DMAREAD for ADC1 DMA reading of ADC1 data outputs
      - 4 or SINC2DMAREAD for SINC2 DMA reading of SINC2 data outputs
	@param iEnable :{0,1}
		- 0 to disable channel
		- 1 to enable
	@return 1:

**/
int AdcDmaCon(int iChan, int iEnable)
{
	switch (iChan)
	{
     case ADC0DMAWRITE:
     if (iEnable == 0)			
		 {
			 pADI_ADCDMA->ADCDMACON &= 0x1E; // disable ADC0 DMA channel
     }			 
		 else
		 {
			 pADI_ADCDMA->ADCDMACON |= 0x1; // Enable ADC0 DMA write channel
		 }
		 break;
		 
		 case ADC0DMAREAD:
		 if (iEnable == 0)			
		 {
			 pADI_ADCDMA->ADCDMACON &= 0x1E; // disable ADC0 DMA channel
     }			 
		 else
		 {
			 pADI_ADCDMA->ADCDMACON |= 0x3; // Enable ADC0 DMA read channel
		 }
		 break;
		 
		 case ADC1DMAWRITE:
     if (iEnable == 0)			
		 {
			 pADI_ADCDMA->ADCDMACON &= 0x1B; // disable ADC1 DMA channel
     }			 
		 else
		 {
			 pADI_ADCDMA->ADCDMACON |= 0x4; // Enable ADC1 DMA write channel
		 }
		 break;
		 
		 case ADC1DMAREAD:
		if (iEnable == 0)			
		 {
			 pADI_ADCDMA->ADCDMACON &= 0x1B; // disable ADC1 DMA channel
     }			 
		 else
		 {
			 pADI_ADCDMA->ADCDMACON |= 0xC; // Enable ADC1 DMA read channel
		 }
		 break;
		 
		case SINC2DMAREAD:
		if (iEnable == 0)			
		 {
			 pADI_ADCDMA->ADCDMACON &= 0xF; // disable SINC2 DMA channel
     }			 
		 else
		 {
			 pADI_ADCDMA->ADCDMACON |= 0x10; // Enable SINC2 DMA read channel
		 }
		 break;
		 default:
			 break;
	 }	
return 1;	 
}


/**@}*/
