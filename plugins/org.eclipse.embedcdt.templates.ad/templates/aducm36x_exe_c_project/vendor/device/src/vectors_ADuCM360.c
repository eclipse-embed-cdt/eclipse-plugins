/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2014 Liviu Ionescu.
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
//

void __attribute__ ((weak, alias ("Default_Handler"))) WakeUp_Int_Handler(void);        //[ 0]
void __attribute__ ((weak, alias ("Default_Handler"))) Ext_Int0_Handler(void);          //[ 1]
void __attribute__ ((weak, alias ("Default_Handler"))) Ext_Int1_Handler(void);          //[ 2]
void __attribute__ ((weak, alias ("Default_Handler"))) Ext_Int2_Handler(void);          //[ 3]
void __attribute__ ((weak, alias ("Default_Handler"))) Ext_Int3_Handler(void);          //[ 4]
void __attribute__ ((weak, alias ("Default_Handler"))) Ext_Int4_Handler(void);          //[ 5]
void __attribute__ ((weak, alias ("Default_Handler"))) Ext_Int5_Handler(void);          //[ 6]
void __attribute__ ((weak, alias ("Default_Handler"))) Ext_Int6_Handler(void);          //[ 7]
void __attribute__ ((weak, alias ("Default_Handler"))) Ext_Int7_Handler(void);          //[ 8]
void __attribute__ ((weak, alias ("Default_Handler"))) WDog_Tmr_Int_Handler(void);      //[ 9]
void __attribute__ ((weak, alias ("Default_Handler"))) GP_Tmr0_Int_Handler(void);       //[11]
void __attribute__ ((weak, alias ("Default_Handler"))) GP_Tmr1_Int_Handler(void);       //[12]
void __attribute__ ((weak, alias ("Default_Handler"))) ADC0_Int_Handler(void);          //[13]
void __attribute__ ((weak, alias ("Default_Handler"))) ADC1_Int_Handler(void);          //[14]
void __attribute__ ((weak, alias ("Default_Handler"))) SINC2_Int_Handler(void);         //[15]
void __attribute__ ((weak, alias ("Default_Handler"))) Flsh_Int_Handler(void);          //[16]
void __attribute__ ((weak, alias ("Default_Handler"))) UART_Int_Handler(void);          //[17]
void __attribute__ ((weak, alias ("Default_Handler"))) SPI0_Int_Handler(void);          //[18]
void __attribute__ ((weak, alias ("Default_Handler"))) SPI1_Int_Handler(void);          //[19]
void __attribute__ ((weak, alias ("Default_Handler"))) I2C0_Slave_Int_Handler(void);    //[20]
void __attribute__ ((weak, alias ("Default_Handler"))) I2C0_Master_Int_Handler(void);   //[21]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_Err_Int_Handler(void);       //[22]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_SPI1_TX_Int_Handler(void);   //[23]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_SPI1_RX_Int_Handler(void);   //[24]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_UART_TX_Int_Handler(void);   //[25]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_UART_RX_Int_Handler(void);   //[26]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_I2C0_STX_Int_Handler(void);  //[27]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_I2C0_SRX_Int_Handler(void);  //[28]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_I2C0_MTX_Int_Handler(void);  //[29]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_I2C0_MRX_Int_Handler(void);  //[30]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_DAC_Out_Int_Handler(void);   //[31]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_ADC0_Int_Handler(void);      //[32]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_ADC1_Int_Handler(void);      //[33]
void __attribute__ ((weak, alias ("Default_Handler"))) DMA_SINC2_Int_Handler(void);     //[34]
void __attribute__ ((weak, alias ("Default_Handler"))) PWMTRIP_Int_Handler(void);       //[35]
void __attribute__ ((weak, alias ("Default_Handler"))) PWM0_Int_Handler(void);          //[36]
void __attribute__ ((weak, alias ("Default_Handler"))) PWM1_Int_Handler(void);          //[37]
void __attribute__ ((weak, alias ("Default_Handler"))) PWM2_Int_Handler(void);          //[38]



