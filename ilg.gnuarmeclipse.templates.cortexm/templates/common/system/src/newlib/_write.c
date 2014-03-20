//
// This file is part of the µOS++ III distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#if !defined(USE_SEMIHOSTING)

// ----------------------------------------------------------------------------

#include <errno.h>

// ----------------------------------------------------------------------------

extern int
trace_write(char* ptr, int len);

// ----------------------------------------------------------------------------

// The standard write() system call, after a long way inside newlib,
// finally calls this implementation function.

// Based on the file descriptor, it can send arrays of characters to
// different physical devices.

// Currently only the output and error file descriptors are tested,
// and the characters are forwarded to the trace channel, mainly
// for debugging purposes.

int
_write(int fd, char* ptr, int len);

int
_write(int fd __attribute__((unused)), char* ptr __attribute__((unused)),
    int len __attribute__((unused)))
{
#if defined(DEBUG)
  if (fd == 1 || fd == 2)
    {

      return trace_write (ptr, len);

    }
#endif // DEBUG

  errno = ENOSYS;
  return -1;
}

// ----------------------------------------------------------------------------

#endif // !defined(USE_SEMIHOSTING)
