// See LICENSE file for licence details

#ifndef N200_TIMER_H
#define N200_TIMER_H

  // Need to know the following info from the soc.h, so include soc.h here
  //   SOC_TIMER_CTRL_ADDR      : what is the base address of TIMER in this SoC 
  //   SOC_TIMER_FREQ           : what is the frequency for TIMER to count (by rtc_toggle_a signal)
#include "soc/drivers/soc.h"


#define TIMER_MSIP 0xFFC
#define TIMER_MSIP_size   0x4
#define TIMER_MTIMECMP 0x8
#define TIMER_MTIMECMP_size 0x8
#define TIMER_MTIME 0x0
#define TIMER_MTIME_size 0x8

#define TIMER_CTRL_ADDR           SOC_TIMER_CTRL_ADDR
#define TIMER_REG(offset)         _REG32(TIMER_CTRL_ADDR, offset)
#define TIMER_FREQ                SOC_TIMER_FREQ


#endif
