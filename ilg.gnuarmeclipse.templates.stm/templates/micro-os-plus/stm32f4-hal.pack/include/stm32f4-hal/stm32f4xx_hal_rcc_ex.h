/**
  ******************************************************************************
  * @file    stm32f4xx_hal_rcc_ex.h
  * @author  MCD Application Team
  * @version V1.2.0
  * @date    26-December-2014
  * @brief   Header file of RCC HAL Extension module.
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT(c) 2014 STMicroelectronics</center></h2>
  *
  * Redistribution and use in source and binary forms, with or without modification,
  * are permitted provided that the following conditions are met:
  *   1. Redistributions of source code must retain the above copyright notice,
  *      this list of conditions and the following disclaimer.
  *   2. Redistributions in binary form must reproduce the above copyright notice,
  *      this list of conditions and the following disclaimer in the documentation
  *      and/or other materials provided with the distribution.
  *   3. Neither the name of STMicroelectronics nor the names of its contributors
  *      may be used to endorse or promote products derived from this software
  *      without specific prior written permission.
  *
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  *
  ******************************************************************************
  */ 

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __STM32F4xx_HAL_RCC_EX_H
#define __STM32F4xx_HAL_RCC_EX_H

#ifdef __cplusplus
 extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f4xx_hal_def.h"

/** @addtogroup STM32F4xx_HAL_Driver
  * @{
  */

/** @addtogroup RCCEx
  * @{
  */ 

/* Exported types ------------------------------------------------------------*/
/** @defgroup RCCEx_Exported_Types RCCEx Exported Types
  * @{
  */
   
#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx)|| defined(STM32F439xx)
/** 
  * @brief  PLLI2S Clock structure definition  
  */
typedef struct
{
  uint32_t PLLI2SN;    /*!< Specifies the multiplication factor for PLLI2S VCO output clock.
                            This parameter must be a number between Min_Data = 192 and Max_Data = 432.
                            This parameter will be used only when PLLI2S is selected as Clock Source I2S or SAI */

  uint32_t PLLI2SR;    /*!< Specifies the division factor for I2S clock.
                            This parameter must be a number between Min_Data = 2 and Max_Data = 7. 
                            This parameter will be used only when PLLI2S is selected as Clock Source I2S or SAI */

  uint32_t PLLI2SQ;    /*!< Specifies the division factor for SAI1 clock.
                            This parameter must be a number between Min_Data = 2 and Max_Data = 15. 
                            This parameter will be used only when PLLI2S is selected as Clock Source SAI */
}RCC_PLLI2SInitTypeDef;

/** 
  * @brief  PLLSAI Clock structure definition  
  */
typedef struct
{
  uint32_t PLLSAIN;    /*!< Specifies the multiplication factor for PLLI2S VCO output clock.
                            This parameter must be a number between Min_Data = 192 and Max_Data = 432.
                            This parameter will be used only when PLLSAI is selected as Clock Source SAI or LTDC */ 
                                 
  uint32_t PLLSAIQ;    /*!< Specifies the division factor for SAI1 clock.
                            This parameter must be a number between Min_Data = 2 and Max_Data = 15.
                            This parameter will be used only when PLLSAI is selected as Clock Source SAI or LTDC */
                              
  uint32_t PLLSAIR;    /*!< specifies the division factor for LTDC clock
                            This parameter must be a number between Min_Data = 2 and Max_Data = 7.
                            This parameter will be used only when PLLSAI is selected as Clock Source LTDC */

}RCC_PLLSAIInitTypeDef;
/** 
  * @brief  RCC extended clocks structure definition  
  */
typedef struct
{
  uint32_t PeriphClockSelection; /*!< The Extended Clock to be configured.
                                      This parameter can be a value of @ref RCCEx_Periph_Clock_Selection */

  RCC_PLLI2SInitTypeDef PLLI2S;  /*!< PLL I2S structure parameters. 
                                      This parameter will be used only when PLLI2S is selected as Clock Source I2S or SAI */

  RCC_PLLSAIInitTypeDef PLLSAI;  /*!< PLL SAI structure parameters. 
                                      This parameter will be used only when PLLI2S is selected as Clock Source SAI or LTDC */

  uint32_t PLLI2SDivQ;           /*!< Specifies the PLLI2S division factor for SAI1 clock.
                                      This parameter must be a number between Min_Data = 1 and Max_Data = 32
                                      This parameter will be used only when PLLI2S is selected as Clock Source SAI */

  uint32_t PLLSAIDivQ;           /*!< Specifies the PLLI2S division factor for SAI1 clock.
                                      This parameter must be a number between Min_Data = 1 and Max_Data = 32
                                      This parameter will be used only when PLLSAI is selected as Clock Source SAI */

  uint32_t PLLSAIDivR;           /*!< Specifies the PLLSAI division factor for LTDC clock.
                                      This parameter must be one value of @ref RCCEx_PLLSAI_DIVR */

  uint32_t RTCClockSelection;      /*!< Specifies RTC Clock Prescalers Selection. 
                                      This parameter can be a value of @ref RCC_RTC_Clock_Source */

  uint8_t TIMPresSelection;      /*!< Specifies TIM Clock Prescalers Selection. 
                                      This parameter can be a value of @ref RCCEx_TIM_PRescaler_Selection */

}RCC_PeriphCLKInitTypeDef;
#endif /* STM32F427xx || STM32F437xx || STM32F429xx || STM32F439xx */

#if defined(STM32F405xx) || defined(STM32F415xx) || defined(STM32F407xx)|| defined(STM32F417xx) ||\
    defined(STM32F401xC) || defined(STM32F401xE) || defined(STM32F411xE)
/** 
  * @brief  PLLI2S Clock structure definition  
  */
typedef struct
{
#if defined(STM32F411xE) 
  uint32_t PLLI2SM;    /*!< PLLM: Division factor for PLLI2S VCO input clock.
                            This parameter must be a number between Min_Data = 2 and Max_Data = 62  */
#endif /* STM32F411xE */
                                
  uint32_t PLLI2SN;    /*!< Specifies the multiplication factor for PLLI2S VCO output clock.
                            This parameter must be a number between Min_Data = 192 and Max_Data = 432
                            This parameter will be used only when PLLI2S is selected as Clock Source I2S or SAI */

  uint32_t PLLI2SR;    /*!< Specifies the division factor for I2S clock.
                            This parameter must be a number between Min_Data = 2 and Max_Data = 7. 
                            This parameter will be used only when PLLI2S is selected as Clock Source I2S or SAI */

}RCC_PLLI2SInitTypeDef;

 
/** 
  * @brief  RCC extended clocks structure definition  
  */
typedef struct
{
  uint32_t PeriphClockSelection; /*!< The Extended Clock to be configured.
                                      This parameter can be a value of @ref RCCEx_Periph_Clock_Selection */

  RCC_PLLI2SInitTypeDef PLLI2S;  /*!< PLL I2S structure parameters.
                                      This parameter will be used only when PLLI2S is selected as Clock Source I2S or SAI */

  uint32_t RTCClockSelection;      /*!< Specifies RTC Clock Prescalers Selection.
                                       This parameter can be a value of @ref RCC_RTC_Clock_Source */                                   

}RCC_PeriphCLKInitTypeDef;
#endif /* STM32F405xx || STM32F415xx || STM32F407xx || STM32F417xx || STM32F401xC || STM32F401xE || STM32F411xE */
/**
  * @}
  */ 

/* Exported constants --------------------------------------------------------*/
/** @defgroup RCCEx_Exported_Constants RCCEx Exported Constants
  * @{
  */

/** @defgroup RCCEx_Periph_Clock_Selection RCC Periph Clock Selection
  * @{
  */
#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx)|| defined(STM32F439xx)
#define RCC_PERIPHCLK_I2S             ((uint32_t)0x00000001)
#define RCC_PERIPHCLK_SAI_PLLI2S      ((uint32_t)0x00000002)
#define RCC_PERIPHCLK_SAI_PLLSAI      ((uint32_t)0x00000004)
#define RCC_PERIPHCLK_LTDC            ((uint32_t)0x00000008)
#define RCC_PERIPHCLK_TIM             ((uint32_t)0x00000010)
#define RCC_PERIPHCLK_RTC             ((uint32_t)0x00000020)
#endif /* STM32F427xx || STM32F437xx || STM32F429xx || STM32F439xx */

#if defined(STM32F405xx) || defined(STM32F415xx) || defined(STM32F407xx)|| defined(STM32F417xx) ||\
    defined(STM32F401xC) || defined(STM32F401xE) || defined(STM32F411xE) 
#define RCC_PERIPHCLK_I2S             ((uint32_t)0x00000001)
#define RCC_PERIPHCLK_RTC             ((uint32_t)0x00000002)
#endif /* STM32F405xx || STM32F415xx || STM32F407xx || STM32F417xx || STM32F401xC || STM32F401xE || STM32F411xE */
/**
  * @}
  */
 
#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx)|| defined(STM32F439xx) 
/** @defgroup RCCEx_PLLSAI_DIVR          RCC PLLSAI DIVR
  * @{
  */
#define RCC_PLLSAIDIVR_2                ((uint32_t)0x00000000)
#define RCC_PLLSAIDIVR_4                ((uint32_t)0x00010000)
#define RCC_PLLSAIDIVR_8                ((uint32_t)0x00020000)
#define RCC_PLLSAIDIVR_16               ((uint32_t)0x00030000)
/**
  * @}
  */
   
/** @defgroup RCCEx_SAI_BlockA_Clock_Source  RCC SAI BlockA Clock Source
  * @{
  */
