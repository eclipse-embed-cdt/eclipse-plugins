/*******************************************************************************
* Copyright 2015(c) Analog Devices, Inc.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification,
* are permitted provided that the following conditions are met:
*  - Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*  - Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*  - Neither the name of Analog Devices, Inc. nor the names of its
*    contributors may be used to endorse or promote products derived
*    from this software without specific prior written permission.
*  - The use of this software may or may not infringe the patent rights
*    of one or more patent holders.  This license does not release you
*    from the requirement that you obtain separate licenses from these
*    patent holders to use this software.
*  - Use of the software either in source or binary form, must be run
*    on or directly connected to an Analog Devices Inc. component.
*
* THIS SOFTWARE IS PROVIDED BY ANALOG DEVICES "AS IS" AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, NON-INFRINGEMENT, MERCHANTABILITY
* AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
* IN NO EVENT SHALL ANALOG DEVICES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* INTELLECTUAL PROPERTY RIGHTS, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*******************************************************************************/

/**
 *****************************************************************************
   @file       IntLib.h
   @brief      Set of interrupt functions.
   - configure external interrupts with EiCfg()
   - clear external interrupt flag with EiClr()
   - Example: Library used in Timers project

   @version    V0.2
   @author     ADI
   @date       October 2015
   @par Revision History:
   - V0.1, August 2011: Initial release.
   - V0.2, October 2015: Coding style cleanup - no functional changes.

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
#define INT_DIS   0x0
#define INT_EN 0x1

//iMode in EiCfg()
#define INT_RISE 0x0
#define INT_FALL 0x1
#define INT_EDGES 0x2
#define INT_HIGH 0x3
#define INT_LOW   0x4