// ----------------------------------------------------------------------------

extern unsigned int _estack;

typedef void
(* const pHandler)(void);

// ----------------------------------------------------------------------------

// The vector table.
// This relies on the linker script to place at correct location in memory.

__attribute__ ((section(".isr_vector")))
pHandler __isr_vectors[] = {
   //
   (pHandler) &_estack,                          // The initial stack pointer
   Reset_Handler,                            // The reset handler

   NMI_Handler,                              // The NMI handler
   HardFault_Handler,                        // The hard fault handler

#if defined(__ARM_ARCH_7M__) || defined(__ARM_ARCH_7EM__)
   MemManage_Handler,                        // The MPU fault handler
   BusFault_Handler,// The bus fault handler
   UsageFault_Handler,// The usage fault handler
#else
   0, 0, 0,             // Reserved
#endif
   0,                                        // Reserved
   0,                                        // Reserved
   0,                                        // Reserved
   0,                                        // Reserved
   SVC_Handler,                              // SVCall handler
#if defined(__ARM_ARCH_7M__) || defined(__ARM_ARCH_7EM__)
   DebugMon_Handler,                         // Debug monitor handler
#else
   0,                // Reserved
#endif
   0,                                        // Reserved
   PendSV_Handler,                           // The PendSV handler
   SysTick_Handler,                          // The SysTick handler

   // ----------------------------------------------------------------------
   // ADuCM360 vectors
   WakeUp_Int_Handler,        //[ 0]
   Ext_Int0_Handler,          //[ 1]
   Ext_Int1_Handler,          //[ 2]
   Ext_Int2_Handler,          //[ 3]
   Ext_Int3_Handler,          //[ 4]
   Ext_Int4_Handler,          //[ 5]
   Ext_Int5_Handler,          //[ 6]
   Ext_Int6_Handler,          //[ 7]
   Ext_Int7_Handler,          //[ 8]
   WDog_Tmr_Int_Handler,      //[ 9]
   0,                         //[10]
   GP_Tmr0_Int_Handler,       //[11]
   GP_Tmr1_Int_Handler,       //[12]
   ADC0_Int_Handler,          //[13]
   ADC1_Int_Handler,          //[14]
   SINC2_Int_Handler,         //[15]
   Flsh_Int_Handler,          //[16]
   UART_Int_Handler,          //[17]
   SPI0_Int_Handler,          //[18]
   SPI1_Int_Handler,          //[19]
   I2C0_Slave_Int_Handler,    //[20]
   I2C0_Master_Int_Handler,   //[21]
   DMA_Err_Int_Handler,       //[22]
   DMA_SPI1_TX_Int_Handler,   //[23]
   DMA_SPI1_RX_Int_Handler,   //[24]
   DMA_UART_TX_Int_Handler,   //[25]
   DMA_UART_RX_Int_Handler,   //[26]
   DMA_I2C0_STX_Int_Handler,  //[27]
   DMA_I2C0_SRX_Int_Handler,  //[28]
   DMA_I2C0_MTX_Int_Handler,  //[29]
   DMA_I2C0_MRX_Int_Handler,  //[30]
   DMA_DAC_Out_Int_Handler,   //[31]
   DMA_ADC0_Int_Handler,      //[32]
   DMA_ADC1_Int_Handler,      //[33]
   DMA_SINC2_Int_Handler,     //[34]
   PWMTRIP_Int_Handler,       //[35]
   PWM0_Int_Handler,          //[36]
   PWM1_Int_Handler,          //[37]
   PWM2_Int_Handler,          //[38]
   0,                         //[39]
};

// ----------------------------------------------------------------------------

// Processor ends up here if an unexpected interrupt occurs or a specific
// handler is not present in the application code.

void __attribute__ ((section(".after_vectors")))
Default_Handler(void)
{
   while (1) {
   }
}

// ----------------------------------------------------------------------------
