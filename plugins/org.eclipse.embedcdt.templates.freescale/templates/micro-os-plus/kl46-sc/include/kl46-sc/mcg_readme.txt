

MCG mode error codes

0x01 - Not in FEI mode
0x02 - Not in FEE mode
0x03 - Not in FBI mode
0x04 - Not in FBE mode
0x05 - Not in BLPI mode
0x06 - Not in BLPE mode
0x07 - Not in PBE mode
0x08 - Not in PEE mode

CLock MUX switching error codes

0x11 - IREFST did not clear within allowed time, FLL reference did not switch over from internal to external clock
0x12 - IREFST did not set within allowed time, the FLL reference did not switch over from external to internal clock(NEED TO CHECK IN MOVES TO FBI MODE)
0x13 - IRCST did not clear within allowed time, slow IRC is not selected
0x14 - IREFST did not set within allowed time, fast IRC is not selected
0x15 - PLLST did not clear, PLLST did not switch to FLL output, FLL is not running
0x16 - PLLST did not set, PLLST did not switch to PLL ouptut, PLL is not running
0x17 - PLLCST did not switch to the correct state, the correct PLL is not selected as PLLS clock source
0x18 - CLKST != 0, MCG did not switch to FLL output 
0x19 - CLKST != 1, MCG did not switch to internal reference clock source 
0x1A - CLKST != 2, MCG did not switch to external clock source
0x1B - CLKST != 3, MCG did not switch to PLL 

Oscillator error codes

0x21 - external frequency is greater than the maximum frequency
0x22 - crystal frequency outside allowed range
0x23 - OSCINIT/OSCINIT2 did not set within allowed time
0x24 - RTC frequency outside allowed range 
0x25 - RTC oscillator did not initialize in time

IRC and FLL error codes

0x31 - slow IRC is outside allowed range
0x32 - fast IRC is outside allowed range
0x33 - FLL frequency is below minimum value for range 0
0x34 - FLL frequency is above maximum value for range 0
0x35 - FLL frequency is below minimum value for range 1
0x36 - FLL frequency is above maximum value for range 1
0x37 - FLL frequency is below minimum value for range 2
0x38 - FLL frequency is above maximum value for range 2
0x39 - FLL frequency is below minimum value for range 3
0x3A - FLL frequency is above maximum value for range 3
0x3B - FLL DRS range is greater than 1

PLL error codes

0x41 - PRDIV outside allowed range
0x42 - VDIV outside allowed range 
0x43 - PLL reference clock frequency is outside allowed range
0x44 - LOCK or LOCK2 bit did not set       
0x45 - PLL output frequency is outside allowed range (NEED TO ADD THIS CHECK TO fbe_pbe and blpe_pbe) only in fei-pee at this time

  


