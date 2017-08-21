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

#ifndef RISCV_ARCH_TYPES_H_
#define RISCV_ARCH_TYPES_H_

#include <stdint.h>

#if defined(__cplusplus)
extern "C"
{
#endif /* defined(__cplusplus) */

#if __riscv_xlen == 32
typedef uint32_t riscv_arch_register_t;
#elif __riscv_xlen == 64
typedef uint64_t riscv_arch_register_t;
#endif

// ----------------------------------------------------------------------------

#if defined(__cplusplus)
}
#endif /* defined(__cplusplus) */

// ============================================================================

#if defined(__cplusplus)

namespace riscv
{
namespace arch
{
// ----------------------------------------------------------------------------

using register_t = riscv_arch_register_t;

 // ---------------------------------------------------------------------------
} /* namespace arch */
// ----------------------------------------------------------------------------
} /* namespace riscv */

#endif /* defined(__cplusplus) */

// ----------------------------------------------------------------------------

#endif /* RISCV_ARCH_TYPES_H_ */
