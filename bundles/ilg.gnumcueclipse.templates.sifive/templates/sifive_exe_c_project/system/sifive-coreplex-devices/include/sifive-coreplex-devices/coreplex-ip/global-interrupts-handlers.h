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

#ifndef SIFIVE_COREPLEX_DEVICES_COREPLEX_IP_GLOBAL_INTERRUPTS_HANDLERS_H_
#define SIFIVE_COREPLEX_DEVICES_COREPLEX_IP_GLOBAL_INTERRUPTS_HANDLERS_H_

/*
 * SiFive Coreplex IP E31/E51 global interrupts handler functions.
 *
 * TODO: split into
 */

// ----------------------------------------------------------------------------
#if defined(__cplusplus)
extern "C"
{
#endif /* defined(__cplusplus) */

  // --------------------------------------------------------------------------

  void
  riscv_irq_global_handle_uart0 (void);

  void
  riscv_irq_global_handle_external (void);

  void
  riscv_irq_global_handle_spi0 (void);

  void
  riscv_irq_global_handle_gpio0 (void);

  void
  riscv_irq_global_handle_gpio1 (void);

  void
  riscv_irq_global_handle_gpio2 (void);

  void
  riscv_irq_global_handle_gpio3 (void);

  void
  riscv_irq_global_handle_gpio4 (void);

  void
  riscv_irq_global_handle_gpio5 (void);

  void
  riscv_irq_global_handle_gpio6 (void);

  void
  riscv_irq_global_handle_gpio7 (void);

  void
  riscv_irq_global_handle_gpio8 (void);

  void
  riscv_irq_global_handle_gpio9 (void);

  void
  riscv_irq_global_handle_gpio10 (void);

  void
  riscv_irq_global_handle_gpio11 (void);

  void
  riscv_irq_global_handle_gpio12 (void);

  void
  riscv_irq_global_handle_gpio13 (void);

  void
  riscv_irq_global_handle_gpio14 (void);

  void
  riscv_irq_global_handle_gpio15 (void);

  void
  riscv_irq_global_handle_pwm0cmp0 (void);

  void
  riscv_irq_global_handle_pwm0cmp1 (void);

  void
  riscv_irq_global_handle_pwm0cmp2 (void);

  void
  riscv_irq_global_handle_pwm0cmp3 (void);

// ----------------------------------------------------------------------------

#if defined(__cplusplus)
}
#endif /* defined(__cplusplus) */

// ----------------------------------------------------------------------------

#endif /* SIFIVE_COREPLEX_DEVICES_COREPLEX_IP_GLOBAL_INTERRUPTS_HANDLERS_H_ */
