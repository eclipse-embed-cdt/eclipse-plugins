//
// This file is part of the GNU ARM Eclipse Plug-ins project
// Copyright (c) 2014 Liviu Ionescu
//

#include "$(CMSIS_name).h"

// Usually main() doesn't return, but if it does, on Debug
// we'll enter an infinite loop, to be noticed when halting the debugger
// while on Release we restart using the NVIC.
//
// You can redefine it in the application, if more functionality
// is required.

void
__attribute__((weak))
_exit(int r)
{
#if defined(DEBUG)
  while(1)
  ;
#else
  NVIC_SystemReset();
  while (1)
    ;
#endif
}

