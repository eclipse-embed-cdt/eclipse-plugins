//
// This file is part of the µOS++ III distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#ifndef DIAG_TRACE_H_
#define DIAG_TRACE_H_

#include <unistd.h>

#if defined(TRACE)

#if defined(__cplusplus)
extern "C"
{
#endif

  void
  trace_initialize(void);

  // Implementation dependent
  ssize_t
  trace_write(const char* buf, size_t nbyte);

  // ----- Portable -----

  int
  trace_printf(const char* format, ...);

  int
  trace_puts(const char *s);

  int
  trace_putchar(int c);

  void
  trace_dump_args(int argc, char* argv[]);

#if defined(__cplusplus)
}
#endif

#else // !defined(TRACE)

#if defined(__cplusplus)
extern "C"
{
#endif

  inline void
  trace_initialize(void);

  // Implementation dependent
  inline ssize_t
  trace_write(const char* buf, size_t nbyte);

  inline int
  trace_printf(const char* format, ...);

  inline int
  trace_puts(const char *s);

  inline int
  trace_putchar(int c);

  inline void
  trace_dump_args(int argc, char* argv[]);

#if defined(__cplusplus)
}
#endif

inline void
trace_initialize(void)
{
}

// Empty definitions when trace is not defined
inline ssize_t
trace_write(const char* buf __attribute__((unused)),
    size_t nbyte __attribute__((unused)))
{
  return 0;
}

inline int
trace_printf(const char* format __attribute__((unused)), ...)
  {
    return 0;
  }

inline int
trace_puts(const char *s __attribute__((unused)))
{
  return 0;
}

inline int
trace_putchar(int c)
{
  return c;
}

inline void
trace_dump_args(int argc __attribute__((unused)),
    char* argv[] __attribute__((unused)))
{
}

#endif // defined(TRACE)

#endif // DIAG_TRACE_H_
