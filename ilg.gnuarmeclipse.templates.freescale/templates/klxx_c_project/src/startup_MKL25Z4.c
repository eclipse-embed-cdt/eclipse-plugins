//
// This file is part of the GNU ARM Eclipse Plug-ins project
// Copyright (c) 2014 Liviu Ionescu
//

#include "MKL25Z4.h"

// Usually main() doesn't return, but if it does, on Debug
// we'll just enter an infinite loop, while on Release we restart.
// You can redefine it in the application, if more functionality
// is required
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

