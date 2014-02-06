Kinetis Sample Code

1) Contents

This package contains a number of bare-metal and Processor Expert example projects for the Kinetis 
L series processors (including header files and initialization code).

Currently supported hardware platforms are:
- TWR_KL25Z48M
- FREEDOM-KL25Z

2) Directory Structure

kl25_sc_revX\

|-klxx-sc-baremetal\
|--build\
|--src\

build\ - All development toolchain specific files are located in this
         subdirectory.

src\   - All source files are arranged in folders inside this 
         directory.

The src\ tree is broken up as follows:

src\common\    - Common utilities such as printf and stdlib are 
                 provided in this directory
src\cpu\       - CPU specific initialization and header files here
src\drivers\   - Drivers for some of the various peripherals are  
                 provided here.
src\platforms\ - Each supported platform has a header file that defines
                 board specific information, such as the input clock
                 frequency used for that board.
src\projects\  - This directory holds all the individual example
                 project source code


|-klxx-sc-pex\
|--common\
|--projects\
|--templates\

common\  - This folder contains header files necessary for the PEx projects to compile correctly. 
		These files are contained within the PEx install but are provided for customers
		who are not using PEx but would still like to utilize the examples provided. 

projects\  - This folder contains the Processor Expert projects and source files generated for 
 		each project.  

templates\  - This folder contains the Processor Expert templates for each project.


3) Toolchain Support

Currently the IAR v6.50.6, CW10.3, and KEIL uVision v4.60 toolchains are supported.



5) Creating new projects

IAR Bare-metal projects
--------------------------

The klxx-sc-baremetal\build\iar\make_new_project.exe files can be used to clone the platinum project.
The script will prompt you for a name to use for the new project, then creates copies of all needed 
files and folders.


KEIL uVision Bare-metal projects
----------------------------------
The klxx-sc-baremetal\build\keil\make_new_project-sc_Keil.exe can be used to clone the platinum 
project.  The script will prompt you for a name to use for the new project, then creates copies of 
all needed files and folders.  

Codewarrior Bare-metal projects
-----------------------------------
The klxx-sc-baremetal\build\cw\make_new_cw_project.exe file can be used to clone the platinum project.
The script will prompt you for a name to use for the new project, then creates copies of all needed
compiler specific files and folders.  The main source file and klxx-sc-baremetal\src\cw\{proj name} 
files will need to be manually created.  

6) Revision History

Date:  10/24/13
Current revision:  Rev10


Changes in Rev3
-------------------------------------------------------------------------------

- Added support for CodeWarrior Bare-metal projects.  

- Updated header files with latest header files.  

- Removed the unknown tool 'Coder' from projects. 

- Updated SystemInit function for KEIL USB projects.  

- Updated default Preprocessor define for twr_spi_demo from FREEDOM to TOWER. 

- Updated default project settings.

- Removed targets that could not be built due to size constraints.  



Changes in Rev4
-------------------------------------------------------------------------------
- Updated CodeWarrior baremetal projects to support changes to the production version
   of CodeWarrior.  

Changes in Rev5
-------------------------------------------------------------------------------
- Deleted hard links in the C/C++ / Build Variables options of the project properties.  

Changes in Rev6
-------------------------------------------------------------------------------
- Removed low_power_demo project (the pre-compiled binary is still available in the binaries folder).

- Improved KEIL project User Commands to generate SREC and binary files upon build using path/project
  relative commands.  

- Removed RAM configurations from projects

- Corrected default workspace configuration for IAR projects.  

- Corrected Projects with warnings.

Changes in Rev7
-------------------------------------------------------------------------------
- Updated the low power demo project, as well as the precompiled binary file.

- Applied update to IAR toolchain version of the MQX_Lite_i2c_demo to be 
  compatible with IAR v6.50.6 and later.  

- Corrected flash loader file selection of IAR version of the FRDM_Demo.  
 
- Corrected default debugger for uVision DAC_Demo.

- Corrected user Commands for uVision Helloworld project. 

- Corrected user commands for uVision MQX_Lite_i2c_demo project. 

- Corrected user commands for uVision pwm_led project.  

- Corrected default debugger for uVision spi_demo project.  

- Corrected default debugger for uVision TPM_demo project.  

- Added uVision project for twr-kl25 demo project.  

Changes in Rev8
-------------------------------------------------------------------------------
- Adding missing TSS library files for MQX_Lite_i2c_demo for the IAR and CW
  IDEs.
 
Changes in Rev9
-------------------------------------------------------------------------------
- Corrected low_power_demo error with missing *.a file.

- Updated low_power_demo pre-compiled binaries with current code base.  
 
Changes in Rev10
-------------------------------------------------------------------------------
- Modified the sysinit in the low_power_demo (frdm & tower) to enable the 
  abort button if DISABLE_ABORT_BUTTON symbol is not defined in the project.  

- Modified list file and linker map default options for the following:
  - FRDM_KL25ZDEMO
  - low_power_demo
  - low_power_dma_uart_demo
  - LQRUG_bme_ex1
  - LQRUG_tpm_ex1
  - LQRUG_tpm_ex2
  - LQRUG_uart_ex1
  - LQRUG_uart_ex2
  - Platinum
  - USB_device
  - vlpr_lls_adc

- Modified unselected the "Override default .board file" option for the following:
  - FRDM_KL25ZDEMO
  - low_power_demo
  - low_power_dma_uart_demo
  - LQRUG_bme_ex1
  - LQRUG_bme_ex2
  - LQRUG_tpm_ex1
  - LQRUG_tpm_ex2
  - LQRUG_uart_ex1
  - LQRUG_uart_ex2
  - Platinum
  - USB_device
  - USB_host
  - vlpr_lls_adc

- Excluded core_portme.c and core_portme.h from the following targets of the 
  low_power_demo:
  - FLASH_64KB
  - FLASH_32KB

- Excluded extraneous sysinit.c file from FLASH_64KB and FLASH_32KB target of 
  LQURG_tpm_ex1.

- Removed extraneous NO_PLL_INIT definition from 32KB Freedom target and 
  64KB, 32KB Tower target for LQRUG_tpm_ex2.  

- Excluded extraneous sysinit and freedom platform file from the 32KB, 64KB
  Freedom target and 64KB, 32KB Tower targets of the LQRUG_tpm_ex2. 

- Removed extraneous NO_PLL_INIT definition from 32KB Freedom target and 
  for LQRUG_uart_ex1.  

- Excluded extraneous sysinit and freedom platform file from the 32KB, 64KB
  Freedom target and 64KB, 32KB Tower targets of the LQRUG_uart_ex1. 

- Removed extraneous NO_PLL_INIT definition from 32KB Freedom target and 
  for LQRUG_uart_ex2.  

- Excluded extraneous sysinit and freedom platform file from the 32KB, 64KB
  Freedom target and 64KB, 32KB Tower targets of the LQRUG_uart_ex2. 

