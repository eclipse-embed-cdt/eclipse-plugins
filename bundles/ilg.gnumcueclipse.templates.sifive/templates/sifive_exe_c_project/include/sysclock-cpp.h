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

// ----------------------------------------------------------------------------

class sysclock;
extern sysclock sysclock;

// ----------------------------------------------------------------------------

/*
 * These classes are super simple versions of the µOS++ clocks.
 */

class clock
{
public:

  using duration_t = uint32_t;
  using timestamp_t = uint64_t;
};

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wpadded"

class sysclock : public clock
{
public:
  static constexpr uint32_t frequency_hz = 1000;

  sysclock (void);

  timestamp_t
  steady_now (void);

  /**
   * @param duration ticks based on frequency_hz.
   */
  void
  sleep_for (duration_t duration);

  void
  internal_increment_count ();

private:
  timestamp_t volatile steady_count_ = 0;
};

#pragma GCC diagnostic pop

inline void
__attribute__((always_inline))
sysclock::internal_increment_count ()
{
  ++steady_count_;
}

inline clock::timestamp_t
__attribute__((always_inline))
sysclock::steady_now (void)
{
  return steady_count_;
}

// ----------------------------------------------------------------------------

#endif /* SYSCLOCK_H_ */
