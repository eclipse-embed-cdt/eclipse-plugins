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

#ifndef LIBC_NEWLIB_C_SYSCALLS_H_
#define LIBC_NEWLIB_C_SYSCALLS_H_

#include <unistd.h>
#include <sys/stat.h>
#include <sys/times.h>

// ----------------------------------------------------------------------------

// Declarations for most syscall implementations specific to newlib.
// Should be included as `#include <newlib/c-syscalls.h>` in all files
// that implement the retargetted system calls.

int
_chown (const char* path, uid_t owner, gid_t group);

int
_close (int fildes);

int
_execve (const char* name, char* const argv[], char* const envp[]);

pid_t
_fork (void);

int
_fstat (int fildes, struct stat* st);

pid_t
_getpid (void);

int
_gettimeofday (struct timeval* ptimeval, void* ptimezone);

int
_isatty (int fildes);

int
_kill (pid_t pid, int sig);

int
_link (const char* existing, const char* _new);

off_t
_lseek (int fildes, int ptr, int dir);

int
_open (const char* file, int flags, int mode);

int
_openat (int dirfd, const char* name, int flags, int mode);

ssize_t
_read (int fildes, void* ptr, size_t len);

int
_readlink (const char* path, void* buf, size_t bufsize);

int
_stat (const char* file, struct stat* st);

int
_symlink (const char* existing, const char* _new);

clock_t
_times (struct tms* buf);

int
_unlink (const char* name);

pid_t
_wait (int* status);

ssize_t
_write (int fildes, const void* ptr, size_t len);

// ----------------------------------------------------------------------------

#endif /* LIBC_NEWLIB_C_SYSCALLS_H_ */
