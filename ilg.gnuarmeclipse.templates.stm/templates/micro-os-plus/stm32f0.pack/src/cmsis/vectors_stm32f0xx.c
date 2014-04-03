//
// This file is part of the µOS++ III distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

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
WWDG_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PVD_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
RTC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
FLASH_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
RCC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI0_1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI2_3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI4_15_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TS_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel2_3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel4_5_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
ADC1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM1_BRK_UP_TRG_COM_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM1_CC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM6_DAC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM14_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM15_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM16_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM17_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USART1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USART2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
CEC_IRQHandler(void);

void __attribute__ ((weak, alias ("Default_Handler")))
ADC1_COMP_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
PVD_VDDIO2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
RCC_CRS_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TSC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM7_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USART3_4_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
CEC_CAN_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USB_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel4_5_6_7_IRQHandler(void);

// ----------------------------------------------------------------------------

extern unsigned int _estack;

typedef void
(* const pHandler)(void);

// ----------------------------------------------------------------------------

// The vector table.
// This relies on the linker script to place at correct location in memory.

__attribute__ ((section(".isr_vector")))
pHandler g_pfnVectors[] =
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

#if defined(STM32F030)

      // Chip Level - STM32F030
      WWDG_IRQHandler, //
      0, //
      RTC_IRQHandler, //
      FLASH_IRQHandler, //
      RCC_IRQHandler, //
      EXTI0_1_IRQHandler, //
      EXTI2_3_IRQHandler, //
      EXTI4_15_IRQHandler, //
      0, //
      DMA1_Channel1_IRQHandler, //
      DMA1_Channel2_3_IRQHandler, //
      DMA1_Channel4_5_IRQHandler, //
      ADC1_IRQHandler, //
      TIM1_BRK_UP_TRG_COM_IRQHandler, //
      TIM1_CC_IRQHandler, //
      0, //
      TIM3_IRQHandler, //
      0, //
      0, //
      TIM14_IRQHandler, //
      TIM15_IRQHandler, //
      TIM16_IRQHandler, //
      TIM17_IRQHandler, //
      I2C1_IRQHandler, //
      I2C2_IRQHandler, //
      SPI1_IRQHandler, //
      SPI2_IRQHandler, //
      USART1_IRQHandler, //
      USART2_IRQHandler, //
      0, //
      0, //
      0, //

#elif defined(STM32F031)

      // Chip Level - STM32F031 (was STM32F0xx LD)
      WWDG_IRQHandler, //
      PVD_IRQHandler, //
      RTC_IRQHandler, //
      FLASH_IRQHandler, //
      RCC_IRQHandler, //
      EXTI0_1_IRQHandler, //
      EXTI2_3_IRQHandler, //
      EXTI4_15_IRQHandler, //
      0, //
      DMA1_Channel1_IRQHandler, //
      DMA1_Channel2_3_IRQHandler, //
      DMA1_Channel4_5_IRQHandler, //
      ADC1_IRQHandler, //
      TIM1_BRK_UP_TRG_COM_IRQHandler, //
      TIM1_CC_IRQHandler, //
      TIM2_IRQHandler, //
      TIM3_IRQHandler, //
      0, //
      0, //
      TIM14_IRQHandler, //
      0, //
      TIM16_IRQHandler, //
      TIM17_IRQHandler, //
      I2C1_IRQHandler, //
      0, //
      SPI1_IRQHandler, //
      0, //
      USART1_IRQHandler, //
      0, //
      0, //
      0, //
      0, //

