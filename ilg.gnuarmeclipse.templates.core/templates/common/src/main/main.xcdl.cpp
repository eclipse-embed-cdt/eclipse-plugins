//@XCDL @version "1.1"
/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2016 Liviu Ionescu.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

// ----------------------------------------------------------------------------

#include <stdio.h>
#include "diag/Trace.h"
//@XCDL @if "$(content)"=="blinky"

//@XCDL @if "$(fileExtension)"=="c"
#include "Timer.h"
#include "BlinkLed.h"
//@XCDL @elif "$(fileExtension)"=="cpp"
#include "Timer.h"
#include "BlinkLed.h"
//@XCDL @endif // fileExtension
//@XCDL @endif // content

// ----------------------------------------------------------------------------
//
//@XCDL @if "$(content)"=="blinky"
//@XCDL @if "$(syscalls)"=="none"
//@XCDL @line "// Standalone $(shortChipFamily) led blink sample (trace via $(trace))."
// In debug configurations, demonstrate how to print a greeting message
// on the trace device. In release configurations the message is
// simply discarded.
//
// Then demonstrates how to blink a led with 1 Hz, using a
// continuous loop and SysTick delays.
//@XCDL @elif "$(syscalls)"=="retarget"
//@XCDL @line "// $(shortChipFamily) led blink sample (trace via $(trace))."
// In debug configurations, demonstrate how to print a greeting message
// on the trace device. In release configurations the message is
// simply discarded.
//
// To demonstrate POSIX retargetting, reroute the STDOUT and STDERR to the
// trace device and display messages on both of them.
//
// Then demonstrates how to blink a led with 1 Hz, using a
// continuous loop and SysTick delays.
//
// On DEBUG, the uptime in seconds is also displayed on the trace device.
//@XCDL @elif "$(syscalls)"=="semihosting"
//@XCDL @line "// Semihosting $(shortChipFamily) led blink sample (trace via $(trace))."
// In debug configurations, demonstrate how to print a greeting message
// on the trace device. In release configurations the message is
// simply discarded.
//
// To demonstrate semihosting, display a message on the standard output
// and another message on the standard error.
//
// Then demonstrates how to blink a led with 1 Hz, using a
// continuous loop and SysTick delays.
//
// On DEBUG, the uptime in seconds is also displayed on the trace device.
//@XCDL @endif // syscalls
//@XCDL @elif "$(content)"=="empty"
//@XCDL @if "$(syscalls)"=="none"
//@XCDL @line "// Standalone $(shortChipFamily) empty sample (trace via $(trace))."
//@XCDL @elif "$(syscalls)"=="retarget"
//@XCDL @line "// $(shortChipFamily) empty sample (trace via $(trace))."
//@XCDL @elif "$(syscalls)"=="semihosting"
//@XCDL @line "// Semihosting $(shortChipFamily) empty sample (trace via $(trace))."
//@XCDL @endif // syscalls
//@XCDL @endif // content
//
// Trace support is enabled by adding the TRACE macro definition.
//@XCDL @line "// By default the trace messages are forwarded to the $(trace) output,"
// but can be rerouted to any device or completely suppressed, by
// changing the definitions required in system/src/diag/trace_impl.c
// (currently OS_USE_TRACE_ITM, OS_USE_TRACE_SEMIHOSTING_DEBUG/_STDOUT).
//
//@XCDL @if "$(content)"=="blinky"
// The external clock frequency is specified as a preprocessor definition
// passed to the compiler via a command line option (see the 'C/C++ General' ->
// 'Paths and Symbols' -> the 'Symbols' tab, if you want to change it).
//@XCDL @line "// The value selected during project creation was HSE_VALUE=$(hseValue)."
//
// Note: The default clock settings take the user defined HSE_VALUE and try
// to reach the maximum possible system clock. For the default 8 MHz input
// the result is guaranteed, but for other values it might not be possible,
//@XCDL @line "// so please adjust the PLL settings in system/src/cmsis/system_$(CMSIS_name).c"
//

//@XCDL @if "$(fileExtension)"=="c"
// ----- Timing definitions ---------------------------------------------------

