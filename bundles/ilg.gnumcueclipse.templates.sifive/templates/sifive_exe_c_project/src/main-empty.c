/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2017 Liviu Ionescu.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

#include <micro-os-plus/board.h>
#include <micro-os-plus/diag/trace.h>

#include <sysclock.h>
#include <led.h>

#include <stdint.h>
#include <stdio.h>

// ----------------------------------------------------------------------------

/*
 * This program counts seconds on the SiFive HiFive1 board.
 */

// ----------------------------------------------------------------------------
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"

int
main (int argc, char* argv[])
{
  // Send a greeting to the trace device (skipped on Release).
  trace_puts ("Hello RISC-V World!");

  // Send a message to the standard output.
  puts ("Standard output message.");

  // Send a message to the standard error.
  fprintf (stderr, "Standard error message.\n");

  // At this stage the system clock should have already been configured
  // at high speed.
  trace_printf ("System clock: %u Hz\n", riscv_core_get_cpu_frequency ());

  uint32_t seconds = 0;

  // Loop forever.
  while (true)
    {
      os_sysclock_sleep_for (os_sysclock_get_frequency_hz ());

      ++seconds;
      trace_printf ("Second %u\n", seconds);
    }
}

#pragma GCC diagnostic pop

// ----------------------------------------------------------------------------

