/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2017 Liviu Ionescu.
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

#include <micro-os-plus/board.h>
{% if trace != 'NONE' %}
#include <micro-os-plus/diag/trace.h>
{% endif %}

#include <sysclock.h>
{% if content == 'blinky' %}
#include <led.h>
{% endif %}

#include <stdint.h>
#include <stdio.h>
{% if language == 'c' %}
#include <stdbool.h>
{% endif %}

// ----------------------------------------------------------------------------
//
{% if content == 'blinky' %}
// This program blinks all LEDs on the SiFive {{ boardDescription }} board.
{% elsif content == 'empty' %}
// This program counts seconds on the SiFive {{ boardDescription }} board.
{% endif %}
//
// ----------------------------------------------------------------------------

{% if content == 'blinky' %}
{% if language == 'cpp' %}
// Definitions visible only within this translation unit.
namespace
{
  // ----- Timing definitions -------------------------------------------------

  // Keep the LED on for 3/4 of a second.
  constexpr clock::duration_t BLINK_ON_TICKS = sysclock.frequency_hz * 3 / 4;
  constexpr clock::duration_t BLINK_OFF_TICKS = sysclock.frequency_hz
      - BLINK_ON_TICKS;
}
{% elsif language == 'c' %}
// ----- Timing definitions ---------------------------------------------------
// Keep the LED on for 3/4 of a second.
#define BLINK_ON_TICKS (OS_INTEGER_SYSCLOCK_FREQUENCY_HZ * 3 / 4)
#define BLINK_OFF_TICKS (OS_INTEGER_SYSCLOCK_FREQUENCY_HZ - BLINK_ON_TICKS)
{% endif %}

// ----- LED definitions ------------------------------------------------------

#define BLINK_PORT_NUMBER         (0)
#define BLINK_ACTIVE_LOW          (true)
#define BLINK_ACTIVE_HIGH         (false)

// Instantiate a static array of led objects.
{% if language == 'cpp' %}
led blink_leds[] =
  {
{% if board == 'hifive1' %}
    { BLINK_PORT_NUMBER, RED_LED_OFFSET, BLINK_ACTIVE_LOW },
    { BLINK_PORT_NUMBER, GREEN_LED_OFFSET, BLINK_ACTIVE_LOW },
    { BLINK_PORT_NUMBER, BLUE_LED_OFFSET, BLINK_ACTIVE_LOW },
{% else %}
    { BLINK_PORT_NUMBER, RED_LED_OFFSET, BLINK_ACTIVE_HIGH },
    { BLINK_PORT_NUMBER, GREEN_LED_OFFSET, BLINK_ACTIVE_HIGH },
    { BLINK_PORT_NUMBER, BLUE_LED_OFFSET, BLINK_ACTIVE_HIGH },
{% endif %}
  /**/
  };
{% elsif language == 'c' %}
led_t blink_leds[3];

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wmissing-declarations"
#pragma GCC diagnostic ignored "-Wmissing-prototypes"

// Automatically called during startup, as for the C++ static
// constructors, before reaching `main()`.
void
__attribute__((constructor))
led_array_construct (void)
{
{% if board == 'hifive1' %}
  led_construct (&blink_leds[0], BLINK_PORT_NUMBER, RED_LED_OFFSET,
                 BLINK_ACTIVE_LOW);
  led_construct (&blink_leds[1], BLINK_PORT_NUMBER, GREEN_LED_OFFSET,
                 BLINK_ACTIVE_LOW);
  led_construct (&blink_leds[2], BLINK_PORT_NUMBER, BLUE_LED_OFFSET,
                 BLINK_ACTIVE_LOW);
{% else %}
  led_construct (&blink_leds[0], BLINK_PORT_NUMBER, RED_LED_OFFSET,
                 BLINK_ACTIVE_HIGH);
  led_construct (&blink_leds[1], BLINK_PORT_NUMBER, GREEN_LED_OFFSET,
                 BLINK_ACTIVE_HIGH);
  led_construct (&blink_leds[2], BLINK_PORT_NUMBER, BLUE_LED_OFFSET,
                 BLINK_ACTIVE_HIGH);
{% endif %}
}

#pragma GCC diagnostic pop
{% endif %}

// ----------------------------------------------------------------------------

{% endif %}
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"
#pragma GCC diagnostic ignored "-Wmissing-declarations"

