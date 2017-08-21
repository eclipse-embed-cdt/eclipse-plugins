## C library support files

These are some files that complement or extend the C library:

- `c-syscalls-empty.c` define weak empty syscalls to keep the linker happy; redefine those needed for retargeted configurations;
- `_sbrk.c` allocates block of memory in the free store (the heap) at the end of RAM, up to the stack; must match the definitons in the linker script;
- `stdlib/assert.c` defines the function to be called when assertions are taken;
- `stdlib/atexit.cpp` defines a simple registry for functions to be called when the program is terminated; useful for semihosting applications that terminate normally; the size of the static array is `OS_INTEGER_ATEXIT_ARRAY_SIZE`, default 3;
- `stdlib/exit.c` defines the functionality of `exit()` and `abort()`
- `stdlib/init-fini.c` defines weak empty `_init()` and `_fini()`, to keep linker happy.