#define RCC_SAIACLKSOURCE_PLLSAI             ((uint32_t)0x00000000)
#define RCC_SAIACLKSOURCE_PLLI2S             ((uint32_t)0x00100000)
#define RCC_SAIACLKSOURCE_EXT                ((uint32_t)0x00200000)
/**
  * @}
  */ 

/** @defgroup RCCEx_SAI_BlockB_Clock_Source  RCC SAI BlockB Clock Source
  * @{
  */
#define RCC_SAIBCLKSOURCE_PLLSAI             ((uint32_t)0x00000000)
#define RCC_SAIBCLKSOURCE_PLLI2S             ((uint32_t)0x00400000)
#define RCC_SAIBCLKSOURCE_EXT                ((uint32_t)0x00800000)
/**
  * @}
  */ 

/** @defgroup RCCEx_TIM_PRescaler_Selection  RCC TIM PRescaler Selection
  * @{
  */
#define RCC_TIMPRES_DESACTIVATED        ((uint8_t)0x00)
#define RCC_TIMPRES_ACTIVATED           ((uint8_t)0x01)
/**
  * @}
  */
#endif /* STM32F427xx || STM32F437xx || STM32F429xx || STM32F439xx */

#if defined(STM32F411xE) 
/** @defgroup RCCEx_LSE_Dual_Mode_Selection  RCC LSE Dual Mode Selection
  * @{
  */
#define RCC_LSE_LOWPOWER_MODE           ((uint8_t)0x00)
#define RCC_LSE_HIGHDRIVE_MODE          ((uint8_t)0x01)
/**
  * @}
  */
#endif /* STM32F411xE */

/**
  * @}
  */
     
/* Exported macro ------------------------------------------------------------*/
/** @defgroup RCCEx_Exported_Macros RCCEx Exported Macros
  * @{
  */
/*----------------------------------- STM32F42xxx/STM32F43xxx----------------------------------*/
#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx)|| defined(STM32F439xx)
/** @brief  Enables or disables the AHB1 peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it.
  */
#define __HAL_RCC_GPIOI_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOIEN))
#define __HAL_RCC_GPIOF_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOFEN))
#define __HAL_RCC_GPIOG_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOGEN))
#define __HAL_RCC_GPIOJ_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOJEN))
#define __HAL_RCC_GPIOK_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOKEN))
#define __HAL_RCC_DMA2D_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_DMA2DEN))
#define __HAL_RCC_ETHMAC_CLK_ENABLE()           (RCC->AHB1ENR |= (RCC_AHB1ENR_ETHMACEN))
#define __HAL_RCC_ETHMACTX_CLK_ENABLE()         (RCC->AHB1ENR |= (RCC_AHB1ENR_ETHMACTXEN))
#define __HAL_RCC_ETHMACRX_CLK_ENABLE()         (RCC->AHB1ENR |= (RCC_AHB1ENR_ETHMACRXEN))
#define __HAL_RCC_ETHMACPTP_CLK_ENABLE()        (RCC->AHB1ENR |= (RCC_AHB1ENR_ETHMACPTPEN))
#define __HAL_RCC_USB_OTG_HS_CLK_ENABLE()       (RCC->AHB1ENR |= (RCC_AHB1ENR_OTGHSEN))
#define __HAL_RCC_USB_OTG_HS_ULPI_CLK_ENABLE()  (RCC->AHB1ENR |= (RCC_AHB1ENR_OTGHSULPIEN))

#define __HAL_RCC_GPIOF_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_GPIOFEN))
#define __HAL_RCC_GPIOG_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_GPIOGEN))
#define __HAL_RCC_GPIOI_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_GPIOIEN))
#define __HAL_RCC_GPIOJ_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_GPIOJEN))
#define __HAL_RCC_GPIOK_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_GPIOKEN))
#define __HAL_RCC_DMA2D_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_DMA2DEN))
#define __HAL_RCC_ETHMAC_CLK_DISABLE()          (RCC->AHB1ENR &= ~(RCC_AHB1ENR_ETHMACEN))
#define __HAL_RCC_ETHMACTX_CLK_DISABLE()        (RCC->AHB1ENR &= ~(RCC_AHB1ENR_ETHMACTXEN))
#define __HAL_RCC_ETHMACRX_CLK_DISABLE()        (RCC->AHB1ENR &= ~(RCC_AHB1ENR_ETHMACRXEN))
#define __HAL_RCC_ETHMACPTP_CLK_DISABLE()       (RCC->AHB1ENR &= ~(RCC_AHB1ENR_ETHMACPTPEN))
#define __HAL_RCC_USB_OTG_HS_CLK_DISABLE()      (RCC->AHB1ENR &= ~(RCC_AHB1ENR_OTGHSEN))
#define __HAL_RCC_USB_OTG_HS_ULPI_CLK_DISABLE() (RCC->AHB1ENR &= ~(RCC_AHB1ENR_OTGHSULPIEN))

/**
  * @brief  Enable ETHERNET clock.
  */
#define __HAL_RCC_ETH_CLK_ENABLE() do {                                     \
                                        __HAL_RCC_ETHMAC_CLK_ENABLE();      \
                                        __HAL_RCC_ETHMACTX_CLK_ENABLE();    \
                                        __HAL_RCC_ETHMACRX_CLK_ENABLE();    \
                                      } while(0)
/**
  * @brief  Disable ETHERNET clock.
  */
#define __HAL_RCC_ETH_CLK_DISABLE()  do {                                      \
                                          __HAL_RCC_ETHMACTX_CLK_DISABLE();    \
                                          __HAL_RCC_ETHMACRX_CLK_DISABLE();    \
                                          __HAL_RCC_ETHMAC_CLK_DISABLE();      \
                                        } while(0)
                                     
/** @brief  Enable or disable the AHB2 peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it.
  */
 
#define __HAL_RCC_DCMI_CLK_ENABLE()   (RCC->AHB2ENR |= (RCC_AHB2ENR_DCMIEN))
#define __HAL_RCC_DCMI_CLK_DISABLE()  (RCC->AHB2ENR &= ~(RCC_AHB2ENR_DCMIEN))

#if defined(STM32F437xx)|| defined(STM32F439xx)
#define __HAL_RCC_CRYP_CLK_ENABLE()   (RCC->AHB2ENR |= (RCC_AHB2ENR_CRYPEN))
#define __HAL_RCC_HASH_CLK_ENABLE()   (RCC->AHB2ENR |= (RCC_AHB2ENR_HASHEN))

#define __HAL_RCC_CRYP_CLK_DISABLE()  (RCC->AHB2ENR &= ~(RCC_AHB2ENR_CRYPEN))
#define __HAL_RCC_HASH_CLK_DISABLE()  (RCC->AHB2ENR &= ~(RCC_AHB2ENR_HASHEN))
#endif /* STM32F437xx || STM32F439xx */

/** @brief  Enables or disables the AHB3 peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it. 
  */
#define __HAL_RCC_FMC_CLK_ENABLE()   (RCC->AHB3ENR |= (RCC_AHB3ENR_FMCEN))
#define __HAL_RCC_FMC_CLK_DISABLE()  (RCC->AHB3ENR &= ~(RCC_AHB3ENR_FMCEN))

/** @brief  Enable or disable the Low Speed APB (APB1) peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it. 
  */
#define __HAL_RCC_TIM6_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_TIM6EN))
#define __HAL_RCC_TIM7_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_TIM7EN))
#define __HAL_RCC_TIM12_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_TIM12EN))
#define __HAL_RCC_TIM13_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_TIM13EN))
#define __HAL_RCC_TIM14_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_TIM14EN))
#define __HAL_RCC_WWDG_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_WWDGEN))
#define __HAL_RCC_USART3_CLK_ENABLE()  (RCC->APB1ENR |= (RCC_APB1ENR_USART3EN))
#define __HAL_RCC_UART4_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_UART4EN))
#define __HAL_RCC_UART5_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_UART5EN))
#define __HAL_RCC_CAN1_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_CAN1EN))
#define __HAL_RCC_CAN2_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_CAN2EN))
#define __HAL_RCC_DAC_CLK_ENABLE()     (RCC->APB1ENR |= (RCC_APB1ENR_DACEN))
#define __HAL_RCC_UART7_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_UART7EN))
#define __HAL_RCC_UART8_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_UART8EN))

#define __HAL_RCC_TIM6_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM6EN))
#define __HAL_RCC_TIM7_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM7EN))
#define __HAL_RCC_TIM12_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM12EN))
#define __HAL_RCC_TIM13_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM13EN))
#define __HAL_RCC_TIM14_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM14EN))
#define __HAL_RCC_WWDG_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_WWDGEN))
#define __HAL_RCC_USART3_CLK_DISABLE() (RCC->APB1ENR &= ~(RCC_APB1ENR_USART3EN))
#define __HAL_RCC_UART4_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_UART4EN))
#define __HAL_RCC_UART5_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_UART5EN))
#define __HAL_RCC_CAN1_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_CAN1EN))
#define __HAL_RCC_CAN2_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_CAN2EN))
#define __HAL_RCC_DAC_CLK_DISABLE()    (RCC->APB1ENR &= ~(RCC_APB1ENR_DACEN))
#define __HAL_RCC_UART7_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_UART7EN))
#define __HAL_RCC_UART8_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_UART8EN))

/** @brief  Enable or disable the High Speed APB (APB2) peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it.
  */