// Keep the LED on for 2/3 of a second.
#define BLINK_1S_TICKS  (TIMER_FREQUENCY_HZ)
#define BLINK_ON_TICKS  (TIMER_FREQUENCY_HZ * 2 / 3)
#define BLINK_OFF_TICKS (TIMER_FREQUENCY_HZ - BLINK_ON_TICKS)
//@XCDL @elif "$(fileExtension)"=="cpp"
// Definitions visible only within this translation unit.
//@XCDL @line "namespace"
{
	// ----- Timing definitions -----------------------------------------------

	// Keep the LED on for 2/3 of a second.
	constexpr Timer::ticks_t BLINK_1S_TICKS = Timer::FREQUENCY_HZ;
	constexpr Timer::ticks_t BLINK_ON_TICKS = Timer::FREQUENCY_HZ * 2 / 3;
	constexpr Timer::ticks_t BLINK_OFF_TICKS = Timer::FREQUENCY_HZ - BLINK_ON_TICKS;
}
//@XCDL @endif // fileExtension
//@XCDL @endif // content

// ----- main() ---------------------------------------------------------------

// Sample pragmas to cope with warnings. Please note the related line at
// the end of this function, used to pop the compiler diagnostics status.
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"
#pragma GCC diagnostic ignored "-Wmissing-declarations"
#pragma GCC diagnostic ignored "-Wreturn-type"

int main(int argc, char* argv[]) {
//@XCDL @if "$(content)"=="blinky"
//@XCDL @if "$(syscalls)"=="retarget"
	// By customising __initialize_args() it is possible to pass arguments,
	// for example when running tests with semihosting you can pass various
	// options to the test.
	// trace_dump_args(argc, argv);
//@XCDL @elif "$(syscalls)"=="semihosting"
	// Show the program parameters (passed via semihosting).
	// Output is via the semihosting output channel.
	trace_dump_args(argc, argv);
//@XCDL @endif // syscalls

	// Send a greeting to the trace device (skipped on Release).
	trace_puts("Hello ARM World!");

//@XCDL @if "$(syscalls)"=="retarget"
	// The standard output and the standard error should be forwarded to
	// the trace device. For this to work, a redirection in _write.c is
	// required.

	// Send a message to the standard output.
	puts("Standard output message.");

	// Send a message to the standard error.
	fprintf(stderr, "Standard error message.\n");
//@XCDL @elif "$(syscalls)"=="semihosting"
	// Send a message to the standard output.
	puts("Standard output message.");

	// Send a message to the standard error.
	fprintf(stderr, "Standard error message.\n");
//@XCDL @endif // syscalls

	// At this stage the system clock should have already been configured
	// at high speed.
	trace_printf("System clock: %u Hz\n", SystemCoreClock);

//@XCDL @if "$(fileExtension)"=="c"
	timer_start();

	blink_led_init();

	uint32_t seconds = 0;

	// Infinite loop
	for (int i = 0;; i++) {
		blink_led_on();
		timer_sleep(i == 0 ? BLINK_1S_TICKS : BLINK_ON_TICKS);

		blink_led_off();
		timer_sleep(BLINK_OFF_TICKS);

		++seconds;
		// Count seconds on the trace device.
		trace_printf("Second %u\n", seconds);
	}
//@XCDL @elif "$(fileExtension)"=="cpp"
	Timer timer;
	timer.start();

	BlinkLed blinkLed;

	// Perform all necessary initialisations for the LED.
	blinkLed.powerUp();

	uint32_t seconds = 0;

	// Infinite loop
	for (int i = 0;; i++) {
		blinkLed.turnOn();
		timer_sleep(i == 0 ? BLINK_1S_TICKS : BLINK_ON_TICKS);

		blinkLed.turnOff();
		timer.sleep(BLINK_OFF_TICKS);

		++seconds;
		// Count seconds on the trace device.
		trace_printf("Second %u\n", seconds);
	}
//@XCDL @endif // fileExtension
	// Infinite loop, never return.
//@XCDL @elif "$(content)"=="empty"
	// At this stage the system clock should have already been configured
	// at high speed.

	// Infinite loop
	while (1) {
		// Add your code here.
	}
//@XCDL @endif // content
}

#pragma GCC diagnostic pop

// ----------------------------------------------------------------------------
