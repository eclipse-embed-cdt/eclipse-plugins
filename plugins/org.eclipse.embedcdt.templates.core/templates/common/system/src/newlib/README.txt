
The following files extend or replace some of the the newlib functionality:

startup.c: a customised startup sequence, written in C

exit.c: a customised exit() implementation

syscalls.c: local versions of the libnosys/librdimon code

sbrk.c: a custom _sbrk() to match the actual linker scripts

assert.c: implementation for the asserion macros

cxx.cpp: local versions of some C++ support, to avoid references to 
	large functions.

