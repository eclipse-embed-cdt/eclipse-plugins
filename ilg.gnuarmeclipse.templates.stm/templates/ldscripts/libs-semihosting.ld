
/*
 * The librdimon.a library implements all system calls via the
 * semihosting API, with all functionality provided by the host. 
 * In this configuration, the debug output is forwarded to the
 * host standard output.
 * When using librdimon.a, the startup files are required to
 * provide all specific initialisation, and the rdimon.specs
 * must be added to the linker. Also, the Reset_Handlers must jump
 * to crt0 _start() (define USE_STARTUP_FILES to enable this).
 */

GROUP(
 libgcc.a
 libg.a
 libc.a
 libm.a
 
 librdimon.a
)
