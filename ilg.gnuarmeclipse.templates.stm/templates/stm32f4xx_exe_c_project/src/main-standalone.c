#include "stm32f4xx.h"
//#include <stdio.h>

/*
 * Standalone STM32F4 led blink sample.
 *
 * This program demonstrates how to blink a led with 1Hz, using a
 * continuous loop and SysTick delays.
 *
 * The external clock frequency is specified as a preprocessor definition
 * passed to the compiler via a command line option (see the 'C/C++ General' ->
 * 'Paths and Symbols' -> the 'Symbols' tab, if you want to change it).
 * The value selected during project creation was HSE_VALUE=$(STM32F4hseValue).
 *
 * Note: The default clock settings assume that the HSE_VALUE is a multiple
 * of 1MHz, and try to reach the maximum speed available for the
 * board. It does NOT guarantee that the required USB clock of 48MHz is
 * available. If you need this, please update the settings of PLL_M, PLL_N,
 * PLL_P, PLL_Q in libs/CMSIS/src/system_stm32f4xx.c to match your needs.
 *
 * The build does not use startup files, and, in the simple template version,
 * does not even use any standard library function.
 *
 * If the application requires special initialisation code present
 * in some other libraries (for example librdimon.a, for semihosting),
 * define USE_STARTUP_FILES and uncheck the corresponding option in the
 * linker configuration.
 *
 */

// ----------------------------------------------------------------------------

static void
Delay(__IO uint32_t nTime);

static void
TimingDelay_Decrement(void);

void
SysTick_Handler(void);

/* ----- SysTick definitions ----------------------------------------------- */

#define SYSTICK_FREQUENCY_HZ       1000

/* ----- LED definitions --------------------------------------------------- */

/* STM32F4DISCOVERY definitions (the GREEN LED) */
/* Adjust them for your own board. */

#define BLINK_PORT      GPIOD
#define BLINK_PIN       12
#define BLINK_RCC_BIT   RCC_AHB1Periph_GPIOD

#define BLINK_TICKS     SYSTICK_FREQUENCY_HZ/2

// ----------------------------------------------------------------------------

int
main(void)
{

  /*
   * At this stage the microcontroller clock setting is already configured,
   * this is done through SystemInit() function which is called from startup
   * files (startup_cm.c) before to branch to the
   * application main. To reconfigure the default setting of SystemInit()
   * function, refer to system_stm32f4xx.c file.
   */

  RCC_ClocksTypeDef RCC_Clocks;

  /* Use SysTick as reference for the timer */
  RCC_GetClocksFreq(&RCC_Clocks);
  SysTick_Config(RCC_Clocks.HCLK_Frequency / SYSTICK_FREQUENCY_HZ);

  /* GPIO Periph clock enable */
  RCC_AHB1PeriphClockCmd(BLINK_RCC_BIT, ENABLE);

  GPIO_InitTypeDef GPIO_InitStructure;

  /* Configure pin in output push/pull mode */
  GPIO_InitStructure.GPIO_Pin = (1 << BLINK_PIN);
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_OUT;
  GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_100MHz;
  GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL;
  GPIO_Init(BLINK_PORT, &GPIO_InitStructure);

  int seconds = 0;

  /* Infinite loop */
  while (1)
    {
      /* Assume the LED is active low */

      /* Turn on led by setting the pin low */
      GPIO_ResetBits(BLINK_PORT, (1 << BLINK_PIN));

      Delay(BLINK_TICKS);

      /* Turn off led by setting the pin high */
      GPIO_SetBits(BLINK_PORT, (1 << BLINK_PIN));

      Delay(BLINK_TICKS);

      ++seconds;
    }
}
