These files are from the GNU ARM Eclipse Plug-ins distribution.

Generally they provide support for the newlib functions, specific for
this configuration, like:

- _sbrk() for heap memory allocation,
- _exit() for program termination
- _write() for semihosting/SWO tracing 

For semihosting applications, the syscalls.c provides support for all
system callts to be passed via the semihosting interface to the host.

For standalone applications, the nosyscalls.c provides empty versions
of all system calls, to keep the linker happy.
