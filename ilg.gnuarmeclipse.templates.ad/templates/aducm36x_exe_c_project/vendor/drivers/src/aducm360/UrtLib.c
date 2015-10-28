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
   @addtogroup urt
   @{
   @file     UrtLib.c
   @brief    Set of UART peripheral functions.
   - Configure the UART pins by setting the mux options in GPCON
   - Configure UART with UrtCfg().
   - Set modem control with UrtMod() if desired.
   - Check space in Tx buffer with UrtLinSta().
   - Output character with UrtTx().
   - Read characters with UrtRx().

   @version  V0.6
   @author   ADI
   @date     October 2015
   @par Revision History:
   - V0.1, March 2012: initial version.
   - V0.2, October 2012: Fixed Baud rate generation function.
   - V0.3, April 2013: Fixed doxygen comments.
   - V0.4, July 2013: Fixed doxygen comments.
   - V0.5, October 2015: Coding style cleanup - no functional changes.
   - V0.6, October 2015: Use Standard Integer Types, prefer unsigned types, add include and C++ guards.

**/

#include "UrtLib.h"
#include "DmaLib.h"

/**
   @brief uint32_t UrtCfg(ADI_UART_TypeDef *pPort, uint32_t iBaud, uint32_t iBits, uint32_t iFormat)
         ==========Configure the UART.
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @param iBaud :{B1200,B2200,B2400,B4800,B9600,B19200,B38400,B57600,B115200,B230400,B430800}   \n
      Set iBaud to the baudrate required:
      Values usually: 1200, 2200 (for HART), 2400, 4800, 9600,
              19200, 38400, 57600, 115200, 230400, 430800, or type in baud-rate directly
   @param iBits :{COMLCR_WLS_5BITS,COMLCR_WLS_6BITS,COMLCR_WLS_7BITS,COMLCR_WLS_8BITS} \n
         - 0 = COMLCR_WLS_5BITS for data length 5bits.
         - 1 = COMLCR_WLS_6BITS for data length 6bits.
         - 2 = COMLCR_WLS_7BITS for data length 7bits.
         - 3 = COMLCR_WLS_8BITS for data length 8bits.
   @param iFormat :{0|COMLCR_STOP_EN|COMLCR_PEN_EN|COMLCR_EPS_EN|COMLCR_SP_EN}   \n
      The bitwise or of the data format required:
         - 0 = URT_STP1 -> 1 stop bits, no parity.
         - 0x4 = COMLCR_STOP_EN  -> Set for 2 stop bits.
         - 0x8 = COMLCR_PEN_EN -> Set for Parity enabled
         - 0x10 = COMLCR_EPS_EN -> Even parity.
         - 0x20 = COMLCR_SP_EN -> Sticky parity.
   @return Value of COMLSR: See UrtLinSta() function for bit details.
   @note
      - Powers up UART if not powered up.
      - Standard baudrates are accurate to better than 0.1% plus clock error.\n
      - Non standard baudrates are accurate to better than 1% plus clock error.
**/

uint32_t UrtCfg(ADI_UART_TypeDef *pPort, uint32_t iBaud, uint32_t iBits, uint32_t iFormat)
{
   uint32_t i1;
   uint32_t iDiv;

   iDiv = (pADI_CLKCTL->CLKCON1 & 0x0E00); // Read UART clock as set by CLKCON1
   iDiv = iDiv >> 9;

   switch (iDiv) {
   case 0:
      iDiv = 1;
      break;

   case 1:
      iDiv = 2;
      break;

   case 2:
      iDiv = 4;
      break;

   case 3:
      iDiv = 8;
      break;

   case 4:
      iDiv = 16;
      break;

   case 5:
      iDiv = 32;
      break;

   case 6:
      iDiv = 64;
      break;

   case 7:
      iDiv = 128;
      break;

   default:
      break;
   }

   if ((pADI_CLKCTL->CLKSYSDIV & 0x1) == 1) {
      iDiv = iDiv * 2;
   }

   i1 = (16000000 / (32 * iDiv)) / iBaud; // UART baud rate clock source is UARTCLK divided by 32
   pPort->COMDIV = i1;
   pPort->COMFBR = 0x8800 | (((((2048 / (32 * iDiv)) * 16000000) / i1) / iBaud) - 2048);
   pPort->COMIEN = 0;
   pPort->COMLCR = (iFormat & 0x3c) | (iBits & 3);
   return   pPort->COMLSR;
}

/**
   @brief uint32_t UrtBrk(ADI_UART_TypeDef *pPort, uint32_t iBrk)
         ==========Force SOUT pin to 0
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @param iBrk :{COMLCR_BRK_DIS, COMLCR_BRK_EN} \n
      - 0 = COMLCR_BRK_DIS to disable SOUT break condition (SOUT behaves as normal)
      - 1 = COMLCR_BRK_EN to force SOUT break condition - SOUT remains low until this bit is cleared
   @return Value of COMLSR: See UrtLinSta() function for bit details.


**/
uint32_t UrtBrk(ADI_UART_TypeDef *pPort, uint32_t iBrk)
{
   if(iBrk == 0) {
      pPort->COMLCR &= 0x3F;   //Disable break condition on SOUT pin.

   } else {
      pPort->COMLCR |= 0x40;   //Force break condition on SOUT pin.
   }

   return   pPort->COMLSR;
}
/**
   @brief uint32_t UrtLinSta(ADI_UART_TypeDef *pPort)
         ==========Read the status byte of the UART.
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @return value of COMLSR:
      - COMLSR_DR = Data ready.
      - COMLSR_OE = Overrun error.
      - COMLSR_PE = Parity error.
      - COMLSR_FE = Framing error.
      - COMLSR_BI = Break indicator.
      - COMLSR_THRE = COMTX empty status bit.
      - COMLSR_TEMT = COMTX and shift register empty status bit.
   @warning UART must be configured before checking status
**/

uint32_t UrtLinSta(ADI_UART_TypeDef *pPort)

{
   return   pPort->COMLSR;
}

/**
   @brief uint32_t UrtTx(ADI_UART_TypeDef *pPort, uint32_t iTx)
         ==========Write 8 bits of iTx to the UART.
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @param iTx :{0-255}  \n
      Byte to transmit.
   @return 1 if successful or 0 if TX buffer full already:
   @warning
      UART must be configured before writing data.\n
      Character is lost if TX buffer already full.
**/

uint32_t UrtTx(ADI_UART_TypeDef *pPort, uint32_t iTx)
{
   if(pPort->COMLSR & COMLSR_THRE) {
      pPort->COMTX = iTx;
      return 1;
   }

   return 0;
}

/**
   @brief uint32_t UrtRx(ADI_UART_TypeDef *pPort)
         ==========Read the UART data.
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @return The byte in the Rx buffer (COMRX).
   @note
      - Does not wait if no new character available.
**/

uint32_t UrtRx(ADI_UART_TypeDef *pPort)
{
   return pPort->COMRX & 0xff;
}

/**
   @brief uint32_t UrtMod(ADI_UART_TypeDef *pPort, uint32_t iMcr, uint32_t iWr)
         ==========Write iMcr to UART Modem Control Register
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @param iMcr :{0|COMMCR_DTR|COMMCR_RTS|COMMCR_LOOPBACK}   \n
      Set to the modem control combination required (COMMCR):
      - 0 to not use DTR, RTS ot LOOPBACK.
      Or a combination of the following.
      - COMMCR_DTR to use Data terminal ready.
      - COMMCR_RTS_to use Request to send.
      - COMMCR_LOOPBACK for Loop back mode.

   @param iWr :{0,1}
      - 0 to read mode only (ignores iMcr).
      - 1 to write and read mode.
   @return value of COMMSR:   \n
      - COMMSR.0 = DCTS -> CTS changed.
      - COMMSR.1 = DDSR -> DSR changed.
      - COMMSR.2 = TERI -> RI Trailing edge.
      - COMMSR.3 = DDCD -> DCD changed.
      - COMMSR.4 = CTS -> Clear to send.
      - COMMSR.5 = DSR -> Data set ready.
      - COMMSR.6 = RI -> Ring indicator.
      - COMMSR.7 = DCD -> Data carrier detect.
   @note This function does not change the Port Multiplexers.
**/

uint32_t UrtMod(ADI_UART_TypeDef *pPort, uint32_t iMcr, uint32_t iWr)
{
   if(iWr) {
      pPort->COMMCR = iMcr;
   }

   return pPort->COMMSR & 0x0ff;
}
/**
   @brief uint32_t UrtModSta(ADI_UART_TypeDef *pPort)
         ==========Read the Modem status register byte of the UART.
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @return value of COMMSR:
      - COMMSR_DCD = Data carrier detect level
      - COMMSR_RI = Ring indicator level
      - COMMSR_DSR = Data set ready status
      - COMMSR_CTS = Clear to Send input level
      - COMMSR_DDCD = Delta DCD status
      - COMMSR_TERI = trailing edge Ring indicator status
      - COMMSR_DDSR = Delta DSR status
      - COMMSR_DCTS = Delta CTS status
   @warning UART must be configured before checking status
**/

uint32_t UrtModSta(ADI_UART_TypeDef *pPort)

{
   return   pPort->COMMSR;
}

/**
   @brief uint32_t UrtDma(ADI_UART_TypeDef *pPort, uint32_t iDmaSel)
         ==========Enables/Disables DMA channel.
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @param iDmaSel :{0|COMIEN_EDMAT| COMIEN_EDMAR}
      - 0 to select neither Tx or Rx DMA.
      Or set to the bitwise or combination of
      - COMIEN_EDMAT enable UART DMA Tx IRQ.
      - COMIEN_EDMAR enable UART DMA Rx IRQ.
   @return 1.
**/

uint32_t UrtDma(ADI_UART_TypeDef *pPort, uint32_t iDmaSel)
{
   uint32_t i1;
   i1 = pPort->COMIEN & ~COMIEN_EDMAT & ~COMIEN_EDMAR;
   i1 |= iDmaSel;
   pPort->COMIEN = i1;
   return 1;
}


/**
   @brief uint32_t UrtIntCfg(ADI_UART_TypeDef *pPort, uint32_t iIrq)
         ==========Enables/Disables UART Interrupt sources.

   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.
   @param iIrq :{COMIEN_ERBFI| COMIEN_ETBEI| COMIEN_ELSI| COMIEN_EDSSI| COMIEN_EDMAT| COMIEN_EDMAR}
      - 0 to select none of the options.
      Or set to the bitwise or combination of
      - COMIEN_ERBFI to enable UART RX IRQ.
      - COMIEN_ETBEI to enable UART TX IRQ.
      - COMIEN_ELSI to enable UART Status IRQ.
      - COMIEN_EDSSI to enable UART Modem status IRQ.
      - COMIEN_EDMAT to enable UART DMA Tx IRQ.
      - COMIEN_EDMAR to enable UART DMA Rx IRQ.
   @return 1.
**/
uint32_t UrtIntCfg(ADI_UART_TypeDef *pPort, uint32_t iIrq)
{
   pPort->COMIEN = iIrq;
   return 1;
}

/**
   @brief uint32_t UrtIntSta(ADI_UART_TypeDef *pPort)
         ==========return UART interrupt status.
   @param pPort :{pADI_UART,} \n
      Set to pADI_UART. Only one channel available.

   @return COMIIR.
**/

uint32_t UrtIntSta(ADI_UART_TypeDef *pPort)
{
   return pPort->COMIIR;
}

/**@}*/
