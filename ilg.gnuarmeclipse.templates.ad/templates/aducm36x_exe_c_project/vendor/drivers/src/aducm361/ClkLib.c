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
   @addtogroup clk
   @{
   @file       ClkLib.c
   @brief      Set of Timer peripheral functions.
   - Example:
   - ClkCfg(CLK_CD3,CLK_HF,CLK_HFO,CLK_OFF);
   - ClkSel(CLK_OFF,CLK_OFF,CLK_CD0,CLK_OFF);

   @version    V0.6
   @author     ADI
   @date    October 2015
   @par Revision History:
   - V0.1, December 2010: initial version.
   - V0.2, January 2012: Changed Clkcfg() - removed write to XOSCCON.
   - V0.3, January 2013: corrected comments.
   - v0.4, February 2013: corrected parameters in ClkDis()
   - V0.5, October 2015: Coding style cleanup - no functional changes.
   - V0.6, October 2015: Use Standard Integer Types, prefer unsigned types, add include and C++ guards.
**/

#include "ClkLib.h"

/**
   @brief uint32_t ClkCfg(uint32_t iCd, uint32_t iClkSrc, uint32_t iSysClockDiv, uint32_t iClkOut)
         ==========Configures clock system.
   @param iCd :{CLK_CD0,CLK_CD1,CLK_CD2,CLK_CD3,CLK_CD4,CLK_CD5,CLK_CD6,CLK_CD7}
      - 0 or CLK_CD0 to divide clock by 1.
      - 1 or CLK_CD1 to divide clock by 2.
      - 2 or CLK_CD2 to divide clock by 4.
      - 3 or CLK_CD3 to divide clock by 8.
      - 4 or CLK_CD4 to divide clock by 16.
      - 5 or CLK_CD5 to divide clock by 32.
      - 6 or CLK_CD6 to divide clock by 64.
      - 7 or CLK_CD7 to divide clock by 128.
   @param iClkSrc :{CLK_HF,CLK_LFX,CLK_LF,CLK_P4}
      - 0 or CLK_HF for 16MHz clock.
      - 1 or CLK_LFX for external 32kHz clock.
      - 2 or CLK_LF for internal 32kHz clock.
      - 3 or CLK_P4 for for clocking from pin P1.0.
   @param iSysClockDiv :{CLKSYSDIV_DIV2EN_DIS,CLKSYSDIV_DIV2EN}
      - 0 or CLKSYSDIV_DIV2EN_DIS for 16MHz clock when internal HF oscillator is selected
      - 1 or CLKSYSDIV_DIV2EN_EN for 8MHz clock when internal HF oscillator is selected
   @param iClkOut :{CLK_UCLKCG,CLK_UCLK,CLK_UDIV,CLK_HFO,CLK_LFO,CLK_LFXO}
      - 0 or CLK_UCLKCG to output UCLKCG clock.
      - 1 or CLK_UCLK to output UCLK clock.
      - 2 or CLK_UDIV to output UCLK divided by CD clock.
      - 5 or CLK_HFO to output 16MHz clock.
      - 6 or CLK_LFO to output internal 32kHz clock.
      - 7 or CLK_LFXO to output external 32kHz clock.
   @return  1
**/

uint32_t ClkCfg(uint32_t iCd, uint32_t iClkSrc, uint32_t iSysClockDiv, uint32_t iClkOut)
{
   uint32_t   i1;

   i1 = iCd & 7;
   i1 |= (iClkSrc & 3) << 3;
   i1 |= (iClkOut & 7) << 5;
   pADI_CLKCTL->CLKCON0 = i1;
   pADI_CLKCTL->CLKSYSDIV = iSysClockDiv;
   return 1;
}

/**
   @brief uint32_t ClkSel(uint32_t iSpiCd, uint32_t iI2cCd, uint32_t iUrtCd, uint32_t iPwmCd)
         ==========Sets clocks of digital peripherals - SPI0/SPI1 will have same setting
   @param iSpiCd :{CLK_CD0,CLK_CD1,CLK_CD2,CLK_CD3,CLK_CD4,CLK_CD5,CLK_CD6,CLK_CD7}
      - 0 or CLK_CD0 to divide SPI clock by 1.
      - 1 or CLK_CD1 to divide SPI clock by 2.
      - 2 or CLK_CD2 to divide SPI clock by 4.
      - 3 or CLK_CD3 to divide SPI clock by 8.
      - 4 or CLK_CD4 to divide SPI clock by 16.
      - 5 or CLK_CD5 to divide SPI clock by 32.
      - 6 or CLK_CD6 to divide SPI clock by 64.
      - 7 or CLK_CD7 to divide SPI clock by 128.
   @param iI2cCd :{CLK_CD0,CLK_CD1,CLK_CD2,CLK_CD3,CLK_CD4,CLK_CD5,CLK_CD6,CLK_CD7}
      - 0 or CLK_CD0 to divide I2C clock by 1.
      - 1 or CLK_CD1 to divide I2C clock by 2.
      - 2 or CLK_CD2 to divide I2C clock by 4.
      - 3 or CLK_CD3 to divide I2C clock by 8.
      - 4 or CLK_CD4 to divide I2C clock by 16.
      - 5 or CLK_CD5 to divide I2C clock by 32.
      - 6 or CLK_CD6 to divide I2C clock by 64.
      - 7 or CLK_CD7 to divide I2C clock by 128.
   @param iUrtCd :{CLK_CD0,CLK_CD1,CLK_CD2,CLK_CD3,CLK_CD4,CLK_CD5,CLK_CD6,CLK_CD7}
      - 0 or CLK_CD0 to divide UART clock by 1.
      - 1 or CLK_CD1 to divide UART clock by 2.
      - 2 or CLK_CD2 to divide UART clock by 4.
      - 3 or CLK_CD3 to divide UART clock by 8.
      - 4 or CLK_CD4 to divide UART clock by 16.
      - 5 or CLK_CD5 to divide UART clock by 32.
      - 6 or CLK_CD6 to divide UART clock by 64.
      - 7 or CLK_CD7 to divide UART clock by 128.
   @param iPwmCd :{CLK_CD0,CLK_CD1,CLK_CD2,CLK_CD3,CLK_CD4,CLK_CD5,CLK_CD6,CLK_CD7}
      - 0 or CLK_CD0 to divide PWM clock by 1.
      - 1 or CLK_CD1 to divide PWM clock by 2.
      - 2 or CLK_CD2 to divide PWM clock by 4.
      - 3 or CLK_CD3 to divide PWM clock by 8.
      - 4 or CLK_CD4 to divide PWM clock by 16.
      - 5 or CLK_CD5 to divide PWM clock by 32.
      - 6 or CLK_CD6 to divide PWM clock by 64.
      - 7 or CLK_CD7 to divide PWM clock by 128.
   @return 1.
**/

uint32_t ClkSel(uint32_t iSpiCd, uint32_t iI2cCd, uint32_t iUrtCd, uint32_t iPwmCd)
{
   uint32_t   i1;

   i1 = iSpiCd & 0x7;
   i1 |= (iSpiCd << 3);       // Same clock divide setting for SPI0 and SPI1
   i1 |= (iI2cCd & 0x7) << 6;
   i1 |= (iUrtCd & 0x7) << 9;
   i1 |= (iPwmCd & 0x7) << 12;
   pADI_CLKCTL->CLKCON1 = i1;
   return 1;
}

/**
   @brief uint32_t ClkDis(uint32_t iClkDis)
         ==========Disables Clock to Peripheral blocks

   @param iClkDis :{0|CLKDIS_DISSPI0CLK| CLKDIS_DISSPI1CLK| CLKDIS_DISI2CCLK| CLKDIS_DISUARTCLK| CLKDIS_DISPWMCLK| CLKDIS_DIST0CLK| CLKDIS_DIST1CLK| CLKDIS_DISDACCLK| CLKDIS_DISDMACLK| CLKDIS_DISADCCLK}
   - CLKDIS
   - Combination of the following features :
      - 0 or Enable clock to all peripherals
      - 1 or DISSPI0CLK disable SPI0 peripheral Clock
      - 2 or DISSPI1CLK disable SPI0 peripheral Clock
      - 4 or DISI2CCLK disable I2C peripheral Clock
      - 8 or DISUARTCLK disable UART peripheral Clock
      - 16 or DISPWMCLK disable PWM peripheral Clock
      - 32 or DIST0CLK disable T0 peripheral Clock
      - 64 or DIST1CLK disable T1 peripheral Clock
      - 128 or DISDACCLK disable DAC peripheral Clock
      - 256 or DISDMACLK disable DMA peripheral Clock
      - 512 or DISADCCLK disable ADC peripheral Clock
   @return 1.
**/
uint32_t ClkDis(uint32_t iClkDis)
{

   ADI_CLKCTL_TypeDef *pTmp;

   pTmp = pADI_CLKCTL;
   pTmp->CLKDIS = iClkDis;
   return 1;
}

/**
   @brief uint32_t XOSCCfg(uint32_t iXosc)
         ==========Configures External Crystal.

   @param iXosc :{CLK_XOFF,CLK_XON,CLK_XON2}
      - 0 or CLK_OFF to power external crystal oscillator off.
      - 1 or CLK_XON to power external crystal oscillator on.
      - 4 or CLK_XON2 to divide external crystal oscillator by 2.
      - 8 or CLK_STA to read external crystal status bit
   @return  1
**/
uint32_t XOSCCfg(uint32_t iXosc)
{
   uint32_t   i1;

   i1 = iXosc & 5;
   pADI_CLKCTL->XOSCCON = i1;
   return pADI_CLKCTL->XOSCCON;
}
/**@}*/
