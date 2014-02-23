
  // Use SysTick as reference for the delay loops.
  SysTick_Config(SystemCoreClock / SYSTICK_FREQUENCY_HZ);

  blink_led_init();
  
  int seconds = 0;

  // Infinite loop
  while (1)
    {
      blink_led_on();

      /* Keep the led ON 2/3 of the interval */
      delay(BLINK_TICKS*2);

      blink_led_off();

      /* Keep the led OFF 1/3 of the interval */
      delay(BLINK_TICKS);

      ++seconds;
