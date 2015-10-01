/**
 *****************************************************************************
   @addtogroup iexc 
   @{
   @file     IexcLib.c
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


#include	"IexcLib.h"
#include <ADuCM360.h>

/**
	@brief int IexcCfg(int iPd, int iRefsel, int iPinsel1, int iPinsel0)
			==========Configures clock system.
	@param iPd :{IEXCCON_PD_En, IEXCCON_PD_off}
		- 128 or IEXCCON_PD_En to Power down Excitation Current source block.
		- 0 or IEXCCON_PD_Off to enable Excitation Current source block.
	@param iRefsel :{IEXCCON_REFSEL_Ext,IEXCCON_REFSEL_Int}
		- 64 or IEXCCON_REFSEL_Int to select Internal Current reference resistor
		- 0 or  IEXCCON_REFSEL_Ext to select External Current reference resistor
	@param iPinsel1 :{IEXCCON_IPSEL1_Off, IEXCCON_IPSEL1_AIN4, IEXCCON_IPSEL1_AIN5, IEXCCON_IPSEL1_AIN6, IEXCCON_IPSEL1_AIN7}
		- 0 or IEXCCON_IPSEL1_Off to disable IEXC1
		- 32 or IEXCCON_IPSEL1_AIN4 to output IEXC1 to AIN4.
		- 40 or IEXCCON_IPSEL1_AIN5 to output IEXC1 to AIN5.
		- 48 or IEXCCON_IPSEL1_AIN6 to output IEXC1 to AIN6.
		- 56 or IEXCCON_IPSEL1_AIN7 to output IEXC1 to AIN7.
	@param iPinsel0 :{IEXCCON_IPSEL0_Off, IEXCCON_IPSEL0_AIN4, IEXCCON_IPSEL0_AIN5, IEXCCON_IPSEL0_AIN6, IEXCCON_IPSEL0_AIN7}
		- 0 or IEXCCON_IPSEL0_Off to disable IEXC0
		- 4 or IEXCCON_IPSEL0_AIN4 to output IEXC0 to AIN4.
		- 5 or IEXCCON_IPSEL0_AIN5 to output IEXC0 to AIN5.
		- 6 or IEXCCON_IPSEL0_AIN6 to output IEXC0 to AIN6.
		- 7 or IEXCCON_IPSEL0_AIN7 to output IEXC0 to AIN7.
	@return - 0 if not busy

**/

int IexcCfg(int iPd, int iRefsel, int iPinsel1, int iPinsel0)
	{
	int	i1;
	
	i1 = iPd & 0x80;
	i1 |= (iRefsel & 0x40);
	i1 |= iPinsel1 ;
	i1 |= iPinsel0;
	pADI_ANA->IEXCCON = i1;

	return 1;
	}
/**
	@brief  int IexcDat(int iIDAT, int iIDAT0)
			==========Sets Excitation Current output value.
	@param iIDAT :{IEXCDAT_IDAT_0uA, IEXCDAT_IDAT_50uA, IEXCDAT_IDAT_100uA, IEXCDAT_IDAT_150uA, IEXCDAT_IDAT_200uA,
	IEXCDAT_IDAT_250uA, IEXCDAT_IDAT_300uA, IEXCDAT_IDAT_400uA, IEXCDAT_IDAT_450uA, IEXCDAT_IDAT_500uA,
	IEXCDAT_IDAT_600uA, IEXCDAT_IDAT_750uA, IEXCDAT_IDAT_800uA, IEXCDAT_IDAT_1mA}
		- 0x0 or IEXCDAT_IDAT_0uA to output 0uA.
		- 0x8 or IEXCDAT_IDAT_50uA to output 50uA.
		- 0xA or IEXCDAT_IDAT_100uA to output 100uA.
		- 0xC or IEXCDAT_IDAT_150uA to output 150uA.
		- 0xE or IEXCDAT_IDAT_200uA to output 200uA.
		- 0x28 or IEXCDAT_IDAT_250uA to output 250uA.
		- 0x14 or IEXCDAT_IDAT_300uA to output 300uA.
		- 0x16 or IEXCDAT_IDAT_400uA to output 400uA.
		- 0x1c or IEXCDAT_IDAT_450uA to output 450uA.
		- 0x2A or IEXCDAT_IDAT_500uA to output 500uA.
		- 0x1E or IEXCDAT_IDAT_600uA to output 600uA.
		- 0x2C or IEXCDAT_IDAT_750uA to output 750uA.
		- 0x26 or IEXCDAT_IDAT_800uA to output 800uA.
		- 0x3E or IEXCDAT_IDAT_1mA to output 1mA.
	@param iIDAT0 :{IDAT0En,IDAT0Dis}
		- 0 or IDAT0Dis to disable 10uA source
		- 1 or IDAT0En to enable 10uA source

	@return 1.
**/
int IexcDat(int iIDAT, int iIDAT0)
	{
	int	i1;
	
	i1 = iIDAT0&0x1;
	i1 |= iIDAT&IEXCDAT_IDAT_MSK;

	pADI_ANA->IEXCDAT = i1;
	return 1;
	}

/**@}*/
