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

#ifndef RISCV_DEVICE_FUNCTIONS_H_
#define RISCV_DEVICE_FUNCTIONS_H_

#include <riscv-arch/arch-types.h>

/*
 * RISC-V device support functions.
 *
 * The declarations are part of the common design, but each device
 * must define the actual address and include the file
 * <riscv-arch/device-functions-inlines.h>.
 */

// ----------------------------------------------------------------------------
#if defined(__cplusplus)
extern "C"
{
#endif /* defined(__cplusplus) */

  // --------------------------------------------------------------------------
  // `mtime` functions.

#if __riscv_xlen == 64
  static
#endif /* __riscv_xlen == 64 */
  uint64_t
  riscv_device_read_mtime (void);

  static uint32_t
  riscv_device_read_mtime_low (void);

  static uint32_t
  riscv_device_read_mtime_high (void);

#if __riscv_xlen == 64
  static
#endif /* __riscv_xlen == 64 */
  void
  riscv_device_write_mtime (uint64_t value);

  static void
  riscv_device_write_mtime_low (uint32_t value);

  static void
  riscv_device_write_mtime_high (uint32_t value);

  // --------------------------------------------------------------------------
  // `mtimecmp` functions.

  static uint64_t
  riscv_device_read_mtimecmp (void);

  static uint32_t
  riscv_device_read_mtimecmp_low (void);

  static uint32_t
  riscv_device_read_mtimecmp_high (void);

#if __riscv_xlen == 64
  static
#endif /* __riscv_xlen == 64 */
  void
  riscv_device_write_mtimecmp (uint64_t value);

  static void
  riscv_device_write_mtimecmp_low (uint32_t value);

  static void
  riscv_device_write_mtimecmp_high (uint32_t value);

// ----------------------------------------------------------------------------

#if defined(__cplusplus)
}
#endif /* defined(__cplusplus) */

// ============================================================================

#if defined(__cplusplus)

namespace riscv
{
  namespace device
  {
    // ----------------------------------------------------------------------------
    // `mtime` functions.

    /**
     * Read the RTC current counter.
     */
    uint64_t
    mtime (void);

    uint32_t
    mtime_low (void);

    uint32_t
    mtime_high (void);

    void
    mtime (uint64_t value);

    void
    mtime_low (uint32_t value);

    void
    mtime_high (uint32_t value);

    // ------------------------------------------------------------------------
    // `mtimecmp` functions.

    /**
     * Read the RTC comparator.
     */
    uint64_t
    mtimecmp (void);

    uint32_t
    mtimecmp_low (void);

    uint32_t
    mtimecmp_high (void);

    /**
     * Write the RTC comparator.
     */
    void
    mtimecmp (uint64_t value);

    void
    mtimecmp_low (uint32_t value);

    void
    mtimecmp_high (uint32_t value);

  // --------------------------------------------------------------------------
  } /* namespace device */

// ----------------------------------------------------------------------------
} /* namespace riscv */

#endif /* defined(__cplusplus) */

// ----------------------------------------------------------------------------

#endif /* RISCV_DEVICE_FUNCTIONS_H_ */
