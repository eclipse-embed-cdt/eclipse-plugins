//
// This file is part of the GNU ARM Eclipse Plug-in
// Copyright (c) 2013 Liviu Ionescu
//

#include <sys/types.h>
#include <errno.h>

/* Stack register. */
register char * stack_ptr asm ("sp");

caddr_t
_sbrk(int incr)
{
  extern char end asm ("end"); /* Defined by the linker.  */
  static char * heap_end;
  char * prev_heap_end;

  if (heap_end == NULL)
    heap_end = &end;

  prev_heap_end = heap_end;

  if (heap_end + incr > stack_ptr)
    {
      /* Some of the libstdc++-v3 tests rely upon detecting
       out of memory errors, so do not abort here.  */
#if 0
      extern void abort (void);

      _write (1, "_sbrk: Heap and stack collision\n", 32);

      abort ();
#else
      errno = ENOMEM;
      return (caddr_t) - 1;
#endif
    }

  heap_end += incr;

  return (caddr_t) prev_heap_end;
}
