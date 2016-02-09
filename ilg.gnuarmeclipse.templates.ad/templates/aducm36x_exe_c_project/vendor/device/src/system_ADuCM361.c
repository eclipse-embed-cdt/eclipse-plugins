/**************************************************************************//**
 * @file     system_ADuCM360.c
 * @brief    CMSIS Device System Source File for
 *           Analog Devices ADuCM360 Device Series
 * @version  V2.00
 * @date     October 2015
 *
 * @note
 * Copyright (C) 2012 ARM Limited. All rights reserved.
 * Copyright (C) 2015 Analog Devices. All rights reserved.
 *
 * @par
 * ARM Limited (ARM) is supplying this software for use with Cortex-M
 * processor based microcontrollers.  This file can be freely distributed
 * within development tools that are supporting such ARM based processors.
 *
 * @par
 * THIS SOFTWARE IS PROVIDED "AS IS".  NO WARRANTIES, WHETHER EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE APPLY TO THIS SOFTWARE.
 * ARM SHALL NOT, IN ANY CIRCUMSTANCES, BE LIABLE FOR SPECIAL, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES, FOR ANY REASON WHATSOEVER.
 *
 ******************************************************************************/

#include "ADuCM361.h"

/*----------------------------------------------------------------------------
  Define clocks
 *----------------------------------------------------------------------------*/
#define __HFOSC           (16000000UL)    /* Oscillator frequency             */
#define __XTAL               (32768UL)    /* 32kHz Frequency                  */

/*----------------------------------------------------------------------------
  Clock Variable definitions
 *----------------------------------------------------------------------------*/
uint32_t SystemCoreClock;  /*!< System Clock Frequency (Core Clock)*/
uint32_t uClk;             /* Undivided System Clock Frequency (UCLK)   */

/* Frequency of the external clock source connected to P1.0 */
uint32_t SystemExtClock;


/*----------------------------------------------------------------------------
  Clock functions
 *----------------------------------------------------------------------------*/
void SystemCoreClockUpdate (void)            /* Get Core Clock Frequency      */
{
   int iDiv;

   switch (pADI_CLKCTL->CLKCON0 & CLKCON0_CLKMUX_MSK ) {
   case CLKCON0_CLKMUX_HFOSC:
      uClk = __HFOSC >> (pADI_CLKCTL->CLKSYSDIV & CLKSYSDIV_DIV2EN_MSK);
      break;

   case CLKCON0_CLKMUX_LFOSC:
   case CLKCON0_CLKMUX_LFXTAL:
      uClk = __XTAL;
      break;

   case CLKCON0_CLKMUX_EXTCLK:
      uClk = SystemExtClock;
      break;

   default:
      break;
   }

   iDiv = (pADI_CLKCTL->CLKCON0 & CLKCON0_CD_MSK);             // Read CLKCON0 divider bits
   SystemCoreClock = uClk >> iDiv;
}

/**
 * Initialize the system
 *
 * @param  none
 * @return none
 *
 * @brief  Setup the microcontroller system.
 *         Initialize the System.
 */
void SystemInit (void)
{
   /* SetSystemExtClkFreq(Freq); // if an external clock is used SetSystemExtClkFreq must be called
                                 // before calling SystemCoreClockUpdate() */

   pADI_WDT->T3CON  = 0;                    /* disable watchdog */
   pADI_CLKCTL->CLKCON0   = 0x0;            /* 16MHz output of UCLK divide */
   pADI_CLKCTL->CLKSYSDIV = 0;              /* No divide of 16MHz system clock */
   pADI_CLKCTL->CLKCON1   = 0x0;            /* PWM = 16MHz, UART = 16MHz, SPI1 = 16MHz, SPI0=16MHz */
   pADI_CLKCTL->CLKDIS    = 0x0;            /* Enable clock to all peripherals */

   /* compute internal clocks */
   SystemCoreClockUpdate();
}

/**
 * @brief  Sets the system external clock frequency
 *
 * @param  ExtClkFreq   External clock frequency in Hz
 * @return none
 *
 * Sets the clock frequency of the source connected to P1.0 clock input source
 */
void SetSystemExtClkFreq (uint32_t ExtClkFreq)
{
   SystemExtClock = ExtClkFreq;
}

/**
 * @brief  Gets the system external clock frequency
 *
 * @return External Clock frequency
 *
 * Gets the clock frequency of the source connected to P1.0 clock input source
 */
uint32_t GetSystemExtClkFreq (void)
{
   return  SystemExtClock;
}
