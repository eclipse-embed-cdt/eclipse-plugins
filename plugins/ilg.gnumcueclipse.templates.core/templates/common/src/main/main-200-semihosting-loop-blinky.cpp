
  Timer timer;
  timer.start();

  BlinkLed blinkLed;

  // Perform all necessary initialisations for the LED.
  blinkLed.powerUp();

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
      blinkLed.turnOn();
      timer.sleep(i == 0 ? Timer::FREQUENCY_HZ : BLINK_ON_TICKS);

      blinkLed.turnOff();
      timer.sleep(BLINK_OFF_TICKS);

      ++seconds;
