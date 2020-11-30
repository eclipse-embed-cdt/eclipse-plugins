
  timer timer;
  timer.start ();

  // Perform all necessary initialisations for the LEDs.
  for (size_t i = 0; i < (sizeof(blinkLeds) / sizeof(blinkLeds[0])); ++i)
    {
      blinkLeds[i].power_up ();
    }

  uint32_t seconds = 0;

#define LOOP_COUNT (1 << (sizeof(blinkLeds) / sizeof(blinkLeds[0])))

  int loops = LOOP_COUNT > 2 ? LOOP_COUNT : (5);
  if (argc > 1)
    {
      // If defined, get the number of loops from the command line,
      // configurable via semihosting.
      loops = atoi (argv[1]);
      if (loops < LOOP_COUNT)
        {
          loops = LOOP_COUNT;
        }
    }

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
      for (int i = 0; i < loops; i++)
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
    }
  else
    {
      for (int i = 0; i < loops; i++)
        {
          blinkLeds[0].turn_on ();
          timer.sleep (BLINK_ON_TICKS);

          blinkLeds[0].turn_off ();
          timer.sleep (BLINK_OFF_TICKS);

          ++seconds;
          trace_printf ("Second %u\n", seconds);
        }
    }

  for (size_t i = 0; i < (sizeof(blinkLeds) / sizeof(blinkLeds[0])); ++i)
    {
      blinkLeds[i].turn_on ();
    }

  timer.sleep (timer::FREQUENCY_HZ);

