/*
 * File:		pmc.h
 * Purpose:     Provides low power mode entry routines
 *
 * Notes:
 */

#ifndef __PMC_H__
#define __PMC_H__

/********************************************************************/
/* 
LVWV if LVD high range selected
2.62
2.72
2.82
2.92
LVDW if LVD low range selected
1.74
1.84
1.94
2.04
*/
/* 00 Low trip point selected (V LVD = V LVDL )
 * 01 High trip point selected (V LVD = V LVDH )
*/
#define VLVDH 1
#define VLVDL 0
#define LVDRESETEN  1
#define LVDRESETDIS 0
#define LVDINTEN  1
#define LVDINTDIS 0
/* 00 Low trip point selected (V LVW = V LVW1 )
 * 01 Mid 1 trip point selected (V LVW = V LVW2 )
 * 10 Mid 2 trip point selected (V LVW = V LVW3 )
 * 11 High trip point selected (V LVW = V LVW4 )
*/
#define VLVW4 3
#define VLVW3 2
#define VLVW2 1
#define VLVW1 0
#define LVWINTEN  1
#define LVWINTDIS 0

// function prototypes
/* LVD initialization code */
void LVD_Initalize(unsigned char lvd_select,
                   unsigned char lvd_reset_enable,
                   unsigned char lvd_int_enable, 
                   unsigned char lvw_select,
                   unsigned char lvw_int_enable);
void LVD_Init(void);
/* LVD interrupt service routine - If entering into this interrupt from stop you are in pbe mode.*/  
void pmc_lvd_isr(void);

/********************************************************************/
#endif /* __PMC_H__ */
