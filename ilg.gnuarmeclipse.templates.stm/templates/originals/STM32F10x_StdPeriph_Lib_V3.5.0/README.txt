This is the original ST stsw-stm32054.zip archive, 
with the following folders removed to save space:

- _htmresc
- Project
- Utilities

Also the following files were removed:
- stm32f10x_stdperiph_lib_um.chm

The only modified file is core_cm3.c, where the strexb, strexh, strex calls 
were changed from:

 __ASM volatile ("strex %0, %2, [%1]" : "=r" (result) : "r" (addr), "r" (value) );

to

 __ASM volatile ("strex %0, %2, [%1]" : "=&r" (result) : "r" (addr), "r" (value) );

to address the strex limitation of having different input/output registers.


For more details, manuals and the complete set of samples, please refer to the
original ST archives.