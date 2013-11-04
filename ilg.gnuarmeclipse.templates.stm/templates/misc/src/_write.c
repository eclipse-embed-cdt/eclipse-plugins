#include "debug_impl.h"

int
_write(int fd, char * ptr, int len)
{
  if (fd == 1)
    {
#if defined(DEBUG)
      if (ptr[len] == '\0')
        {
          BKPT(0x04, (void*) ptr, (void*) 0);
        }
      else
        {
          int i;
          for (i = 0; i < len; ++i, ++ptr)
            {
              BKPT(0x03, (void*) ptr, (void*) 0);
            }
        }
      return len;
#endif
    }
  return -1;
}

