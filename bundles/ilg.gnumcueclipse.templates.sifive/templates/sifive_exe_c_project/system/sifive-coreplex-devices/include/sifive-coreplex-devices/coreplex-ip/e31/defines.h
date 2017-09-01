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

#ifndef SIFIVE_COREPLEX_IP_E31_DEFINES_H_
#define SIFIVE_COREPLEX_IP_E31_DEFINES_H_

#include <stdint.h>

#include <sifive/const.h>

// ----------------------------------------------------------------------------
// Definitions from SiFive bsp/env/coreplexip-31-arty/platform.h

#ifdef VECT_IRQ
    #define MTVEC_VECTORED     0x01
#else
    #define MTVEC_VECTORED     0x00
#endif

#define IRQ_M_LOCAL        16
#define MIP_MLIP(x)        (1 << (IRQ_M_LOCAL + x))

#include <sifive/peripherals/aon.h>
#include <sifive/peripherals/clint.h>
#include <sifive/peripherals/gpio.h>
#include <sifive/peripherals/plic.h>
#include <sifive/peripherals/pwm.h>
#include <sifive/peripherals/spi.h>
#include <sifive/peripherals/uart.h>

// Memory map
#define CLINT_CTRL_ADDR _AC(0x02000000,UL)
#define GPIO_CTRL_ADDR _AC(0x20002000,UL)
#define PLIC_CTRL_ADDR _AC(0x0C000000,UL)
#define PWM0_CTRL_ADDR _AC(0x20005000,UL)
#define RAM_MEM_ADDR _AC(0x80000000,UL)
#define RAM_MEM_SIZE _AC(0x10000,UL)
#define SPI0_CTRL_ADDR _AC(0x20004000,UL)
#define SPI0_MEM_ADDR _AC(0x40000000,UL)
#define SPI0_MEM_SIZE _AC(0x20000000,UL)
#define TESTBENCH_MEM_ADDR _AC(0x20000000,UL)
#define TESTBENCH_MEM_SIZE _AC(0x10000000,UL)
#define TRAPVEC_TABLE_CTRL_ADDR _AC(0x00001010,UL)
#define UART0_CTRL_ADDR _AC(0x20000000,UL)

// IOF masks

// Interrupt numbers
#define RESERVED_INT_BASE 0
#define UART0_INT_BASE 1
#define EXTERNAL_INT_BASE 2
#define SPI0_INT_BASE 6
#define GPIO_INT_BASE 7
#define PWM0_INT_BASE 23

// Helper functions
#define _REG64(p, i) (*(volatile uint64_t *)((p) + (uint32_t)(i)))
#define _REG32(p, i) (*(volatile uint32_t *)((p) + (uint32_t)(i)))
#define _REG16(p, i) (*(volatile uint16_t *)((p) + (uint32_t)(i)))

// Bulk set bits in `reg` to either 0 or 1.
// E.g. SET_BITS(MY_REG, 0x00000007, 0) would generate MY_REG &= ~0x7
// E.g. SET_BITS(MY_REG, 0x00000007, 1) would generate MY_REG |= 0x7
#define SET_BITS(reg, mask, value) if ((value) == 0) { (reg) &= ~(mask); } else { (reg) |= (mask); }
#define CLINT_REG(offset) _REG32(CLINT_CTRL_ADDR, offset)
#define GPIO_REG(offset) _REG32(GPIO_CTRL_ADDR, offset)
#define PLIC_REG(offset) _REG32(PLIC_CTRL_ADDR, offset)
#define PWM0_REG(offset) _REG32(PWM0_CTRL_ADDR, offset)
#define SPI0_REG(offset) _REG32(SPI0_CTRL_ADDR, offset)
#define TRAPVEC_TABLE_REG(offset) _REG32(TRAPVEC_TABLE_CTRL_ADDR, offset)
#define UART0_REG(offset) _REG32(UART0_CTRL_ADDR, offset)
#define CLINT_REG64(offset) _REG64(CLINT_CTRL_ADDR, offset)
#define GPIO_REG64(offset) _REG64(GPIO_CTRL_ADDR, offset)
#define PLIC_REG64(offset) _REG64(PLIC_CTRL_ADDR, offset)
#define PWM0_REG64(offset) _REG64(PWM0_CTRL_ADDR, offset)
#define SPI0_REG64(offset) _REG64(SPI0_CTRL_ADDR, offset)
#define TRAPVEC_TABLE_REG64(offset) _REG64(TRAPVEC_TABLE_CTRL_ADDR, offset)
#define UART0_REG64(offset) _REG64(UART0_CTRL_ADDR, offset)

// Misc

#define NUM_GPIO 16

#define PLIC_NUM_INTERRUPTS 28
#define PLIC_NUM_PRIORITIES 7

// End of SiFive definitions.
// ----------------------------------------------------------------------------

#define IRQ_GLOBAL_ARRAY_SIZE (PLIC_NUM_INTERRUPTS - 1)

// ----------------------------------------------------------------------------

#endif /* SIFIVE_COREPLEX_IP_E31_DEFINES_H_ */
