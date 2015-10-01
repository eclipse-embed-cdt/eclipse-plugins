/**
 *****************************************************************************
   @file     PwmLib.h
   @brief    Set of PWM peripheral functions.
   @version  V0.2
   @author   ADI
   @date     September 2012 

All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

*****************************************************************************/

extern int PwmInit(unsigned int iPWMCP, unsigned int iPWMIEN, unsigned int iSYNC, unsigned int iTRIP);
extern int PwmClrInt(unsigned int iSource);
extern int PwmTime(int iPair, unsigned int uiFreq, unsigned int uiPWMH_High, unsigned int uiPWML_High); 
extern int PwmGo(unsigned int iPWMEN, unsigned int iHMODE);
extern int PwmHBCfg(unsigned int iENA, unsigned int iPOINV, unsigned int iHOFF, unsigned int iDIR);
extern int PwmInvert(int iInv1, int iInv3, int iInv5);
extern int PwmLoad(int iLoad);

#define	UCLK_2 0
#define	UCLK_4 0x40
#define	UCLK_8 0x80
#define	UCLK_16 0xC0
#define	UCLK_32 0x100
#define	UCLK_64 0x140
#define	UCLK_128 0x180
#define	UCLK_256 0x1C0

#define	PWM0_1 0
#define	PWM2_3 1
#define	PWM4_5 2
