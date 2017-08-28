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

#if defined(TRACE)

#include <micro-os-plus/diag/trace.h>

#include <cstdarg>
#include <cstdio>
#include <cstring>

#ifndef OS_INTEGER_TRACE_PRINTF_TMP_ARRAY_SIZE
#define OS_INTEGER_TRACE_PRINTF_TMP_ARRAY_SIZE (200)
#endif

// ----------------------------------------------------------------------------

namespace os
{
  namespace trace
  {
    // ------------------------------------------------------------------------

    // Weak empty defaults, in case no output is defined.
    void __attribute__((weak))
    initialize (void)
    {
      ;
    }

    /**
     * @brief Write the given number of bytes to the trace output channel.
     * @return  The number of characters actually written, or -1 if error.
     */
    ssize_t __attribute__((weak))
    write (const void* buf __attribute__((unused)), std::size_t nbyte)
    {
      return static_cast<ssize_t> (nbyte);
    }

    void __attribute__((weak))
    flush (void)
    {
      ;
    }

    // ------------------------------------------------------------------------

    int __attribute__((weak))
    printf (const char* format, ...)
    {
      std::va_list args;
      va_start(args, format);

      int ret = vprintf (format, args);

      va_end(args);
      return ret;
    }

    int __attribute__((weak))
    vprintf (const char* format, std::va_list args)
    {
      // Caution: allocated on the stack!
      char buf[OS_INTEGER_TRACE_PRINTF_TMP_ARRAY_SIZE];

      // TODO: possibly rewrite it to no longer use newlib,
      // (although the nano version is no longer very heavy).

      // Print to the local buffer
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wformat-nonliteral"
      int ret = ::vsnprintf (buf, sizeof(buf), format, args);
#pragma GCC diagnostic pop
      if (ret > 0)
	{
	  // Transfer the buffer to the device.
	  ret = static_cast<int> (write (buf, static_cast<size_t> (ret)));
	}
      return ret;
    }

    int __attribute__((weak))
    puts (const char* s)
    {
      int ret = static_cast<int> (write (s, strlen (s)));
      if (ret >= 0)
	{
	  ret = static_cast<int> (write ("\n", 1)); // Add a line terminator
	}
      if (ret > 0)
	{
	  return ret;
	}
      else
	{
	  return EOF;
	}
    }

    int __attribute__((weak))
    putchar (int c)
    {
      int ret = static_cast<int> (write (reinterpret_cast<const char*> (&c), 1));
      if (ret > 0)
	{
	  return c;
	}
      else
	{
	  return EOF;
	}
    }

    void __attribute__((weak))
    dump_args (int argc, char* argv[])
    {
      printf ("main(argc=%d, argv=[", argc);
      for (int i = 0; i < argc; ++i)
	{
	  if (i != 0)
	    {
	      printf (", ");
	    }
	  printf ("\"%s\"", argv[i]);
	}
      printf ("]);\n");
    }

    // --------------------------------------------------------------------------
  } /* namespace trace */
} /* namespace os */

// ----------------------------------------------------------------------------

using namespace os;

// These cannot be aliased, since they usually are defined
// in a different translation unit.

void __attribute__((weak))
trace_initialize (void)
{
  trace::initialize ();
}

ssize_t __attribute__((weak))
trace_write (const void* buf, std::size_t nbyte)
{
  return trace::write (buf, nbyte);
}

void __attribute__((weak))
trace_flush (void)
{
  return trace::flush ();
}

// ----------------------------------------------------------------------------

#if defined(__ARM_EABI__) || defined(__riscv)

// For embedded platforms, optimise with aliases.
//
// Aliases can only refer symbols defined in the same translation unit
// and C++ de-mangling must be done manually.

int __attribute__((weak, alias ("_ZN2os5trace6printfEPKcz")))
trace_printf (const char* format, ...);

#if defined(__ARM_EABI__)
int __attribute__((weak, alias ("_ZN2os5trace7vprintfEPKcSt9__va_list")))
#elif defined(__riscv)
int __attribute__((weak, alias ("_ZN2os5trace7vprintfEPKcPv")))
#endif /* defined(__ARM_EABI__) */
trace_vprintf (const char* format, ...);

int __attribute__((weak, alias("_ZN2os5trace4putsEPKc")))
trace_puts (const char *s);

int __attribute__((weak, alias("_ZN2os5trace7putcharEi")))
trace_putchar (int c);

void __attribute__((weak, alias("_ZN2os5trace9dump_argsEiPPc")))
trace_dump_args (int argc, char* argv[]);

#else

// For non-embedded platforms, to remain compatible with OS X which does
// not support aliases, redefine the C functions to call the C++ versions.

int
trace_printf (const char* format, ...)
  {
    std::va_list args;
    va_start(args, format);

    int ret = trace::vprintf (format, args);

    va_end(args);
    return ret;
  }

int
trace_vprintf (const char* format, va_list args)
  {
    return trace::vprintf (format, args);
  }

int
trace_puts (const char* s)
  {
    return trace::puts (s);
  }

int
trace_putchar (int c)
  {
    return trace::putchar (c);
  }

void
trace_dump_args (int argc, char* argv[])
  {
    trace::dump_args (argc, argv);
  }

#endif /* __ARM_EABI__ __riscv */

// ----------------------------------------------------------------------------

#endif /* defined(TRACE) */
