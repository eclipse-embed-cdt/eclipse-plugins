/* ------------------------------------------------------------------------- */

static void
delay_decrement(void);

void
SysTick_Handler(void);

static __IO uint32_t delay_count;

/**
 * @brief  Insert a delay time.
 * @param  nTime: specifies the delay time length, in SysTick ticks.
 * @retval None
 */
void
delay(__IO uint32_t nTime)
{
  delay_count = nTime;

  /* Busy wait until the interrupt routine decrements the counter to zero */
  while (delay_count != 0)
    ;
}

/**
 * @brief  Decrement the delay_count variable.
 * @param  None
 * @retval None
 */
void
delay_decrement(void)
{
  if (delay_count != 0)
    {
      delay_count--;
    }
}

/* ------------------------------------------------------------------------- */

/**
 * @brief  This function is the SysTick Handler.
 * @param  None
 * @retval None
 */
void
SysTick_Handler(void)
{
  delay_decrement();
}

/* ------------------------------------------------------------------------- */
