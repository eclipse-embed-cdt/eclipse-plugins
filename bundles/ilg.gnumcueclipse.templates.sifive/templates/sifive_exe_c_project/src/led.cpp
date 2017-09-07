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

led::led (unsigned int port, unsigned int bit, bool active_low)
{
  gpio_ptr_ = riscv_device_gpio_ptr; // Fixed, there is only one GPIO port.
  port_number_ = (uint16_t) port;
  bit_number_ = (uint16_t) bit;
  is_active_low_ = active_low;
  bit_mask_ = (uint32_t) (1 << bit);
}

void
led::power_up ()
{
  // Disable I/O Functions on this bit, keep it as plain digital pin.
  gpio_ptr_->iof_en &= ~bit_mask_;
  // Clear the toggle bit.
  gpio_ptr_->out_xor &= ~bit_mask_;
  // Enable output.
  gpio_ptr_->output_en |= bit_mask_;

  // Start with led turned off
  turn_off ();
}

void
led::turn_on ()
{
  if (is_active_low_)
    {
      gpio_ptr_->value &= ~bit_mask_;
    }
  else
    {
      gpio_ptr_->value |= bit_mask_;
    }
}

void
led::turn_off ()
{
  if (is_active_low_)
    {
      gpio_ptr_->value |= bit_mask_;
    }
  else
    {
      gpio_ptr_->value &= ~bit_mask_;
    }
}

void
led::toggle ()
{
  gpio_ptr_->out_xor |= bit_mask_;
}

bool
led::is_on ()
{
  // TODO: clarify if GPIO_INPUT_VAL is correct here, or GPIO_OUTPUT_VAL
  // is more appropriate, due to XOR and input actually not being enabled.
  if (is_active_low_)
    {
      return (gpio_ptr_->value & bit_mask_) == 0;
    }
  else
    {
      return (gpio_ptr_->value & bit_mask_) != 0;
    }
}

// ----------------------------------------------------------------------------
