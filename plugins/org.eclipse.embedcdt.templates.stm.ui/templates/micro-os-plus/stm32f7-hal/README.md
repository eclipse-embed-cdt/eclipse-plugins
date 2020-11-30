# STM32CubeF7 MCU Firmware Package

**STM32Cube** is an STMicroelectronics original initiative to ease the developers life by reducing efforts, time and cost.

**STM32Cube** covers the overall STM32 products portfolio. It includes a comprehensive embedded software platform (this repo), delivered for each series (such as the STM32CubeF7 for the STM32F7 series).
   * The CMSIS modules (core and device) corresponding to the ARM-tm core implemented in this STM32 product
   * The STM32 HAL-LL drivers : an abstraction drivers layer, the API ensuring maximized portability across the STM32 portfolio 
   * The BSP Drivers of each evaluation or demonstration board provided by this STM32 series 
   * A consistent set of middlewares components such as RTOS, USB, FatFS, LwIP, Graphics ...
   * A full set of software projects (basic examples, applications or demonstrations) for each board provided by this STM32 series
   
The **STM32CubeF7 MCU Package** projects are directly running on the STM32F7 series boards. You can find in each Projects/*Board name* directories a set of software projects (Applications/Demonstration/Examples) 

In this FW Package, the modules **Middlewares/ST/TouchGFX** **Middlewares/ST/STemWin** **Middlewares/ST/STM32_Audio** are not directly accessible. They must be downloaded from a ST server, the respective URL are available in a readme.txt file inside each module.

## Boards available
  * STM32F7 
    * [STM32F7508-DK](https://www.st.com/en/evaluation-tools/stm32f7508-dk.html)
    * [32F723EDISCOVERY](https://www.st.com/en/evaluation-tools/32f723ediscovery.html)
	* [32F746GDISCOVERY](https://www.st.com/en/evaluation-tools/32f746gdiscovery.html)
	* [32F769IDISCOVERY](https://www.st.com/en/evaluation-tools/32f769idiscovery.html)
	* [NUCLEO-F722ZE](https://www.st.com/en/evaluation-tools/nucleo-f722ze.html)
	* [NUCLEO-F746ZG](https://www.st.com/en/evaluation-tools/nucleo-f746zg.html)
	* [NUCLEO-F756ZG](https://www.st.com/en/evaluation-tools/nucleo-f756zg.html)
	* [NUCLEO-F767ZI](https://www.st.com/en/evaluation-tools/nucleo-f767zi.html)
	* [STM32746G-EVAL](https://www.st.com/en/evaluation-tools/stm32746g-eval.html)
	* [STM32756G-EVAL](https://www.st.com/en/evaluation-tools/stm32756g-eval.html)
	* [STM32F769I-EVAL](https://www.st.com/en/evaluation-tools/stm32f769i-eval.html)
	* [STM32F779I-EVAL](https://www.st.com/en/evaluation-tools/stm32f779i-eval.html)
	* [STM32F7308-DK](https://www.st.com/en/evaluation-tools/stm32f7308-dk.html)

	
## Troubleshooting

**Caution** : The **Issues** requests are strictly limited to submit problems or suggestions related to the software delivered in this repo 

**For any other question** related to the STM32F7 product, the hardware performance, the hardware characteristics, the tools, the environment, you can submit a topic on the [ST Community/STM32 MCUs forum](https://community.st.com/s/group/0F90X000000AXsASAW/stm32-mcus)
 