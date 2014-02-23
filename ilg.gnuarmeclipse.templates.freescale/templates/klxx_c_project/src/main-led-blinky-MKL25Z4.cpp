
// Definitions visible only within this translation unit.
namespace
{
  // ----- LED definitions ----------------------------------------------------

  // FRDM-KL25Z definitions (the GREEN LED, active low, B19).
  // (adjust them for your own board)

  constexpr klxx::portNumber_t BLINK_PORT_NUMBER = klxx::Port::B;
  constexpr klxx::portBitNumber_t BLINK_PORT_BIT_NUMBER = klxx::PortBit::_19;
}
