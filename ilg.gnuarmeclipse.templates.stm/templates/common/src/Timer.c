//
// This file is part of the GNU ARM Eclipse distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#include "Timer.h"
#include "cortexm/ExceptionHandlers.h"

// ----------------------------------------------------------------------------

volatile timer_ticks_t timer_delayCount;

// ----------------------------------------------------------------------------

void
timer_start()
{
  // Use SysTick as reference for the delay loops.
  SysTick_Config(SystemCoreClock / TIMER_FREQUENCY_HZ);
}

void
timer_sleep(timer_ticks_t ticks)
{
  timer_delayCount = ticks;

  // Busy wait until the SysTick decrements the counter to zero.
  while (timer_delayCount != 0u)
    ;
}

void
timer_tick()
{
  // Decrement to zero the counter used by the delay routine.
  if (timer_delayCount != 0u)
    {
      --timer_delayCount;
    }
}

// ----- SysTick_Handler() ----------------------------------------------------

void
SysTick_Handler(void)
{
  timer_tick();
}

// ----------------------------------------------------------------------------
