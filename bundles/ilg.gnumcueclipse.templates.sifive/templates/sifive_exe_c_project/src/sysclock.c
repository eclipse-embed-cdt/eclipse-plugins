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

// ----------------------------------------------------------------------------

void
os_sysclock_construct (void)
{
  sysclock.steady_count_ = 0;
}

void
os_sysclock_sleep_for (os_clock_duration_t duration)
{
  // Compute the timestamp when the sleep should end.
  os_clock_timestamp_t then = os_sysclock_steady_now () + duration;

  // Busy wait until the current time reaches the desired timestamp.
  while (os_sysclock_steady_now () < then)
    {
      riscv_arch_wfi ();
    }
}

// ----------------------------------------------------------------------------

// Instantiate a static system clock object.
os_clock_t sysclock;

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wmissing-declarations"
#pragma GCC diagnostic ignored "-Wmissing-prototypes"

// Automatically called during startup, as for the C++ static
// constructors, before reaching `main()`.
void
__attribute__((constructor))
clock_construct (void)
{
  os_sysclock_construct ();
}

#pragma GCC diagnostic pop

// ----------------------------------------------------------------------------
