//
// This file is part of the GNU ARM Eclipse Plug-in
// Copyright (c) 2013 Liviu Ionescu
//

#if defined (__cplusplus)
extern "C"
  {
#endif

//*****************************************************************************
//
// Forward declaration of the default handlers. These are aliased.
// When the application defines a handler (with the same name), this will
// automatically take precedence over these weak definitions
//
//*****************************************************************************
void __attribute__((weak))
Reset_Handler(void);
void __attribute__((weak))
NMI_Handler(void);
void __attribute__((weak))
HardFault_Handler(void);
void __attribute__((weak))
SVC_Handler(void);
void __attribute__((weak))
PendSV_Handler(void);
void __attribute__((weak))
SysTick_Handler(void);

void __attribute__((weak))
Default_Handler(void);

//*****************************************************************************
//
// Forward declaration of the specific IRQ handlers. These are aliased
// to the Default_Handler, which is a 'forever' loop. When the application
// defines a handler (with the same name), this will automatically take
// precedence over these weak definitions
//
//*****************************************************************************
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
ADC1_COMP_IRQHandler(void);
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

//*****************************************************************************

extern unsigned int _estack;

typedef void
(* const pfn)(void);
//extern pfn g_pfnVectors[];

//*****************************************************************************
#if defined (__cplusplus)
} // extern "C"
#endif

//*****************************************************************************
//
// The vector table.
// This relies on the linker script to place at correct location in memory.
//
//*****************************************************************************

__attribute__ ((section(".isr_vector")))
pfn g_pfnVectors[] =
  {
  // Core Level - CM0
      (pfn) &_estack, // The initial stack pointer
      Reset_Handler, // The reset handler

      NMI_Handler, // The NMI handler
      HardFault_Handler, // The hard fault handler
      0, // Reserved
      0, // Reserved
      0, // Reserved
      0, // Reserved
      0, // Reserved
      0, // Reserved
      0, // Reserved
      SVC_Handler, // SVCall handler
      0, // Reserved
      0, // Reserved
      PendSV_Handler, // The PendSV handler
      SysTick_Handler, // The SysTick handler

#if defined(STM32F0XX_LD)

      // Chip Level - STM32F0xx LD
      WWDG_IRQHandler,// Window WatchDog
      PVD_IRQHandler,// PVD through EXTI Line detection
      RTC_IRQHandler,// RTC Wakeup through the EXTI line
      FLASH_IRQHandler,// FLASH
      RCC_IRQHandler,// RCC
      EXTI0_1_IRQHandler,//
      EXTI2_3_IRQHandler,//
      EXTI4_15_IRQHandler,//
      0,//
      DMA1_Channel1_IRQHandler,//
      DMA1_Channel2_3_IRQHandler,//
      DMA1_Channel4_5_IRQHandler,//
      ADC1_COMP_IRQHandler,//
      TIM1_BRK_UP_TRG_COM_IRQHandler,//
      TIM1_CC_IRQHandler,//
      TIM2_IRQHandler,//
      TIM3_IRQHandler,//
      0,//
      0,//
      TIM14_IRQHandler,//
      0,//
      TIM16_IRQHandler,//
      TIM17_IRQHandler,//
      I2C1_IRQHandler,//
      0,//
      SPI1_IRQHandler,//
      0,//
      USART1_IRQHandler,//
      0,//
      0,//
      0,//
      0,//

#elif defined(STM32F0XX_MD)

      // Chip Level - STM32F0xx MD
      WWDG_IRQHandler,// Window WatchDog
      PVD_IRQHandler,// PVD through EXTI Line detection
      RTC_IRQHandler,// RTC Wakeup through the EXTI line
      FLASH_IRQHandler,// FLASH
      RCC_IRQHandler,// RCC
      EXTI0_1_IRQHandler,//
      EXTI2_3_IRQHandler,//
      EXTI4_15_IRQHandler,//
      TS_IRQHandler,//
      DMA1_Channel1_IRQHandler,//
      DMA1_Channel2_3_IRQHandler,//
      DMA1_Channel4_5_IRQHandler,//
      ADC1_COMP_IRQHandler,//
      TIM1_BRK_UP_TRG_COM_IRQHandler,//
      TIM1_CC_IRQHandler,//
      TIM2_IRQHandler,//
      TIM3_IRQHandler,//
      TIM6_DAC_IRQHandler,//
      0,//
      TIM14_IRQHandler,//
      TIM15_IRQHandler,//
      TIM16_IRQHandler,//
      TIM17_IRQHandler,//
      I2C1_IRQHandler,//
      I2C2_IRQHandler,//
      SPI1_IRQHandler,//
      SPI2_IRQHandler,//
      USART1_IRQHandler,//
      USART2_IRQHandler,//
      0,//
      CEC_IRQHandler,//
      0,//

#elif defined(STM32F030)

      // Chip Level - STM32F030
      WWDG_IRQHandler,// Window WatchDog
      0,//
      RTC_IRQHandler,// RTC Wakeup through the EXTI line
      FLASH_IRQHandler,// FLASH
      RCC_IRQHandler,// RCC
      EXTI0_1_IRQHandler,//
      EXTI2_3_IRQHandler,//
      EXTI4_15_IRQHandler,//
      0,//
      DMA1_Channel1_IRQHandler,//
      DMA1_Channel2_3_IRQHandler,//
      DMA1_Channel4_5_IRQHandler,//
      ADC1_COMP_IRQHandler,//
      TIM1_BRK_UP_TRG_COM_IRQHandler,//
      TIM1_CC_IRQHandler,//
      0,//
      TIM3_IRQHandler,//
      0,//
      0,//
      TIM14_IRQHandler,//
      TIM15_IRQHandler,//
      TIM16_IRQHandler,//
      TIM17_IRQHandler,//
      I2C1_IRQHandler,//
      I2C2_IRQHandler,//
      SPI1_IRQHandler,//
      SPI2_IRQHandler,//
      USART1_IRQHandler,//
      USART2_IRQHandler,//
      0,//
      0,//
      0,//

#else
#error "missing vectors"
#endif

      // @0x108. This is for boot in RAM mode for STM32F0xx devices.
      (pfn) 0xF108F85F

  };

//*****************************************************************************
//
// Processor ends up here if an unexpected interrupt occurs or a specific
// handler is not present in the application code.
//
//*****************************************************************************

void __attribute__ ((section(".after_vectors")))
Default_Handler(void)
{
  while (1)
    {
    }
}
