## µOS++ tracing support

### C++ API

The following functions are available:

`int 	os::trace::printf (const char *format,...)`
 Write a formatted string to the trace device.
 
`int 	os::trace::putchar (int c)`
 Write the single character to the trace device.
 
`int 	os::trace::puts (const char *s)`
 Write the string and a line terminator to the trace device.
 
`int 	os::trace::vprintf (const char *format, std::va_list args)`
 Write a formatted variable arguments list to the trace device.
 
 ### C API

The following functions are available:

`int 	trace_printf (const char *format,...)`
 Write a formatted string to the trace device.
 
`int 	trace_putchar (int c)`
 Write the single character to the trace device.
 
`int 	trace_puts (const char *s)`
 Write the string and a line terminator to the trace device.
 
`int 	trace_vprintf (const char *format, std::va_list args)`
 Write a formatted variable arguments list to the trace device.
 
 