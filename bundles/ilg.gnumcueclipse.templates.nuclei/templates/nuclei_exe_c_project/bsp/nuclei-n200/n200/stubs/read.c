/* See LICENSE of license details. */

#include <stdint.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>

#include "n200/stubs/stub.h"

ssize_t _read(int fd, void* ptr, size_t len)
{
  return _stub(EBADF);
}
