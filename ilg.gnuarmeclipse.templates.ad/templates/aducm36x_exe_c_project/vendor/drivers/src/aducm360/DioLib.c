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
   @addtogroup dio
   @{
   @file     DioLib.c
   @brief    Set of Digital IO peripheral functions.
   @version  V0.5
   @author   ADI
   @date     October 2015

   @par Revision History:
   - V0.1, May 2012: initial version.
   - V0.2, October 2012: comment correction,
                         addition of pin configuration functions,
                         addition of Tristate functions.
   - V0.3, November 2012: several comment corrections.
   - V0.4, October 2015: Coding style cleanup - no functional changes.
   - V0.5, October 2015: Use Standard Integer Types, prefer unsigned types, add include and C++ guards.

**/

#include "DioLib.h"

/**
   @brief uint32_t DioCfg(ADI_GPIO_TypeDef *pPort, uint32_t iMpx)
         ========== Sets Digital IO port multiplexer.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iMpx :{0-0xFFFF}
      - Set iMpx accoring to the multiplex options required.
   @return 1.
**/

uint32_t DioCfg(ADI_GPIO_TypeDef *pPort, uint32_t iMpx)
{
   pPort->GPCON = iMpx;
   return 1;
}

/**
   @brief uint32_t DioDrv(ADI_GPIO_TypeDef *pPort, uint32_t iOen, uint32_t iPul, uint32_t iOce)
         ========== Sets output drive of port pins.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iOen :{0-0xFF}
      - Select combination of BIT0 to BIT7 outputs to connect to pin.
   @param iPul :{0xFF,enter value}
      - Select combination of BIT0 to BIT7 pull ups to connect to pin.
   @param iOce :{0x00,enter value}
      - Select combination of BIT0 to BIT7 outputs to be open collector.
   @return 1.
**/
uint32_t DioDrv(ADI_GPIO_TypeDef *pPort, uint32_t iOen, uint32_t iPul, uint32_t iOce)
{
   pPort->GPOEN = iOen;
   pPort->GPPUL = iPul;
   pPort->GPOCE = iOce;
   return 1;
}

/**
   @brief uint32_t DioOen(ADI_GPIO_TypeDef *pPort, uint32_t iOen)
         ========== Sets GPIO direction, in or out.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iOen :{0-0xFF}
                - Select combination of BIT0 to BIT7 outputs to connect to pin e.g.
                - 0, all pins are configured as inputs.
                - BITX|BITY, all pins are inputs except Pin X and Pin Y of the specified port.
   @return 1.
**/
uint32_t DioOen(ADI_GPIO_TypeDef *pPort, uint32_t iOen)
{
   pPort->GPOEN = iOen;
   return 1;
}

/**
   @brief uint32_t DioPul(ADI_GPIO_TypeDef *pPort, uint32_t iPul)
         ========== Sets pull up resistors of port pins.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iPul :{0-0xFF}
      - Select combination of BIT0 to BIT7 to enable the pull ups of pins e.g.
                - 0, all pull ups are disabled.
                - BITX|BITY, all pull ups are disabled except on Pin X and Pin Y of the specified port.
        @return 1.
**/
uint32_t DioPul(ADI_GPIO_TypeDef *pPort, uint32_t iPul)
{
   pPort->GPPUL = iPul;
   return 1;
}

/**
   @brief uint32_t DioOce(ADI_GPIO_TypeDef *pPort, uint32_t iOce)
         ========== Sets open collector  of port pins.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iOce :{0-0xFF}
      - Select combination of BIT0 to BIT7 outputs to be open collector.
        @note For a GPIO to be tristate, the corresponding bit should be set in OCE, OEN and OUT register.
              If OCE and OEN are set, and OUT is cleared, the input is not available and the pin drives 0.
              If OCE only is set, then the GPIO is operating normally as an input.
   @return 1.
**/
uint32_t DioOce(ADI_GPIO_TypeDef *pPort, uint32_t iOce)
{
   pPort->GPOCE = iOce;
   return 1;
}

/**
   @brief uint32_t DioTriState(ADI_GPIO_TypeDef *pPort)
         ========== Places all pins of the specified port in tristate.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @return 1.
**/
uint32_t DioTriState(ADI_GPIO_TypeDef *pPort)
{
   pPort->GPOCE = 0xFF;
   pPort->GPOEN = 0xFF;
   pPort->GPOUT = 0xFF;
   return 1;
}
/**
   @brief uint32_t DioCfgPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iMode)
         ========== Configures the mode of 1 GPIO of the specified port.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iPin :{PIN0, PIN1, PIN2, PIN3, PIN4, PIN5, PIN6, PIN7}
      - PIN0 to configure Px.0.
      - PIN1 to configure Px.1.
      - PIN2 to configure Px.2.
      - PIN3 to configure Px.3.
      - PIN4 to configure Px.4.
      - PIN5 to configure Px.5.
      - PIN6 to configure Px.6.
                - PIN7 to configure Px.7.
   @param iMode :{0, 1, 2, 3}
      - Set the mode accoring to the multiplex options required.
   @return 1.
**/

uint32_t DioCfgPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iMode)
{
   uint16_t a = pPort->GPCON;
   a &= (0xFFFF - (0x3 << (2 * iPin)));  // keep all configurations except iPin
   a += (iMode << 2 * iPin);             // configure iPin
   pPort->GPCON = a;
   return 1;
}


