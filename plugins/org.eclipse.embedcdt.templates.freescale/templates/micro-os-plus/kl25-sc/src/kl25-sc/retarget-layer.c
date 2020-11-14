#include <stdio.h>
#include "common.h"
#include "uart.h"

// #pragma import(__use_no_semihosting_swi)

void
_ttywrch(int c);
void
sys_exit(int return_code);

int
SER_GetChar(void);
int
SER_PutChar(int c);

#ifdef __DBG_ITM
volatile int32_t ITM_RxBuffer;          /* CMSIS Debug Input                  */
#endif

struct __FILE {
	int handle; /* Add whatever you need here */
};
FILE __stdout;
FILE __stdin;

int fputc(int c, FILE *f __attribute__((unused))) {
	return (SER_PutChar(c));
}

int fgetc(FILE *f __attribute__((unused))) {
	return (SER_GetChar());
}

void _ttywrch(int c __attribute__((unused))) {
	SER_PutChar(c);
}

void sys_exit(int return_code __attribute__((unused))) {
	// endless loop.
	while (1)
		;
}

// ----------------------------------------------------------------------------
// Write a character.

int SER_PutChar(int ch) {
#ifdef __DBG_ITM
	int i;
	ITM_SendChar (ch & 0xFF);
	for (i = 10000; i; i--)
		;
#else
	if (TERM_PORT_NUM == 0)
		uart0_putchar(UART0_BASE_PTR, (char) ch);
	else if (TERM_PORT_NUM == 1)
		uart_putchar(UART1_BASE_PTR, (char) ch);
	else
		uart_putchar(UART2_BASE_PTR, (char) ch);
#endif
	return (ch & 0xFF);
}

// ----------------------------------------------------------------------------
// Read a character.

int SER_GetChar(void) {
#ifdef __DBG_ITM
	if (ITM_CheckChar())
		return ITM_ReceiveChar();
#else
	if (TERM_PORT_NUM == 0)
		return uart0_getchar(UART0_BASE_PTR);
	else if (TERM_PORT_NUM == 1)
		return uart_getchar(UART1_BASE_PTR);
	else
		return uart_getchar(UART2_BASE_PTR);
#endif
}
