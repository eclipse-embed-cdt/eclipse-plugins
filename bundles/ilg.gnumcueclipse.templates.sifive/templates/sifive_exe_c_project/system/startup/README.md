## Portable startup code

The myth that startup code must be written in assembly because 'the C environment is not ready' is plainly wrong.

The 'C environment' is mainly the stack, and this can be set in a short assembly entry code and then the standard `_start()` function can be safely called.

