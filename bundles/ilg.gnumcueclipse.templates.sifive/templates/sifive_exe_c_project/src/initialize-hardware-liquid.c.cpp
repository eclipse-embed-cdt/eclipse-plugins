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

{% if content == 'blinky' %}
extern plic_instance_t g_plic;

{% endif %}
// Called early, before copying .data and clearing .bss.
// Should initialise the clocks and possible other RAM areas.
void
os_startup_initialize_hardware_early (void)
{
  // Disable all interrupts.
{% if language == 'cpp' %}
  riscv::csr::clear_mstatus (MSTATUS_MIE);
{% elsif language == 'c' %}
  riscv_csr_clear_mstatus (MSTATUS_MIE);
{% endif %}
  // Disable all individual interrupts.
{% if language == 'cpp' %}
  riscv::csr::mie (0);
{% elsif language == 'c' %}
  riscv_csr_write_mie (0);
{% endif %}

  // Set the trap assembly handler.
{% if language == 'cpp' %}
  riscv::csr::mtvec ((riscv::arch::register_t) riscv_trap_entry);
{% elsif language == 'c' %}
  riscv_csr_write_mtvec ((riscv_arch_register_t) riscv_trap_entry);
{% endif %}

  // TODO: add support for the PRCI peripheral and use it.

{% if deviceName == 'fe310' %}
  // TODO: add to C/C++ API
  // Make sure the HFROSC is on before the next line:
  PRCI_REG(PRCI_HFROSCCFG) |= ROSC_EN(1);
  // Run off 16 MHz Crystal for accuracy.
  PRCI_REG(PRCI_PLLCFG) = (PLL_REFSEL(1) | PLL_BYPASS(1));
  PRCI_REG(PRCI_PLLCFG) |= (PLL_SEL(1));
  // Turn off HFROSC to save power
  PRCI_REG(PRCI_HFROSCCFG) &= ~((uint32_t)ROSC_EN(1));

{% endif %}
{% if boardName == 'e31arty' or  boardName == 'e51arty' %}
  // For the Arty board, be sure LED1 is off, since it is very bright.
  PWM0_REG(PWM_CMP1) = 0xFF;
  PWM0_REG(PWM_CMP2) = 0xFF;
  PWM0_REG(PWM_CMP3) = 0xFF;

{% endif %}
  // TODO: check Arduino main.cpp for more/better initialisations.
}

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wsign-conversion"

// Called before running the static constructors.
void
os_startup_initialize_hardware (void)
{
  // Measure the CPU frequency in cycles, with the RTC as reference.
{% if language == 'cpp' %}
  riscv::core::update_running_frequency ();
{% elsif language == 'c' %}
  riscv_core_update_running_frequency ();
{% endif %}

{% if content == 'blinky' %}
  // TODO: make PLIC static inline.
  PLIC_init (&g_plic, PLIC_CTRL_ADDR, PLIC_NUM_INTERRUPTS, PLIC_NUM_PRIORITIES);

{% endif %}
  // Disable M timer interrupt.
{% if language == 'cpp' %}
  riscv::csr::clear_mie (MIP_MTIP);
{% elsif language == 'c' %}
  riscv_csr_clear_mie (MIP_MTIP);
{% endif %}

  // Clear both mtime and mtimecmp to start afresh.
  // Should trigger an interrupt as soon as enabled.
{% if language == 'cpp' %}
  riscv::device::mtime (0);
  riscv::device::mtimecmp (0);
{% elsif language == 'c' %}
  riscv_device_write_mtime (0);
  riscv_device_write_mtimecmp (0);
{% endif %}

{% if content == 'blinky' %}
  // -------------------------------------------------------------------
  // Configure Button 0 as a global GPIO irq.

  // Disable output.
  GPIO_REG(GPIO_OUTPUT_EN) &= ~((0x1 << BUTTON_0_OFFSET));

  // Disable hw io function
  GPIO_REG(GPIO_IOF_EN) &= ~(1 << BUTTON_0_OFFSET);

  // Configure as input
  GPIO_REG(GPIO_INPUT_EN) |= (1 << BUTTON_0_OFFSET);
  GPIO_REG(GPIO_PULLUP_EN) |= (1 << BUTTON_0_OFFSET);

  // Configure to interrupt on both falling and rising edges.
  GPIO_REG(GPIO_FALL_IE) |= (1 << BUTTON_0_OFFSET);
  GPIO_REG(GPIO_RISE_IE) |= (1 << BUTTON_0_OFFSET);

  // Enable the BUTTON interrupt in PLIC.
  PLIC_enable_interrupt (&g_plic, INT_DEVICE_BUTTON_0);

  // Configure the BUTTON priority in PLIC. 2 out of 7.
  PLIC_set_priority (&g_plic, INT_DEVICE_BUTTON_0, 2);

  // Enable Global (PLIC) interrupt.
{% if language == 'cpp' %}
  riscv::csr::set_mie (MIP_MEIP);
{% elsif language == 'c' %}
  riscv_csr_set_mie (MIP_MEIP);
{% endif %}

{% endif %}
  // -------------------------------------------------------------------
  // When everything else is ready...

  // Enable M timer interrupt.
{% if language == 'cpp' %}
  riscv::csr::set_mie (MIP_MTIP);
{% elsif language == 'c' %}
  riscv_csr_set_mie (MIP_MTIP);
{% endif %}

  // Enable interrupts.
{% if language == 'cpp' %}
  riscv::csr::set_mstatus (MSTATUS_MIE);
{% elsif language == 'c' %}
  riscv_csr_set_mstatus (MSTATUS_MIE);
{% endif %}
}

#pragma GCC diagnostic pop

// ----------------------------------------------------------------------------
