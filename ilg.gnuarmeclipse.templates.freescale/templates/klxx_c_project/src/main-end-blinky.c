// ----- delay() --------------------------------------------------------------

static volatile uint32_t delay_count;

// Insert a delay, with duration given in ticks.
void
delay(uint32_t nTime)
{
  delay_count = nTime;

  // Busy wait until the SysTick decrements the counter to zero.
  while (delay_count != 0)
    ;
}

// ----- SysTick_Handler() ----------------------------------------------------

void
SysTick_Handler(void)
{
  // Decrement to zero the counter used by the delay routine.
  if (delay_count != 0)
    {
      delay_count--;
    }
}

/* ------------------------------------------------------------------------- */
