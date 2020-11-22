
  timer timer;
  timer.start ();

  // Perform all necessary initialisations for the LEDs.
  for (size_t i = 0; i < (sizeof(blinkLeds) / sizeof(blinkLeds[0])); ++i)
    {
      blinkLeds[i].power_up ();
    }

  uint32_t seconds = 0;

  for (size_t i = 0; i < (sizeof(blinkLeds) / sizeof(blinkLeds[0])); ++i)
    {
      blinkLeds[i].turn_on ();
    }

  // First second is long.
  timer.sleep (timer::FREQUENCY_HZ);

  for (size_t i = 0; i < (sizeof(blinkLeds) / sizeof(blinkLeds[0])); ++i)
    {
      blinkLeds[i].turn_off ();
    }

  timer.sleep (BLINK_OFF_TICKS);

  ++seconds;
  trace_printf ("Second %u\n", seconds);

  if ((sizeof(blinkLeds) / sizeof(blinkLeds[0])) > 1)
    {
      // Blink individual LEDs.
      for (size_t i = 0; i < (sizeof(blinkLeds) / sizeof(blinkLeds[0])); ++i)
        {
          blinkLeds[i].turn_on ();
          timer.sleep (BLINK_ON_TICKS);

          blinkLeds[i].turn_off ();
          timer.sleep (BLINK_OFF_TICKS);

          ++seconds;
          trace_printf ("Second %u\n", seconds);
        }

      // Blink binary.
      while (1)
        {
          for (size_t l = 0; l < (sizeof(blinkLeds) / sizeof(blinkLeds[0]));
              ++l)
            {
              blinkLeds[l].toggle ();
              if (blinkLeds[l].isOn ())
                {
                  break;
                }
            }
          timer.sleep (timer::FREQUENCY_HZ);

          ++seconds;
          trace_printf ("Second %u\n", seconds);
        }
      // Infinite loop, never return.
    }
  else
    {
      while (1)
        {
          blinkLeds[0].turn_on ();
          timer.sleep (BLINK_ON_TICKS);

          blinkLeds[0].turn_off ();
          timer.sleep (BLINK_OFF_TICKS);

          ++seconds;
          trace_printf ("Second %u\n", seconds);
        }
      // Infinite loop, never return.
    }
