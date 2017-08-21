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

#ifndef RISCV_CSR_FUNCTIONS_H_
#define RISCV_CSR_FUNCTIONS_H_

#include <riscv/arch-types.h>

/*
 * RISC-V CSR support functions.
 */

#if defined(__cplusplus)
extern "C"
{
#endif /* defined(__cplusplus) */

  // --------------------------------------------------------------------------

#if 0
  // Not available, due to ISA limitations.
  // The workaround is to use distinct functions
  // for each CSR. There are only 4096 of them.

  riscv_arch_register_t
  riscv_csr_read (uint32_t reg);

  void
  riscv_csr_write (uint32_t reg, riscv_arch_register_t value);
#endif

  // --------------------------------------------------------------------------
  // `mstatus`

  /**
   * Read `mstatus` CSR.
   */
  static riscv_arch_register_t
  riscv_csr_read_mstatus (void);

  /**
   * Write `mstatus` CSR.
   */
  static void
  riscv_csr_write_mstatus (riscv_arch_register_t value);

  /**
   * Clear bits in `mstatus` CSR.
   */
  static riscv_arch_register_t
  riscv_csr_clear_mstatus (riscv_arch_register_t mask);

  /**
   * Set bits in `mstatus` CSR.
   */
  static riscv_arch_register_t
  riscv_csr_set_mstatus (riscv_arch_register_t mask);

  // --------------------------------------------------------------------------
  // `mtvec`

  /**
   * Read `mtvec` CSR.
   */
  static riscv_arch_register_t
  riscv_csr_read_mtvec (void);

  /**
   * Write `mtvec` CSR.
   */
  static void
  riscv_csr_write_mtvec (riscv_arch_register_t value);

  // --------------------------------------------------------------------------
  // `mie`

  /**
   * Read `mie` CSR.
   */
  static riscv_arch_register_t
  riscv_csr_read_mie (void);

  /**
   * Write `mie` CSR.
   */
  static void
  riscv_csr_write_mie (riscv_arch_register_t value);

  /**
   * Clear bits in `mie` CSR.
   */
  static riscv_arch_register_t
  riscv_csr_clear_mie (riscv_arch_register_t mask);

  /**
   * Set bits in `mie` CSR.
   */
  static riscv_arch_register_t
  riscv_csr_set_mie (riscv_arch_register_t mask);

  // --------------------------------------------------------------------------
  // `mcycle`

  /**
   * Read `mcycle` CSR.
   */
#if __riscv_xlen == 64
  static
#endif /* __riscv_xlen == 64 */
  uint64_t
  riscv_csr_read_mcycle (void);

  static uint32_t
  riscv_csr_read_mcycle_low (void);

  static uint32_t
  riscv_csr_read_mcycle_high (void);

// ----------------------------------------------------------------------------

#if defined(__cplusplus)
}
#endif /* defined(__cplusplus) */

// ============================================================================

#if defined(__cplusplus)

namespace riscv
{
  namespace csr
  {
    // ------------------------------------------------------------------------
    // `mstatus`

    arch::register_t
    mstatus (void);

    void
    mstatus (arch::register_t value);

    void
    clear_mstatus (arch::register_t mask);

    void
    set_mstatus (arch::register_t mask);

    // ------------------------------------------------------------------------
    // `mtvec`

    arch::register_t
    mtvec (void);

    void
    mtvec (arch::register_t value);

    // ------------------------------------------------------------------------
    // `mie`

    arch::register_t
    mie (void);

    void
    mie (arch::register_t value);

    void
    clear_mie (arch::register_t mask);

    void
    set_mie (arch::register_t mask);

    // ------------------------------------------------------------------------
    // `mcycle`

    /**
     * Read the mcycle counter.
     */
    uint64_t
    mcycle (void);

    uint32_t
    mcycle_low (void);

    uint32_t
    mcycle_high (void);

  // --------------------------------------------------------------------------
  } /* namespace csr */

// ----------------------------------------------------------------------------
} /* namespace riscv */

#endif /* defined(__cplusplus) */

// ----------------------------------------------------------------------------

#endif /* RISCV_CSR_FUNCTIONS_H_ */
