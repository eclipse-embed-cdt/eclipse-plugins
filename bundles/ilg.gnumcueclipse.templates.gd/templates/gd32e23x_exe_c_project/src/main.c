/*!
    \file  main.c
    \brief running LED
    
    \version 2019-02-19, V1.0.0, firmware for GD32E23x
*/

/*
    Copyright (c) 2019, GigaDevice Semiconductor Inc.

    All rights reserved.

    Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice, this 
       list of conditions and the following disclaimer.
    2. Redistributions in binary form must reproduce the above copyright notice, 
       this list of conditions and the following disclaimer in the documentation 
       and/or other materials provided with the distribution.
    3. Neither the name of the copyright holder nor the names of its contributors 
       may be used to endorse or promote products derived from this software without 
       specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.
*/

#include "gd32e23x.h"
#include "systick.h"
#include <stdio.h>

/*!
    \brief      main function
    \param[in]  none
    \param[out] none
    \retval     none
*/
int main(void)
{
    systick_config();
    
    /* enable the LED2 GPIO clock */
    rcu_periph_clock_enable(RCU_GPIOA);
    /* configure LED2 GPIO port */ 
    gpio_mode_set(GPIOA, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, GPIO_PIN_8);
    gpio_output_options_set(GPIOA, GPIO_OTYPE_PP, GPIO_OSPEED_50MHZ, GPIO_PIN_8);
    /* reset LED2 GPIO pin */
    gpio_bit_reset(GPIOA,GPIO_PIN_8);

    /* enable the LED3 GPIO clock */
    /* configure LED3 GPIO port */ 
    gpio_mode_set(GPIOA, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, GPIO_PIN_11);
    gpio_output_options_set(GPIOA, GPIO_OTYPE_PP, GPIO_OSPEED_50MHZ, GPIO_PIN_11);
    /* reset LED3 GPIO pin */
    gpio_bit_reset(GPIOA,GPIO_PIN_11);
    
    /* enable the LED4 GPIO clock */
    /* configure LED4 GPIO port */ 
    gpio_mode_set(GPIOA, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, GPIO_PIN_12);
    gpio_output_options_set(GPIOA, GPIO_OTYPE_PP, GPIO_OSPEED_50MHZ, GPIO_PIN_12);
    /* reset LED4 GPIO pin */
    gpio_bit_reset(GPIOA,GPIO_PIN_12);
    
    /* enable the LED5 GPIO clock */
    /* configure LED5 GPIO port */ 
    gpio_mode_set(GPIOA, GPIO_MODE_OUTPUT, GPIO_PUPD_NONE, GPIO_PIN_15);
    gpio_output_options_set(GPIOA, GPIO_OTYPE_PP, GPIO_OSPEED_50MHZ, GPIO_PIN_15);
    /* reset LED5 GPIO pin */
    gpio_bit_reset(GPIOA,GPIO_PIN_15);

    while(1){
        /* turn on LED2, turn off LED5 */
        gpio_bit_set(GPIOA,GPIO_PIN_8);
        gpio_bit_reset(GPIOA,GPIO_PIN_15);
        delay_1ms(500);
        /* turn on LED3, turn off LED2 */
        gpio_bit_set(GPIOA,GPIO_PIN_11);
        gpio_bit_reset(GPIOA,GPIO_PIN_8);
        delay_1ms(500);
        /* turn on LED4, turn off LED3 */
        gpio_bit_set(GPIOA,GPIO_PIN_12);
        gpio_bit_reset(GPIOA,GPIO_PIN_11);
        delay_1ms(500);
        /* turn on LED5, turn off LED4 */
        gpio_bit_set(GPIOA,GPIO_PIN_15);
        gpio_bit_reset(GPIOA,GPIO_PIN_12);
        delay_1ms(500);
    }
}
