/* See LICENSE of license details. */

#include <errno.h>
#include "n200/stubs/stub.h"

int _close(int fd)
{
  return _stub(EBADF);
}
