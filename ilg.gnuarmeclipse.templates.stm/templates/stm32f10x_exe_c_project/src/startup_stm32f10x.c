//
// This file is part of the GNU ARM Eclipse Plug-in
// Copyright (c) 2013 Liviu Ionescu
//

#include "stm32f10x.h"

// Usually main() doesn't return, but if it does, on debug
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

