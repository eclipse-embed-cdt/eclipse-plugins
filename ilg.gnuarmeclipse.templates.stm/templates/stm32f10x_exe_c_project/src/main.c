/**
  ******************************************************************************
  * @file    Project/STM32F10x_StdPeriph_Template/main.c (modified)
  * @author  MCD Application Team
  * @version V3.5.0
  * @date    08-April-2011
  * @brief   Main program body
  ******************************************************************************
  * @attention
  *
  * THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS
  * WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE
  * TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY
  * DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS ARISING
  * FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS OF THE
  * CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.
  *
  * <h2><center>&copy; COPYRIGHT 2011 STMicroelectronics</center></h2>
  ******************************************************************************
  */

#include "stm32f10x.h"

/*

 In debug configurations, demonstrate how to print a greeting message
 on standard output.

 Then enter a continuous loop and blink a led.

 On embedded platforms printing the greeting might require
 semi-hosting or similar.

 For example, for toolchains derived from GNU Tools for Embedded,
 to enable semi-hosting, the following was added to the linker:

 --specs=rdimon.specs -Wl,--start-group -lgcc -lc -lc -lm -lrdimon -Wl,--end-group

 */

/* Olimex STM32-H103 LED definitions */
#define BLINK_PORT      GPIOC
#define BLINK_PIN       12
#define BLINK_RCC_BIT   RCC_APB2Periph_GPIOC

#define BLINK_LOOPS     1000000

#if defined(DEBUG)

#include <stdio.h>

extern void initialise_monitor_handles(void);

#endif

int
main(void)
{
#if defined(DEBUG)
  /* required for semi-hosting initialisation */
  initialise_monitor_handles();

  /* send greeting to semi-hosting output */
  printf("Hello ARM World!\n");
#endif

  GPIO_InitTypeDef GPIO_InitStructure;

  /*!< At this stage the microcontroller clock setting is already configured,
   this is done through SystemInit() function which is called from startup
   file (startup_stm32f10x_xx.s) before to branch to application main.
   To reconfigure the default setting of SystemInit() function, refer to
   system_stm32f10x.c file
   */

  /* GPIO Periph clock enable */
  RCC_APB2PeriphClockCmd(BLINK_RCC_BIT, ENABLE);

  /* Configure pin in output push/pull mode */
  GPIO_InitStructure.GPIO_Pin = (1 << BLINK_PIN);
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
  GPIO_Init(BLINK_PORT, &GPIO_InitStructure);

  while (1)
    {
      /* Assume the LED is active low */

      /* Turn on led by setting the pin low */
      GPIO_ResetBits(BLINK_PORT, (1 << BLINK_PIN));

      uint32_t i;

      i = 2 * BLINK_LOOPS;
      while (--i)
              ;

      /* Turn off led by setting the pin high */
      GPIO_SetBits(BLINK_PORT, (1 << BLINK_PIN));

      i = BLINK_LOOPS;
      while (--i)
        ;
    }

  return 0;
}
