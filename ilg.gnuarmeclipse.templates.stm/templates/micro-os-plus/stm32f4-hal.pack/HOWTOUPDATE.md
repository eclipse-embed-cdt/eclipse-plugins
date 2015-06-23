The original ST sources are available as a separate branch. This branch should be updated with each new HAL version, then merged into the main branch.

- (in Git) change to checkout the **stm32f4-hal** branch
- (in Finder) remove include/stm32f4-hal/*.h
- remove include/stm32f4-hal/Legacy
- remove src/stm32f4-hal/*.c
- copy Drivers/STM32F4xx_HAL_Driver/Inc/* to include/stm32f4-hal
- copy Drivers/STM32F4xx_HAL_Driver/Src/* to src/stm32f4-hal
- update include/stm32f4-hal/README_HAL.txt
- update src/stm32f4-hal/README_HAL.txt
- (in Git) change to master
- merge the stm32f4-hal
- process conflicts

(Don't forget to update the CMSI part too)

