/*-----------------------------------------------------------------------------
 * Name:    Serial.c
 * Purpose: Low Level Serial Routines
 * Note(s): Possible defines select the used communication interface:
 *            __DBG_ITM   - ITM SWO interface
 *                        - UART3 interface  (default)
 *-----------------------------------------------------------------------------
 * This file is part of the uVision/ARM development tools.
 * This software may only be used under the terms of a valid, current,
 * end user licence from KEIL for a compatible version of KEIL software
 * development tools. Nothing else gives you the right to use this software.
 *
 * This software is supplied "AS IS" without warranties of any kind.
 *
 * Copyright (c) 2004-2011 Keil - An ARM Company. All rights reserved.
 *----------------------------------------------------------------------------*/
				
//#include "PCK20LN128.h"

#include "common.h"
#include "Serial.h"
#include "uart.h"

#ifdef __DBG_ITM
volatile int32_t ITM_RxBuffer;          /* CMSIS Debug Input                  */
#endif





/*-----------------------------------------------------------------------------
 *       SER_PutChar:  Write a character to the Serial Port
 *----------------------------------------------------------------------------*/
int32_t SER_PutChar (int32_t ch) {
#ifdef __DBG_ITM
  int i;
  ITM_SendChar (ch & 0xFF);
  for (i = 10000; i; i--);
#else
  //while (!(UART1_S1 & UART_S1_TDRE_MASK));
  //UART1_D = (ch & 0xFF);
  // Wait until space is available in the FIFO //
//  while (!(UART_S1_REG(TERM_PORT) & UART_S1_TDRE_MASK))
  //{}
    
  // Now Send the character //
  //UART_D_REG(TERM_PORT) = (uint8)ch;
	
	if (TERM_PORT_NUM == 0)
    uart0_putchar(UART0_BASE_PTR, ch);
  else if (TERM_PORT_NUM == 1)
    uart_putchar(UART1_BASE_PTR, ch);
  else
    uart_putchar(UART2_BASE_PTR, ch);

#endif  
  return (ch & 0xFF);
}


/*-----------------------------------------------------------------------------
 *       SER_GetChar:  Read a character from the Serial Port
 *----------------------------------------------------------------------------*/
int32_t SER_GetChar (void) {
#ifdef __DBG_ITM
  if (ITM_CheckChar())
    return ITM_ReceiveChar();
#else
//  if (UART1_S1 & UART_S1_RDRF_MASK) {
    //while (!(UART1_S1 & UART_S1_RDRF_MASK));
    //return (UART1_D);

	// Wait until character has been received //
    //while (!(UART_S1_REG(TERM_PORT) & UART_S1_RDRF_MASK));
    
    // Return the 8-bit data from the receiver //
    //return UART_D_REG(TERM_PORT);
	
	if (TERM_PORT_NUM == 0)
    return uart0_getchar(UART0_BASE_PTR);
  else if (TERM_PORT_NUM == 1)
    return uart_getchar(UART1_BASE_PTR);
  else
    return uart_getchar(UART2_BASE_PTR);
  
#endif
  return (-1);
}

/*-----------------------------------------------------------------------------
 * End of file
 *----------------------------------------------------------------------------*/
