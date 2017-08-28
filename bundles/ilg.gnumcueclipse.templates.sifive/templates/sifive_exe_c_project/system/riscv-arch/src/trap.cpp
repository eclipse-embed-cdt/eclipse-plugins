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

#include <stddef.h>
#include <stdbool.h>

// ----------------------------------------------------------------------------

extern "C"
{
  // Forward declarations.
  void
  riscv_core_handle_trap (riscv::arch::register_t mcause,
			  riscv::arch::register_t epc,
			  riscv::arch::register_t sp);
  void
  riscv_trap_handle_unused (void);

  typedef void
  (*trap_handler_ptr_t) (void);
}

// ----------------------------------------------------------------------------

// To provide the desired functionality, redefine any of these
// functions in the application.

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_misaligned_fetch (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_fault_fetch (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_illegal_instruction (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_breakpoint (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_misaligned_load (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_fault_load (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_misaligned_store (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_fault_store (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_user_ecall (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_supervisor_ecall (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_machine_ecall (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_page_fetch (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_page_load (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_exc_handle_page_store (void);

/*
 * Array of pointers to exception handlers. See Table 3.6 from Volume II.
 */
trap_handler_ptr_t __attribute__ ((section(".trap_array")))
riscv_exc_handlers[] =
  { //
    riscv_exc_handle_misaligned_fetch, /* 0 */
    riscv_exc_handle_fault_fetch, /* 1 */
    riscv_exc_handle_illegal_instruction, /* 2 */
    riscv_exc_handle_breakpoint, /* 3 */
    riscv_exc_handle_misaligned_load, /* 4 */
    riscv_exc_handle_fault_load, /* 5 */
    riscv_exc_handle_misaligned_store, /* 6 */
    riscv_exc_handle_fault_store, /* 7 */
    riscv_exc_handle_user_ecall, /* 8 */
    riscv_exc_handle_supervisor_ecall, /* 9 */
    riscv_trap_handle_unused, /* 10 */
    riscv_exc_handle_machine_ecall, /* 11 */
    riscv_exc_handle_page_fetch, /* 12 */
    riscv_exc_handle_page_load, /* 13 */
    riscv_trap_handle_unused, /* 14 */
    riscv_exc_handle_page_store /* 15 */
    };

static_assert(
    sizeof(riscv_exc_handlers)/sizeof(riscv_exc_handlers[0]) == EXC_ARRAY_SIZE,
    "riscv_exc_handlers[] size must match EXC_ARRAY_SIZE");

// ----------------------------------------------------------------------------

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_user_software (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_supervisor_software (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_machine_software (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_user_timer (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_supervisor_timer (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_machine_timer (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_user_ext (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_supervisor_ext (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_machine_ext (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_cop (void);

void __attribute__ ((weak, alias ("riscv_trap_handle_unused")))
riscv_irq_handle_host (void);

/*
 * Array of pointers to interrupt handlers. See Table 3.6 from Volume II.
 */
trap_handler_ptr_t __attribute__ ((section(".trap_array")))
riscv_irq_handlers[] =
  { //
    riscv_irq_handle_user_software, /* 0 */
    riscv_irq_handle_supervisor_software, /* 1 */
    riscv_trap_handle_unused, /* 2 */
    riscv_irq_handle_machine_software, /* 3 */
    riscv_irq_handle_user_timer, /* 4 */
    riscv_irq_handle_supervisor_timer, /* 5 */
    riscv_trap_handle_unused, /* 6 */
    riscv_irq_handle_machine_timer, /* 7 */
    riscv_irq_handle_user_ext, /* 8 */
    riscv_irq_handle_supervisor_ext, /* 9 */
    riscv_trap_handle_unused, /* 10 */
    riscv_irq_handle_machine_ext, /* 11 */
    /* Warning, not in the specs. */
    /* TODO: perhaps a more elaborate solution is needed for custom IRQs. */
    riscv_irq_handle_cop, /* 12 */
    riscv_irq_handle_host /* 13 */
    };

static_assert(
    sizeof(riscv_irq_handlers)/sizeof(riscv_irq_handlers[0]) == IRQ_ARRAY_SIZE,
    "riscv_irq_handlers[] size must match IRQ_ARRAY_SIZE");

// ----------------------------------------------------------------------------

extern "C" void
__attribute__ ((section(".trap_handlers")))
riscv_core_handle_trap (riscv::arch::register_t mcause,
			riscv::arch::register_t epc __attribute__((unused)),
			riscv::arch::register_t sp __attribute__((unused)))
{
  if ((mcause & MCAUSE_INT) != 0)
    {
      size_t index = (mcause & MCAUSE_CAUSE);
      if (index < IRQ_ARRAY_SIZE)
	{
	  // Call the interrupt handler via the pointer.
	  riscv_irq_handlers[index] ();
	  return;
	}
    }
  else
    {
      size_t index = mcause;
      if (index < EXC_ARRAY_SIZE)
	{
	  // Call the exception handler via the pointer.
	  riscv_exc_handlers[index] ();
	  return;
	}
    }

#if defined(DEBUG)
  riscv::arch::ebreak ();
#endif /* defined(DEBUG) */

  while (true)
    {
      riscv::arch::nop ();
    }
}

void
__attribute__ ((section(".trap_handlers"),weak))
riscv_trap_handle_unused (void)
{
#if defined(DEBUG)
  riscv::arch::ebreak ();
#endif /* defined(DEBUG) */

  while (true)
    {
      riscv::arch::nop ();
    }
}

// ----------------------------------------------------------------------------
