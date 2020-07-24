
  Timer timer;
  timer.start();

  BlinkLed blinkLed;

  // Perform all necessary initialisations for the LED.
  blinkLed.powerUp();

  uint32_t seconds = 0;

  // Infinite loop
  while (1)
    {
      blinkLed.turnOn();
      timer.sleep(seconds== 0 ? Timer::FREQUENCY_HZ : BLINK_ON_TICKS);

      blinkLed.turnOff();
      timer.sleep(BLINK_OFF_TICKS);

      ++seconds;
