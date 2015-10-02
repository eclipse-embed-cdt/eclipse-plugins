/**
 *****************************************************************************  
   @file       IntLib.h
   @brief 		Set of interrupt functions.
   - configure external interrupts with EiCfg()
   - clear external interrupt flag with EiClr()
   - Example: Library used in Timers project

   @version		V0.1
   @author     ADI
   @date			August 2011

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/


extern int EiCfg(int iEiNr, int iEnable, int iMode);
extern int EiClr(int iEiNr);


//iEiNr in EiCfg()
#define EXTINT0 0x0 
#define EXTINT1 0x1 
#define EXTINT2 0x2 
#define EXTINT3 0x3 
#define EXTINT4 0x4 
#define EXTINT5 0x5 
#define EXTINT6 0x6 
#define EXTINT7 0x7 

//iEnable in EiCfg()	
#define INT_DIS	0x0	
#define INT_EN 0x1

//iMode in EiCfg()	
#define INT_RISE 0x0	
#define INT_FALL 0x1
#define INT_EDGES 0x2
#define INT_HIGH 0x3
#define INT_LOW	0x4	

