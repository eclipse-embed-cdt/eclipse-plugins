These files extend or replace the functionality provided by newlib.

They provide:
- a customised startup routine, written in C
- the common Cortex-M exception handlers
- the implementation of several _write() routines used to reroute
the tracing output either over SWO or semihosting
- local versions of the libnosys/librdimon code
- a custom _sbrk() to match the actual linker scripts (inside syscalls.c)
- a custom _exit() to reset the core (inside syscalls.c)
