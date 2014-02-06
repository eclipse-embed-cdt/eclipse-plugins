//
// This file is part of the GNU ARM Eclipse Plug-ins project
// Copyright (c) 2014 Liviu Ionescu
//

#ifndef _TRACE_IMPL_H
#define _TRACE_IMPL_H

// ITM is the ARM standard mechanism, running over SWD/SWO on Cortex-M devices,
// and is the recommended setting, if available (on small devices, like
// Cortex-M0 or M0+ it is not available).
//
// The J-Link probe together with the J-Link GDB server fully support SWD/SWO
// and the J-Link Debugging plug-in enables it by default.
//
// The current OpenOCD does not support SWD/SWO, so this configuration
// will not work on OpenOCD (will not crash, but nothing will be displayed;
// use semihosting instead).
//
// Semihosting is another mechanism that can be used for tracing. It uses
// specific breakpoints in the code, and works only when assisted by a
// semihosting aware GDB server (both J-Link GDB server and OpenOCD support
// semihosting).
//
// Choosing between semihosting stdout and debug depends on the capabilities
// of your GDB server, and also on specific needs; STDOUT is buffered, so
// nothing is displayed until a \n is encountered; DEBUG is not buffered,
// but can be painfully slow.
//
// The J-Link GDB server fully support semihosting, and both configurations
// are available; to activate it, use "monitor semihosting enable" or check
// the corresponding button in the J-Link Debugging plug-in.
//
// In OpenOCD, support for semihosting can be enabled using
// "monitor arm semihosting enable".
//
// Note: Applications built with semihosting output active cannot be
// executed without the debugger connected and active. Attempts to run them
// standalone or without semihosting enabled will usually be terminated with
// hardware faults.

