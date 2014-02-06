/*
 * File:        sysinit.h
 * Purpose:     Kinetis L Family Configuration
 *              Initializes clock, abort button, clock output, and UART to a default state
 *
 * Notes:
 *
 */

/********************************************************************/

// function prototypes
void sysinit (void);
void enable_abort_button(void);
void clk_out_init(void);
/********************************************************************/
