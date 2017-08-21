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

#include <led.h>

#include <micro-os-plus/device.h>

// ----------------------------------------------------------------------------

void
led_construct (led_t* _this, unsigned int port, unsigned int bit,
               bool active_low)
{
  _this->port_addr_ = GPIO_CTRL_ADDR; // Fixed, there is only one GPIO port.
  _this->port_number_ = (uint16_t) port;
  _this->bit_number_ = (uint16_t) bit;
  _this->is_active_low_ = active_low;
  _this->bit_mask_ = (uint32_t) (1 << bit);
}

void
led_power_up (led_t* _this)
{
  // Disable I/O Functions on this bit, keep it as plain digital pin.
  _REG32 (_this->port_addr_, GPIO_IOF_EN) &= ~_this->bit_mask_;
  // Clear the toggle bit.
  _REG32 (_this->port_addr_, GPIO_OUTPUT_XOR) &= ~_this->bit_mask_;
  // Enable output.
  _REG32 (_this->port_addr_, GPIO_OUTPUT_EN) |= _this->bit_mask_;

  // Start with led turned off
  led_turn_off (_this);
}

void
led_turn_on (led_t* _this)
{
  if (_this->is_active_low_)
    {
      _REG32 (_this->port_addr_, GPIO_OUTPUT_VAL) &= ~_this->bit_mask_;
    }
  else
    {
      _REG32 (_this->port_addr_, GPIO_OUTPUT_VAL) |= _this->bit_mask_;
    }
}

void
led_turn_off (led_t* _this)
{
  if (_this->is_active_low_)
    {
      _REG32 (_this->port_addr_, GPIO_OUTPUT_VAL) |= _this->bit_mask_;
    }
  else
    {
      _REG32 (_this->port_addr_, GPIO_OUTPUT_VAL) &= ~_this->bit_mask_;
    }
}

void
led_toggle (led_t* _this)
{
  _REG32 (_this->port_addr_, GPIO_OUTPUT_XOR) |= _this->bit_mask_;
}

bool
led_is_on (led_t* _this)
{
  // TODO: clarify if GPIO_INPUT_VAL is correct here, or GPIO_OUTPUT_VAL
  // is more appropriate, due to XOR and input actually not being enabled.
  if (_this->is_active_low_)
    {
      return (_REG32 (_this->port_addr_, GPIO_INPUT_VAL) & _this->bit_mask_)
          == 0;
    }
  else
    {
      return (_REG32 (_this->port_addr_, GPIO_INPUT_VAL) & _this->bit_mask_)
          != 0;
    }
}

// ----------------------------------------------------------------------------
