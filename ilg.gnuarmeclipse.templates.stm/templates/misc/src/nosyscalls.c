//
// These functions are from the newlib/libgloss/libnosys folder, and are
// used to keep the linker happy in standalone application.
//
// They provide no functionality, just set the error code and return
// an error condition.
// By redefining the _DEFUN() macro, all symbols are weak, so they can be
// safely redefined by the application.
//

#include "sys/config.h"
#include <_ansi.h>
#include <_syslist.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/times.h>
#include <limits.h>

#undef errno
// extern int errno;
// [ilg]: Make sure the errno is defined.
int errno;

// #include "warning.h"
// [ilg]: Prefer an empty definition, just to preserve the original sources.
#define stub_warning(_X)

// Version of environ for no OS.
char *__env[1] = { 0 };
char **environ = __env;

// [ilg]: Make all symbols weak
#undef _DEFUN
#define _DEFUN(name, arglist, args)     __attribute__((weak)) name(args)

int
_DEFUN (_chown, (path, owner, group),
        const char *path  _AND
        uid_t owner _AND
        gid_t group)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_chown)

int
_DEFUN (_close, (fildes),
        int fildes)
{
  errno = ENOSYS;
  return -1;
}

stub_warning (_close)

int
_DEFUN (_execve, (name, argv, env),
        char  *name  _AND
        char **argv  _AND
        char **env)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_execve)

// [ilg]: We use a separate _exit(), to restart the processor.
#if 0
_VOID
_DEFUN (_exit, (rc),
        int rc)
{
  /* Default stub just causes a divide by 0 exception.  */
  int x = rc / INT_MAX;
  x = 4 / x;

  /* Convince GCC that this function never returns.  */
  for (;;)
    ;
}
#endif

int
_DEFUN (_fork, (),
        _NOARGS)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_fork)

int
_DEFUN (_fstat, (fildes, st),
        int          fildes _AND
        struct stat *st)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_fstat)

int
_DEFUN (_getpid, (),
        _NOARGS)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_getpid)

int
_DEFUN (_gettimeofday, (ptimeval, ptimezone),
        struct timeval  *ptimeval  _AND
        void *ptimezone)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_gettimeofday)

int
_DEFUN (_isatty, (file),
        int file)
{
  errno = ENOSYS;
  return 0;
}

stub_warning(_isatty)

int
_DEFUN (_kill, (pid, sig),
        int pid  _AND
        int sig)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_kill)

int
_DEFUN (_link, (existing, new),
        char *existing _AND
        char *new)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_link)

int
_DEFUN (_lseek, (file, ptr, dir),
        int   file  _AND
        int   ptr   _AND
        int   dir)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_lseek)

int
_DEFUN (_open, (file, flags, mode),
        char *file  _AND
        int   flags _AND
        int   mode)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_open)

int
_DEFUN (_read, (file, ptr, len),
        int   file  _AND
        char *ptr   _AND
        int   len)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_read)

int
_DEFUN (_readlink, (path, buf, bufsize),
        const char *path _AND
        char *buf _AND
        size_t bufsize)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_readlink)

// [ilg] We use a separate _sbrk(), adapted to our linker scripts.
#if 0
void *
_sbrk (incr)
     int incr;
{
   extern char   end; /* Set by linker.  */
   static char * heap_end;
   char *        prev_heap_end;

   if (heap_end == 0)
     heap_end = & end;

   prev_heap_end = heap_end;
   heap_end += incr;

   return (void *) prev_heap_end;
}
#endif

int
_DEFUN (_stat, (file, st),
        const char  *file _AND
        struct stat *st)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_stat)

int
_DEFUN (_symlink, (path1, path2),
        const char *path1 _AND
        const char *path2)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_symlink)

clock_t
_DEFUN (_times, (buf),
        struct tms *buf)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_times)

int
_DEFUN (_unlink, (name),
        char *name)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_unlink)

int
_DEFUN (_wait, (status),
        int  *status)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_wait)

int
_DEFUN (_write, (file, ptr, len),
        int   file  _AND
        char *ptr   _AND
        int   len)
{
  errno = ENOSYS;
  return -1;
}

stub_warning(_write)

