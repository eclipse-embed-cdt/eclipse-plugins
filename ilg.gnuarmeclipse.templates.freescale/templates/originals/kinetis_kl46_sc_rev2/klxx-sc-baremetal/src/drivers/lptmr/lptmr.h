/*
 * File:        lptmr.h
 * Purpose:     Provide common low power timer functions
 *
 * Notes:
 */

#ifndef __LPTMR_H__
#define __LPTMR_H__

/********************************************************************/
/* Miscellaneous defined */

#define LPTMR_USE_IRCLK 0 
#define LPTMR_USE_LPOCLK 1
#define LPTMR_USE_ERCLK32 2
#define LPTMR_USE_OSCERCLK 3

/* Function prototypes */
void time_delay_ms(unsigned int count_val);
void lptmr_isr(void);
void lptmr_init(int count, int clock_source);

/********************************************************************/

#endif /* __LPTMR_H__ */
