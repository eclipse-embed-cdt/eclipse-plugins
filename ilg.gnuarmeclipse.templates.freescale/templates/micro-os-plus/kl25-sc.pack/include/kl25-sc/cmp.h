/*
 * File:		cmp.h
 * Purpose:     Provide common ColdFire UART routines for polled serial IO
 *
 * Notes:
 */

#ifndef __CMP_H__
#define __CMP_H__

/********************************************************************/
// Function prototypes
void cmp_init ( unsigned char rising_falling )  ;
void cmp0_isr(void);


/********************************************************************/

#endif /* __CMP_H__ */
