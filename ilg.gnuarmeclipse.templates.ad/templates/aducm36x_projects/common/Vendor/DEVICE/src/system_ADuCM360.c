/**************************************************************************//**
 * @file     system_ADuCM360.c
 * @brief    CMSIS Device System Source File for
 *           Analog Devices ADuCM360 Device Series
 * @version  V1.00
 * @date     09. July 2012
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

#include "ADuCM360.h"
#include <WdtLib.h>
#include <ClkLib.h>
#include <DioLib.h>
#include <GptLib.h>
#include <AdcLib.h>

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

	/* Disable Watchdog timer resets */
	WdtCfg(T3CON_PRE_DIV1, T3CON_IRQ_EN, T3CON_PD_DIS);

	/* Enable clock to all peripherals */
	ClkDis(0);

	/*Configures system clock */
	ClkCfg(CLK_CD0, CLK_HF, CLKSYSDIV_DIV2EN_DIS, CLK_UCLKCG);
	ClkSel(CLK_CD0,CLK_CD0,CLK_CD0,CLK_CD0);

}
