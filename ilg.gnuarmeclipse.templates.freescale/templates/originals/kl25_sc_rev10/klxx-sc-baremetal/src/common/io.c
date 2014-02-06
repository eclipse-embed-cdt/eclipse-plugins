/*
 * File:		io.c
 * Purpose:		Serial Input/Output routines
 *
 * Notes:       TERMINAL_PORT defined in <board>.h
 */

#include "common.h"
#include "uart.h"

/********************************************************************/
char
in_char (void)
{
  if (TERM_PORT_NUM == 0)
    return uart0_getchar(UART0_BASE_PTR);
  else if (TERM_PORT_NUM == 1)
    return uart_getchar(UART1_BASE_PTR);
  else
    return uart_getchar(UART2_BASE_PTR);
 
}
/********************************************************************/
void
out_char (char ch)
{
  if (TERM_PORT_NUM == 0)
    uart0_putchar(UART0_BASE_PTR, ch);
  else if (TERM_PORT_NUM == 1)
    uart_putchar(UART1_BASE_PTR, ch);
  else
    uart_putchar(UART2_BASE_PTR, ch);
  
}

/********************************************************************/
int
char_present (void)
{
  if (TERM_PORT_NUM == 0)
    return uart0_getchar_present(UART0_BASE_PTR);
  else if (TERM_PORT_NUM == 1)
    return uart_getchar_present(UART1_BASE_PTR);
  else
    return uart_getchar_present(UART2_BASE_PTR);
  
}
/********************************************************************/
