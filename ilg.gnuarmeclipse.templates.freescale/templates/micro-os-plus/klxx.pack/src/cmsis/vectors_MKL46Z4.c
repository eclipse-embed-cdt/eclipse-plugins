/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2016 Liviu Ionescu.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

// ----------------------------------------------------------------------------

#include "cortexm/ExceptionHandlers.h"

// ----------------------------------------------------------------------------

void __attribute__((weak))
Default_Handler(void);

// Forward declaration of the specific IRQ handlers. These are aliased
// to the Default_Handler, which is a 'forever' loop. When the application
// defines a handler (with the same name), this will automatically take
// precedence over these weak definitions

void __attribute__ ((weak, alias ("Default_Handler")))
DMA0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Reserved20_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
FTFA_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
LVD_LVW_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
LLW_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
UART0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
UART1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
UART2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
ADC0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
CMP0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TPM0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TPM1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TPM2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
RTC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
RTC_Seconds_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PIT_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2S0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USB0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DAC0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TSI0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
MCG_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
LPTimer_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
LCD_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PORTA_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PORTC_PORTD_IRQHandler(void);

// ----------------------------------------------------------------------------

extern unsigned int _estack;

typedef void
(* const pHandler)(void);

// ----------------------------------------------------------------------------

// The vector table.
// This relies on the linker script to place at correct location in memory.

#define VECTOR_PADDING  (pHandler)0xffffffff

/* Flash configuration field values below */
/* Please be careful when modifying any of
 * the values below as it can secure the
 * flash (possibly permanently): 0x400-0x409.
 */
#define CONFIG_1                (pHandler)0xffffffff
#define CONFIG_2                (pHandler)0xffffffff
#define CONFIG_3                (pHandler)0xffffffff
#define CONFIG_4                (pHandler)0xfffffffe

__attribute__ ((section(".isr_vector"),used))
pHandler gHandlers[] =
  {
  // Core Level - CM0
      (pHandler) &_estack, // The initial stack pointer
      Reset_Handler, // The reset handler

      NMI_Handler, // The NMI handler
      HardFault_Handler, // The hard fault handler
#if defined(__ARM_ARCH_7M__) || defined(__ARM_ARCH_7EM__)
      MemManage_Handler,                        // The MPU fault handler
      BusFault_Handler,                        // The bus fault handler
      UsageFault_Handler,                        // The usage fault handler
#else
      0, 0, 0,                                  // Reserved
#endif
      0,                                        // Reserved
      0,                                        // Reserved
      0,                                        // Reserved
      0,                                        // Reserved
      SVC_Handler,                              // SVCall handler
#if defined(__ARM_ARCH_7M__) || defined(__ARM_ARCH_7EM__)
      DebugMon_Handler,                         // Debug monitor handler
#else
      0,                                        // Reserved
#endif
      0, // Reserved
      PendSV_Handler, // The PendSV handler
      SysTick_Handler, // The SysTick handler

      // ----------------------------------------------------------------------
      // Chip Level - KL46
      DMA0_IRQHandler, // DMA channel 0 transfer complete/error interrupt
      DMA1_IRQHandler, // DMA channel 1 transfer complete/error interrupt
      DMA2_IRQHandler, // DMA channel 2 transfer complete/error interrupt
      DMA3_IRQHandler, // DMA channel 3 transfer complete/error interrupt
      Reserved20_IRQHandler, // Reserved interrupt 20
      FTFA_IRQHandler, // FTFA command complete/read collision interrupt
      LVD_LVW_IRQHandler, // Low Voltage Detect, Low Voltage Warning
      LLW_IRQHandler, // Low Leakage Wakeup
      I2C0_IRQHandler, // I2C0 interrupt
      I2C1_IRQHandler, // I2C0 interrupt 25
      SPI0_IRQHandler, // SPI0 interrupt
      SPI1_IRQHandler, // SPI1 interrupt
      UART0_IRQHandler, // UART0 status/error interrupt
      UART1_IRQHandler, // UART1 status/error interrupt
      UART2_IRQHandler, // UART2 status/error interrupt
      ADC0_IRQHandler, // ADC0 interrupt
      CMP0_IRQHandler, // CMP0 interrupt
      TPM0_IRQHandler, // TPM0 fault, overflow and channels interrupt
      TPM1_IRQHandler, // TPM1 fault, overflow and channels interrupt
      TPM2_IRQHandler, // TPM2 fault, overflow and channels interrupt
      RTC_IRQHandler, // RTC interrupt
      RTC_Seconds_IRQHandler, // RTC seconds interrupt
      PIT_IRQHandler, // PIT timer interrupt
      I2S0_IRQHandler, // I2S0 transmit interrupt
      USB0_IRQHandler, // USB0 interrupt
      DAC0_IRQHandler, // DAC0 interrupt
      TSI0_IRQHandler, // TSI0 interrupt
      MCG_IRQHandler, // MCG interrupt
      LPTimer_IRQHandler, // LPTimer interrupt
      LCD_IRQHandler, // Segment LCD Interrupt
      PORTA_IRQHandler, // Port A interrupt
      PORTC_PORTD_IRQHandler, // Port C and port D interrupt

      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING,
      VECTOR_PADDING //
    };

__attribute__ ((section(".cfmconfig")))
pHandler gConfigs[] =
  {
  CONFIG_1,
  CONFIG_2,
  CONFIG_3,
  CONFIG_4 //
    };

// ----------------------------------------------------------------------------

// Processor ends up here if an unexpected interrupt occurs or a specific
// handler is not present in the application code.

void __attribute__ ((section(".after_vectors")))
Default_Handler(void)
{
#if defined(DEBUG)
  __DEBUG_BKPT();
#endif
  while (1)
    {
    }
}

// ----------------------------------------------------------------------------
