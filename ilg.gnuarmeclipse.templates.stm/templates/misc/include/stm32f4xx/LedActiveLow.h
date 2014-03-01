//
// This file is part of the GNU ARM Eclipse distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#ifndef LED_ACTIVE_LOW_H_
#define LED_ACTIVE_LOW_H_

namespace stm32f
{
  template<typename Gpio_T>
    class TLedActiveLowConst
    {
    private:
      using Gpio = Gpio_T;

      // Normally this object has no members, it is fully static, we use
      // it here as a more flexible method to access the member functions.
      Gpio m_gpio;

    public:
      TLedActiveLowConst() = default;

      void
      powerUp(void) const
      {
        m_gpio.enableClock();
        m_gpio.configureMode(gpio::Mode::Output);
        m_gpio.configureOutputType(gpio::OutputType::PushPull);
        m_gpio.configureOutputSpeed(gpio::OutputSpeed::High_100MHz);
        m_gpio.configureResistors(gpio::Resistors::None);

        m_gpio.setHigh();
      }

      void
      powerDown(void) const
      {
        m_gpio.configureMode(gpio::Mode::Input);
        m_gpio.disableClock();
      }

      inline void
      __attribute__((always_inline))
      turnOn(void) const
      {
        m_gpio.setLow();
      }

      inline void
      __attribute__((always_inline))
      turnOff(void) const
      {
        m_gpio.setHigh();
      }

      inline void
      __attribute__((always_inline))
      toggle(void) const
      {
        m_gpio.toggle();
      }

      inline bool
      __attribute__((always_inline))
      isOn(void) const
      {
        return m_gpio.isLow();
      }
    };
}

#endif // LED_ACTIVE_LOW_H_
