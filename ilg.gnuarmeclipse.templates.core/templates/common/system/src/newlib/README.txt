These files extend or replace some of the the newlib functions.

They provide:
- a customised startup sequence, written in C
- a customised exit() implementation
- local versions of the libnosys/librdimon code
- a custom _sbrk() to match the actual linker scripts
