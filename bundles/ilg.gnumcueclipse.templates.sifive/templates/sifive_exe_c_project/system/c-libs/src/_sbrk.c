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

#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <errno.h>
#include <stddef.h>

// ----------------------------------------------------------------------------

void*
_sbrk (ptrdiff_t incr);

// ----------------------------------------------------------------------------

// The definitions used here should be kept in sync with the
// stack definitions in the linker script.

void*
_sbrk (ptrdiff_t incr)
{
  extern char __heap_begin__; // Defined by the linker.
  extern char __heap_end__; // Defined by the linker.

  static char* current_heap_end; // STATIC! Zero after BSS init.
  char* current_block_address;

  if (current_heap_end == 0)
    {
      current_heap_end = &__heap_begin__;
    }

  current_block_address = current_heap_end;

  // Need to align heap to word boundary, for efficiency reasons and
  // to possibly avoid hardware faults.
  // So we assume that the heap starts on word boundary,
  // hence make sure we always add a multiple of 4 to it.
  incr = (incr + 3) & (~3); // align value to 4
  if (current_heap_end + incr > &__heap_end__)
    {
      // Some of the libstdc++-v3 tests rely upon detecting 'out of memory'
      // errors, so DO NOT abort here, but return error.

      errno = ENOMEM; // Heap has overflowed.
      return (caddr_t) -1;
    }

  current_heap_end += incr;

  return (caddr_t) current_block_address;
}

// Guarantee that all standard and reentrant functions get here directly.

void*
__attribute__((weak, alias ("_sbrk")))
sbrk (ptrdiff_t incr);

void*
_sbrk_r (struct _reent* impure, ptrdiff_t incr);

void*
_sbrk_r (struct _reent* impure __attribute__((unused)), ptrdiff_t incr)
{
  return _sbrk (incr);
}

// ----------------------------------------------------------------------------
