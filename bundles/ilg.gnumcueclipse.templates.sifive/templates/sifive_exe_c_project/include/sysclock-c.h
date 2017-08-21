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

#ifndef SYSCLOCK_H_
#define SYSCLOCK_H_

#include <stdint.h>

#if defined(__cplusplus)
extern "C"
{
#endif /* __cplusplus */

// ----------------------------------------------------------------------------

#define OS_INTEGER_SYSCLOCK_FREQUENCY_HZ  (1000)

  typedef uint64_t os_clock_timestamp_t;
  typedef uint32_t os_clock_duration_t;
  typedef uint32_t os_result_t;

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wpadded"

  typedef struct os_clock_s
  {
    os_clock_timestamp_t volatile steady_count_;
  } os_clock_t;

#pragma GCC diagnostic pop

  extern os_clock_t sysclock;

  /*
   * This code is a super simple version of the µOS++ clocks,
   * re-implemented in C.
   */

  void
  os_sysclock_construct (void);

  void
  os_sysclock_sleep_for (os_clock_duration_t duration);

  static inline void
  __attribute__((always_inline))
  os_sysclock_internal_increment_count (void)
  {
    ++sysclock.steady_count_;
  }

  static inline os_clock_timestamp_t
  __attribute__((always_inline))
  os_sysclock_steady_now (void)
  {
    return sysclock.steady_count_;
  }

  static inline os_clock_duration_t
  __attribute__((always_inline))
  os_sysclock_get_frequency_hz (void)
  {
    return OS_INTEGER_SYSCLOCK_FREQUENCY_HZ;
  }

// ----------------------------------------------------------------------------

#if defined(__cplusplus)
}
#endif /* __cplusplus */

#endif /* SYSCLOCK_H_ */
