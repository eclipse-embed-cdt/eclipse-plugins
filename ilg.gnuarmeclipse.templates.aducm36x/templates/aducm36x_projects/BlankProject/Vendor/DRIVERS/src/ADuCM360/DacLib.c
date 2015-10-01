/**
 *****************************************************************************  
   @addtogroup dac 
   @{
   @file       DacLib.c
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

#include	"DacLib.h"
#include <ADuCM360.h>


/**
	@brief int DacWr(int iChan, int iData)
			==========Writes the DAC value.
	@param iChan :{0,}	\n
		Set to 0. This value is ignored since there is only one channel.
	@param iData :{}	\n
		- DACDAT
		- Data to output to DAC.
	@return DAC data.
**/
int DacWr(int iChan, int iData)
	{
	pADI_DAC->DACDAT = iData;
	return pADI_DAC->DACDAT;
	}

/**
	@brief int DacCfg(int iDisable, int iRef, int iDrv, int iMode)
			==========Sets the output range of a DAC.
	@param iDisable :{DACCON_CLR_Off, DACCON_CLR_On}
		- 0 or DACCON_CLR_On to Disable DAC output.
		- 16 or DACCON_CLR_Off to Enable DAC output
	@param iRef :{DACCON_RNG_IntVref, DACCON_RNG_AVdd}
		- DACCON.0,1
		- 0 or DACCON_RNG_IntVref to use internal as reference.
		- 3 or DACCON_RNG_AVdd	to use AVDD reference.
	@param iDrv :{DACCON_CLK_HCLK|DACCON_CLK_Timer1| DACCON_BUFBYP| DACCON_NPN}
		- DACCON.5,6,8
		- 0 or DACCON_CLK_HCLK to use HCLK to update DAC output.
		- 32 or DACCON_CLK_Timer1 to use Timer 1 to update DAC output.
		- 64 or DACCON_BUFBYP   	for buffered voltage output.
		- 256 or DACCON_NPN		for NPN mode.
	@param iMode :{DACCON_MDE_12bit, DACCON_MDE_16BitSlow, DACCON_MDE_16BitFast, DACCON_PD}
		- DACCON.2,3
		- 0 or DACCON_MDE_12bit		for 12 bit mode.
		- 8 or DACCON_MDE_16BitFast	for Fast 16 bit mode.
		- 12 or DACCON_MDE_16BitSlow for Slow 16 bit mode.
		- 512 or DACCON_PD	to Power Down DAC output
	@return new DACCON.
**/
	
int DacCfg(int iDisable, int iRef, int iDrv, int iMode)
	{
	int	i1;
	
	if((iMode&0x200) == 0x200) 
	{
		i1 = 0x200;				//Power down.
		pADI_DAC->DACCON = i1;
		return pADI_DAC->DACCON;
	}
	else
	{
	  i1 = iRef + iDrv + iDisable + iMode;
	}
	
	pADI_DAC->DACCON = i1;
	return pADI_DAC->DACCON;
	}


/**
	@brief int DacSync(int iChan, int iSync, int iTime)
			==========Sets DAC sync mode.
	@param iChan :{0,}	\n
		Set to 0. This value is ignored since there is only one channel.
	@param iSync :{0,1}
		- DACCON.5
		- 0 to to set immediate mode.
		- 1 to set sync mode using T1.
	@param iTime :(0-2147483548)	\n
		- T1LD
		- Set sync period in micro seconds assuming 1MHz clock.
	@return time set.
	@note
		Resolution up to 65535 is 4 counts.	\n
		Resolution up to 65536 to 262143 is 16 counts.	\n
		Resolution up to 262144 to 16777215 is 256 counts.	\n
		Resolution up to 16777216 to 2147483548 is 32768 counts.
	@warning
		Uses T1.
**/
int DacSync(int iChan, int iSync, int iTime)
	{
	int i1;
	
	if(iTime>=0x1000000)
		{
		iTime /= 0x8000;
		i1 = 3;
		}
	else if(iTime>=0x100000)
		{
		iTime /= 0x100;
		i1 = 2;
		}
	else if(iTime>=0x40000)
		{
		iTime /= 0x10;
		i1 = 1;
		}
	else
		{
		iTime /= 0x04;
		i1 = 0;
		}
	i1 |= 0x18;				//Enabled with periodic down mode UCLK/4.
//	i1 |= 0x19;				//Enabled with periodic down mode UCLK/16.
	pADI_TM1->LD = iTime;
	pADI_TM1->CON = i1;
	i1 = pADI_DAC->DACCON;
	i1 &= 0xfdf;
	if(iSync)	i1 |= 0x20;
	pADI_DAC->DACCON = i1;
	return pADI_DAC->DACCON;
	}

/**
	@brief int DacDma(int iChan, int iDmaSel)
			==========Enables/Disables DAC DMA.
	@param iChan :{0,}
		Set to 0. This value is ignored since there is only one channel.
	@param iDmaSel :{DACCON_DMAEN_Off,DACCON_DMAEN_On}
		- 0 or DACCON_DMAEN_Off to disable DAC DMA chanel.
		- 1 or DACCON_DMAEN_On to enable DAC DMA channel.
	@return 1.
*/

int DacDma(int iChan, int iDmaSel)
	{
	if(iDmaSel)	pADI_DAC->DACCON |= 1<<10;
	else	pADI_DAC->DACCON &= ~(1<<10);
	return 1;
	}


/**@}*/