#define __HAL_RCC_TIM8_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_TIM8EN))
#define __HAL_RCC_ADC2_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_ADC2EN))
#define __HAL_RCC_ADC3_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_ADC3EN))
#define __HAL_RCC_SPI5_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_SPI5EN))
#define __HAL_RCC_SPI6_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_SPI6EN))
#define __HAL_RCC_SAI1_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_SAI1EN))

#define __HAL_RCC_TIM8_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_TIM8EN))
#define __HAL_RCC_ADC2_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_ADC2EN))
#define __HAL_RCC_ADC3_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_ADC3EN))
#define __HAL_RCC_SPI5_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_SPI5EN))
#define __HAL_RCC_SPI6_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_SPI6EN))
#define __HAL_RCC_SAI1_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_SAI1EN))

#if defined(STM32F429xx)|| defined(STM32F439xx)
#define __HAL_RCC_LTDC_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_LTDCEN))

#define __HAL_RCC_LTDC_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_LTDCEN))
#endif /* STM32F429xx || STM32F439xx */

/** @brief  Force or release AHB1 peripheral reset.
  */  
#define __HAL_RCC_GPIOF_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOFRST))
#define __HAL_RCC_GPIOG_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOGRST))
#define __HAL_RCC_GPIOI_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOIRST))
#define __HAL_RCC_ETHMAC_FORCE_RESET()   (RCC->AHB1RSTR |= (RCC_AHB1RSTR_ETHMACRST))
#define __HAL_RCC_OTGHS_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_OTGHRST))
#define __HAL_RCC_GPIOJ_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOJRST))
#define __HAL_RCC_GPIOK_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOKRST))
#define __HAL_RCC_DMA2D_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_DMA2DRST))

#define __HAL_RCC_GPIOF_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOFRST))
#define __HAL_RCC_GPIOG_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOGRST))
#define __HAL_RCC_GPIOI_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOIRST))
#define __HAL_RCC_ETHMAC_RELEASE_RESET() (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_ETHMACRST))
#define __HAL_RCC_OTGHS_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_OTGHRST))
#define __HAL_RCC_GPIOJ_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOJRST))
#define __HAL_RCC_GPIOK_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOKRST))
#define __HAL_RCC_DMA2D_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_DMA2DRST))
 
/** @brief  Force or release AHB2 peripheral reset.
  */
#define __HAL_RCC_DCMI_FORCE_RESET()   (RCC->AHB2RSTR |= (RCC_AHB2RSTR_DCMIRST))
#define __HAL_RCC_DCMI_RELEASE_RESET() (RCC->AHB2RSTR &= ~(RCC_AHB2RSTR_DCMIRST))

#if defined(STM32F437xx)|| defined(STM32F439xx) 
#define __HAL_RCC_CRYP_FORCE_RESET()   (RCC->AHB2RSTR |= (RCC_AHB2RSTR_CRYPRST))
#define __HAL_RCC_HASH_FORCE_RESET()   (RCC->AHB2RSTR |= (RCC_AHB2RSTR_HASHRST))

#define __HAL_RCC_CRYP_RELEASE_RESET() (RCC->AHB2RSTR &= ~(RCC_AHB2RSTR_CRYPRST))
#define __HAL_RCC_HASH_RELEASE_RESET() (RCC->AHB2RSTR &= ~(RCC_AHB2RSTR_HASHRST))
#endif /* STM32F437xx || STM32F439xx */

/** @brief  Force or release AHB3 peripheral reset
  */ 
#define __HAL_RCC_FMC_FORCE_RESET()    (RCC->AHB3RSTR |= (RCC_AHB3RSTR_FMCRST))
#define __HAL_RCC_FMC_RELEASE_RESET()  (RCC->AHB3RSTR &= ~(RCC_AHB3RSTR_FMCRST))
 
/** @brief  Force or release APB1 peripheral reset.
  */ 
#define __HAL_RCC_TIM6_FORCE_RESET()     (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM6RST))
#define __HAL_RCC_TIM7_FORCE_RESET()     (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM7RST))
#define __HAL_RCC_TIM12_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM12RST))
#define __HAL_RCC_TIM13_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM13RST))
#define __HAL_RCC_TIM14_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM14RST))
#define __HAL_RCC_USART3_FORCE_RESET()   (RCC->APB1RSTR |= (RCC_APB1RSTR_USART3RST))
#define __HAL_RCC_UART4_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_UART4RST))
#define __HAL_RCC_UART5_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_UART5RST))
#define __HAL_RCC_CAN1_FORCE_RESET()     (RCC->APB1RSTR |= (RCC_APB1RSTR_CAN1RST))
#define __HAL_RCC_CAN2_FORCE_RESET()     (RCC->APB1RSTR |= (RCC_APB1RSTR_CAN2RST))
#define __HAL_RCC_DAC_FORCE_RESET()      (RCC->APB1RSTR |= (RCC_APB1RSTR_DACRST))
#define __HAL_RCC_UART7_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_UART7RST))
#define __HAL_RCC_UART8_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_UART8RST))

#define __HAL_RCC_TIM6_RELEASE_RESET()   (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM6RST))
#define __HAL_RCC_TIM7_RELEASE_RESET()   (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM7RST))
#define __HAL_RCC_TIM12_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM12RST))
#define __HAL_RCC_TIM13_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM13RST))
#define __HAL_RCC_TIM14_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM14RST))
#define __HAL_RCC_USART3_RELEASE_RESET() (RCC->APB1RSTR &= ~(RCC_APB1RSTR_USART3RST))
#define __HAL_RCC_UART4_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_UART4RST))
#define __HAL_RCC_UART5_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_UART5RST))
#define __HAL_RCC_CAN1_RELEASE_RESET()   (RCC->APB1RSTR &= ~(RCC_APB1RSTR_CAN1RST))
#define __HAL_RCC_CAN2_RELEASE_RESET()   (RCC->APB1RSTR &= ~(RCC_APB1RSTR_CAN2RST))
#define __HAL_RCC_DAC_RELEASE_RESET()    (RCC->APB1RSTR &= ~(RCC_APB1RSTR_DACRST))
#define __HAL_RCC_UART7_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_UART7RST))
#define __HAL_RCC_UART8_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_UART8RST))

/** @brief  Force or release APB2 peripheral reset.
  */
#define __HAL_RCC_TIM8_FORCE_RESET()   (RCC->APB2RSTR |= (RCC_APB2RSTR_TIM8RST))
#define __HAL_RCC_SPI5_FORCE_RESET()   (RCC->APB2RSTR |= (RCC_APB2RSTR_SPI5RST))
#define __HAL_RCC_SPI6_FORCE_RESET()   (RCC->APB2RSTR |= (RCC_APB2RSTR_SPI6RST))
#define __HAL_RCC_SAI1_FORCE_RESET()   (RCC->APB2RSTR |= (RCC_APB2RSTR_SAI1RST))

#define __HAL_RCC_TIM8_RELEASE_RESET() (RCC->APB2RSTR &= ~(RCC_APB2RSTR_TIM8RST))
#define __HAL_RCC_SPI5_RELEASE_RESET() (RCC->APB2RSTR &= ~(RCC_APB2RSTR_SPI5RST))
#define __HAL_RCC_SPI6_RELEASE_RESET() (RCC->APB2RSTR &= ~(RCC_APB2RSTR_SPI6RST))
#define __HAL_RCC_SAI1_RELEASE_RESET() (RCC->APB2RSTR &= ~(RCC_APB2RSTR_SAI1RST))

#if defined(STM32F429xx)|| defined(STM32F439xx)
#define __HAL_RCC_LTDC_FORCE_RESET()   (RCC->APB2RSTR |= (RCC_APB2RSTR_LTDCRST))
#define __HAL_RCC_LTDC_RELEASE_RESET() (RCC->APB2RSTR &= ~(RCC_APB2RSTR_LTDCRST))
#endif /* STM32F429xx|| STM32F439xx */

/** @brief  Enable or disable the AHB1 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */ 
#define __HAL_RCC_GPIOF_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_GPIOFLPEN))
#define __HAL_RCC_GPIOG_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_GPIOGLPEN))
#define __HAL_RCC_GPIOI_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_GPIOILPEN))
#define __HAL_RCC_SRAM2_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_SRAM2LPEN))
#define __HAL_RCC_ETHMAC_CLK_SLEEP_ENABLE()     (RCC->AHB1LPENR |= (RCC_AHB1LPENR_ETHMACLPEN))
#define __HAL_RCC_ETHMACTX_CLK_SLEEP_ENABLE()   (RCC->AHB1LPENR |= (RCC_AHB1LPENR_ETHMACTXLPEN))
#define __HAL_RCC_ETHMACRX_CLK_SLEEP_ENABLE()   (RCC->AHB1LPENR |= (RCC_AHB1LPENR_ETHMACRXLPEN))
#define __HAL_RCC_ETHMACPTP_CLK_SLEEP_ENABLE()  (RCC->AHB1LPENR |= (RCC_AHB1LPENR_ETHMACPTPLPEN))
#define __HAL_RCC_OTGHS_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_OTGHSLPEN))
#define __HAL_RCC_OTGHSULPI_CLK_SLEEP_ENABLE()  (RCC->AHB1LPENR |= (RCC_AHB1LPENR_OTGHSULPILPEN))
#define __HAL_RCC_GPIOJ_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_GPIOJLPEN))
#define __HAL_RCC_GPIOK_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_GPIOKLPEN))
#define __HAL_RCC_SRAM3_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_SRAM3LPEN))
#define __HAL_RCC_DMA2D_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_DMA2DLPEN))

