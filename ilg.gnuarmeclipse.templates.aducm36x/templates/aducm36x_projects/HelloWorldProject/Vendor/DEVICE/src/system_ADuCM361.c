/**************************************************************************//**
 * @file     system_ADUCM361.c
 * @brief    CMSIS Device System Source File for
 *           Analog Devices ADUCM361 Device Series
 * @version  V1.00
 * @date     16. January 2013
 *
 * @note
 * Copyright (C) 2012 ARM Limited. All rights reserved.
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

#include "ADUCM361.h"

/*----------------------------------------------------------------------------
  Define clocks
 *----------------------------------------------------------------------------*/
#define __HSI             (16000000UL)
#define __XTAL            (16000000UL)    /* Oscillator frequency             */

#define __SYSTEM_CLOCK    (1 * __XTAL)


/*----------------------------------------------------------------------------
  Clock Variable definitions
 *----------------------------------------------------------------------------*/
uint32_t SystemCoreClock = __SYSTEM_CLOCK;/*!< System Clock Frequency (Core Clock)*/


/*----------------------------------------------------------------------------
  Clock functions
 *----------------------------------------------------------------------------*/
void SystemCoreClockUpdate (void)            /* Get Core Clock Frequency      */
{

  SystemCoreClock = __SYSTEM_CLOCK;

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
  pADI_WDT->T3CON  = 0;                    /* disable watchdog */

  pADI_CLKCTL->CLKCON0   = 0x0;            /* 16MHz output of UCLK divide */
  pADI_CLKCTL->CLKSYSDIV = 0;              /* No divide of 16MHz system clock */
  pADI_CLKCTL->CLKCON1   = 0x0;            /* PWM = 16MHz, UART = 16MHz, SPI1 = 16MHz, SPI0=16MHz */
  pADI_CLKCTL->CLKDIS    = 0x0;            /* Enable clock to all peripherals */

}
