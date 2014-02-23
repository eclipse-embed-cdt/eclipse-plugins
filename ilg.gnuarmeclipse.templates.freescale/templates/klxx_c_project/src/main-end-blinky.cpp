// ----- delay() --------------------------------------------------------------

// Definitions visible only within this translation unit.
namespace
{
  volatile ticks_t delayCount;

  // Insert a delay, with duration given in ticks.
  void
  delay(ticks_t ticks)
  {
    delayCount = ticks;

    // Busy wait until the SysTick decrements the counter to zero.
    while (delayCount != 0u)
      ;
  }
}

// ----- SysTick_Handler() ----------------------------------------------------

extern "C" void
SysTick_Handler(void)
{
  // Decrement to zero the counter used by the delay routine.
  if (delayCount != 0u)
    {
      --delayCount;
    }
}

// ----------------------------------------------------------------------------
