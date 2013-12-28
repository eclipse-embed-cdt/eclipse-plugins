//
// This file is part of the GNU ARM Eclipse Plug-in
// Copyright (c) 2013 Liviu Ionescu
//

#include "trace_impl.h"

// The standard write() system call, after a long way inside newlib,
// finally calls this implementation function.

// Based on the file descriptor, it can send arrays of characters to
// different physical devices.

// Currently only the output file descriptor is tested, and there are
// three output channels implemented, mainly for debugging purposes.

// The usual method to display trace messages is via printf().

// The preprocessor definitions used for selection are in trace_impl.h.

int
_write(int fd, char* ptr, int len)
{
  if (fd == 1)
    {
#if defined(DEBUG)

#if defined(INCLUDE_TRACE_ITM)
      return _write_trace_itm(ptr, len);
#elif defined(INCLUDE_TRACE_SEMIHOSTING_STDOUT)
      return _write_trace_semihosting_stdout(ptr, len);
#elif defined(INCLUDE_TRACE_SEMIHOSTING_DEBUG)
      return _write_trace_semihosting_debug(ptr, len);
#else
#warning "no trace implementation"
#endif

#endif // DEBUG
    }
  return -1;
}

