
// Definitions visible only within this translation unit.
namespace
{
  // ----- LED definitions ----------------------------------------------------

  // FRDM-KL465Z definitions (the GREEN LED, active low, D5).
  // (adjust them for your own board)

  constexpr klxx::portNumber_t BLINK_PORT_NUMBER = klxx::Port::D;
  constexpr klxx::portBitNumber_t BLINK_PORT_BIT_NUMBER = klxx::PortBit::_5;
}
