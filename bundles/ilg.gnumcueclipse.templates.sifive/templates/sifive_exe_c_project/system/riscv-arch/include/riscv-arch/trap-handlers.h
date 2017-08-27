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

#ifndef RISCV_TRAP_HANDLERS_H_
#define RISCV_TRAP_HANDLERS_H_

/*
 * RISC-V core trap handler functions.
 */

// ----------------------------------------------------------------------------
#if defined(__cplusplus)
extern "C"
{
#endif /* defined(__cplusplus) */

  // --------------------------------------------------------------------------

  /**
   * Hardware trap entry point (assembly).
   */
  void
  riscv_trap_entry (void);

  // --------------------------------------------------------------------------
  // Exception handlers.

  void
  riscv_exc_handle_misaligned_fetch (void);

  void
  riscv_exc_handle_fault_fetch (void);

  void
  riscv_exc_handle_illegal_instruction (void);

  void
  riscv_exc_handle_breakpoint (void);

  void
  riscv_exc_handle_misaligned_load (void);

  void
  riscv_exc_handle_fault_load (void);

  void
  riscv_exc_handle_misaligned_store (void);

  void
  riscv_exc_handle_fault_store (void);

  void
  riscv_exc_handle_user_ecall (void);

  void
  riscv_exc_handle_supervisor_ecall (void);

  void
  riscv_exc_handle_machine_ecall (void);

  void
  riscv_exc_handle_page_fetch (void);

  void
  riscv_exc_handle_page_load (void);

  void
  riscv_exc_handle_page_store (void);

  // --------------------------------------------------------------------------
  // Interrupt handlers.

  void
  riscv_trap_entry (void);

  void
  riscv_irq_handle_user_software (void);

  void
  riscv_irq_handle_supervisor_software (void);

  void
  riscv_irq_handle_machine_software (void);

  void
  riscv_irq_handle_user_timer (void);

  void
  riscv_irq_handle_supervisor_timer (void);

  void
  riscv_irq_handle_machine_timer (void);

  void
  riscv_irq_handle_user_ext (void);

  void
  riscv_irq_handle_supervisor_ext (void);

  void
  riscv_irq_handle_machine_ext (void);

  void
  riscv_irq_handle_cop (void);

  void
  riscv_irq_handle_host (void);

// ----------------------------------------------------------------------------

#if defined(__cplusplus)
}
#endif /* defined(__cplusplus) */

// ----------------------------------------------------------------------------

#endif /* RISCV_TRAP_HANDLERS_H_ */
