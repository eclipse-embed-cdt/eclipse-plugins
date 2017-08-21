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
#include <micro-os-plus/startup/initialize-hooks.h>

// ----------------------------------------------------------------------------

// Called early, before copying .data and clearing .bss.
// Should initialise the clocks and possible other RAM areas.
void
os_startup_initialize_hardware_early (void)
{
  // Disable all interrupts.
  riscv_csr_clear_mstatus (MSTATUS_MIE);
  // Disable all individual interrupts.
  riscv_csr_write_mie (0);

  // Set the trap assembly handler.
  riscv_csr_write_mtvec ((riscv_arch_register_t) riscv_trap_entry);

  // TODO: add support for the PRCI peripheral and use it.

  // Make sure the HFROSC is on before the next line:
  PRCI_REG(PRCI_HFROSCCFG) |= ROSC_EN(1);
  // Run off 16 MHz Crystal for accuracy.
  PRCI_REG(PRCI_PLLCFG) = (PLL_REFSEL(1) | PLL_BYPASS(1));
  PRCI_REG(PRCI_PLLCFG) |= (PLL_SEL(1));
  // Turn off HFROSC to save power
  PRCI_REG(PRCI_HFROSCCFG) &= ~((uint32_t)ROSC_EN(1));

  // TODO: check Arduino main.cpp for more/better initialisations.
}

// Called before running the static constructors.
void
os_startup_initialize_hardware (void)
{
  // Measure the CPU frequency in cycles, with the RTC as reference.
  riscv_core_update_cpu_frequency ();

  // Disable M timer interrupt.
  riscv_csr_clear_mie (MIP_MTIP);

  // Clear both mtime and mtimecmp to start afresh.
  // Should trigger an interrupt as soon as enabled.
  riscv_device_write_mtime (0);
  riscv_device_write_mtimecmp (0);

  // Enable M timer interrupt.
  riscv_csr_set_mie (MIP_MTIP);

  // Enable interrupts.
  riscv_csr_set_mstatus (MSTATUS_MIE);
}

// ----------------------------------------------------------------------------
