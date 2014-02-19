#include "stm32f4xx.h"
#include <stdio.h>

/*
 * Semihosting STM32F4 led blink sample.
 *
 * In debug configurations, demonstrate how to print a greeting message
 * on the standard output. In release configurations the message is
 * simply discarded. By default the trace messages are forwarded to
 * the semihosting output.
 *
 * Then demonstrates how to blink a led with 1Hz, using a
 * continuous loop and SysTick delays.
 *
 * On DEBUG, the uptime in seconds is also displayed on the standard output.
 *
 * The external clock frequency is specified as a preprocessor definition
 * passed to the compiler via a command line option (see the 'C/C++ General' ->
 * 'Paths and Symbols' -> the 'Symbols' tab, if you want to change it).
 * The value selected during project creation was HSE_VALUE=$(STM32F4hseValue).
 *
 * Note1: The default clock settings assume that the HSE_VALUE is a multiple
 * of 1MHz, and try to reach the maximum speed available for the
 * board. It does NOT guarantee that the required USB clock of 48MHz is
 * available. If you need this, please update the settings of PLL_M, PLL_N,
 * PLL_P, PLL_Q in libs/CMSIS/src/system_stm32f4xx.c to match your needs.
 *
 * * Note2: The external memory controllers are not enabled. If needed, you
 * have to define DATA_IN_ExtSRAM or DATA_IN_ExtSDRAM and to configure
 * the memory banks in libs/CMSIS/src/system_stm32f4xx.c to match your needs.
 *
 * The build uses the startup files; on Release it does not use
 * any standard library function (on Debug the printf() brings lots of
 * functions; removing it should also use no other standard lib functions).
 *
 * To use the special initialisation code present in librdimon.a, for
 * semihosting, the definition USE_STARTUP_FILES is added for the
 * startup code.
 *
 */

// ----------------------------------------------------------------------------

#if defined(__cplusplus)
extern "C"
{
#endif

void
SysTick_Handler(void);

#if defined(__cplusplus)
}
#endif

// ----------------------------------------------------------------------------

static void
Delay(__IO uint32_t nTime);

static void
TimingDelay_Decrement(void);

/* ----- SysTick definitions ----------------------------------------------- */

#define SYSTICK_FREQUENCY_HZ       1000

/* ----- LED definitions --------------------------------------------------- */

/* Adjust them for your own board. */

#if defined(BOARD_OLIMEX_STM32_E407)

/* STM32-E407 definitions (the GREEN LED) */

#define BLINK_PORT      GPIOC
#define BLINK_PIN       13
#define BLINK_RCC_BIT   RCC_AHB1Periph_GPIOC

#else

/* STM32F4DISCOVERY definitions (the GREEN LED) */

#define BLINK_PORT      GPIOD
#define BLINK_PIN       12
#define BLINK_RCC_BIT   RCC_AHB1Periph_GPIOD

#endif

#define BLINK_TICKS     SYSTICK_FREQUENCY_HZ/2

// ----------------------------------------------------------------------------

int
main(int argc, char* argv[])
{
#if defined(DEBUG)

  /*
   * Show the program parameters (passed via semihosting).
   * Output is via the semihosting output channel.
   */
  printf("main(argc=%d, argv=[", argc);
  for (int i = 0; i < argc; ++i)
    {
      if (i != 0)
        {
          printf(", ");
        }
      printf("'%s'", argv[i]);
    }
  printf("])\n");

  /*
   * Send a greeting.
   */
  printf("Hello ARM World!\n");

#endif

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

#if defined(DEBUG)
      /*
       * Count seconds on the debug channel.
       */
      printf("Second %d\n", seconds);
#endif
    }
}
