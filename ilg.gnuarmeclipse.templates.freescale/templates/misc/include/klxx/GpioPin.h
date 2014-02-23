//
// This file is part of the GNU ARM Eclipse distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#include "$(KLchipFamily).h"

#ifndef GPIO_PIN_H_
#define GPIO_PIN_H_

namespace klxx
{
  // General definitions, should probably be moved to a higher header
  typedef uint32_t registerAddress_t;
  typedef uint32_t registerMask_t;

  // GPIO specific definitions
  typedef uint32_t portNumber_t;
  typedef uint32_t portBitNumber_t;
  typedef uint32_t portBitMask_t;

  // Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E
  class Port
  {
    Port() = delete;

  public:
    static constexpr portNumber_t A = 0;
    static constexpr portNumber_t B = 1;
    static constexpr portNumber_t C = 2;
    static constexpr portNumber_t D = 3;
    static constexpr portNumber_t E = 4;
  };

  // Port bit numbers: 0-31
  class PortBit
  {
    PortBit() = delete;

  public:
    static constexpr portBitMask_t _0 = 0;
    static constexpr portBitMask_t _1 = 1;
    static constexpr portBitMask_t _2 = 2;
    static constexpr portBitMask_t _3 = 3;
    static constexpr portBitMask_t _4 = 4;
    static constexpr portBitMask_t _5 = 5;
    static constexpr portBitMask_t _6 = 6;
    static constexpr portBitMask_t _7 = 7;
    static constexpr portBitMask_t _8 = 8;
    static constexpr portBitMask_t _9 = 9;
    static constexpr portBitMask_t _10 = 10;
    static constexpr portBitMask_t _11 = 11;
    static constexpr portBitMask_t _12 = 12;
    static constexpr portBitMask_t _13 = 13;
    static constexpr portBitMask_t _14 = 14;
    static constexpr portBitMask_t _15 = 15;
    static constexpr portBitMask_t _16 = 16;
    static constexpr portBitMask_t _17 = 17;
    static constexpr portBitMask_t _18 = 18;
    static constexpr portBitMask_t _19 = 19;
    static constexpr portBitMask_t _20 = 20;
    static constexpr portBitMask_t _21 = 21;
    static constexpr portBitMask_t _22 = 22;
    static constexpr portBitMask_t _23 = 23;
    static constexpr portBitMask_t _24 = 24;
    static constexpr portBitMask_t _25 = 25;
    static constexpr portBitMask_t _26 = 26;
    static constexpr portBitMask_t _27 = 27;
    static constexpr portBitMask_t _28 = 28;
    static constexpr portBitMask_t _29 = 29;
    static constexpr portBitMask_t _30 = 30;
    static constexpr portBitMask_t _31 = 31;
  };

  template<const portNumber_t PORT_T, const portBitNumber_t PORT_BIT_T>
    class TGpioPinConst
    {
    private:
      // Validate template constant parameters
      static_assert(PORT_T <= 4u, "Port number higher than 4");
      static_assert(PORT_BIT_T <= 31u, "Port bit number higher than 31");

      // Define local addresses and masks
      static constexpr portNumber_t m_portNumber = PORT_T;
      static constexpr portBitNumber_t m_portBitNumber = PORT_BIT_T;
      static constexpr portBitMask_t m_portBitMask = (1u << PORT_BIT_T);

      static constexpr registerAddress_t m_gpioAddress = (PTA_BASE
          + (PTB_BASE - PTA_BASE) * (m_portNumber));

    public:
      // Deleted constructor
      TGpioPinConst() = default;

      void
      powerUp(void) const
      {
        registerMask_t scg5Mask = (1u
            << ((SIM_SCGC5_PORTA_SHIFT) + (m_portNumber)));

        // Turn on clock for port module
        SIM->SCGC5 |= scg5Mask;

        registerAddress_t portAddress = (PORTA_BASE
            + (PORTB_BASE - PORTA_BASE) * (m_portNumber));

        // Set the pin multiplexer to GPIO mode (1)
        (reinterpret_cast<PORT_Type*>(portAddress))->PCR[m_portBitNumber] =
            PORT_PCR_MUX(1);
      }

      void
      powerDown(void) const
      {
        configureDirectionInput();

        registerMask_t scg5Mask = (1u
            << ((SIM_SCGC5_PORTA_SHIFT) + (m_portNumber)));

        // Turn off clock for port module
        SIM->SCGC5 &= ~scg5Mask;
      }

      inline void
      __attribute__((always_inline))
      configureDirectionOutput(void) const
      {
        // Set the pin direction bit
        (reinterpret_cast<GPIO_Type*>(m_gpioAddress))->PDDR |= m_portBitMask;
      }

      inline void
      __attribute__((always_inline))
      configureDirectionInput(void) const
      {
        // Clear the pin direction bit
        (reinterpret_cast<GPIO_Type*>(m_gpioAddress))->PDDR &= ~m_portBitMask;
      }

      inline void
      __attribute__((always_inline))
      setHigh(void) const
      {
        // Set a bit in the set register
        (reinterpret_cast<GPIO_Type*>(m_gpioAddress))->PSOR = m_portBitMask;
      }

      inline void
      __attribute__((always_inline))
      setLow(void) const
      {
        // Set a bit in the clear register
        (reinterpret_cast<GPIO_Type*>(m_gpioAddress))->PCOR = m_portBitMask;
      }

      inline void
      __attribute__((always_inline))
      toggle(void) const
      {
        // Set a bit in the toggle register
        (reinterpret_cast<GPIO_Type*>(m_gpioAddress))->PTOR = m_portBitMask;
      }

      inline bool
      __attribute__((always_inline))
      isLow(void) const
      {
        // Test if bit in input register is 0
        return (((reinterpret_cast<GPIO_Type*>(m_gpioAddress))->PDIR
            & m_portBitMask) == 0);
      }

      inline bool
      __attribute__((always_inline))
      isHigh(void) const
      {
        // Test if bit in input register is 1
        return (((reinterpret_cast<GPIO_Type*>(m_gpioAddress))->PDIR
            & m_portBitMask) != 0);
      }

    };
}

#endif // GPIO_PIN_H_