#define __HAL_RCC_GPIOF_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_GPIOFLPEN))
#define __HAL_RCC_GPIOG_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_GPIOGLPEN))
#define __HAL_RCC_GPIOI_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_GPIOILPEN))
#define __HAL_RCC_SRAM2_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_SRAM2LPEN))
#define __HAL_RCC_ETHMAC_CLK_SLEEP_DISABLE()    (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_ETHMACLPEN))
#define __HAL_RCC_ETHMACTX_CLK_SLEEP_DISABLE()  (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_ETHMACTXLPEN))
#define __HAL_RCC_ETHMACRX_CLK_SLEEP_DISABLE()  (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_ETHMACRXLPEN))
#define __HAL_RCC_ETHMACPTP_CLK_SLEEP_DISABLE() (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_ETHMACPTPLPEN))
#define __HAL_RCC_OTGHS_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_OTGHSLPEN))
#define __HAL_RCC_OTGHSULPI_CLK_SLEEP_DISABLE() (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_OTGHSULPILPEN))
#define __HAL_RCC_GPIOJ_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_GPIOJLPEN))
#define __HAL_RCC_GPIOK_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_GPIOKLPEN))
#define __HAL_RCC_DMA2D_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_DMA2DLPEN))

/** @brief  Enable or disable the AHB2 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */
#define __HAL_RCC_DCMI_CLK_SLEEP_ENABLE()  (RCC->AHB2LPENR |= (RCC_AHB2LPENR_DCMILPEN))
#define __HAL_RCC_DCMI_CLK_SLEEP_DISABLE() (RCC->AHB2LPENR &= ~(RCC_AHB2LPENR_DCMILPEN))

#if defined(STM32F437xx)|| defined(STM32F439xx) 
#define __HAL_RCC_CRYP_CLK_SLEEP_ENABLE()  (RCC->AHB2LPENR |= (RCC_AHB2LPENR_CRYPLPEN))
#define __HAL_RCC_HASH_CLK_SLEEP_ENABLE()  (RCC->AHB2LPENR |= (RCC_AHB2LPENR_HASHLPEN))

#define __HAL_RCC_CRYP_CLK_SLEEP_DISABLE() (RCC->AHB2LPENR &= ~(RCC_AHB2LPENR_CRYPLPEN))
#define __HAL_RCC_HASH_CLK_SLEEP_DISABLE() (RCC->AHB2LPENR &= ~(RCC_AHB2LPENR_HASHLPEN))
#endif /* STM32F437xx || STM32F439xx */

/** @brief  Enable or disable the AHB3 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */
#define __HAL_RCC_FMC_CLK_SLEEP_ENABLE()  (RCC->AHB3LPENR |= (RCC_AHB3LPENR_FMCLPEN))
#define __HAL_RCC_FMC_CLK_SLEEP_DISABLE() (RCC->AHB3LPENR &= ~(RCC_AHB3LPENR_FMCLPEN))

/** @brief  Enable or disable the APB1 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */  
#define __HAL_RCC_TIM6_CLK_SLEEP_ENABLE()    (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM6LPEN))
#define __HAL_RCC_TIM7_CLK_SLEEP_ENABLE()    (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM7LPEN))
#define __HAL_RCC_TIM12_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM12LPEN))
#define __HAL_RCC_TIM13_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM13LPEN))
#define __HAL_RCC_TIM14_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM14LPEN))
#define __HAL_RCC_USART3_CLK_SLEEP_ENABLE()  (RCC->APB1LPENR |= (RCC_APB1LPENR_USART3LPEN))
#define __HAL_RCC_UART4_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_UART4LPEN))
#define __HAL_RCC_UART5_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_UART5LPEN))
#define __HAL_RCC_CAN1_CLK_SLEEP_ENABLE()    (RCC->APB1LPENR |= (RCC_APB1LPENR_CAN1LPEN))
#define __HAL_RCC_CAN2_CLK_SLEEP_ENABLE()    (RCC->APB1LPENR |= (RCC_APB1LPENR_CAN2LPEN))
#define __HAL_RCC_DAC_CLK_SLEEP_ENABLE()     (RCC->APB1LPENR |= (RCC_APB1LPENR_DACLPEN))
#define __HAL_RCC_UART7_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_UART7LPEN))
#define __HAL_RCC_UART8_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_UART8LPEN))

#define __HAL_RCC_TIM6_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM6LPEN))
#define __HAL_RCC_TIM7_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM7LPEN))
#define __HAL_RCC_TIM12_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM12LPEN))
#define __HAL_RCC_TIM13_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM13LPEN))
#define __HAL_RCC_TIM14_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM14LPEN))
#define __HAL_RCC_USART3_CLK_SLEEP_DISABLE() (RCC->APB1LPENR &= ~(RCC_APB1LPENR_USART3LPEN))
#define __HAL_RCC_UART4_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_UART4LPEN))
#define __HAL_RCC_UART5_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_UART5LPEN))
#define __HAL_RCC_CAN1_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_CAN1LPEN))
#define __HAL_RCC_CAN2_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_CAN2LPEN))
#define __HAL_RCC_DAC_CLK_SLEEP_DISABLE()    (RCC->APB1LPENR &= ~(RCC_APB1LPENR_DACLPEN))
#define __HAL_RCC_UART7_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_UART7LPEN))
#define __HAL_RCC_UART8_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_UART8LPEN))

/** @brief  Enable or disable the APB2 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */ 
#define __HAL_RCC_TIM8_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_TIM8LPEN))
#define __HAL_RCC_ADC2_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_ADC2LPEN))
#define __HAL_RCC_ADC3_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_ADC3LPEN))
#define __HAL_RCC_SPI5_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_SPI5LPEN))
#define __HAL_RCC_SPI6_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_SPI6LPEN))
#define __HAL_RCC_SAI1_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_SAI1LPEN))

#define __HAL_RCC_TIM8_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_TIM8LPEN))
#define __HAL_RCC_ADC2_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_ADC2LPEN))
#define __HAL_RCC_ADC3_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_ADC3LPEN))
#define __HAL_RCC_SPI5_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_SPI5LPEN))
#define __HAL_RCC_SPI6_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_SPI6LPEN))
#define __HAL_RCC_SAI1_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_SAI1LPEN))

#if defined(STM32F429xx)|| defined(STM32F439xx)
#define __HAL_RCC_LTDC_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_LTDCLPEN))

#define __HAL_RCC_LTDC_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_LTDCLPEN))
#endif /* STM32F429xx || STM32F439xx */
#endif /* STM32F427xx || STM32F437xx || STM32F429xx|| STM32F439xx */
/*---------------------------------------------------------------------------------------------*/

/*----------------------------------- STM32F40xxx/STM32F41xxx----------------------------------*/
#if defined(STM32F405xx) || defined(STM32F415xx) || defined(STM32F407xx)|| defined(STM32F417xx)
/** @brief  Enables or disables the AHB1 peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it.
  */
#define __HAL_RCC_GPIOI_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOIEN))
#define __HAL_RCC_GPIOF_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOFEN))
#define __HAL_RCC_GPIOG_CLK_ENABLE()            (RCC->AHB1ENR |= (RCC_AHB1ENR_GPIOGEN))
#define __HAL_RCC_USB_OTG_HS_CLK_ENABLE()       (RCC->AHB1ENR |= (RCC_AHB1ENR_OTGHSEN))
#define __HAL_RCC_USB_OTG_HS_ULPI_CLK_ENABLE()  (RCC->AHB1ENR |= (RCC_AHB1ENR_OTGHSULPIEN))

#define __HAL_RCC_GPIOF_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_GPIOFEN))
#define __HAL_RCC_GPIOG_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_GPIOGEN))
#define __HAL_RCC_GPIOI_CLK_DISABLE()           (RCC->AHB1ENR &= ~(RCC_AHB1ENR_GPIOIEN))
#define __HAL_RCC_USB_OTG_HS_CLK_DISABLE()      (RCC->AHB1ENR &= ~(RCC_AHB1ENR_OTGHSEN))
#define __HAL_RCC_USB_OTG_HS_ULPI_CLK_DISABLE() (RCC->AHB1ENR &= ~(RCC_AHB1ENR_OTGHSULPIEN))

#if defined(STM32F407xx)|| defined(STM32F417xx)
/**
  * @brief  Enable ETHERNET clock.
  */
#define __HAL_RCC_ETHMAC_CLK_ENABLE()     (RCC->AHB1ENR |= (RCC_AHB1ENR_ETHMACEN))
#define __HAL_RCC_ETHMACTX_CLK_ENABLE()   (RCC->AHB1ENR |= (RCC_AHB1ENR_ETHMACTXEN))
#define __HAL_RCC_ETHMACRX_CLK_ENABLE()   (RCC->AHB1ENR |= (RCC_AHB1ENR_ETHMACRXEN))
#define __HAL_RCC_ETHMACPTP_CLK_ENABLE()  (RCC->AHB1ENR |= (RCC_AHB1ENR_ETHMACPTPEN))  
#define __HAL_RCC_ETH_CLK_ENABLE()       do {                            \
                                     __HAL_RCC_ETHMAC_CLK_ENABLE();      \
                                     __HAL_RCC_ETHMACTX_CLK_ENABLE();    \
                                     __HAL_RCC_ETHMACRX_CLK_ENABLE();    \
                                    } while(0)

/**
  * @brief  Disable ETHERNET clock.
  */
