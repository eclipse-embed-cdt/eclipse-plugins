
  // Use SysTick as reference for the delay loops.
  SysTick_Config(SystemCoreClock / SYSTICK_FREQUENCY_HZ);

  // Since the led is known at compile time, with the constant templates
  // the compiler is able to inline most of the code.

  // Instantiate a constant template defining the GPIO pin used for the LED.
  using BlinkGpioPin = klxx::TGpioPinConst<BLINK_PORT_NUMBER, BLINK_PORT_BIT_NUMBER>;

  // Instantiate a constant template defining the actual behaviour of the LED.
  using BlinkLed = klxx::TLedActiveLowConst<BlinkGpioPin>;

  // Construct an empty object, it behaves like having all methods static.
  BlinkLed blinkLed;

  // Perform all necessary initialisations for the LED.
  blinkLed.powerUp();

  int seconds = 0;

  // Infinite loop
  while (1)
    {
      blinkLed.turnOn();

      // Keep the led ON 2/3 of the interval.
      delay(BLINK_TICKS * 2);

      blinkLed.turnOff();

      // Keep the led OFF 1/3 of the interval.
      delay(BLINK_TICKS);

      ++seconds;
