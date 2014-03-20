//
// This file is part of the GNU ARM Eclipse Plug-ins project.
// Copyright (c) 2014 Liviu Ionescu.
//

#include <stdio.h>

#include "Timer.h"

// Print a greeting message on standard output and enter a loop
// to count seconds.
//
// To run this, semihosting or SWV support is required. To select the
// output channel, update the definition in libs/system/trace_impl.h.

void
dump_args (int argc, char* argv[]);

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"

int
main (int argc, char* argv[])
{
#if defined(DEBUG)
  dump_args(argc, argv);

  printf ("Hello ARM World!\n");
#endif

  // At this stage the microcontroller clock was already configured;
  // this happened within the CMSIS SystemInit() function, invoked from
  // the startup file (libs/system/src/_start.c) before calling main().
  //
  // To reconfigure the default setting of the SystemInit() function,
  // refer to the libs/Vendor/Device/src/system_DEVICE.c file.

  timer_start ();

  int seconds = 0;

  // Infinite loop
  while (1)
    {
      timer_sleep (TIMER_FREQUENCY_HZ);

      ++seconds;

#if defined(DEBUG)
      // Count seconds on the debug channel.
      printf ("Second %d\n", seconds);
#endif
    }
  // Infinite loop, never return.
}

#pragma GCC diagnostic pop

#if defined(DEBUG)

void
dump_args (int argc, char* argv[])
{
  printf ("main(argc=%d, argv=[", argc);
  for (int i = 0; i < argc; ++i)
    {
      if (i != 0)
	{
	  printf (", ");
	}
      printf ("\"%s\"", argv[i]);
    }
  printf ("]);\n");
}

#endif

