/**
 *****************************************************************************
   @addtogroup rst 
   @{
   @file     RstLib.c
   @brief    Reads the reset status bits and allows them to be reset.  
   @version  V0.2
   @author   ADI
   @date     September 2012
   @par Revision History:
   - V0.1, December 2010: initial version. 
   - V0.2, September 2012: Functions now CMSIS compliant
   

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/
#include	"RstLib.h"
#include <ADuCM360.h>

/**
	@brief int ReadRstSta(void)
			==========Read RstSta status bits.
	@return Value of RSTSTA. 
	@note .
**/

int ReadRstSta(void)
	{
	return pADI_RESET->RSTSTA;
	}
	
	/**
	@brief int ClearRstSta(int iStaClr)
			==========Clear and read selected status bits.
	@param iStaClr :{RST_NONE|RSTSTA_POR|RSTSTA_EXTRST|RSTSTA_WDRST|RSTSTA_SWRST}	\n
		RSTSTA.0-3
		The bitwise OR of the respective data bits to clear:
			- 0 or RST_NONE.
			- 1 or RSTSTA_POR to reset POR status bit.
			- 2 or RSTSTA_EXTRST to reset reset pin status bit.
			- 4 or RSTSTA_WDRST to reset watchdog timeout status bit.
			- 8 or RSTSTA_SWRST to reset software reset status bit.
	@return Value of RSTSTA before it is written.  Bits as defined above.
	@note If iStaClr = RST_NONE then RSTSTA is read without changing it.
**/

int ClearRstSta(int iStaClr )
	{
		(pADI_RESET->RSTCLR) = (iStaClr& 0xF);
		return pADI_RESET->RSTSTA;
	}


/**@}*/
