/*
 * This file was automatically generated from the Arm assembly file.
 * Copyright (c) 2020 Liviu Ionescu.
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

// The list of external handlers is from the Arm assembly startup files.

// ----------------------------------------------------------------------------

#include <cortexm/exception-handlers.h>

// ----------------------------------------------------------------------------

void __attribute__((weak))
Default_Handler(void);

// Forward declaration of the specific IRQ handlers. These are aliased
// to the Default_Handler, which is a 'forever' loop. When the application
// defines a handler (with the same name), this will automatically take
// precedence over these weak definitions

void __attribute__ ((weak, alias ("Default_Handler")))
WakeUp_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Ext_Int0_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Ext_Int1_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Ext_Int2_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Ext_Int3_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Ext_Int4_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Ext_Int5_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Ext_Int6_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Ext_Int7_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
WDog_Tmr_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
GP_Tmr0_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
GP_Tmr1_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
ADC0_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
ADC1_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SINC2_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
Flsh_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
UART_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI0_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI1_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C0_Slave_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C0_Master_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_Err_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_SPI1_TX_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_SPI1_RX_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_UART_TX_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_UART_RX_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_I2C0_STX_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_I2C0_SRX_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_I2C0_MTX_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_I2C0_MRX_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_DAC_Out_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_ADC0_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_ADC1_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA_SINC2_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PWMTRIP_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PWM0_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PWM1_Int_Handler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PWM2_Int_Handler(void);

// ----------------------------------------------------------------------------

extern unsigned int _estack;

typedef void
(* const pHandler)(void);

// ----------------------------------------------------------------------------

// The table of interrupt handlers. It has an explicit section name
// and relies on the linker script to place it at the correct location
// in memory.

__attribute__ ((section(".isr_vector"),used))
pHandler __isr_vectors[] =
  {
    // Cortex-M Core Handlers
    (pHandler) &_estack,               // The initial stack pointer
    Reset_Handler,                     // The reset handler

    NMI_Handler,                       // The NMI handler
    HardFault_Handler,                 // The hard fault handler

#if defined(__ARM_ARCH_7M__) || defined(__ARM_ARCH_7EM__)
    MemManage_Handler,                 // The MPU fault handler
    BusFault_Handler,                  // The bus fault handler
    UsageFault_Handler,                // The usage fault handler
#else
    0,                                 // Reserved
    0,                                 // Reserved
    0,                                 // Reserved
#endif
    0,                                 // Reserved
    0,                                 // Reserved
    0,                                 // Reserved
    0,                                 // Reserved
    SVC_Handler,                       // SVCall handler
#if defined(__ARM_ARCH_7M__) || defined(__ARM_ARCH_7EM__)
    DebugMon_Handler,                  // Debug monitor handler
#else
    0,                                 // Reserved
#endif
    0,                                 // Reserved
    PendSV_Handler,                    // The PendSV handler
    SysTick_Handler,                   // The SysTick handler

    // ----------------------------------------------------------------------
    // External Interrupts
    WakeUp_Int_Handler,        // Wake Up Timer              [ 0]
    Ext_Int0_Handler,          // External Interrupt 0       [ 1]
    Ext_Int1_Handler,          // External Interrupt 1       [ 2]
    Ext_Int2_Handler,          // External Interrupt 2       [ 3]
    Ext_Int3_Handler,          // External Interrupt 3       [ 4]
    Ext_Int4_Handler,          // External Interrupt 4       [ 5]
    Ext_Int5_Handler,          // External Interrupt 5       [ 6]
    Ext_Int6_Handler,          // External Interrupt 6       [ 7]
    Ext_Int7_Handler,          // External Interrupt 7       [ 8]
    WDog_Tmr_Int_Handler,      // Watchdog timer handler     [ 9]
    0,                         //                            [10]
    GP_Tmr0_Int_Handler,       // General purpose timer 0    [11]
    GP_Tmr1_Int_Handler,       // General purpose timer 1    [12]
    ADC0_Int_Handler,          // ADC0 Interrupt             [13]
    ADC1_Int_Handler,          // ADC1 Interrupt             [14]
    SINC2_Int_Handler,         // SINC2 Interrupt            [15]
    Flsh_Int_Handler,          // Flash Interrupt            [16]
    UART_Int_Handler,          // UART0                      [17]
    SPI0_Int_Handler,          // SPI 0                      [18]
    SPI1_Int_Handler,          // SPI 1                      [19]
    I2C0_Slave_Int_Handler,    // I2C0 Slave                 [20]
    I2C0_Master_Int_Handler,   // I2C0 Master                [21]
    DMA_Err_Int_Handler,       // DMA Error interrupt        [22]
    DMA_SPI1_TX_Int_Handler,   // DMA SPI1 TX                [23]
    DMA_SPI1_RX_Int_Handler,   // DMA SPI1 RX                [24]
    DMA_UART_TX_Int_Handler,   // DMA UART TX                [25]
    DMA_UART_RX_Int_Handler,   // DMA UART RX                [26]
    DMA_I2C0_STX_Int_Handler,  // DMA I2C0 Slave TX          [27]
    DMA_I2C0_SRX_Int_Handler,  // DMA I2C0 Slave RX          [28]
    DMA_I2C0_MTX_Int_Handler,  // DMA I2C0 Master TX         [29]
    DMA_I2C0_MRX_Int_Handler,  // DMA I2C0 Master RX         [30]
    DMA_DAC_Out_Int_Handler,   // DMA DAC out                [31]
    DMA_ADC0_Int_Handler,      // DMA ADC0                   [32]
    DMA_ADC1_Int_Handler,      // DMA ADC1                   [33]
    DMA_SINC2_Int_Handler,     // SINC2                      [34]
    PWMTRIP_Int_Handler,       // PWMTRIP                    [35]
    PWM0_Int_Handler,          // PWM0                       [36]
    PWM1_Int_Handler,          // PWM1                       [37]
    PWM2_Int_Handler,          // PWM2                       [38]
    0,                         //                            [39]
};

// ----------------------------------------------------------------------------

// Processor ends up here if an unexpected interrupt occurs or a
// specific handler is not present in the application code.
// When in DEBUG, trigger a debug exception to clearly notify
// the user of the exception and help identify the cause.

void __attribute__ ((section(".after_vectors")))
Default_Handler(void)
{
#if defined(DEBUG)
__DEBUG_BKPT();
#endif
while (1)
  {
    ;
  }
}

// ----------------------------------------------------------------------------