/**
   @brief DioOenPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iOen)
         ========== Configures the output drive of 1 GPIO of the specified port.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iPin :{PIN0, PIN1, PIN2, PIN3, PIN4, PIN5, PIN6, PIN7}
      - PIN0 to configure Px.0.
      - PIN1 to configure Px.1.
      - PIN2 to configure Px.2.
      - PIN3 to configure Px.3.
      - PIN4 to configure Px.4.
      - PIN5 to configure Px.5.
      - PIN6 to configure Px.6.
                - PIN7 to configure Px.7.
   @param iOen :{0, 1}
      - 0 to configure as an input
                - 1 to configure as an output
   @return 1.
**/
uint32_t DioOenPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iOen)
{
   uint16_t a = pPort->GPOEN;
   a &= (0xFF - (0x1 << iPin));   // keep all configurations except iPin
   a += (iOen << iPin);            // configure iPin
   pPort->GPOEN = a;
   return 1;
}


/**
   @brief DioPulPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iPul)
         ========== Configures the pull up of 1 GPIO of the specified port.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iPin :{PIN0, PIN1, PIN2, PIN3, PIN4, PIN5, PIN6, PIN7}
      - PIN0 to configure Px.0.
      - PIN1 to configure Px.1.
      - PIN2 to configure Px.2.
      - PIN3 to configure Px.3.
      - PIN4 to configure Px.4.
      - PIN5 to configure Px.5.
      - PIN6 to configure Px.6.
                - PIN7 to configure Px.7.
        @param iPul :{0, 1}
      - 0 to disable the pull up
                - 1 to enable the pull up
   @return 1.
**/
uint32_t DioPulPin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iPul)
{
   uint16_t a = pPort->GPPUL;
   a &= (0xFF - (0x1 << iPin));   // keep all configurations except iPin
   a += (iPul << iPin);            // configure iPin
   pPort->GPPUL = a;
   return 1;
}

/**
   @brief DioOcePin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iOce)
         ========== Configures the collector of 1 GPIO of the specified port.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iPin :{PIN0, PIN1, PIN2, PIN3, PIN4, PIN5, PIN6, PIN7}
      - PIN0 to configure Px.0.
      - PIN1 to configure Px.1.
      - PIN2 to configure Px.2.
      - PIN3 to configure Px.3.
      - PIN4 to configure Px.4.
      - PIN5 to configure Px.5.
      - PIN6 to configure Px.6.
                - PIN7 to configure Px.7.
        @param iOce :{0, 1}
      - 0 to disable open collector
                - 1 to enable open collector
        @note For a GPIO to be tristate, the corresponding bit should be set in OCE, OEN and OUT register.
              If OCE and OEN are set, and OUT is cleared, the input is not available and the pin drives 0.
              If OCE only is set, then the GPIO is operating normally as an input.
   @return 1.
**/
uint32_t DioOcePin(ADI_GPIO_TypeDef *pPort, uint32_t iPin, uint32_t iOce)
{
   uint16_t a = pPort->GPOCE;
   a &= (0xFF - (0x1 << iPin));   // keep all configurations except iPin
   a += (iOce << iPin);            // configure iPin
   pPort->GPOCE = a;
   return 1;

}

