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

#ifndef LED_H_
#define LED_H_

#include <micro-os-plus/device.h>

#include <stdint.h>
#include <stdbool.h>

#if defined(__cplusplus)
extern "C"
{
#endif /* __cplusplus */

// ----------------------------------------------------------------------------

  /*
   * Functions to manage a discrete (on/off) LED.
   */

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wpadded"

// Instance data.
  typedef struct led_s
  {
    riscv_device_gpio_t* gpio_ptr_;
    uint16_t port_number_;
    uint16_t bit_number_;
    uint32_t bit_mask_;
    bool is_active_low_;
  } led_t;

#pragma GCC diagnostic pop

  void
  led_construct (led_t* _this, unsigned int port, unsigned int bit,
                 bool active_low);

  void
  led_power_up (led_t* _this);

  void
  led_turn_on (led_t* _this);

  void
  led_turn_off (led_t* _this);

  void
  led_toggle (led_t* _this);

  bool
  led_is_on (led_t* _this);

// ----------------------------------------------------------------------------

#if defined(__cplusplus)
}
#endif /* __cplusplus */

#endif /* LED_H_ */
