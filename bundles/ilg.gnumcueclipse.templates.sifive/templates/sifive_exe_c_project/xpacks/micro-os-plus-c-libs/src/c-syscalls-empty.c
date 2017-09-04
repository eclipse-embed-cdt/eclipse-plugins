/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2015 Liviu Ionescu.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

#include <newlib/c-syscalls.h>

#include <errno.h>

// ----------------------------------------------------------------------------

// Semihosting has its own set of implementations.

#if !defined(OS_USE_SEMIHOSTING)

int __attribute__((weak))
_chown (const char* path __attribute__((unused)),
        uid_t owner __attribute__((unused)),
        gid_t group __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_close (int fildes __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_execve (const char* name __attribute__((unused)),
         char* const argv[] __attribute__((unused)),
         char* const envp[] __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

pid_t __attribute__((weak))
_fork (void)
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_fstat (int fildes __attribute__((unused)),
        struct stat* st __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

pid_t __attribute__((weak))
_getpid (void)
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_gettimeofday (struct timeval* ptimeval __attribute__((unused)),
               void* ptimezone __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_isatty (int fildes __attribute__((unused)))
{
  errno = ENOSYS;
  return 0;
}

int __attribute__((weak))
_kill (pid_t pid __attribute__((unused)), int sig __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_link (const char* existing __attribute__((unused)),
       const char* _new __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

off_t __attribute__((weak))
_lseek (int fildes __attribute__((unused)), int ptr __attribute__((unused)),
        int dir __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_open (const char* file __attribute__((unused)),
       int flags __attribute__((unused)), int mode __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_openat (int dirfd __attribute__((unused)),
         const char* name __attribute__((unused)),
         int flags __attribute__((unused)), int mode __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

ssize_t __attribute__((weak))
_read (int fildes __attribute__((unused)), void* ptr __attribute__((unused)),
       size_t len __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_readlink (const char* path __attribute__((unused)),
           void* buf __attribute__((unused)),
           size_t bufsize __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_stat (const char* file __attribute__((unused)),
       struct stat* st __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

int __attribute__((weak))
_symlink (const char* existing __attribute__((unused)),
          const char* _new __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

clock_t __attribute__((weak))
_times (struct tms* buf __attribute__((unused)))
{
  errno = ENOSYS;
  return ((clock_t) - 1);
}

int __attribute__((weak))
_unlink (const char* name __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

pid_t __attribute__((weak))
_wait (int* status __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

ssize_t __attribute__((weak))
_write (int fildes __attribute__((unused)),
        const void* ptr __attribute__((unused)),
        size_t len __attribute__((unused)))
{
  errno = ENOSYS;
  return -1;
}

#endif /* !defined(OS_USE_SEMIHOSTING) */

// ----------------------------------------------------------------------------
