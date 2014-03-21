//
// This file is part of the µOS++ III distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

// ----------------------------------------------------------------------------

#include <cstdlib>
#include <sys/types.h>

// ----------------------------------------------------------------------------

namespace __gnu_cxx
{
  void
  __verbose_terminate_handler();

  void
  __verbose_terminate_handler()
  {
    for (;;)
      ;
  }
}

// ----------------------------------------------------------------------------

extern "C"
{
  void
  __cxa_pure_virtual();

  void
  __cxa_pure_virtual()
  {
    for (;;)
      ;
  }
}

// ----------------------------------------------------------------------------

