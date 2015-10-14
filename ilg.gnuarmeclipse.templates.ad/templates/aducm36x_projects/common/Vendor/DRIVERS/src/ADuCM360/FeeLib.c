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
   @addtogroup fee
   @{

   @file     FeeLib.c
   @brief    Set of Flash peripheral functions.
   @version  V0.4
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.1, October 2012: initial version.
   - V0.2, November 2012: Added warnings about 64k parts
   - V0.3, November 2013: Added notes about FeeFAKey() and FeeWrPro()
   - V0.4, October 2015: Coding style cleanup - no functional changes.

**/

#include "FeeLib.h"
#include <ADuCM360.h>


/**
   @brief int FeeMErs();
         ========== Performs a mass erase if the flash controller is not busy.

   @return 1 if the command was issued, 0 if the the flash controller was busy.

**/
int FeeMErs(void)
{
   if (pADI_FEE->FEESTA == 1) {
      return 0;
   }

   pADI_FEE->FEEKEY =  0xF456;
   pADI_FEE->FEEKEY =  0xF123;

   pADI_FEE->FEECMD = 0x3;

   return 1;
}

/**
   @brief int FeePErs(unsigned long lPage);
         ========== Performs a page erase if the flash controller is not busy.

   @param lPage :{0-0x1FFFF}
      - Specifies the address of the page to be erased
   @return 1 if the command was issued, 0 if the the flash controller was busy.

**/

int FeePErs(unsigned long lPage)
{
   if (pADI_FEE->FEESTA  == 1) {
      return 0;
   }

   pADI_FEE->FEEKEY =  0xF456;
   pADI_FEE->FEEKEY =  0xF123;

   pADI_FEE->FEEADR0L = lPage & 0xFFFF;
   pADI_FEE->FEEADR0H = lPage >> 16;

   pADI_FEE->FEECMD = 0x1;

   return 1;
}

/**
   @brief int FeeWrPro(unsigned long lKey);
         ========== Enables write protection on the part.

   @param lKey :{0-0x7FFFFFFF}
      - Block 0 is write protected by clearing bit 0, Block 1 is write protected
      by clearing bit 1 and so forth for all 32 blocks
   @return 1
   @note This function always protects the last block of flash, which contains the write protection location.
         This function should be used for debugging. If protection is required during
         production the protection patterns should be stored within the executable image.
   @warning For 64k parts you must change the WR_PR #define in FeeLib.h to 0x0FFF8.

**/


int FeeWrPro(unsigned long lKey)
{
   pADI_FEE->FEEKEY =  0xF456;
   pADI_FEE->FEEKEY =  0xF123;
   WR_PR = lKey & 0x7FFFFFFF;
   return 1;
}

/**
   @brief int FeeWrProTmp(unsigned long lKey);
         ========== Temporarily enables write protection on the part.
         Write protectiong is disables after a reset

   @param lKey :{0-0x7FFFFFFF}
      - Block 0 is write protected by clearing bit 0, Block 1 is write protected
      by clearing bit 1 and so forth for all 32 blocks
   @return 1

**/


int FeeWrProTmp(unsigned long lKey)
{
   pADI_FEE->FEEKEY =  0xF456;
   pADI_FEE->FEEKEY =  0xF123;
   pADI_FEE->FEEPROL = lKey & 0xFFFF;
   pADI_FEE->FEEPROH = lKey >> 16;

   return 1;
}

/**
   @brief int FeeRdProTmp(int iMde);
         ========== Temporarly enables or disables read protection on the part.

   @param iMde :{FEECON1_DBG_DIS,FEECON1_DBG_EN}
      - 0 or FEECON1_DBG_DIS disables serial wire access
      - 1 or FEECON1_DBG_EN enables serial wire access
   @return 1
   @note A permanent protection can be enabled in the startup file as shown
         in the FlashProtect project

**/

int FeeRdProTmp(int iMde)
{
   pADI_FEE->FEEKEY =  0xF456;
   pADI_FEE->FEEKEY =  0xF123;

   pADI_FEE->FEECON1 = iMde & 0x1;
   pADI_FEE->FEEKEY = 0x0;
   pADI_FEE->FEEKEY = 0x0;
   return 1;
}

/**
   @brief int FeeWrEn(int iMde);
         ========== Enables or disables writing to flash.

   @param iMde :{0,1}
      - 0 disables writing to flash
      - 1 enables writing to flash
   @return 1

**/

int FeeWrEn(int iMde)
{
   if (iMde) {
      pADI_FEE->FEECON0 |= 0x4;

   } else {
      pADI_FEE->FEECON0 &= 0xFB;
   }

   return 1;
}

/**
   @brief int FeeSta(void);
         ========== Returns the status register of the flash controller.

   @return value of FEESTA
      - FEESTA_CMDBUSY = Flash block busy
      - FEESTA_WRBUSY = Flash write in progress
      - FEESTA_CMDDONE = Command complete
      - FEESTA_WRDONE = Write complete
      - FEESTA_CMDRES_SUCCESS = Successful completion of a command or write
      - FEESTA_CMDRES_PROTECTED = Attempted erase of protected location
      - FEESTA_CMDRES_VERIFYERR = Read verify error
      - FEESTA_CMDRES_ABORT = Command aborted
      - FEESTA_SIGNERR = Singnature check failed
**/

int FeeSta(void)
{
   return pADI_FEE->FEESTA;
}

/**
   @brief int FeeFAKey(unsigned long long udKey);
         ========== Writes the FA key to a specific location.

   @param udKey :{0-0xFFFFFFFFFFFFFFFF}
      - This key is used for failure analysis
   @return 1
   @note This function should be used for debugging. During production the keys should be stored
         within the executable image
   @warning For 64k parts you must change the FA_KEYH and FA_KEYL #defines in FeeLib.h
      to 0x0FFF4 and 0x0FFF0.

**/

int FeeFAKey(unsigned long long udKey)
{
   if (pADI_FEE->FEESTA == 1) {
      return 0;
   }

   pADI_FEE->FEEKEY =  0xF456;
   pADI_FEE->FEEKEY =  0xF123;

   FA_KEYL = udKey & 0xFFFFFFFF;
   FA_KEYH = udKey >> 32;
   return 1;
}

/**
   @brief int FeeIntAbt(unsigned int iAEN0, unsigned int iAEN1, unsigned int iAEN2);
         ========== Choose which interrupts can abort flash commands.

   @param iAEN0 :{0|FEEAEN0_T2|FEEAEN0_EXTINT0|FEEAEN0_EXTINT1|FEEAEN0_EXTINT2|FEEAEN0_EXTINT3|FEEAEN0_EXTINT4|
   FEEAEN0_EXTINT5|FEEAEN0_EXTINT6|FEEAEN0_EXTINT7|FEEAEN0_T3|FEEAEN0_T0|FEEAEN0_T1|FEEAEN0_ADC0|FEEAEN0_ADC1|FEEAEN0_SINC2}
      - FEEAEN0_T2 = Timer 2 interrupt will abort flash commands
      - FEEAEN0_EXTINT0 = External interrupt 0 will abort flash commands
      - FEEAEN0_EXTINT1 = External interrupt 1 will abort flash commands
      - FEEAEN0_EXTINT2 = External interrupt 2 will abort flash commands
      - FEEAEN0_EXTINT3 = External interrupt 3 will abort flash commands
      - FEEAEN0_EXTINT4 = External interrupt 4 will abort flash commands
      - FEEAEN0_EXTINT5 = External interrupt 5 will abort flash commands
      - FEEAEN0_EXTINT6 = External interrupt 6 will abort flash commands
      - FEEAEN0_EXTINT7 = External interrupt 7 will abort flash commands
      - FEEAEN0_T3 = Timer 3 interrupt will abort flash commands
      - FEEAEN0_T0 = Timer 0 interrupt will abort flash commands
      - FEEAEN0_T1 = Timer 1 interrupt will abort flash commands
      - FEEAEN0_ADC0 = ADC0 interrupt will abort flash commands
      - FEEAEN0_ADC1 = ADC1 interrupt will abort flash commands
      - FEEAEN0_SINC2 = Sinc2 interrupt will abort flash commands
   @param iAEN1 :{0|FEEAEN1_FEE|FEEAEN1_UART|FEEAEN1_SPI0|FEEAEN1_SPI1|FEEAEN1_I2CS|FEEAEN1_I2CM|FEEAEN1_DMASPI1TX|FEEAEN1_DMASPI1RX|
   FEEAEN1_DMAUARTTX|FEEAEN1_DMAUARTRX|FEEAEN1_DMAI2CSTX|FEEAEN1_DMAI2CSRX|FEEAEN1_DMAI2CMTX|FEEAEN1_DMAI2CMRX|FEEAEN1_DMADAC}
      - FEEAEN1_FEE = Flash interrupt will abort flash commands
      - FEEAEN1_UART = UART interrupt will abort flash commands
      - FEEAEN1_SPI0 = SPI0 interrupt will abort flash commands
      - FEEAEN1_SPI1 = SPI1 interrupt will abort flash commands
      - FEEAEN1_I2CS = I2C slave interrupt will abort flash commands
      - FEEAEN1_I2CM = I2C master interrupt will abort flash commands
      - FEEAEN1_DMASPI1TX = DMA SPI1 Tx interrupt will abort flash commands
      - FEEAEN1_DMASPI1RX = DMA SPI1 Rx interrupt will abort flash commands
      - FEEAEN1_DMAUARTTX = DMA UART Tx interrupt will abort flash commands
      - FEEAEN1_DMAUARTRX = DMA UART Rx interrupt will abort flash commands
      - FEEAEN1_DMAI2CSTX = DMA I2C Slave Tx interrupt will abort flash commands
      - FEEAEN1_DMAI2CSRX = DMA I2C Slave Rx interrupt will abort flash commands
      - FEEAEN1_DMAI2CMTX = DMA I2C Master Tx interrupt will abort flash commands
      - FEEAEN1_DMAI2CMRX = DMA I2C Master Rx interrupt will abort flash commands
      - FEEAEN1_DMADAC = DMA DAC interrupt will abort flash commands
   @param iAEN2 :{0|FEEAEN2_DMAADC0|FEEAEN2_DMAADC1|FEEAEN2_DMASINC2|FEEAEN2_PWMTRIP|FEEAEN2_PWM0|FEEAEN2_PWM1|FEEAEN2_PWM2}
      - FEEAEN2_DMAADC0 = DMA ADC0 interrupt will abort flash commands
      - FEEAEN2_DMAADC1 = DMA ADC1 interrupt will abort flash commands
      - FEEAEN2_DMASINC2 = DMA SINC2 interrupt will abort flash commands
      - FEEAEN2_PWMTRIP = PWM TRIP interrupt will abort flash commands
      - FEEAEN2_PWM0 = PWM0 interrupt will abort flash commands
      - FEEAEN2_PWM1 = PWM1 interrupt will abort flash commands
      - FEEAEN2_PWM2 = PWM2 interrupt will abort flash commands
   @return 1

**/

int FeeIntAbt(unsigned int iAEN0, unsigned int iAEN1, unsigned int iAEN2)
{
   pADI_FEE->FEEAEN0 = iAEN0;
   pADI_FEE->FEEAEN1 = iAEN1;
   pADI_FEE->FEEAEN2 = iAEN2;

   return 1;
}

/**
   @brief int FeeAbtAdr();
         ========== Return the address of the location written when the write was aborted.

   @return ((FEEADRAH<<16) | FEEADRAL)

**/

int FeeAbtAdr(void)
{
   int ret = pADI_FEE->FEEADRAL & 0xFFFF;
   ret |= (pADI_FEE->FEEADRAH << 16);
   return ret;
}

/**
   @brief int FeeSign(unsigned long ulStartAddr, unsigned long ulEndAddr)();
         ========== Perform flash integrity signature check.

   @param ulStartAddr :{0-0x1FFFF}
      - The start address of the block to be checked;
      - Blocks are integer number of flash pages (0x200 bytes)
   @param ulEndAddr :{0-0x1FFFF}
      - The end address of the block to be checked
   @return 1 if the command was issued, 0 if the the flash controller was busy.

**/

int FeeSign(unsigned long ulStartAddr, unsigned long ulEndAddr)
{
   if (pADI_FEE->FEESTA == 1) {
      return 0;
   }

   pADI_FEE->FEEADR0L = ulStartAddr & 0xFFFF;
   pADI_FEE->FEEADR0H = ulStartAddr >> 16;

   pADI_FEE->FEEADR1L = ulEndAddr & 0xFFFF;
   pADI_FEE->FEEADR1H = ulEndAddr >> 16;

   pADI_FEE->FEEKEY =  0xF456;
   pADI_FEE->FEEKEY =  0xF123;

   pADI_FEE->FEECMD = 0x2;
   return 1;
}

/**
   @brief int FeeSig();
         ========== Return the flash integrity signature calculated by the controller.

   @return ((FEESIGH<<16) | FEESIGL)

**/

int FeeSig(void)
{
   int ret = pADI_FEE->FEESIGL & 0xFFFF;
   ret |= (pADI_FEE->FEESIGH << 16);
   return ret;
}

/**@}*/