#define __HAL_RCC_ETHMAC_CLK_DISABLE()    (RCC->AHB1ENR &= ~(RCC_AHB1ENR_ETHMACEN))
#define __HAL_RCC_ETHMACTX_CLK_DISABLE()  (RCC->AHB1ENR &= ~(RCC_AHB1ENR_ETHMACTXEN))
#define __HAL_RCC_ETHMACRX_CLK_DISABLE()  (RCC->AHB1ENR &= ~(RCC_AHB1ENR_ETHMACRXEN))
#define __HAL_RCC_ETHMACPTP_CLK_DISABLE() (RCC->AHB1ENR &= ~(RCC_AHB1ENR_ETHMACPTPEN))  
#define __HAL_RCC_ETH_CLK_DISABLE()       do {                             \
                                      __HAL_RCC_ETHMACTX_CLK_DISABLE();    \
                                      __HAL_RCC_ETHMACRX_CLK_DISABLE();    \
                                      __HAL_RCC_ETHMAC_CLK_DISABLE();      \
                                     } while(0)
#endif /* STM32F407xx || STM32F417xx */
 
/** @brief  Enable or disable the AHB2 peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it.
  */
#if defined(STM32F407xx)|| defined(STM32F417xx) 
#define __HAL_RCC_DCMI_CLK_ENABLE()   (RCC->AHB2ENR |= (RCC_AHB2ENR_DCMIEN))
#define __HAL_RCC_DCMI_CLK_DISABLE()  (RCC->AHB2ENR &= ~(RCC_AHB2ENR_DCMIEN))
#endif /* STM32F407xx || STM32F417xx */

#if defined(STM32F415xx) || defined(STM32F417xx)
#define __HAL_RCC_CRYP_CLK_ENABLE()   (RCC->AHB2ENR |= (RCC_AHB2ENR_CRYPEN))
#define __HAL_RCC_HASH_CLK_ENABLE()   (RCC->AHB2ENR |= (RCC_AHB2ENR_HASHEN))

#define __HAL_RCC_CRYP_CLK_DISABLE()  (RCC->AHB2ENR &= ~(RCC_AHB2ENR_CRYPEN))
#define __HAL_RCC_HASH_CLK_DISABLE()  (RCC->AHB2ENR &= ~(RCC_AHB2ENR_HASHEN))
#endif /* STM32F415xx || STM32F417xx */

/** @brief  Enables or disables the AHB3 peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it. 
  */
#define __HAL_RCC_FSMC_CLK_ENABLE()  (RCC->AHB3ENR |= (RCC_AHB3ENR_FSMCEN))
#define __HAL_RCC_FSMC_CLK_DISABLE() (RCC->AHB3ENR &= ~(RCC_AHB3ENR_FSMCEN))

/** @brief  Enable or disable the Low Speed APB (APB1) peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it. 
  */
#define __HAL_RCC_TIM6_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_TIM6EN))
#define __HAL_RCC_TIM7_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_TIM7EN))
#define __HAL_RCC_TIM12_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_TIM12EN))
#define __HAL_RCC_TIM13_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_TIM13EN))
#define __HAL_RCC_TIM14_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_TIM14EN))
#define __HAL_RCC_WWDG_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_WWDGEN))
#define __HAL_RCC_USART3_CLK_ENABLE()  (RCC->APB1ENR |= (RCC_APB1ENR_USART3EN))
#define __HAL_RCC_UART4_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_UART4EN))
#define __HAL_RCC_UART5_CLK_ENABLE()   (RCC->APB1ENR |= (RCC_APB1ENR_UART5EN))
#define __HAL_RCC_CAN1_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_CAN1EN))
#define __HAL_RCC_CAN2_CLK_ENABLE()    (RCC->APB1ENR |= (RCC_APB1ENR_CAN2EN))
#define __HAL_RCC_DAC_CLK_ENABLE()     (RCC->APB1ENR |= (RCC_APB1ENR_DACEN))

#define __HAL_RCC_TIM6_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM6EN))
#define __HAL_RCC_TIM7_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM7EN))
#define __HAL_RCC_TIM12_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM12EN))
#define __HAL_RCC_TIM13_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM13EN))
#define __HAL_RCC_TIM14_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_TIM14EN))
#define __HAL_RCC_WWDG_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_WWDGEN))
#define __HAL_RCC_USART3_CLK_DISABLE() (RCC->APB1ENR &= ~(RCC_APB1ENR_USART3EN))
#define __HAL_RCC_UART4_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_UART4EN))
#define __HAL_RCC_UART5_CLK_DISABLE()  (RCC->APB1ENR &= ~(RCC_APB1ENR_UART5EN))
#define __HAL_RCC_CAN1_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_CAN1EN))
#define __HAL_RCC_CAN2_CLK_DISABLE()   (RCC->APB1ENR &= ~(RCC_APB1ENR_CAN2EN))
#define __HAL_RCC_DAC_CLK_DISABLE()    (RCC->APB1ENR &= ~(RCC_APB1ENR_DACEN))

/** @brief  Enable or disable the High Speed APB (APB2) peripheral clock.
  * @note   After reset, the peripheral clock (used for registers read/write access)
  *         is disabled and the application software has to enable this clock before 
  *         using it.
  */
#define __HAL_RCC_TIM8_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_TIM8EN))
#define __HAL_RCC_ADC2_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_ADC2EN))
#define __HAL_RCC_ADC3_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_ADC3EN))

#define __HAL_RCC_TIM8_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_TIM8EN))
#define __HAL_RCC_ADC2_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_ADC2EN))
#define __HAL_RCC_ADC3_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_ADC3EN))

/** @brief  Force or release AHB1 peripheral reset.
  */  
#define __HAL_RCC_GPIOF_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOFRST))
#define __HAL_RCC_GPIOG_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOGRST))
#define __HAL_RCC_GPIOI_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_GPIOIRST))
#define __HAL_RCC_ETHMAC_FORCE_RESET()   (RCC->AHB1RSTR |= (RCC_AHB1RSTR_ETHMACRST))
#define __HAL_RCC_OTGHS_FORCE_RESET()    (RCC->AHB1RSTR |= (RCC_AHB1RSTR_OTGHRST))

#define __HAL_RCC_GPIOF_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOFRST))
#define __HAL_RCC_GPIOG_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOGRST))
#define __HAL_RCC_GPIOI_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_GPIOIRST))
#define __HAL_RCC_ETHMAC_RELEASE_RESET() (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_ETHMACRST))
#define __HAL_RCC_OTGHS_RELEASE_RESET()  (RCC->AHB1RSTR &= ~(RCC_AHB1RSTR_OTGHRST))

/** @brief  Force or release AHB2 peripheral reset.
  */
#if defined(STM32F407xx)|| defined(STM32F417xx)  
#define __HAL_RCC_DCMI_FORCE_RESET()   (RCC->AHB2RSTR |= (RCC_AHB2RSTR_DCMIRST))
#define __HAL_RCC_DCMI_RELEASE_RESET() (RCC->AHB2RSTR &= ~(RCC_AHB2RSTR_DCMIRST))
#endif /* STM32F407xx || STM32F417xx */

#if defined(STM32F415xx) || defined(STM32F417xx) 
#define __HAL_RCC_CRYP_FORCE_RESET()   (RCC->AHB2RSTR |= (RCC_AHB2RSTR_CRYPRST))
#define __HAL_RCC_HASH_FORCE_RESET()   (RCC->AHB2RSTR |= (RCC_AHB2RSTR_HASHRST))

#define __HAL_RCC_CRYP_RELEASE_RESET() (RCC->AHB2RSTR &= ~(RCC_AHB2RSTR_CRYPRST))
#define __HAL_RCC_HASH_RELEASE_RESET() (RCC->AHB2RSTR &= ~(RCC_AHB2RSTR_HASHRST))

#endif /* STM32F415xx || STM32F417xx */

/** @brief  Force or release AHB3 peripheral reset
  */
#define __HAL_RCC_FSMC_FORCE_RESET()   (RCC->AHB3RSTR |= (RCC_AHB3RSTR_FSMCRST))
#define __HAL_RCC_FSMC_RELEASE_RESET() (RCC->AHB3RSTR &= ~(RCC_AHB3RSTR_FSMCRST))

/** @brief  Force or release APB1 peripheral reset.
  */ 
#define __HAL_RCC_TIM6_FORCE_RESET()     (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM6RST))
#define __HAL_RCC_TIM7_FORCE_RESET()     (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM7RST))
#define __HAL_RCC_TIM12_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM12RST))
#define __HAL_RCC_TIM13_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM13RST))
#define __HAL_RCC_TIM14_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_TIM14RST))
#define __HAL_RCC_USART3_FORCE_RESET()   (RCC->APB1RSTR |= (RCC_APB1RSTR_USART3RST))
#define __HAL_RCC_UART4_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_UART4RST))
#define __HAL_RCC_UART5_FORCE_RESET()    (RCC->APB1RSTR |= (RCC_APB1RSTR_UART5RST))
#define __HAL_RCC_CAN1_FORCE_RESET()     (RCC->APB1RSTR |= (RCC_APB1RSTR_CAN1RST))
#define __HAL_RCC_CAN2_FORCE_RESET()     (RCC->APB1RSTR |= (RCC_APB1RSTR_CAN2RST))
#define __HAL_RCC_DAC_FORCE_RESET()      (RCC->APB1RSTR |= (RCC_APB1RSTR_DACRST))

