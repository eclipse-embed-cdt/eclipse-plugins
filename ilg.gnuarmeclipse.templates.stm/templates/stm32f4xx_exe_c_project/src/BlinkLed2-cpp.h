//
// This file is part of the GNU ARM Eclipse distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#ifndef BLINKLED_H_
#define BLINKLED_H_

// ----------------------------------------------------------------------------

class BlinkLed
{
public:
  BlinkLed (unsigned int port, unsigned int bit, bool active_low);

  void
  powerUp ();

  void
  turnOn ();

  void
  turnOff ();

  void
  toggle ();

  bool
  isOn ();

private:
  unsigned int fPortNumber;
  unsigned int fBitNumber;
  unsigned int fBitMask;
  bool fIsActiveLow;
};

// ----------------------------------------------------------------------------

#endif // BLINKLED_H_
