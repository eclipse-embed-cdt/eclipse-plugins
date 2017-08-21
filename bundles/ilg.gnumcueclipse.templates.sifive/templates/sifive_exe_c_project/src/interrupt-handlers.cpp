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
riscv_irq_handle_machine_timer (void)
{
  // Disable M timer interrupt.
  riscv::csr::clear_mie (MIP_MTIP);

  uint64_t tim = riscv::device::mtime ();
  uint64_t cmp;

  // The simple method to compute mtimecmp by adding a value to the
  // current mtime might not be accurate in case
  // the sysclock frequency is not a divisor of RTC frequency,
  // like 1000 Hz with a typical 32768 Hz RTC.
  //
  // A better way is to advance one or more ticks and recompute
  // the comparator; this continuous computation should spread the error
  // over time, to produce a fairly accurate clock. In the usual
  // case the comparator will advance with either 32 or 33 ticks,
  // uniformly distributed.
  //
  // The disadvantage is that it uses 64-bits operations, and might
  // not produce accurate results at very high values.
  // The variable duration will add an occasional jitter of the
  // system clock, but this should not be a problem.
  do
    {
      sysclock.internal_increment_count ();
      cmp = sysclock.steady_now () * riscv::board::rtc_frequency_hz ()
	  / sysclock.frequency_hz;
    }
  while (cmp <= tim);

  // The interrupt remains posted until it is cleared by writing
  // the mtimecmp register.
  riscv::device::mtimecmp (cmp);

  // os::trace::putchar('.');

  // Enable M timer interrupt.
  riscv::csr::set_mie (MIP_MTIP);
}

// ----------------------------------------------------------------------------