#elif defined(STM32F042)

      // Chip Level - STM32F042 (was STM32F0xx MD)
      WWDG_IRQHandler, //
      PVD_VDDIO2_IRQHandler, //
      RTC_IRQHandler, //
      FLASH_IRQHandler, //
      RCC_CRS_IRQHandler, //
      EXTI0_1_IRQHandler, //
      EXTI2_3_IRQHandler, //
      EXTI4_15_IRQHandler, //
      TSC_IRQHandler, //
      DMA1_Channel1_IRQHandler, //
      DMA1_Channel2_3_IRQHandler, //
      DMA1_Channel4_5_IRQHandler, //
      ADC1_IRQHandler, //
      TIM1_BRK_UP_TRG_COM_IRQHandler, //
      TIM1_CC_IRQHandler, //
      TIM2_IRQHandler, //
      TIM3_IRQHandler, //
      0, //
      0, //
      TIM14_IRQHandler, //
      0, //
      TIM16_IRQHandler, //
      TIM17_IRQHandler, //
      I2C1_IRQHandler, //
      0, //
      SPI1_IRQHandler, //
      SPI2_IRQHandler, //
      USART1_IRQHandler, //
      USART2_IRQHandler, //
      0, //
      CEC_CAN_IRQHandler, //
      USB_IRQHandler, //

#elif defined(STM32F051)

      // Chip Level - STM32F051 (was STM32F0xx MD)
      WWDG_IRQHandler, //
      PVD_IRQHandler, //
      RTC_IRQHandler, //
      FLASH_IRQHandler, //
      RCC_IRQHandler, //
      EXTI0_1_IRQHandler, //
      EXTI2_3_IRQHandler, //
      EXTI4_15_IRQHandler, //
      TS_IRQHandler, //
      DMA1_Channel1_IRQHandler, //
      DMA1_Channel2_3_IRQHandler, //
      DMA1_Channel4_5_IRQHandler, //
      ADC1_COMP_IRQHandler, //
      TIM1_BRK_UP_TRG_COM_IRQHandler, //
      TIM1_CC_IRQHandler, //
      TIM2_IRQHandler, //
      TIM3_IRQHandler, //
      TIM6_DAC_IRQHandler, //
      0, //
      TIM14_IRQHandler, //
      TIM15_IRQHandler, //
      TIM16_IRQHandler, //
      TIM17_IRQHandler, //
      I2C1_IRQHandler, //
      I2C2_IRQHandler, //
      SPI1_IRQHandler, //
      SPI2_IRQHandler, //
      USART1_IRQHandler, //
      USART2_IRQHandler, //
      0, //
      CEC_IRQHandler, //
      0, //

#elif defined(STM32F072)

      // Chip Level - STM32F051 (was STM32F0xx MD)
      WWDG_IRQHandler, //
      PVD_VDDIO2_IRQHandler, //
      RTC_IRQHandler, //
      FLASH_IRQHandler, //
      RCC_CRS_IRQHandler, //
      EXTI0_1_IRQHandler, //
      EXTI2_3_IRQHandler, //
      EXTI4_15_IRQHandler, //
      TSC_IRQHandler, //
      DMA1_Channel1_IRQHandler, //
      DMA1_Channel2_3_IRQHandler, //
      DMA1_Channel4_5_6_7_IRQHandler, //
      ADC1_COMP_IRQHandler, //
      TIM1_BRK_UP_TRG_COM_IRQHandler, //
      TIM1_CC_IRQHandler, //
      TIM2_IRQHandler, //
      TIM3_IRQHandler, //
      TIM6_DAC_IRQHandler, //
      TIM7_IRQHandler, //
      TIM14_IRQHandler, //
      TIM15_IRQHandler, //
      TIM16_IRQHandler, //
      TIM17_IRQHandler, //
      I2C1_IRQHandler, //
      I2C2_IRQHandler, //
      SPI1_IRQHandler, //
      SPI2_IRQHandler, //
      USART1_IRQHandler, //
      USART2_IRQHandler, //
      USART3_4_IRQHandler, //
      CEC_CAN_IRQHandler, //
      USB_IRQHandler, //

#else
#error "missing vectors"
#endif

      // @0x108. This is for boot in RAM mode for STM32F0xx devices.
      (pHandler) 0xF108F85F

  };

// ----------------------------------------------------------------------------

// Processor ends up here if an unexpected interrupt occurs or a specific
// handler is not present in the application code.

void __attribute__ ((section(".after_vectors")))
Default_Handler(void)
{
  while (1)
    {
    }
}

// ----------------------------------------------------------------------------
