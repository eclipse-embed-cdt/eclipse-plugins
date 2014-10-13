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
TAMPER_STAMP_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
RTC_WKUP_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
FLASH_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
RCC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI2_TS_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI4_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel4_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel5_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel6_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA1_Channel7_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
ADC1_2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USB_HP_CAN1_TX_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USB_LP_CAN1_RX0_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
CAN1_RX1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
CAN1_SCE_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI9_5_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM1_BRK_TIM15_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM1_UP_TIM16_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM1_TRG_COM_TIM17_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM1_CC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM4_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C1_EV_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C1_ER_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C2_EV_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
I2C2_ER_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USART1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USART2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USART3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
EXTI15_10_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
RTC_Alarm_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USBWakeUp_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM8_BRK_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM8_UP_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM8_TRG_COM_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM8_CC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
ADC3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
SPI3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
UART4_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
UART5_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM6_DAC_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
TIM7_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA2_Channel1_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA2_Channel2_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA2_Channel3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA2_Channel4_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
DMA2_Channel5_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
ADC4_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
COMP1_2_3_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
COMP4_5_6_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
COMP7_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USB_HP_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USB_LP_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
USBWakeUp_RMP_IRQHandler(void);
void __attribute__ ((weak, alias ("Default_Handler")))
FPU_IRQHandler(void);

// ----------------------------------------------------------------------------

extern unsigned int _estack;

typedef void
(* const pHandler)(void);

// ----------------------------------------------------------------------------

// The vector table.
// This relies on the linker script to place at correct location in memory.

__attribute__ ((section(".isr_vector"),used))
pHandler __isr_vectors[] =
  {
  // Core Level - CM4
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
      // Chip Level - STM32F30x
      WWDG_IRQHandler, //
      PVD_IRQHandler, //
      TAMPER_STAMP_IRQHandler, //
      RTC_WKUP_IRQHandler, //
      FLASH_IRQHandler, //
      RCC_IRQHandler, //
      EXTI0_IRQHandler, //
      EXTI1_IRQHandler, //
      EXTI2_TS_IRQHandler, //
      EXTI3_IRQHandler, //
      EXTI4_IRQHandler, //
      DMA1_Channel1_IRQHandler, //
      DMA1_Channel2_IRQHandler, //
      DMA1_Channel3_IRQHandler, //
      DMA1_Channel4_IRQHandler, //
      DMA1_Channel5_IRQHandler, //
      DMA1_Channel6_IRQHandler, //
      DMA1_Channel7_IRQHandler, //
      ADC1_2_IRQHandler, //
      USB_HP_CAN1_TX_IRQHandler, //
      USB_LP_CAN1_RX0_IRQHandler, //
      CAN1_RX1_IRQHandler, //
      CAN1_SCE_IRQHandler, //
      EXTI9_5_IRQHandler, //
      TIM1_BRK_TIM15_IRQHandler, //
      TIM1_UP_TIM16_IRQHandler, //
      TIM1_TRG_COM_TIM17_IRQHandler, //
      TIM1_CC_IRQHandler, //
      TIM2_IRQHandler, //
      TIM3_IRQHandler, //
      TIM4_IRQHandler, //
      I2C1_EV_IRQHandler, //
      I2C1_ER_IRQHandler, //
      I2C2_EV_IRQHandler, //
      I2C2_ER_IRQHandler, //
      SPI1_IRQHandler, //
      SPI2_IRQHandler, //
      USART1_IRQHandler, //
      USART2_IRQHandler, //
      USART3_IRQHandler, //
      EXTI15_10_IRQHandler, //
      RTC_Alarm_IRQHandler, //
      USBWakeUp_IRQHandler, //
      TIM8_BRK_IRQHandler, //
      TIM8_UP_IRQHandler, //
      TIM8_TRG_COM_IRQHandler, //
      TIM8_CC_IRQHandler, //
      ADC3_IRQHandler, //
      0, //
      0, //
      0, //
      SPI3_IRQHandler, //
      UART4_IRQHandler, //
      UART5_IRQHandler, //
      TIM6_DAC_IRQHandler, //
      TIM7_IRQHandler, //
      DMA2_Channel1_IRQHandler, //
      DMA2_Channel2_IRQHandler, //
      DMA2_Channel3_IRQHandler, //
      DMA2_Channel4_IRQHandler, //
      DMA2_Channel5_IRQHandler, //
      ADC4_IRQHandler, //
      0, //
      0, //
      COMP1_2_3_IRQHandler, //
      COMP4_5_6_IRQHandler, //
      COMP7_IRQHandler, //
      0, //
      0, //
      0, //
      0, //
      0, //
      0, //
      0, //
      USB_HP_IRQHandler, //
      USB_LP_IRQHandler, //
      USBWakeUp_RMP_IRQHandler, //
      0, //
      0, //
      0, //
      0, //
      FPU_IRQHandler //
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
