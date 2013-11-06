Content
-------

The files copied to these folders are the original ones, from the 
STM stsw-stm32054.zip archive with the following changes:

- stm32f4xx_fmc.c was modified, to compile only on STM32F427_437xx 
or STM32F429_439xx

- core_cm3.c was modified, the strexb, strexh, strex calls 
were changed from:

 __ASM volatile ("strex %0, %2, [%1]" : "=r" (result) : "r" (addr), "r" (value) );

to

 __ASM volatile ("strex %0, %2, [%1]" : "=&r" (result) : "r" (addr), "r" (value) );


Startup
-------

The original multiple assembly startup files were replaced by 
- a portable startup_cm.c 
- a family initialisation file (startup_stm32f10x.c)
- the interrupt vectors (vectors_stm32f10x.c)


Application interrupt handlers
------------------------------

It is recommended that interrupt handlers redefined by applications
to be located in the same files where the application drivers are; 
there is no need to use the stm32f10x_it.* files, since the default
system handlers are already defined in the vectors_stm32f10x.c file.


Manuals and samples
-------------------

For more details, manuals and the complete set of samples, please 
refer to the original STM archives.

