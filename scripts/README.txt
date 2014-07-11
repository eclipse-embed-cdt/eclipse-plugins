
Publish to SourceForge
======================

Double click on publish-updates-test.command or publish-updates.command


Vectors
=======

Always use the sed script to get the vectors from the assembly file:

$ sed -f vectors.sed startup_stm32f411xe.s >x.s


Copy from µOS++
===============

$ cd .../scripts
$ bash copy-from-micro-os.sh


