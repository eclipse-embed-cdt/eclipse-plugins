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

#ifndef RISCV_DEVICE_FUNCTIONS_INLINES_H_
#define RISCV_DEVICE_FUNCTIONS_INLINES_H_

#include <stdint.h>

/*
 * Inline implementations for the common device support functions.
 *
 * Not included by architecture files, but by device files.
 */

#if defined(__cplusplus)
extern "C"
{
#endif /* defined(__cplusplus) */

  // --------------------------------------------------------------------------

#if __riscv_xlen == 64

  static inline uint64_t
  __attribute__((always_inline))
  riscv_device_read_mtime (void)
    {
      return *(volatile uint64_t *) (RISCV_MMIO_MTIME_ADDR);
    }

#endif /* __riscv_xlen == 64 */

  static inline uint32_t
  __attribute__((always_inline))
  riscv_device_read_mtime_low (void)
  {
    return *(volatile uint32_t *) (RISCV_MMIO_MTIME_ADDR);
  }

  static inline uint32_t
  __attribute__((always_inline))
  riscv_device_read_mtime_high (void)
  {
    return *(volatile uint32_t *) (RISCV_MMIO_MTIME_ADDR + 4);
  }

#if __riscv_xlen == 64

  static inline void
  __attribute__((always_inline))
  riscv_device_write_mtime (uint64_t value)
    {
      *(volatile uint64_t *) (RISCV_MMIO_MTIME_ADDR) = value;
    }

#endif /* __riscv_xlen == 64 */

  static inline void
  __attribute__((always_inline))
  riscv_device_write_mtime_low (uint32_t value)
  {
    *(volatile uint32_t *) (RISCV_MMIO_MTIME_ADDR) = value;
  }

  static inline void
  __attribute__((always_inline))
  riscv_device_write_mtime_high (uint32_t value)
  {
    *(volatile uint32_t *) (RISCV_MMIO_MTIME_ADDR + 4) = value;
  }

  // --------------------------------------------------------------------------

  static inline uint64_t
  __attribute__((always_inline))
  riscv_device_read_mtimecmp (void)
  {
    // On RV32 the compiler generates two word accesses.
    return *(uint64_t *) (RISCV_MMIO_MTIMECMP_ADDR);
  }

  static inline uint32_t
  __attribute__((always_inline))
  riscv_device_read_mtimecmp_low (void)
  {
    return *(uint32_t *) (RISCV_MMIO_MTIMECMP_ADDR);
  }

  static inline uint32_t
  __attribute__((always_inline))
  riscv_device_read_mtimecmp_high (void)
  {
    return *(uint32_t *) (RISCV_MMIO_MTIMECMP_ADDR + 4);
  }

#if __riscv_xlen == 64

  static inline void
  __attribute__((always_inline))
  riscv_device_write_mtimecmp (uint64_t value)
    {
      *(uint64_t *) (RISCV_MMIO_MTIMECMP_ADDR) = value;
    }

#endif /* __riscv_xlen == 64 */

  static inline void
  __attribute__((always_inline))
  riscv_device_write_mtimecmp_low (uint32_t value)
  {
    *(uint32_t *) (RISCV_MMIO_MTIMECMP_ADDR) = value;
  }

  static inline void
  __attribute__((always_inline))
  riscv_device_write_mtimecmp_high (uint32_t value)
  {
    *(uint32_t *) (RISCV_MMIO_MTIMECMP_ADDR + 4) = value;
  }

// --------------------------------------------------------------------------

#if defined(__cplusplus)
}
#endif /* defined(__cplusplus) */

// ============================================================================

#if defined(__cplusplus)

namespace riscv
{
  namespace device
  {
    // ------------------------------------------------------------------------

#if __riscv_xlen == 64

    inline uint64_t
    __attribute__((always_inline))
    mtime (void)
      {
        return riscv_device_read_mtime ();
      }

#endif /* __riscv_xlen == 64 */

    inline uint32_t
    __attribute__((always_inline))
    mtime_low (void)
    {
      return riscv_device_read_mtime_low ();
    }

    inline uint32_t
    __attribute__((always_inline))
    mtime_high (void)
    {
      return riscv_device_read_mtime_high ();
    }

#if __riscv_xlen == 64

    inline void
    __attribute__((always_inline))
    mtime (uint64_t value)
      {
        riscv_device_write_mtime (value);
      }

#endif /* __riscv_xlen == 64 */

    inline void
    __attribute__((always_inline))
    mtime_low (uint32_t value)
    {
      riscv_device_write_mtime_low (value);
    }

    inline void
    __attribute__((always_inline))
    mtime_high (uint32_t value)
    {
      riscv_device_write_mtime_high (value);
    }

    // ------------------------------------------------------------------------

    inline uint64_t
    __attribute__((always_inline))
    mtimecmp (void)
    {
      return riscv_device_read_mtimecmp ();
    }

    inline uint32_t
    __attribute__((always_inline))
    mtimecmp_low (void)
    {
      return riscv_device_read_mtimecmp_low ();
    }

    inline uint32_t
    __attribute__((always_inline))
    mtimecmp_high (void)
    {
      return riscv_device_read_mtimecmp_high ();
    }

#if __riscv_xlen == 64

    inline void
    __attribute__((always_inline))
    mtimecmp (uint64_t value)
      {
        riscv_device_write_mtimecmp (value);
      }

#endif /* __riscv_xlen == 64 */

    inline void
    __attribute__((always_inline))
    mtimecmp_low (uint32_t value)
    {
      riscv_device_write_mtimecmp_low (value);
    }

    inline void
    __attribute__((always_inline))
    mtimecmp_high (uint32_t value)
    {
      riscv_device_write_mtimecmp_high (value);
    }

  // --------------------------------------------------------------------------
  } /* namespace device */

// ----------------------------------------------------------------------------
} /* namespace riscv */

#endif /* defined(__cplusplus) */

// ----------------------------------------------------------------------------

#endif /* RISCV_DEVICE_FUNCTIONS_INLINES_H_ */
