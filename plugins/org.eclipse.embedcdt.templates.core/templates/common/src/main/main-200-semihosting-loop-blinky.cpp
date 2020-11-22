
  timer timer;
  timer.start();

  led blink_led;

  // Perform all necessary initialisations for the LED.
  blink_led.power_up();

  uint32_t seconds = 0;

#define LOOP_COUNT (5)
  int loops = LOOP_COUNT;
  if (argc > 1)
    {
      // If defined, get the number of loops from the command line,
      // configurable via semihosting.
      loops = atoi (argv[1]);
    }

  // Short loop.
  for (int i = 0; i < loops; i++)
    {
      blink_led.turn_on();
      timer.sleep(i == 0 ? timer::FREQUENCY_HZ : BLINK_ON_TICKS);

      blink_led.turn_off();
      timer.sleep(BLINK_OFF_TICKS);

      ++seconds;
