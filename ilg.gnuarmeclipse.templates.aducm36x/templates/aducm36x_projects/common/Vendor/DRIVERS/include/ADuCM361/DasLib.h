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
@mainpage ADuCM360 Low Level Functions
@version  V0.6
@author   ADI
@date     October 2015

@section intro Introduction
The ADuCM360/361 is a fully integrated microcontroller with dual/single 24-bit ADCs for low power applications.
A set of code examples demonstrating the use of the ADuCM360/361 peripherals are provided. For example, thermocouple/RTD
interfacing examples (CN0221,CN0300) and examples of 4-20mA loop controlled by the on-chip DAC or on-chip PWM (CN0300, CN0319) are provided.
All examples are based on low level functions, also described in this document.

- The Modules tab list the Low Level Functions. These are grouped per peripherals or features.
- The files tab shows the Low Level Functions files.
- The Examples tab list the code examples based on the Low Level Functions.

@section disclaimer Disclaimer
All files for ADuCM360/361 provided by ADI, including this file, are
provided  as is without warranty of any kind, either expressed or implied.
The user assumes any and all risk from the use of this code.
It is the responsibility of the person integrating this code into an application
to ensure that the resulting application performs as required and is safe.

@par Revision History:
   - V0.1, February 2012: initial version.
   - V0.2, October 2012:
            - addition of PwrLib, FeeLib and DmaLib.
            - added dma functions to SPI Library.
            - fixed baud rate generation inside UrtCfg.
            - fixed an issue with WdtClrInt.
            - Modified DioLib to include pin functions.
            - RstLib CMSIS compiant.
            - All libraries now Doxygen commented.
   - V0.3, November 2012:
            - Fixed dio comments.
            - SPI dma functions moved to DmaLib.
            - Modified FlashProtect example.
            - Changes to FeeLib.
   - V0.4, February 2013:
            - Fixed ClkLib comment.
            - Fixed functions in AdcLib and IexcLib.
            - Fixed examples.
            - Added more doxygen comments to Examples.
   - V0.5, June 2013:
            - Fixed UrtLib, I2cLib and PwmLib doxygen comments.
            - Fixed FlashSign example.
            - Fixed functions in GptLib and WutLib.
            - Corrected parameters in ClkLib and AdcLib.
            - Addition of CN-0319 code example.
   - V0.6, October 2015:
            - Coding style cleanup - no functional changes.

@section notes Release notes
Functions and examples still work in progress. Check for updates regularly.

   @defgroup daslibs Low Level Functions
   @{
      @defgroup adc ADC
      @defgroup clk Clock
      @defgroup dac DAC
      @defgroup dio Digital IO
      @defgroup dma DMA
      @defgroup fee Flash
      @defgroup gpt General Purpose Timer
      @defgroup i2c I2C
      @defgroup iexc Excitation Current Source
      @defgroup int Interrupts
      @defgroup pwm PWM
      @defgroup pwr Power
      @defgroup rst Reset
      @defgroup spi SPI
      @defgroup urt UART
      @defgroup wdt Watchdong Timer
      @defgroup wut Wake Up Timer

      Low Level Peripheral functions
   @}

**/