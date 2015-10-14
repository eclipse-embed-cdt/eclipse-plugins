/*******************************************************************************
* Copyright 2015(c) Analog Devices, Inc.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification,
* are permitted provided that the following conditions are met:
*  - Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*  - Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*  - Neither the name of Analog Devices, Inc. nor the names of its
*    contributors may be used to endorse or promote products derived
*    from this software without specific prior written permission.
*  - The use of this software may or may not infringe the patent rights
*    of one or more patent holders.  This license does not release you
*    from the requirement that you obtain separate licenses from these
*    patent holders to use this software.
*  - Use of the software either in source or binary form, must be run
*    on or directly connected to an Analog Devices Inc. component.
*
* THIS SOFTWARE IS PROVIDED BY ANALOG DEVICES "AS IS" AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, NON-INFRINGEMENT, MERCHANTABILITY
* AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
* IN NO EVENT SHALL ANALOG DEVICES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* INTELLECTUAL PROPERTY RIGHTS, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*******************************************************************************/

/**
 *****************************************************************************
   @addtogroup rst
   @{
   @file     RstLib.c
   @brief    Reads the reset status bits and allows them to be reset.
   @version  V0.3
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.1, December 2010: initial version.
   - V0.2, September 2012: Functions now CMSIS compliant
   - V0.3, October 2015: Coding style cleanup - no functional changes.

**/
#include "RstLib.h"
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
@param iStaClr :{RST_NONE|RSTSTA_POR|RSTSTA_EXTRST|RSTSTA_WDRST|RSTSTA_SWRST} \n
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
   (pADI_RESET->RSTCLR) = (iStaClr & 0xF);
   return pADI_RESET->RSTSTA;
}


/**@}*/
