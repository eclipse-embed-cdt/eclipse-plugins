//
// This file is part of the GNU ARM Eclipse Plug-ins project.
// Copyright (c) 2014 Liviu Ionescu.
//

#include <stdio.h>

#include "Timer.h"

// Print a greeting message on standard output and enter a loop
// to count seconds.
//
// To run this, semihosting or ITM/SWV support is required. To select
// the output channel, define one of the macros OS_USE_TRACE_* required
// in system/src/newlib/trace.c.

#if defined(DEBUG)
void
dump_args (int argc, char* argv[]);
#endif

// Sample pragmas to cope with warnings. Please note the related line at
// the end of this function, used to pop the compiler diagnostics status.

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"
#pragma GCC diagnostic ignored "-Wmissing-declarations"
#pragma GCC diagnostic ignored "-Wreturn-type"

int
main (int argc, char* argv[])
{
#if defined(DEBUG)
  dump_args(argc, argv);

  printf ("Hello ARM World!\n");
#endif

  // Normally at this stage the microcontroller clock was already configured;
  // this happened within the CMSIS SystemInit() function, invoked from
  // the startup file (system/src/cortexm/_initialize_hardware.c)
  // before calling main().
  //
  // To reconfigure the default setting of the SystemInit() function,
  // refer to the file system/src/cmsis/system_DEVICE.c provided by the
  // vendor.
  //
  // In this sample the SystemInit() function is just a placeholder,
  // if you do not add the real one, the clock will remain configured with
  // the reset value, usually a relatively low speed RC clock (8-12MHz).

