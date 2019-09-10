Publish to Bintray
==================

Double click on publish-v4-neon-updates-experimental.command (& Co.).

The BINTRAY_API_KEY should be available in the environment (.bash_profile).


Publish to SourceForge (DEPRECATED)
===================================

Double click on publish-updates-test.command or publish-updates.command


Vectors
=======

Always use the sed script to get the vectors from the assembly file:

$ sed -f vectors.sed startup_stm32f411xe.s >x.s


GNU C/C++ Formatting Style
==========================

Available from [Gist](https://gist.github.com/ilg-ul/b14101ce7a58a06f3346).


Copy from µOS++ (DEPRECATED)
============================

$ cd .../scripts
$ bash copy-from-micro-os.sh


