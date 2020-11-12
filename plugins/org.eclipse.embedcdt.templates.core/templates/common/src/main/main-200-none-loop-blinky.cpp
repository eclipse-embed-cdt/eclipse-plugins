
  timer timer;
  timer.start();

  led blink_led;

  // Perform all necessary initialisations for the LED.
  blink_led.power_up();

  uint32_t seconds = 0;

  // Infinite loop
  while (1)
    {
      blink_led.turn_on();
      timer.sleep(seconds== 0 ? timer::FREQUENCY_HZ : BLINK_ON_TICKS);

      blink_led.turn_off();
      timer.sleep(BLINK_OFF_TICKS);

      ++seconds;
