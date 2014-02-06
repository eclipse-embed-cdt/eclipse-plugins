/*
** ###################################################################
**     Processors:          MKL04Z32FK4
**                          MKL04Z32LC4
**                          MKL04Z32VLF4
**
**     Compilers:           ARM Compiler
**                          Freescale C/C++ for Embedded ARM
**                          GNU C Compiler
**                          IAR ANSI C/C++ Compiler for ARM
**
**     Reference manual:    KL04P48M48SF1RM, Rev.3, Sep 2012
**     Version:             rev. 1.2, 2012-10-04
**
**     Abstract:
**         Provides a system configuration function and a global variable that
**         contains the system frequency. It configures the device and initializes
**         the oscillator (PLL) that is part of the microcontroller device.
**
**     Copyright: 2012 Freescale, Inc. All Rights Reserved.
**
**     http:                 www.freescale.com
**     mail:                 support@freescale.com
**
**     Revisions:
**     - rev. 1.0 (2012-06-21)
**         Initial version.
**     - rev. 1.1 (2012-08-01)
**         Device type UARTLP changed to UART0.
**         Missing PORTB_IRQn interrupt number definition added.
**     - rev. 1.2 (2012-10-04)
**         Update according to reference manual rev. 3.
**
** ###################################################################
*/

/**
 * @file MKL04Z4
 * @version 1.2
 * @date 2012-10-04
 * @brief Device specific configuration file for MKL04Z4 (implementation file)
 *
 * Provides a system configuration function and a global variable that contains
 * the system frequency. It configures the device and initializes the oscillator
 * (PLL) that is part of the microcontroller device.
 */

#include <stdint.h>
#include "MKL04Z4.h"

  #define DEFAULT_SYSTEM_CLOCK            20971520u /* Default System clock value */


/* ----------------------------------------------------------------------------
   -- Core clock
   ---------------------------------------------------------------------------- */

uint32_t SystemCoreClock = DEFAULT_SYSTEM_CLOCK;

/* ----------------------------------------------------------------------------
   -- SystemInit()
   ---------------------------------------------------------------------------- */

void SystemInit (void) {
}

/* ----------------------------------------------------------------------------
   -- SystemCoreClockUpdate()
   ---------------------------------------------------------------------------- */

void SystemCoreClockUpdate (void) {
}
