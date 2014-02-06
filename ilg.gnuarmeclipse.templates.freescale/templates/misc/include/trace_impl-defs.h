
#if defined (__cplusplus)
extern "C"
{
#endif

#if defined(INCLUDE_TRACE_ITM)
  int
  _write_trace_itm(char* ptr, int len);
#endif

#if defined(INCLUDE_TRACE_SEMIHOSTING_STDOUT)
int
_write_trace_semihosting_stdout(char* ptr, int len);
#endif

#if defined(INCLUDE_TRACE_SEMIHOSTING_DEBUG)
int
_write_trace_semihosting_debug(char* ptr, int len);
#endif

#if defined (__cplusplus)
} // extern "C"
#endif

#endif // _TRACE_IMPL_H
