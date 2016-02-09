/*
 * File:		rtc.h
 * Purpose:     Provide common rtc functions
 *
 * Notes:
 */
#include "common.h"
#ifndef __RTC_H__
#define __RTC_H__

/********************************************************************/

void rtc_init(uint32 seconds, uint32 alarm, uint8 c_interval, uint8 c_value, uint8 interrupt);      
void rtc_isr(void);
void rtc_reg_report (void);

/********************************************************************/

#endif /* __RTC_H__ */
