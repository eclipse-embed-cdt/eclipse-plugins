
  timer_start();

  blink_led_init();
  
  int seconds = 0;

  // Infinite loop
  while (1)
    {
      blink_led_on();
      timer_sleep(BLINK_ON_TICKS);

      blink_led_off();
      timer_sleep(BLINK_OFF_TICKS);

      ++seconds;
