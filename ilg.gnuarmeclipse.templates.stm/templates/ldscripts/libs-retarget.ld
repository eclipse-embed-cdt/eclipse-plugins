
/*
 * The libnosys.a is used to satisfy all system call references,
 * although with empty calls. In this configuration, the debug output
 * is forwarded to the semihosting debug channel, vis SYS_WRITEC.
 * The application and redefine all syscall implementation functions,
 * like _write(), _read(), etc.
 * When using libnosys.a, the startup files are not needed.
 *
 * Another useful library is librdimon.a, which implements all
 * system calls via the semihosting API, with all functionality
 * provided by the host. In this configuration, the debug output
 * is forwarded to the host standard output.
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
 
 libnosys.a
)
