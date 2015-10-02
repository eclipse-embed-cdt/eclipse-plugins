/**
 *****************************************************************************
   @addtogroup pwr 
   @{
   @file     PwrLib.c
   @brief    Functions for controling power modes
   @version  V0.1
   @author   PAD CSE group
   @date     September 2012 


All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.
		
**/
#include	"PwrLib.h"
#include "ADuCM360.h"

/**
	@brief int PwrCfg(int iMode)
			========== Sets MCU power mode.
	@param iMode :{PWRMOD_MOD_FULLACTIVE,PWRMOD_MOD_MCUHALT,PWRMOD_MOD_PERHALT,PWRMOD_MOD_SYSHALT,PWRMOD_MOD_TOTALHALT,PWRMOD_MOD_HIBERNATE }
		- 0 or PWRMOD_MOD_FULLACTIVE for fully active mode.
		- 1 or PPWRMOD_MOD_MCUHALT to halt the MCU.
		- 2 or PWRMOD_MOD_PERHALT to halt the MCU and clock to peripherals.
		- 3 or PWRMOD_MOD_SYSHALT to halt the MCU, and clock to memory and DMA.
		- 4 or PWRMOD_MOD_TOTALHALT For DEEPSLEEP mode
		- 5 or PWRMOD_MOD_HIBERNATE For DEEPSLEEP mode
	@note	
	@return 1.
**/

int PwrCfg(int iMode)
{  
  int index = 0;	
  if (iMode > 5)	// Check for invalid sleep mode value
		   iMode = 0;
   	if ((iMode == 4) || (iMode == 5))
	   {
		    SCB->SCR = 0x04;		// sleepdeep mode - write to the Cortex-m3 System Control register bit2
	   }
	pADI_PWRCTL->PWRKEY = 0x4859;	// key1
	pADI_PWRCTL->PWRKEY = 0xF27B;	// key2 
	pADI_PWRCTL->PWRMOD = iMode;

	for (index=0;index<2;index++); 
	__WFI();
	for (index=0;index<2;index++);
		 
	return 1;
}
/**
	@brief int PwrRead(void)
			========== reads MCU control register including WCENACK bit.

	@note	
	@return 1.
**/
int PwrRead(void)
{
	return pADI_PWRCTL->PWRMOD;
}

/**@}*/
