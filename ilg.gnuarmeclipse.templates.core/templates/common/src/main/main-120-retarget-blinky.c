
//
// $(shortChipFamily) led blink sample (trace to $(trace)).
//
// In debug configurations, demonstrate how to print a greeting message
// on the trace device. In release configurations the message is
// simply discarded. By default the trace messages are forwarded to the
// $(trace) output, but can be rerouted via any device or completely
// suppressed by changing the definitions required in
// system/src/diag/trace_impl.c.
//
// Then demonstrates how to blink a led with 1Hz, using a
// continuous loop and SysTick delays.
//
// On DEBUG, the uptime in seconds is also displayed on the trace device.
//