#define __HAL_RCC_TIM6_RELEASE_RESET()   (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM6RST))
#define __HAL_RCC_TIM7_RELEASE_RESET()   (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM7RST))
#define __HAL_RCC_TIM12_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM12RST))
#define __HAL_RCC_TIM13_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM13RST))
#define __HAL_RCC_TIM14_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_TIM14RST))
#define __HAL_RCC_USART3_RELEASE_RESET() (RCC->APB1RSTR &= ~(RCC_APB1RSTR_USART3RST))
#define __HAL_RCC_UART4_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_UART4RST))
#define __HAL_RCC_UART5_RELEASE_RESET()  (RCC->APB1RSTR &= ~(RCC_APB1RSTR_UART5RST))
#define __HAL_RCC_CAN1_RELEASE_RESET()   (RCC->APB1RSTR &= ~(RCC_APB1RSTR_CAN1RST))
#define __HAL_RCC_CAN2_RELEASE_RESET()   (RCC->APB1RSTR &= ~(RCC_APB1RSTR_CAN2RST))
#define __HAL_RCC_DAC_RELEASE_RESET()    (RCC->APB1RSTR &= ~(RCC_APB1RSTR_DACRST))

/** @brief  Force or release APB2 peripheral reset.
  */
#define __HAL_RCC_TIM8_FORCE_RESET()   (RCC->APB2RSTR |= (RCC_APB2RSTR_TIM8RST))
#define __HAL_RCC_TIM8_RELEASE_RESET() (RCC->APB2RSTR &= ~(RCC_APB2RSTR_TIM8RST))

/** @brief  Enable or disable the AHB1 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */   
#define __HAL_RCC_GPIOF_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_GPIOFLPEN))
#define __HAL_RCC_GPIOG_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_GPIOGLPEN))
#define __HAL_RCC_GPIOI_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_GPIOILPEN))
#define __HAL_RCC_SRAM2_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_SRAM2LPEN))
#define __HAL_RCC_ETHMAC_CLK_SLEEP_ENABLE()     (RCC->AHB1LPENR |= (RCC_AHB1LPENR_ETHMACLPEN))
#define __HAL_RCC_ETHMACTX_CLK_SLEEP_ENABLE()   (RCC->AHB1LPENR |= (RCC_AHB1LPENR_ETHMACTXLPEN))
#define __HAL_RCC_ETHMACRX_CLK_SLEEP_ENABLE()   (RCC->AHB1LPENR |= (RCC_AHB1LPENR_ETHMACRXLPEN))
#define __HAL_RCC_ETHMACPTP_CLK_SLEEP_ENABLE()  (RCC->AHB1LPENR |= (RCC_AHB1LPENR_ETHMACPTPLPEN))
#define __HAL_RCC_OTGHS_CLK_SLEEP_ENABLE()      (RCC->AHB1LPENR |= (RCC_AHB1LPENR_OTGHSLPEN))
#define __HAL_RCC_OTGHSULPI_CLK_SLEEP_ENABLE()  (RCC->AHB1LPENR |= (RCC_AHB1LPENR_OTGHSULPILPEN))

#define __HAL_RCC_GPIOF_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_GPIOFLPEN))
#define __HAL_RCC_GPIOG_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_GPIOGLPEN))
#define __HAL_RCC_GPIOI_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_GPIOILPEN))
#define __HAL_RCC_SRAM2_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_SRAM2LPEN))
#define __HAL_RCC_ETHMAC_CLK_SLEEP_DISABLE()    (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_ETHMACLPEN))
#define __HAL_RCC_ETHMACTX_CLK_SLEEP_DISABLE()  (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_ETHMACTXLPEN))
#define __HAL_RCC_ETHMACRX_CLK_SLEEP_DISABLE()  (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_ETHMACRXLPEN))
#define __HAL_RCC_ETHMACPTP_CLK_SLEEP_DISABLE() (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_ETHMACPTPLPEN))
#define __HAL_RCC_OTGHS_CLK_SLEEP_DISABLE()     (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_OTGHSLPEN))
#define __HAL_RCC_OTGHSULPI_CLK_SLEEP_DISABLE() (RCC->AHB1LPENR &= ~(RCC_AHB1LPENR_OTGHSULPILPEN))

/** @brief  Enable or disable the AHB2 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */
#if defined(STM32F407xx)|| defined(STM32F417xx) 
#define __HAL_RCC_DCMI_CLK_SLEEP_ENABLE()  (RCC->AHB2LPENR |= (RCC_AHB2LPENR_DCMILPEN))
#define __HAL_RCC_DCMI_CLK_SLEEP_DISABLE() (RCC->AHB2LPENR &= ~(RCC_AHB2LPENR_DCMILPEN))
#endif /* STM32F407xx || STM32F417xx */

#if defined(STM32F415xx) || defined(STM32F417xx) 
#define __HAL_RCC_CRYP_CLK_SLEEP_ENABLE()  (RCC->AHB2LPENR |= (RCC_AHB2LPENR_CRYPLPEN))
#define __HAL_RCC_HASH_CLK_SLEEP_ENABLE()  (RCC->AHB2LPENR |= (RCC_AHB2LPENR_HASHLPEN))

#define __HAL_RCC_CRYP_CLK_SLEEP_DISABLE() (RCC->AHB2LPENR &= ~(RCC_AHB2LPENR_CRYPLPEN))
#define __HAL_RCC_HASH_CLK_SLEEP_DISABLE() (RCC->AHB2LPENR &= ~(RCC_AHB2LPENR_HASHLPEN))
#endif /* STM32F415xx || STM32F417xx */

/** @brief  Enable or disable the AHB3 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */
#define __HAL_RCC_FSMC_CLK_SLEEP_ENABLE()  (RCC->AHB3LPENR |= (RCC_AHB3LPENR_FSMCLPEN))
#define __HAL_RCC_FSMC_CLK_SLEEP_DISABLE() (RCC->AHB3LPENR &= ~(RCC_AHB3LPENR_FSMCLPEN))

/** @brief  Enable or disable the APB1 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */
#define __HAL_RCC_TIM6_CLK_SLEEP_ENABLE()    (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM6LPEN))
#define __HAL_RCC_TIM7_CLK_SLEEP_ENABLE()    (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM7LPEN))
#define __HAL_RCC_TIM12_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM12LPEN))
#define __HAL_RCC_TIM13_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM13LPEN))
#define __HAL_RCC_TIM14_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_TIM14LPEN))
#define __HAL_RCC_USART3_CLK_SLEEP_ENABLE()  (RCC->APB1LPENR |= (RCC_APB1LPENR_USART3LPEN))
#define __HAL_RCC_UART4_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_UART4LPEN))
#define __HAL_RCC_UART5_CLK_SLEEP_ENABLE()   (RCC->APB1LPENR |= (RCC_APB1LPENR_UART5LPEN))
#define __HAL_RCC_CAN1_CLK_SLEEP_ENABLE()    (RCC->APB1LPENR |= (RCC_APB1LPENR_CAN1LPEN))
#define __HAL_RCC_CAN2_CLK_SLEEP_ENABLE()    (RCC->APB1LPENR |= (RCC_APB1LPENR_CAN2LPEN))
#define __HAL_RCC_DAC_CLK_SLEEP_ENABLE()     (RCC->APB1LPENR |= (RCC_APB1LPENR_DACLPEN))

#define __HAL_RCC_TIM6_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM6LPEN))
#define __HAL_RCC_TIM7_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM7LPEN))
#define __HAL_RCC_TIM12_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM12LPEN))
#define __HAL_RCC_TIM13_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM13LPEN))
#define __HAL_RCC_TIM14_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_TIM14LPEN))
#define __HAL_RCC_USART3_CLK_SLEEP_DISABLE() (RCC->APB1LPENR &= ~(RCC_APB1LPENR_USART3LPEN))
#define __HAL_RCC_UART4_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_UART4LPEN))
#define __HAL_RCC_UART5_CLK_SLEEP_DISABLE()  (RCC->APB1LPENR &= ~(RCC_APB1LPENR_UART5LPEN))
#define __HAL_RCC_CAN1_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_CAN1LPEN))
#define __HAL_RCC_CAN2_CLK_SLEEP_DISABLE()   (RCC->APB1LPENR &= ~(RCC_APB1LPENR_CAN2LPEN))
#define __HAL_RCC_DAC_CLK_SLEEP_DISABLE()    (RCC->APB1LPENR &= ~(RCC_APB1LPENR_DACLPEN))

/** @brief  Enable or disable the APB2 peripheral clock during Low Power (Sleep) mode.
  * @note   Peripheral clock gating in SLEEP mode can be used to further reduce
  *         power consumption.
  * @note   After wakeup from SLEEP mode, the peripheral clock is enabled again.
  * @note   By default, all peripheral clocks are enabled during SLEEP mode.
  */ 
#define __HAL_RCC_TIM8_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_TIM8LPEN))
#define __HAL_RCC_ADC2_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_ADC2LPEN))
#define __HAL_RCC_ADC3_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_ADC3LPEN))

#define __HAL_RCC_TIM8_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_TIM8LPEN))
#define __HAL_RCC_ADC2_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_ADC2LPEN))
#define __HAL_RCC_ADC3_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_ADC3LPEN))
#endif /* STM32F405xx || STM32F415xx || STM32F407xx || STM32F417xx */
/*---------------------------------------------------------------------------------------------*/

/*------------------------------------------ STM32F411xx --------------------------------------*/
#if defined(STM32F411xE)
/** @brief  Enable or disable the High Speed APB (APB2) peripheral clock.
  */
