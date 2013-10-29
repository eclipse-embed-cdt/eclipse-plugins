// Selector for the proper assembly startup file, based on family

#if defined(STM32F40_41xxx)
#include "../startup/startup_stm32f40_41xxx.s"
#elif defined(STM32F10X_LD_VL)
#include "../startup/startup_stm32f40xx.s"
#elif defined(STM32F10X_MD)
#include "../startup/startup_stm32f401xx.s"
#elif defined(STM32F427_437xx)
#include "../startup/startup_stm32f427_437xx.s"
//#elif defined(STM32F427)
//#include "../startup/startup_stm32f427x.s"
// TODO: find out which define works for this one
#elif defined(STM32F401xx)
//#include "../startup/startup_???.s"
#error "TODO: find out which file to us"
#else
#error "no startup"
#endif

