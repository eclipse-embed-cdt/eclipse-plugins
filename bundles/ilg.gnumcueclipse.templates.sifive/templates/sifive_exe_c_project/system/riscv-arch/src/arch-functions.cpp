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
#include <cstddef>

// ----------------------------------------------------------------------------

// Anonymous namespace, functions visible only in this file.
namespace
{
  uint32_t cpu_frequency_;

  uint32_t
  measure_cpu_frequency_ (size_t mtime_counts)
  {
    uint32_t start_mtime;
    uint32_t delta_mtime;
    uint32_t mtime_freq = riscv::board::rtc_frequency_hz ();

    // Don't start measuring until we see an mtime tick
    uint32_t tmp = riscv::device::mtime_low ();
    do
      {
        start_mtime = riscv::device::mtime_low ();
      }
    while (start_mtime == tmp);

    // Assume the duration is short enough and the cycle counter
    // will not restart more than once.
    uint32_t start_mcycle = riscv::csr::mcycle_low ();

    do
      {
        delta_mtime = riscv::device::mtime_low () - start_mtime;
      }
    while (delta_mtime < mtime_counts);

    uint32_t delta_mcycle = riscv::csr::mcycle_low () - start_mcycle;

    return (delta_mcycle / delta_mtime) * mtime_freq
        + ((delta_mcycle % delta_mtime) * mtime_freq) / delta_mtime;
  }

}

// ----------------------------------------------------------------------------

namespace riscv
{
  namespace csr
  {
  // ------------------------------------------------------------------------
  // Control and Status Registers (CSRs).

#if __riscv_xlen == 32

  uint64_t
  mcycle (void)
    {
      // TODO: handle carry from low to high.
      uint64_t tmp = riscv_csr_read_mcycle_high ();
      return (tmp << 32) | riscv_csr_read_mcycle ();
    }

#endif /* __riscv_xlen == 32 */

}
/* namespace csr */

namespace core
{
// ------------------------------------------------------------------------
// Support functions.

uint32_t
cpu_frequency (void)
{
  if (cpu_frequency_ == 0)
    {
      update_cpu_frequency ();
    }

  return cpu_frequency_;
}

void
update_cpu_frequency (void)
{
  // warm up I$
  measure_cpu_frequency_ (1);
  // measure for real
  cpu_frequency_ = measure_cpu_frequency_ (10);
}

   // --------------------------------------------------------------------------
} /* namespace core */

   // ==========================================================================

namespace device
{
    // ------------------------------------------------------------------------

    // The system timer (actually the RTC) has a common interface, but
    // its address is device specific.

#if __riscv_xlen == 32

uint64_t
mtime (void)
{
  // Atomic read. The loop is taken one in most cases. Only when the
  // value carries to the high word, two loops are performed.
  while (true)
    {
      uint32_t hi = mtime_high ();
      uint32_t lo = mtime_low ();
      if (hi == mtime_high ())
        {
          return ((uint64_t) hi << 32) | lo;
        }
    }
}

void
mtime (uint64_t value)
{
  // Set high word to 0.
  mtime_high (0);
  // Set low word; if 0xFFFFFFFF it might carry 1 to high word.
  mtime_low ((uint32_t) (value));
  // Add initial high word value to 0 or 1.
  mtime_high (mtime_high () + (uint32_t) (value >> 32));
}

void
mtimecmp (uint64_t value)
{
  // In RV32, memory-mapped writes to mtimecmp modify only one 32-bit
  // part of the register.
  // To avoid spurious interrupts, the manual recommends to first
  // set the high part at max value.
  //
  //  # New comparand is in a1:a0.
  //  li t0, -1
  //  sw t0, mtimecmp   # write low as max; no smaller than old value.
  //  sw a1, mtimecmp+4 # write high; no smaller than new value.
  //  sw a0, mtimecmp   # write low as new value.

  mtimecmp_low ((uint32_t) (-1));
  mtimecmp_high ((uint32_t) (value >> 32));
  mtimecmp_low ((uint32_t) value);
}

#endif /* __riscv_xlen == 32 */

   // --------------------------------------------------------------------------
}
/* namespace device */

// ----------------------------------------------------------------------------
} /* namespace riscv */

// ----------------------------------------------------------------------------
// C aliases to the C++ functions.

#if __riscv_xlen == 32

uint64_t
__attribute__((alias("_ZN5riscv3csr6mcycleEv")))
riscv_csr_read_mcycle (void);

#endif /* __riscv_xlen == 32 */

uint32_t
__attribute__((alias("_ZN5riscv4core13cpu_frequencyEv")))
riscv_core_get_cpu_frequency (void);

void
__attribute__((alias("_ZN5riscv4core20update_cpu_frequencyEv")))
riscv_core_update_cpu_frequency (void);

#if __riscv_xlen == 32

// Device functions.
uint64_t
__attribute__((alias("_ZN5riscv6device5mtimeEv")))
riscv_device_read_mtime (void);

void
__attribute__((alias("_ZN5riscv6device5mtimeEy")))
riscv_device_write_mtime (uint64_t);

void
__attribute__((alias("_ZN5riscv6device8mtimecmpEy")))
riscv_device_write_mtimecmp (uint64_t value);

#endif /* __riscv_xlen == 32 */

// ----------------------------------------------------------------------------
