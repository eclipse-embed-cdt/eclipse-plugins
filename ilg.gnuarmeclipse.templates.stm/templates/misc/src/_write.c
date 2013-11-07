#include "debug_impl.h"

// The standard write() system call, after a long way inside newlib,
// finally calls this implementation function.

// Based on the file descriptor, it can send arrays of characters to
// different physical devices.

// Currently only the output file descriptor is tested, and there are
// three output channels implemented, mainly for debugging purposes.

// The usual method to display trace messages is via printf().

// The preprocessor definitions used for selection are in debug_impl.h.

int
_write(int fd, char* ptr, int len)
{
  if (fd == 1)
    {
#if defined(DEBUG)

#if defined(INCLUDE_SWO)
      return swo_write(ptr, len);
#elif defined(INCLUDE_SEMIHOSTING_DEBUG)
      return semihostig_debug_write(ptr, len);
#elif defined(INCLUDE_SEMIHOSTING_STDOUT)
      return semihostig_stdout_write(ptr, len);
#endif

#endif
    }
  return -1;
}

