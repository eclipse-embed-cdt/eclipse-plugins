//See LICENSE for license details.
#include <stdint.h>
#include <stdio.h>
#include <unistd.h>

#include "soc/drivers/soc.h"
#include "n200/drivers/n200_func.h"


void uart_init(size_t baud_rate)
{
  GPIO_REG(GPIO_IOF_SEL) &= ~IOF0_UART0_MASK;
  GPIO_REG(GPIO_IOF_EN) |= IOF0_UART0_MASK;
  UART0_REG(UART_REG_DIV) = get_cpu_freq() / baud_rate - 1;
  UART0_REG(UART_REG_TXCTRL) |= UART_TXEN;
  UART0_REG(UART_REG_RXCTRL) |= UART_RXEN;
}


void soc_init()
{
    // This function need to be added by user to initialize their SoC
  uart_init(115200);
}
