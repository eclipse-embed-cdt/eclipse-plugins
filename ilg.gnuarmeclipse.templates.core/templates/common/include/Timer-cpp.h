//
// This file is part of the GNU ARM Eclipse distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#ifndef TIMER_H_
#define TIMER_H_

#include "cmsis_device.h"

// ----------------------------------------------------------------------------

class Timer
{
public:
  typedef uint32_t ticks_t;
  static constexpr ticks_t FREQUENCY_HZ = 1000u;

private:
  static volatile ticks_t ms_delayCount;

public:
  // Default constructor
  Timer() = default;

  inline void
  start(void)
  {
    // Use SysTick as reference for the delay loops.
    SysTick_Config(SystemCoreClock / FREQUENCY_HZ);
  }

  static void
  sleep(ticks_t ticks);

  inline static void
  tick(void)
  {
    // Decrement to zero the counter used by the delay routine.
    if (ms_delayCount != 0u)
      {
        --ms_delayCount;
      }
  }
};

// ----------------------------------------------------------------------------

#endif // TIMER_H_
