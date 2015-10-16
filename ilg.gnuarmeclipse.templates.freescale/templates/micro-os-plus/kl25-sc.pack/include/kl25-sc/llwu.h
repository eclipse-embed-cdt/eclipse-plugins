/*!
 * \file    llwu_common.h
 * \brief   common LLWU defines
 *
 * This file defines the functions/interrupt handlers/macros used for LLWU.
 * And some common function prototypes and initializations.
 *
 * \version $Revision: 1.1 $
 * \author  Philip Drake[rxaa60]
 */

#ifndef __LLWU_COMMON_H__
#define __LLWU_COMMON_H__

/* 
 * Misc. Defines
 */
#define LLWU_LPTMR_ME   0x01
#define LLWU_CMP0_ME   0x02
#define LLWU_CMP1_ME   0x04
#define LLWU_CMP2_ME   0x08
#define LLWU_TSI_ME   0x10
#define LLWU_RTCA_ME   0x20
#define LLWU_RESRV_ME   0x40
#define LLWU_RTCS_ME   0x80

#define LLWU_PIN_DIS 0
#define LLWU_PIN_RISING 1
#define LLWU_PIN_FALLING 2
#define LLWU_PIN_ANY 3


/* 
 * Function prototypes
 */

void llwu_configure(unsigned int pin_en, unsigned char rise_fall, unsigned char module_en);
void llwu_isr(void);
void llwu_configure_filter(unsigned int wu_pin_num, unsigned char filter_en, unsigned char rise_fall ); 

#endif /* __LLWU_COMMON_H__ */
