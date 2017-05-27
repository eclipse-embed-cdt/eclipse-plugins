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

#ifndef BLINKLED_H_
#define BLINKLED_H_

#include "$(CMSIS_name).h"
//@XCDL @if F4
#include "$(CMSIS_name)_hal.h"
//@XCDL @endif

// ----- LED definitions ------------------------------------------------------

// Adjust these definitions for your own board.
//@XCDL @if F4
#if defined(BOARD_OLIMEX_STM32_E407)

// STM32-E407 definitions (the GREEN led, C13, active low)

// Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E, 5=F, 6=G, ...
#define BLINK_PORT_NUMBER               (2)
#define BLINK_PIN_NUMBER                (13)
#define BLINK_ACTIVE_LOW                (1)

#else

// STM32F4DISCOVERY definitions (the GREEN led, D12, active high)
// (SEGGER J-Link device name: STM32F407VG).

#define BLINK_PORT_NUMBER               (3)
#define BLINK_PIN_NUMBER                (12)
#define BLINK_ACTIVE_LOW                (0)

#endif

#define BLINK_GPIOx(_N)                 ((GPIO_TypeDef *)(GPIOA_BASE + (GPIOB_BASE-GPIOA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_RCC_MASKx(_N)             (RCC_AHB1ENR_GPIOAEN << (_N))
//@XCDL @elif F3
// STM32F3DISCOVERY definitions (GREEN led, E15, active high)
// (SEGGER J-Link device name: STM32F303VC).

// Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E, 5=F, 6=G, ...
#define BLINK_PORT_NUMBER               (4)
#define BLINK_PIN_NUMBER                (15)
#define BLINK_ACTIVE_LOW                (0)

#define BLINK_GPIOx(_N)                 ((GPIO_TypeDef *)(GPIOA_BASE + (GPIOB_BASE-GPIOA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_RCC_MASKx(_N)             (RCC_AHBPeriph_GPIOA << (_N))
//@XCDL @elif F2
// STM3220G-EVAL definitions (the GREEN led, G6, active high)
// (SEGGER J-Link device name: STM32F207IG).

// Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E, 5=F, 6=G, ...
#define BLINK_PORT_NUMBER               (6)
#define BLINK_PIN_NUMBER                (6)
#define BLINK_ACTIVE_LOW                (0)

#define BLINK_GPIOx(_N)                 ((GPIO_TypeDef *)(GPIOA_BASE + (GPIOB_BASE-GPIOA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_RCC_MASKx(_N)             (RCC_AHB1Periph_GPIOA << (_N))
//@XCDL @elif F1
// Olimex STM32-H103 definitions (the GREEN led, C12, active low)
// (SEGGER J-Link device name: STM32F103RB).

// Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E, 5=F, 6=G, ...
#define BLINK_PORT_NUMBER               (2)
#define BLINK_PIN_NUMBER                (12)
#define BLINK_ACTIVE_LOW                (1)

#define BLINK_GPIOx(_N)                 ((GPIO_TypeDef *)(GPIOA_BASE + (GPIOB_BASE-GPIOA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_RCC_MASKx(_N)             (RCC_APB2Periph_GPIOA << (_N))
//@XCDL @elif F0
// STM32F0DISCOVERY definitions (GREEN led, C9, active high)
// (SEGGER J-Link device name: STM32F051R8).

// Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E, 5=F, 6=G, ...
#define BLINK_PORT_NUMBER               (2)
#define BLINK_PIN_NUMBER                (9)
#define BLINK_ACTIVE_LOW                (0)

#define BLINK_GPIOx(_N)                 ((GPIO_TypeDef *)(GPIOA_BASE + (GPIOB_BASE-GPIOA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_RCC_MASKx(_N)             (RCC_AHBPeriph_GPIOA << (_N))
//@XCDL @endif // F4, F3, F2

//@XCDL @if c

// ----------------------------------------------------------------------------

extern
void
blink_led_init (void);

// ----------------------------------------------------------------------------

inline void
blink_led_on (void);

inline void
blink_led_off (void);

// ----------------------------------------------------------------------------

