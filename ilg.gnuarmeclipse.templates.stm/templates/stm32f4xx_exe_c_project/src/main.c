#include "stm32f4xx.h"
#include <stdio.h>

void
SysTick_Handler(void);

/*
 * STM32F4DISCOVERY led blink sample.
 *
 * In debug configurations, demonstrate how to print a greeting message
 * on the standard output. In release configurations the message is
 * simply discarded.
 *
 * Then enter a continuous loop and blink a led with 1Hz.
 *
 * The external clock frequency is specified as HSE_VALUE=8000000,
 * adjust it for your own board. Also adjust the PLL constants to
 * reach the maximum frequency, or special clock configurations.
 *
 * The build does not use startup files, and on Release it does not even use
 * any standard library function (on Debug the printf() brigs lots of
 * functions; removing it should also use no other standard lib functions).
 *
 * If the application requires to use a special initialisation code present
 * in some other libraries (for example librdimon.a, for semi-hosting),
 * define USE_STARTUP_FILES and uncheck the corresponding option in the
 * linker configuration.
 */

static __IO uint32_t uwTimingDelay;
RCC_ClocksTypeDef RCC_Clocks;

static void
Delay(__IO uint32_t nTime);

static void
TimingDelay_Decrement(void);

/* ----- SysTick definitions ----------------------------------------------- */

#define SYSTICK_FREQUENCY_HZ       1000

/* ----- LED definitions --------------------------------------------------- */

/* STM32F4DISCOVERY definitions (the GREEN LED) */
/* Adjust them for your own board. */

#define BLINK_PORT      GPIOD
#define BLINK_PIN       12
#define BLINK_RCC_BIT   RCC_AHB1Periph_GPIOD

#define BLINK_TICKS     SYSTICK_FREQUENCY_HZ/2

/* ------------------------------------------------------------------------- */

int
main(void)
{
#if defined(DEBUG)
  /*
   * Send a greeting to the standard output (the semi-hosting debug channel
   * on Debug, ignored on Release).
   */
  printf("Hello ARM World!\n");
#endif

  /*
   * At this stage the microcontroller clock setting is already configured,
   * this is done through SystemInit() function which is called from startup
   * files (startup_stm32f4xx.c) before to branch to the
   * application main. To reconfigure the default setting of SystemInit()
   * function, refer to system_stm32f4xx.c file.
   */

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
    }
}

/**
 * @brief  Inserts a delay time.
 * @param  nTime: specifies the delay time length, in SysTick ticks.
 * @retval None
 */
void
Delay(__IO uint32_t nTime)
{
  uwTimingDelay = nTime;

  while (uwTimingDelay != 0)
    ;
}

/**
 * @brief  Decrements the TimingDelay variable.
 * @param  None
 * @retval None
 */
void
TimingDelay_Decrement(void)
{
  if (uwTimingDelay != 0x00)
    {
      uwTimingDelay--;
    }
}

/**
 * @brief  This function is the SysTick Handler.
 * @param  None
 * @retval None
 */
void
SysTick_Handler(void)
{
  TimingDelay_Decrement();
}

#ifdef  USE_FULL_ASSERT

/**
 * @brief  Reports the name of the source file and the source line number
 *         where the assert_param error has occurred.
 * @param  file: pointer to the source file name
 * @param  line: assert_param error line source number
 * @retval None
 */
void assert_failed(uint8_t* file, uint32_t line)
  {
    /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */

    /* Infinite loop */
    while (1)
      {
      }
  }
#endif