#define __HAL_RCC_SPI5_CLK_ENABLE()  (RCC->APB2ENR |= (RCC_APB2ENR_SPI5EN))
#define __HAL_RCC_SPI5_CLK_DISABLE() (RCC->APB2ENR &= ~(RCC_APB2ENR_SPI5EN))
                                       
/** @brief  Force or release APB2 peripheral reset.
  */
#define __HAL_RCC_SPI5_FORCE_RESET()   (RCC->APB2RSTR |= (RCC_APB2RSTR_SPI5RST))
#define __HAL_RCC_SPI5_RELEASE_RESET() (RCC->APB2RSTR &= ~(RCC_APB2RSTR_SPI5RST))                                    

/** @brief  Enable or disable the APB2 peripheral clock during Low Power (Sleep) mode.
  */ 
#define __HAL_RCC_SPI5_CLK_SLEEP_ENABLE() (RCC->APB2LPENR |= (RCC_APB2LPENR_SPI5LPEN))
#define __HAL_RCC_SPI5_CLK_SLEEP_DISABLE() (RCC->APB2LPENR &= ~(RCC_APB2LPENR_SPI5LPEN))

#endif /* STM32F411xE */
/*---------------------------------------------------------------------------------------------*/

#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx)|| defined(STM32F439xx) ||\
    defined(STM32F401xC) || defined(STM32F401xE) || defined(STM32F411xE)
                           
/** @brief  Macro to configure the Timers clocks prescalers 
  * @note   This feature is only available with STM32F429x/439x Devices.  
  * @param  __PRESC__ : specifies the Timers clocks prescalers selection
  *         This parameter can be one of the following values:
  *            @arg RCC_TIMPRES_DESACTIVATED: The Timers kernels clocks prescaler is 
  *                 equal to HPRE if PPREx is corresponding to division by 1 or 2, 
  *                 else it is equal to [(HPRE * PPREx) / 2] if PPREx is corresponding to 
  *                 division by 4 or more.       
  *            @arg RCC_TIMPRES_ACTIVATED: The Timers kernels clocks prescaler is 
  *                 equal to HPRE if PPREx is corresponding to division by 1, 2 or 4, 
  *                 else it is equal to [(HPRE * PPREx) / 4] if PPREx is corresponding 
  *                 to division by 8 or more.
  */     
#define __HAL_RCC_TIMCLKPRESCALER(__PRESC__) (*(__IO uint32_t *) RCC_DCKCFGR_TIMPRE_BB = (__PRESC__))

#endif /* STM32F427xx || STM32F437xx || STM32F429xx || STM32F439xx) || STM32F401xC || STM32F401xE || STM32F411xE */

#if defined(STM32F411xE)
                           
/** @brief  Macro to configure the PLLI2S clock multiplication and division factors .
  * @note   This macro must be used only when the PLLI2S is disabled.
  * @note   This macro must be used only when the PLLI2S is disabled.
  * @note   PLLI2S clock source is common with the main PLL (configured in 
  *         HAL_RCC_ClockConfig() API).
  * @param  __PLLI2SM__: specifies the division factor for PLLI2S VCO input clock
  *         This parameter must be a number between Min_Data = 2 and Max_Data = 63.
  * @note   You have to set the PLLI2SM parameter correctly to ensure that the VCO input
  *         frequency ranges from 1 to 2 MHz. It is recommended to select a frequency
  *         of 2 MHz to limit PLLI2S jitter.    
  * @param  __PLLI2SN__: specifies the multiplication factor for PLLI2S VCO output clock
  *         This parameter must be a number between Min_Data = 192 and Max_Data = 432.
  * @note   You have to set the PLLI2SN parameter correctly to ensure that the VCO 
  *         output frequency is between Min_Data = 192 and Max_Data = 432 MHz.
  * @param  __PLLI2SR__: specifies the division factor for I2S clock
  *         This parameter must be a number between Min_Data = 2 and Max_Data = 7.
  * @note   You have to set the PLLI2SR parameter correctly to not exceed 192 MHz
  *         on the I2S clock frequency.
  */
#define __HAL_RCC_PLLI2S_I2SCLK_CONFIG(__PLLI2SM__, __PLLI2SN__, __PLLI2SR__) (RCC->PLLI2SCFGR = ((__PLLI2SN__) << POSITION_VAL(RCC_PLLI2SCFGR_PLLI2SN)) |\
                                                                                    ((__PLLI2SR__) << POSITION_VAL(RCC_PLLI2SCFGR_PLLI2SR)) | (__PLLI2SM__))
#endif /* STM32F411xE */


#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx) || defined(STM32F439xx)
     
/** @brief Macros to Enable or Disable the PLLISAI. 
  * @note  The PLLSAI is only available with STM32F429x/439x Devices.     
  * @note  The PLLSAI is disabled by hardware when entering STOP and STANDBY modes. 
  */
#define __HAL_RCC_PLLSAI_ENABLE() (*(__IO uint32_t *) RCC_CR_PLLSAION_BB = ENABLE)
#define __HAL_RCC_PLLSAI_DISABLE() (*(__IO uint32_t *) RCC_CR_PLLSAION_BB = DISABLE)

/** @brief  Macro to configure the PLLSAI clock multiplication and division factors.
  * @note   The PLLSAI is only available with STM32F429x/439x Devices.     
  * @note   This function must be used only when the PLLSAI is disabled.
  * @note   PLLSAI clock source is common with the main PLL (configured in 
  *         RCC_PLLConfig function )
  * @param  __PLLSAIN__: specifies the multiplication factor for PLLSAI VCO output clock.
  *         This parameter must be a number between Min_Data = 192 and Max_Data = 432.
  * @note   You have to set the PLLSAIN parameter correctly to ensure that the VCO 
  *         output frequency is between Min_Data = 192 and Max_Data = 432 MHz.
  * @param  __PLLSAIQ__: specifies the division factor for SAI1 clock
  *         This parameter must be a number between Min_Data = 2 and Max_Data = 15.
  * @param  __PLLSAIR__: specifies the division factor for LTDC clock
  *         This parameter must be a number between Min_Data = 2 and Max_Data = 7.
  */   
#define __HAL_RCC_PLLSAI_CONFIG(__PLLSAIN__, __PLLSAIQ__, __PLLSAIR__) (RCC->PLLSAICFGR = ((__PLLSAIN__) << 6) | ((__PLLSAIQ__) << 24) | ((__PLLSAIR__) << 28))

/** @brief  Macro used by the SAI HAL driver to configure the PLLI2S clock multiplication and division factors.
  * @note   This macro must be used only when the PLLI2S is disabled.
  * @note   PLLI2S clock source is common with the main PLL (configured in 
  *         HAL_RCC_ClockConfig() API)             
  * @param  __PLLI2SN__: specifies the multiplication factor for PLLI2S VCO output clock.
  *         This parameter must be a number between Min_Data = 192 and Max_Data = 432.
  * @note   You have to set the PLLI2SN parameter correctly to ensure that the VCO 
  *         output frequency is between Min_Data = 192 and Max_Data = 432 MHz.
  * @param  __PLLI2SQ__: specifies the division factor for SAI1 clock.
  *         This parameter must be a number between Min_Data = 2 and Max_Data = 15. 
  * @note   the PLLI2SQ parameter is only available with STM32F429x/439x Devices
  *         and can be configured using the __HAL_RCC_PLLI2S_PLLSAICLK_CONFIG() macro
  * @param  __PLLI2SR__: specifies the division factor for I2S clock
  *         This parameter must be a number between Min_Data = 2 and Max_Data = 7.
  * @note   You have to set the PLLI2SR parameter correctly to not exceed 192 MHz
  *         on the I2S clock frequency.
  */
#define __HAL_RCC_PLLI2S_SAICLK_CONFIG(__PLLI2SN__, __PLLI2SQ__, __PLLI2SR__) (RCC->PLLI2SCFGR = ((__PLLI2SN__) << 6) | ((__PLLI2SQ__) << 24) | ((__PLLI2SR__) << 28))
    
/** @brief  Macro to configure the SAI clock Divider coming from PLLI2S.
  * @note   The SAI peripheral is only available with STM32F429x/439x Devices.    
  * @note   This function must be called before enabling the PLLI2S.          
  * @param  __PLLI2SDivQ__: specifies the PLLI2S division factor for SAI1 clock .
  *          This parameter must be a number between 1 and 32.
  *          SAI1 clock frequency = f(PLLI2SQ) / __PLLI2SDivQ__ 
  */
#define __HAL_RCC_PLLI2S_PLLSAICLKDIVQ_CONFIG(__PLLI2SDivQ__) (MODIFY_REG(RCC->DCKCFGR, RCC_DCKCFGR_PLLI2SDIVQ, (__PLLI2SDivQ__)-1))

/** @brief  Macro to configure the SAI clock Divider coming from PLLSAI.
  * @note   The SAI peripheral is only available with STM32F429x/439x Devices.
  * @note   This function must be called before enabling the PLLSAI.
  * @param  __PLLSAIDivQ__: specifies the PLLSAI division factor for SAI1 clock .
  *         This parameter must be a number between Min_Data = 1 and Max_Data = 32.
  *         SAI1 clock frequency = f(PLLSAIQ) / __PLLSAIDivQ__  
  */
#define __HAL_RCC_PLLSAI_PLLSAICLKDIVQ_CONFIG(__PLLSAIDivQ__) (MODIFY_REG(RCC->DCKCFGR, RCC_DCKCFGR_PLLSAIDIVQ, ((__PLLSAIDivQ__)-1)<<8))

