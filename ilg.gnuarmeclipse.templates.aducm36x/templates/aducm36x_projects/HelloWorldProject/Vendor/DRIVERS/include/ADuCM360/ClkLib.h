/**
 *****************************************************************************  
   @file       ClkLib.h
   @brief 		Set of Timer peripheral functions.
   - Example:
   - ClkCfg(CLK_CD3,CLK_HF,CLK_HFO,CLK_OFF);
   - ClkSel(CLK_OFF,CLK_OFF,CLK_CD0,CLK_OFF);

   @version		V0.4
   @author     ADI
   @date		January 2013
   @par Revision History:
   - V0.1, December 2010: initial version. 
   - V0.2, January 2012: Changed Clkcfg() - removed write to XOSCCON.
   - V0.3, January 2013: corrected comments.
   - V0.4, February 2013: corrected parameters in ClkDis()
   
All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

**/

extern int ClkCfg(int iCd, int iClkSrc, int iSysClockDiv, int iClkOut);
extern int ClkSel(int iSpiCd, int iI2cCd, int iUrtCd, int iPwmCd);
extern int ClkDis(int iClkDis);
extern int XOSCCfg(int iXosc);

#define	CLK_OFF		-1
#define	CLK_CD0		0
#define	CLK_CD1		1
#define	CLK_CD2		2
#define	CLK_CD3		3
#define	CLK_CD4		4
#define	CLK_CD5		5
#define	CLK_CD6		6
#define	CLK_CD7		7

#define	CLK_HF		0
#define	CLK_LFX		1
#define	CLK_LF		2
#define	CLK_P4		3

#define	CLK_UCLKCG	0
#define	CLK_UCLK	1
#define	CLK_UDIV	2
#define	CLK_HFO		5
#define	CLK_LFO		6
#define	CLK_LFXO	7

#define	CLK_XOFF	0
#define	CLK_XON		1
#define	CLK_XON2	5
