  /*
   * At this stage the microcontroller clock is already configured; this
   * happened within the CMSIS SystemInit() function, called from the 
   * startup file (libs/CMSIS/src/startup_cm.c) before calling main().
   * 
   * To reconfigure the default setting of the SystemInit() function,
   * refer to the libs/CMSIS/src/system_MKL25Z4.c file.
   */

  /* Use SysTick as reference for the timer */
  SysTick_Config(SystemCoreClock / SYSTICK_FREQUENCY_HZ);

  blink_led_init();
  
  int seconds = 0;

  /* Infinite loop */
  while (1)
    {
      /* Turn led ON */
      blink_led_on();

      /* Keep the led ON 2/3 of the interval */
      delay(BLINK_TICKS*2);

      /* Turn led OFF */
      blink_led_off();

      /* Keep the led OFF 1/3 of the interval */
      delay(BLINK_TICKS);

      ++seconds;
