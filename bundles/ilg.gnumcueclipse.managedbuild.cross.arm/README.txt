Supported arch & cpu:

[2014-02-20]
- reordered lexicographically
- updated from 4.8

-march
	armv2 (DEP 6.2 2017-02-20)
	armv2a (DEP 6.2 2017-02-20)
	armv3 (DEP 6.2 2017-02-20)
	armv3m (DEP 6.2 2017-02-20)
	armv4 
	armv4t 
	armv5 
	armv5e 
	armv5t 
	armv5te 
	armv6 
	armv6-m 
	armv6j 
	 	armv6k 	-(4.9)
	 	armv6s-m 	+[2014-02-20, accepted, but not in 4.8] -(4.9)
	armv6t2 
	armv6z 
	armv6zk 
	armv7 
	armv7-a 
	armv7-m 
	armv7-r 
	armv7e-m 
	armv7ve			+[2015-05-22]
	armv8-a 
	armv8-a+crc 	+[2015-05-22]
	armv8-m.base       +[2017-01-14]
	armv8-m.main       +[2017-01-14]
	armv8-m.main+dsp   +[2017-01-14]
	armv8.1-a			+[2017-02-20]
	armv8.1-a+crc		+[2017-02-20]
	
		ep9312 (no longer listed in 6.2)
	iwmmxt 
	iwmmxt2 
	native

AArch64 -march=arch{+[no]feature} (not yet supported by the plug-in)
	armv8-a
	armv8.1-a
	native
	
-mtune/-mcpu
	arm1020e 		
	arm1020t 
	arm1022e 
	arm1026ej-s 
	arm10e 
	arm10tdmi 
	arm1136j-s 
	arm1136jf-s 
	arm1156t2-s 
	arm1156t2f-s 
	arm1176jz-s 
	arm1176jzf-s 	
	arm2 
	arm250 
	arm3 
	arm6 
	arm60 
	arm600 
	arm610		+[2014-02-20]
 	arm620 
	arm7 
	arm70 
	arm700 
	arm700i 
	arm710 
	arm7100 
	arm710c 
	arm710t 
	arm720 
	arm720t 
	arm740t 
	arm7500 
	arm7500fe 
	arm7d 
	arm7di 
	arm7dm 
	arm7dmi 
	arm7m 
	arm7tdmi 
	arm7tdmi-s 
	arm8 
	arm810 
	arm9 
	arm920 
	arm920t 
	arm922t 
	arm926ej-s 
	arm940t 
	arm946e-s 
	arm966e-s 
	arm968e-s 
	arm9e 
	arm9tdmi 
	cortex-a12 		+[2015-05-22]
	cortex-a15 
	cortex-a17 		+[6.2 2017-02-20]
	cortex-a32		+[6.2 2017-02-20]
		cortex-a35 (64-bits) 		+[6.2 2017-02-20]
		cortex-a53 (64-bits)		+? 
		cortex-a57 (64-bits)		+?
	cortex-a5 
	cortex-a7 
		cortex-a72 (64-bits)         [2017-01-14]
	cortex-a8 
	cortex-a9 
	cortex-m0 
	cortex-m0.small-multiply		+[2015-05-22] 
	cortex-m0plus 
	cortex-m0plus.small-multiply	+[2015-05-22] 
	cortex-m1 
	cortex-m1.small-multiply		+[2015-05-22]
	cortex-m23 		+[6.2 2017-02-20]
	cortex-m3 
	cortex-m33 		+[6.2 2017-02-20]
	cortex-m4 
	cortex-m7		+[2014-11-20] 
	cortex-r4 
	cortex-r4f 
	cortex-r5 		+[2014-02-20]
	cortex-r7 		+[2014-02-20]
	cortex-r8          [2017-01-14]
	ep9312 
		exynos-m1 (64-bits)         [2017-01-14]
	fa526 
	fa606te 
	fa626 
	fa626te 
	fa726te 
	fmp626 
	generic-armv7-a +[2014-02-20]
	iwmmxt 
	iwmmxt2 
	marvell-f		? not in 4.8, ... 6.2
	marvell-pj4 
	mpcore 
	mpcorenovfp 
	native 
		qdf24xx (64-bits)		+[6.2 2017-02-20]
	strongarm 
	strongarm110 
	strongarm1100 
	strongarm1110 
		xgene1 (64-bits)           [2017-01-14]
	xscale

AArch -mtune=  (not yet supported by the plug-in)
	generic 
	cortex-a35
	cortex-a53
	cortex-a57
	cortex-a72
	exynos-m1
	qdf24xx
	thunderx
	xgene1
	
-mfpu=
	crypto-neon-fp-armv8
		fpa (deprecated)
		fpe2 (deprecated)
		fpe3 (deprecated)
	fp-armv8
	fpv4-sp-d16
	fpv5-d16
	fpv5-sp-d16
		maverick (deprecated)
	neon
	neon-fp16
	neon-fp-armv8
	neon-vfpv4
	vfp
	vfpv3
	vfpv3-d16
	vfpv3-d16-fp16
	vfpv3-fp16
	vfpv3xd
	vfpv3xd-fp16 
	vfpv4
	vfpv4-d16


[2017-02-20]

- updated for 6.2
- added -mfpu in this file, and sorted definitions in xml