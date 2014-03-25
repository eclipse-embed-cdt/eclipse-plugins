#! /bin/bash

src=../../../uOS/micro-os-plus-iii.git/packages
dst=../ilg.gnuarmeclipse.templates.core/templates/common
system=$dst/system

stm=../ilg.gnuarmeclipse.templates.stm/templates/micro-os-plus

rm -rf $system

for i in 'arm' 'cmsis' 'cortexm' 'diag' 
do
    mkdir -p $dst/system/include/$i
done

cp -n $src/architectures/arm/arm.pack/include/arm/semihosting.h $system/include/arm

cp -n $src/architectures/arm/cortexm.pack/include/cmsis/*.h $system/include/cmsis
cp -n $src/architectures/arm/cortexm.pack/include/cmsis/README*.txt $system/include/cmsis

cp -n $src/architectures/arm/cortexm.pack/include/cortexm/ExceptionHandlers.h $system/include/cortexm

cp -n $src/portable/diag.pack/include/diag/Trace.h $system/include/diag

for i in 'cortexm' 'diag' 'newlib'
do
    mkdir -p $system/src/$i
done

cp -n $src/architectures/arm/cortexm.pack/src/cortexm/*.c* $system/src/cortexm

cp -n $src/portable/diag.pack/src/diag/*.c* $system/src/diag
cp -n $src/architectures/arm/cortexm.pack/src/diag/*.c* $system/src/diag

cp -n $src/architectures/arm/cortexm.pack/src/newlib/*.c* $system/src/newlib
cp -n $src/portable/newlib.pack/src/newlib/*.c* $system/src/newlib
cp -n $src/portable/newlib.pack/src/newlib/README.txt $system/src/newlib


# stm32f4 cmsis
rm -rf $stm/stm32f4.pack
mkdir -p $stm/stm32f4.pack/include/cmsis
mkdir -p $stm/stm32f4.pack/src/cmsis
cp -Rn $src/vendors/stm/stm32f4.pack/include/cmsis/* $stm/stm32f4.pack/include/cmsis
cp -Rn $src/vendors/stm/stm32f4.pack/src/cmsis/* $stm/stm32f4.pack/src/cmsis

# stm32f4 hal drivers
rm -rf $stm/stm32f4-hal.pack
mkdir -p $stm/stm32f4-hal.pack
cp -Rn $src/vendors/stm/stm32f4-hal.pack/* $stm/stm32f4-hal.pack