/** @brief  Macro to configure the LTDC clock Divider coming from PLLSAI.
  * 
  * @note   The LTDC peripheral is only available with STM32F429x/439x Devices.   
  * @note   This function must be called before enabling the PLLSAI. 
  * @param  __PLLSAIDivR__: specifies the PLLSAI division factor for LTDC clock .
  *          This parameter must be a number between Min_Data = 2 and Max_Data = 16.
  *          LTDC clock frequency = f(PLLSAIR) / __PLLSAIDivR__ 
  */   
#define __HAL_RCC_PLLSAI_PLLSAICLKDIVR_CONFIG(__PLLSAIDivR__) (MODIFY_REG(RCC->DCKCFGR, RCC_DCKCFGR_PLLSAIDIVR, (__PLLSAIDivR__)))

/** @brief  Macro to configure SAI1BlockA clock source selection.
  * @note   The SAI peripheral is only available with STM32F429x/439x Devices.      
  * @note   This function must be called before enabling PLLSAI, PLLI2S and  
  *         the SAI clock.
  * @param  __SOURCE__: specifies the SAI Block A clock source.
  *         This parameter can be one of the following values:
  *            @arg RCC_SAIACLKSOURCE_PLLI2S: PLLI2S_Q clock divided by PLLI2SDIVQ used 
  *                                           as SAI1 Block A clock. 
  *            @arg RCC_SAIACLKSOURCE_PLLSAI: PLLISAI_Q clock divided by PLLSAIDIVQ used 
  *                                           as SAI1 Block A clock.
  *            @arg RCC_SAIACLKSOURCE_Ext: External clock mapped on the I2S_CKIN pin
  *                                        used as SAI1 Block A clock.
  */
#define __HAL_RCC_SAI_BLOCKACLKSOURCE_CONFIG(__SOURCE__) (MODIFY_REG(RCC->DCKCFGR, RCC_DCKCFGR_SAI1ASRC, (__SOURCE__)))

/** @brief  Macro to configure SAI1BlockB clock source selection.
  * @note   The SAI peripheral is only available with STM32F429x/439x Devices.      
  * @note   This function must be called before enabling PLLSAI, PLLI2S and  
  *         the SAI clock.
  * @param  __SOURCE__: specifies the SAI Block B clock source.
  *         This parameter can be one of the following values:
  *            @arg RCC_SAIBCLKSOURCE_PLLI2S: PLLI2S_Q clock divided by PLLI2SDIVQ used 
  *                                           as SAI1 Block B clock. 
  *            @arg RCC_SAIBCLKSOURCE_PLLSAI: PLLISAI_Q clock divided by PLLSAIDIVQ used 
  *                                           as SAI1 Block B clock. 
  *            @arg RCC_SAIBCLKSOURCE_Ext: External clock mapped on the I2S_CKIN pin
  *                                        used as SAI1 Block B clock.
  */
#define __HAL_RCC_SAI_BLOCKBCLKSOURCE_CONFIG(__SOURCE__) (MODIFY_REG(RCC->DCKCFGR, RCC_DCKCFGR_SAI1BSRC, (__SOURCE__)))

/** @brief Enable PLLSAI_RDY interrupt.
  */
#define __HAL_RCC_PLLSAI_ENABLE_IT() (RCC->CIR |= (RCC_CIR_PLLSAIRDYIE))

/** @brief Disable PLLSAI_RDY interrupt.
  */
#define __HAL_RCC_PLLSAI_DISABLE_IT() (RCC->CIR &= ~(RCC_CIR_PLLSAIRDYIE))

/** @brief Clear the PLLSAI RDY interrupt pending bits.
  */
#define __HAL_RCC_PLLSAI_CLEAR_IT() (RCC->CIR |= (RCC_CIR_PLLSAIRDYF))

/** @brief Check the PLLSAI RDY interrupt has occurred or not.
  * @retval The new state (TRUE or FALSE).
  */
#define __HAL_RCC_PLLSAI_GET_IT() ((RCC->CIR & (RCC_CIR_PLLSAIRDYIE)) == (RCC_CIR_PLLSAIRDYIE))

/** @brief  Check PLLSAI RDY flag is set or not.
  * @retval The new state (TRUE or FALSE).
  */
#define __HAL_RCC_PLLSAI_GET_FLAG() ((RCC->CR & (RCC_CR_PLLSAIRDY)) == (RCC_CR_PLLSAIRDY))

#endif /* STM32F427xx || STM32F437xx || STM32F429xx || STM32F439xx */
/**
  * @}
  */

/* Exported functions --------------------------------------------------------*/
/** @addtogroup RCCEx_Exported_Functions
  *  @{
  */

/** @addtogroup RCCEx_Exported_Functions_Group1
  *  @{
  */
HAL_StatusTypeDef HAL_RCCEx_PeriphCLKConfig(RCC_PeriphCLKInitTypeDef  *PeriphClkInit);
void HAL_RCCEx_GetPeriphCLKConfig(RCC_PeriphCLKInitTypeDef  *PeriphClkInit);

#if defined(STM32F411xE)
void HAL_RCCEx_SelectLSEMode(uint8_t Mode);
#endif /* STM32F411xE */
/**
  * @}
  */ 

/**
  * @}
  */
/* Private types -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
/* Private constants ---------------------------------------------------------*/
/** @defgroup RCCEx_Private_Constants RCCEx Private Constants
  * @{
  */
#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx)|| defined(STM32F439xx) 
/** @defgroup RCCEx_BitAddress_AliasRegion RCC BitAddress AliasRegion
  * @brief RCC registers bit address in the alias region
  * @{
  */
/* --- CR Register ---*/
/* Alias word address of PLLSAION bit */
#define RCC_PLLSAION_BIT_NUMBER        0x1C
#define RCC_CR_PLLSAION_BB            (PERIPH_BB_BASE + (RCC_CR_OFFSET * 32) + (RCC_PLLSAION_BIT_NUMBER * 4))

/* --- DCKCFGR Register ---*/
/* Alias word address of TIMPRE bit */
#define RCC_DCKCFGR_OFFSET            (RCC_OFFSET + 0x8C)
#define RCC_TIMPRE_BIT_NUMBER          0x18
#define RCC_DCKCFGR_TIMPRE_BB         (PERIPH_BB_BASE + (RCC_DCKCFGR_OFFSET * 32) + (RCC_TIMPRE_BIT_NUMBER * 4))
/**
  * @}
  */
#endif /* STM32F427xx || STM32F437xx || STM32F429xx || STM32F439xx */

/**
  * @}
  */

/* Private macros ------------------------------------------------------------*/
/** @addtogroup RCCEx_Private_Macros RCCEx Private Macros
  * @{
  */
/** @defgroup RCCEx_IS_RCC_Definitions RCC Private macros to check input parameters
  * @{
  */
#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx)|| defined(STM32F439xx)
#define IS_RCC_PERIPHCLOCK(SELECTION) ((1 <= (SELECTION)) && ((SELECTION) <= 0x0000002F))
#endif /* STM32F427xx || STM32F437xx || STM32F429xx || STM32F439xx */

#if defined(STM32F405xx) || defined(STM32F415xx) || defined(STM32F407xx)|| defined(STM32F417xx) ||\
    defined(STM32F401xC) || defined(STM32F401xE) || defined(STM32F411xE) 
#define IS_RCC_PERIPHCLOCK(SELECTION) ((1 <= (SELECTION)) && ((SELECTION) <= 0x00000003))
#endif /* STM32F405xx || STM32F415xx || STM32F407xx || STM32F417xx || STM32F401xC || STM32F401xE || STM32F411xE */

#if defined(STM32F427xx) || defined(STM32F437xx) || defined(STM32F429xx)|| defined(STM32F439xx) 
#define IS_RCC_PLLI2SQ_VALUE(VALUE) ((2 <= (VALUE)) && ((VALUE) <= 15))
#define IS_RCC_PLLSAIN_VALUE(VALUE) ((49 <= (VALUE)) && ((VALUE) <= 432))
#define IS_RCC_PLLSAIQ_VALUE(VALUE) ((2 <= (VALUE)) && ((VALUE) <= 15))
#define IS_RCC_PLLSAIR_VALUE(VALUE) ((2 <= (VALUE)) && ((VALUE) <= 7))
#define IS_RCC_PLLSAI_DIVQ_VALUE(VALUE) ((1 <= (VALUE)) && ((VALUE) <= 32))
#define IS_RCC_PLLI2S_DIVQ_VALUE(VALUE) ((1 <= (VALUE)) && ((VALUE) <= 32))
#define IS_RCC_PLLSAI_DIVR_VALUE(VALUE) (((VALUE) == RCC_PLLSAIDIVR_2) ||\
                                         ((VALUE) == RCC_PLLSAIDIVR_4)  ||\
                                         ((VALUE) == RCC_PLLSAIDIVR_8)  ||\
                                         ((VALUE) == RCC_PLLSAIDIVR_16))
#endif /* STM32F427xx || STM32F437xx || STM32F429xx || STM32F439xx */

#if defined(STM32F411xE) 

#define IS_RCC_PLLI2SM_VALUE(VALUE) ((VALUE) <= 63)  
#define IS_RCC_LSE_MODE(MODE)           (((MODE) == RCC_LSE_LOWPOWER_MODE) ||\
                                         ((MODE) == RCC_LSE_HIGHDRIVE_MODE))
#endif /* STM32F411xE */

/**
  * @}
  */

/**
  * @}
  */

/**
  * @}
  */ 

/**
  * @}
  */  
#ifdef __cplusplus
}
#endif

#endif /* __STM32F4xx_HAL_RCC_EX_H */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
