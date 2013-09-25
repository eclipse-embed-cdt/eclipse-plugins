//============================================================================
// Name        : $(baseName).cpp
// Author      : $(author)
// Version     :
// Copyright   : $(copyright)
// Description : Hello World in C++
//============================================================================

#include <iostream>
using namespace std;

//
// Print a greeting on standard output and exit.
//
// On embedded platforms this might need to enable semi-hosting or similar.
//
// For example, for toolchains derived from GNU Tools for Embedded,
// the following should be added to the linker:
//
// --specs=rdimon.specs -Wl,--start-group -lgcc -lc -lc -lm -lrdimon -Wl,--end-group
//

int
main()
{
  cout << "$(messagearm)" << endl;
  return 0;
}