/**
   @brief DioTriStatePin(ADI_GPIO_TypeDef *pPort, uint32_t iPin)
         ========== Configures in tristate 1 GPIO of the specified port.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iPin :{PIN0, PIN1, PIN2, PIN3, PIN4, PIN5, PIN6, PIN7}
      - PIN0 to configure Px.0.
      - PIN1 to configure Px.1.
      - PIN2 to configure Px.2.
      - PIN3 to configure Px.3.
      - PIN4 to configure Px.4.
      - PIN5 to configure Px.5.
      - PIN6 to configure Px.6.
                - PIN7 to configure Px.7.
        @note For a GPIO to be tristate, the corresponding bit should be set in OCE, OEN and OUT register.
              If OCE and OEN are set, and OUT is cleared, the input is not available and the pin drives 0.
              If OCE only is set, then the GPIO is operating normally as an input.

   @return 1.
**/
uint32_t DioTriStatePin(ADI_GPIO_TypeDef *pPort, uint32_t iPin)
{
   uint16_t a = (1 << iPin);
   pPort->GPOCE |= a;
   pPort->GPOEN |= a;
   pPort->GPOUT |= a;
   return 1;
}

/**
   @brief uint32_t DioRd(ADI_GPIO_TypeDef *pPort)
         ========== Reads values of port pins.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
        @return value on port pins.
**/
uint32_t DioRd(ADI_GPIO_TypeDef *pPort)
{
   return (pPort->GPIN);
}

/**
   @brief uint32_t DioWr(ADI_GPIO_TypeDef *pPort, uint32_t iVal)
         ========== Writes values to outputs.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iVal :{0-0xFF}
      - Select combination of BIT0 to BIT7 outputs to be high.
      -  unselected outputs will be low.
   @return value on port pins.
**/
uint32_t DioWr(ADI_GPIO_TypeDef *pPort, uint32_t iVal)
{
   pPort->GPOUT = iVal;
   return (pPort->GPOUT);
}

/**
   @brief uint32_t DioSet(ADI_GPIO_TypeDef *pPort, uint32_t iVal)
         ========== Sets individual outputs.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iVal :{0-0xFF}
      - Select combination of BIT0 to BIT7 outputs to be high.
      - unselected outputs will be unchanged.
   @return value on port pins.
**/
uint32_t DioSet(ADI_GPIO_TypeDef *pPort, uint32_t iVal)
{
   pPort->GPSET = iVal;
   return (pPort->GPSET);
}

/**
   @brief uint32_t DioClr(ADI_GPIO_TypeDef *pPort, uint32_t iVal)
         ========== Clears individual outputs.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iVal :{0-0xFF}
      - Select combination of BIT0 to BIT7 outputs to be cleared.
      -  unselected outputs will be unchanged.
   @return value on port pins.
**/
uint32_t DioClr(ADI_GPIO_TypeDef *pPort, uint32_t iVal)
{
   pPort->GPCLR = iVal;
   return (pPort->GPCLR);
}

/**
   @brief uint32_t DioTgl(ADI_GPIO_TypeDef *pPort, uint32_t iVal)
         ========== Toggles individual outputs.
   @param pPort :{pADI_GP0,pADI_GP1,pADI_GP2}
      - pADI_GP0 for GP0.
      - pADI_GP1 for GP1.
      - pADI_GP2 for GP2.
   @param iVal :{0-0xFF}
      - Select combination of BIT0 to BIT7 outputs to be toggled.
      -  unselected outputs will be unchanged.
   @return value on port pins.
**/
uint32_t DioTgl(ADI_GPIO_TypeDef *pPort, uint32_t iVal)
{
   pPort->GPTGL = iVal;
   return (pPort->GPTGL);
}

/**@}*/
