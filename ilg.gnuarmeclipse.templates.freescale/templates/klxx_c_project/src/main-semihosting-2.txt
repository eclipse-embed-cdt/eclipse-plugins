 * The build uses the startup files; on Release it does not use
 * any standard library function (on Debug the printf() brings lots of
 * functions; removing it should also use no other standard lib functions).
 *
 * To use the special initialisation code present in librdimon.a, for
 * semihosting, the definition USE_STARTUP_FILES is added for the
 * startup code.
 *
 */
