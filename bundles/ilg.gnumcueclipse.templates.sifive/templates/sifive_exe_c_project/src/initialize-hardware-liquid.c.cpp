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
// Should initialize the clocks and possible other RAM areas.
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
  riscv_device_prci_ptr->hfrosccfg |= ROSC_EN(1);
  // Run off 16 MHz Crystal for accuracy.
  riscv_device_prci_ptr->pllcfg = (PLL_REFSEL(1) | PLL_BYPASS(1));
  riscv_device_prci_ptr->pllcfg |= (PLL_SEL(1));
  // Turn off HFROSC to save power
  riscv_device_prci_ptr->hfrosccfg &= ~((uint32_t)ROSC_EN(1));

{% endif %}
{% if boardName == 'e31arty' or  boardName == 'e51arty' %}
  // For the Arty board, be sure LED1 is off, since it is very bright.
  riscv_device_pwm0_ptr->pwmcmp1 = 0xFF;
  riscv_device_pwm0_ptr->pwmcmp2 = 0xFF;
  riscv_device_pwm0_ptr->pwmcmp3 = 0xFF;

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
{% if language == 'cpp' %}
  riscv::plic::clear_priorities ();
{% elsif language == 'c' %}
  riscv_plic_clear_priorities ();
{% endif %}
  // Hart specific initializations.
{% if language == 'cpp' %}
  riscv::plic::initialize ();
{% elsif language == 'c' %}
  riscv_plic_initialize ();
{% endif %}

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
  riscv_device_gpio_ptr->output_en &= ~(1u << BUTTON_0_OFFSET);

  // Disable hw io function
  riscv_device_gpio_ptr->iof_en &= ~(1u << BUTTON_0_OFFSET);

  // Configure as input
  riscv_device_gpio_ptr->input_en |= (1u << BUTTON_0_OFFSET);
  riscv_device_gpio_ptr->pue |= (1u << BUTTON_0_OFFSET);

  // Configure to interrupt on both falling and rising edges.
  riscv_device_gpio_ptr->fall_ie |= (1u << BUTTON_0_OFFSET);
  riscv_device_gpio_ptr->rise_ie |= (1u << BUTTON_0_OFFSET);

  // Enable the BUTTON interrupt in PLIC.
{% if language == 'cpp' %}
  riscv::plic::enable_interrupt (INT_DEVICE_BUTTON_0);
{% elsif language == 'c' %}
  riscv_plic_enable_interrupt (INT_DEVICE_BUTTON_0);
{% endif %}

  // Configure the BUTTON priority in PLIC. 2 out of 7.
{% if language == 'cpp' %}
  riscv::plic::priority (INT_DEVICE_BUTTON_0, 2);
{% elsif language == 'c' %}
  riscv_plic_write_priority (INT_DEVICE_BUTTON_0, 2);
{% endif %}

  // Enable Global (PLIC) interrupt.
{% if language == 'cpp' %}
  riscv::core::enable_machine_external_interrupts ();
{% elsif language == 'c' %}
  riscv_core_enable_machine_external_interrupts ();
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
