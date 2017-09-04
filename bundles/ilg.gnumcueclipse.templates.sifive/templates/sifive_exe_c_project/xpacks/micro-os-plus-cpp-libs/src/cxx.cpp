/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2015 Liviu Ionescu.
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

#include <cstdlib>
#include <sys/types.h>
#include <micro-os-plus/diag/trace.h>

// ----------------------------------------------------------------------------

// These functions are redefined locally, to avoid references to some
// heavy implementations in the standard C++ library.

namespace __gnu_cxx
{
  [[noreturn]] void
  __verbose_terminate_handler ();

  void
  __verbose_terminate_handler ()
  {
    os::trace::puts (__func__);
    abort ();
    /* NOTREACHED */
  }
}

// ----------------------------------------------------------------------------

extern "C"
{
  [[noreturn]] void
  __cxa_pure_virtual ();

  [[noreturn]] void
  __cxa_deleted_virtual ();

  void
  __cxa_pure_virtual ()
  {
    // Attempt to use a virtual function before object has been constructed
    os::trace::puts (__func__);
    abort ();
    /* NOTREACHED */
  }

  void
  __cxa_deleted_virtual ()
  {
    os::trace::puts (__func__);
    abort ();
    /* NOTREACHED */
  }
}

// ----------------------------------------------------------------------------

void *__dso_handle __attribute__ ((weak));

// ----------------------------------------------------------------------------