inline void
__attribute__((always_inline))
blink_led_on (void)
{
//@XCDL @if F4
#if (BLINK_ACTIVE_LOW)
  HAL_GPIO_WritePin(BLINK_GPIOx(BLINK_PORT_NUMBER),
      BLINK_PIN_MASK(BLINK_PIN_NUMBER), GPIO_PIN_RESET);
#else
  HAL_GPIO_WritePin (BLINK_GPIOx(BLINK_PORT_NUMBER),
		     BLINK_PIN_MASK(BLINK_PIN_NUMBER), GPIO_PIN_SET);
#endif
//@XCDL @if F3 or F2 or F1 or F0
#if (BLINK_ACTIVE_LOW)
  GPIO_ResetBits(BLINK_GPIOx(BLINK_PORT_NUMBER),
      BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#else
  GPIO_SetBits (BLINK_GPIOx(BLINK_PORT_NUMBER),
		BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#endif
//@XCDL @endif // family
}

inline void
__attribute__((always_inline))
blink_led_off (void)
{
//@XCDL @if F4
#if (BLINK_ACTIVE_LOW)
  HAL_GPIO_WritePin(BLINK_GPIOx(BLINK_PORT_NUMBER),
      BLINK_PIN_MASK(BLINK_PIN_NUMBER), GPIO_PIN_SET);
#else
  HAL_GPIO_WritePin (BLINK_GPIOx(BLINK_PORT_NUMBER),
		     BLINK_PIN_MASK(BLINK_PIN_NUMBER), GPIO_PIN_RESET);
#endif
//@XCDL @if F3 or F2 or F1 or F0
#if (BLINK_ACTIVE_LOW)
  GPIO_SetBits(BLINK_GPIOx(BLINK_PORT_NUMBER),
      BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#else
  GPIO_ResetBits (BLINK_GPIOx(BLINK_PORT_NUMBER),
		  BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#endif
//@XCDL @endif // family
}
//@XCDL @elif cpp

// ----------------------------------------------------------------------------

class BlinkLed
{
public:
  BlinkLed () = default;

  void
  powerUp ();

  inline void
  __attribute__((always_inline))
  turnOn ()
  {
//@XCDL @if F4
#if (BLINK_ACTIVE_LOW)
    HAL_GPIO_WritePin(BLINK_GPIOx(BLINK_PORT_NUMBER),
	BLINK_PIN_MASK(BLINK_PIN_NUMBER), GPIO_PIN_RESET);
#else
    HAL_GPIO_WritePin (BLINK_GPIOx(BLINK_PORT_NUMBER),
		       BLINK_PIN_MASK(BLINK_PIN_NUMBER), GPIO_PIN_SET);
#endif
//@XCDL @if F3 or F2 or F1 or F0
#if (BLINK_ACTIVE_LOW)
    GPIO_ResetBits(BLINK_GPIOx(BLINK_PORT_NUMBER),
	BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#else
    GPIO_SetBits (BLINK_GPIOx(BLINK_PORT_NUMBER),
		  BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#endif
//@XCDL @endif // family
  }

  inline void
  __attribute__((always_inline))
  turnOff ()
  {
//@XCDL @if F4
#if (BLINK_ACTIVE_LOW)
    HAL_GPIO_WritePin(BLINK_GPIOx(BLINK_PORT_NUMBER),
	BLINK_PIN_MASK(BLINK_PIN_NUMBER), GPIO_PIN_SET);
#else
    HAL_GPIO_WritePin (BLINK_GPIOx(BLINK_PORT_NUMBER),
		       BLINK_PIN_MASK(BLINK_PIN_NUMBER), GPIO_PIN_RESET);
#endif
//@XCDL @if F3 or F2 or F1 or F0
#if (BLINK_ACTIVE_LOW)
    GPIO_SetBits(BLINK_GPIOx(BLINK_PORT_NUMBER),
	BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#else
    GPIO_ResetBits (BLINK_GPIOx(BLINK_PORT_NUMBER),
		    BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#endif
//@XCDL @endif // family
  }
};

//@XCDL @endif // language
// ----------------------------------------------------------------------------

#endif // BLINKLED_H_