int
main (int argc, char* argv[])
{
{% if trace != 'NONE' %}
  // Send a greeting to the trace device (skipped on Release).
{% if language == 'cpp' %}
  os::trace::puts ("Hello RISC-V World!");
{% elsif language == 'c' %}
  trace_puts ("Hello RISC-V World!");
{% endif %}

{% endif %}
{% if syscalls != 'none' %}
  // Send a message to the standard output.
  puts ("Standard output message.");

  // Send a message to the standard error.
  fprintf (stderr, "Standard error message.\n");

{% endif %}
  // At this stage the system clock should have already been configured
  // at high speed.
{% if trace != 'NONE' %}
{% if language == 'cpp' %}
  os::trace::printf ("System clock: %u Hz\n", riscv::core::running_frequency_hz ());
{% elsif language == 'c' %}
  trace_printf ("System clock: %u Hz\n", riscv_core_get_running_frequency_hz ());
{% endif %}
{% endif %}

{% if content == 'blinky' %}
  // Power up all LEDs.
  for (size_t i = 0; i < (sizeof(blink_leds) / sizeof(blink_leds[0])); ++i)
    {
{% if language == 'cpp' %}
      blink_leds[i].power_up ();
{% elsif language == 'c' %}
      led_power_up (&blink_leds[i]);
{% endif %}
    }

{% endif %}
{% if trace != 'NONE' %}
  uint32_t seconds = 0;

{% endif %}
{% if content == 'blinky' %}
  // Turn on all LEDs.
  for (size_t i = 0; i < (sizeof(blink_leds) / sizeof(blink_leds[0])); ++i)
    {
{% if language == 'cpp' %}
      blink_leds[i].turn_on ();
{% elsif language == 'c' %}
      led_turn_on (&blink_leds[i]);
{% endif %}
    }

  // First interval is longer (one full second).
{% if language == 'cpp' %}
  sysclock.sleep_for (sysclock.frequency_hz);
{% elsif language == 'c' %}
  os_sysclock_sleep_for (os_sysclock_get_frequency_hz ());
{% endif %}

  // Turn off all LEDs.
  for (size_t i = 0; i < (sizeof(blink_leds) / sizeof(blink_leds[0])); ++i)
    {
{% if language == 'cpp' %}
      blink_leds[i].turn_off ();
{% elsif language == 'c' %}
      led_turn_off (&blink_leds[i]);
{% endif %}
    }

{% if language == 'cpp' %}
  sysclock.sleep_for (BLINK_OFF_TICKS);
{% if trace != 'NONE' %}

  ++seconds;
  os::trace::printf ("Second %u\n", seconds);
{% endif %}
{% elsif language == 'c' %}
  os_sysclock_sleep_for (BLINK_OFF_TICKS);
{% if trace != 'NONE' %}

  ++seconds;
  trace_printf ("Second %u\n", seconds);
{% endif %}
{% endif %}

{% endif %}
  // Loop forever, one second at a time.
  while (true)
    {
{% if content == 'blinky' %}
      // Blink individual LEDs.
      for (size_t i = 0; i < (sizeof(blink_leds) / sizeof(blink_leds[0])); ++i)
        {
{% if language == 'cpp' %}
          blink_leds[i].turn_on ();
          sysclock.sleep_for (BLINK_ON_TICKS);

          blink_leds[i].turn_off ();
          sysclock.sleep_for (BLINK_OFF_TICKS);
{% if trace != 'NONE' %}

          ++seconds;
          os::trace::printf ("Second %u\n", seconds);
{% endif %}
{% elsif language == 'c' %}
          led_turn_on (&blink_leds[i]);
          os_sysclock_sleep_for (BLINK_ON_TICKS);

          led_turn_off (&blink_leds[i]);
          os_sysclock_sleep_for (BLINK_OFF_TICKS);
{% if trace != 'NONE' %}

          ++seconds;
          trace_printf ("Second %u\n", seconds);
{% endif %}
{% endif %}
        }
{% elsif content == 'empty' %}
{% if language == 'cpp' %}
      sysclock.sleep_for (sysclock.frequency_hz);
{% if trace != 'NONE' %}

      ++seconds;
      os::trace::printf ("Second %u\n", seconds);
{% endif %}
{% elsif language == 'c' %}
      os_sysclock_sleep_for (os_sysclock_get_frequency_hz ());
{% if trace != 'NONE' %}

      ++seconds;
      trace_printf ("Second %u\n", seconds);
{% endif %}
{% endif %}
{% endif %}
    }
}

#pragma GCC diagnostic pop

// ----------------------------------------------------------------------------

