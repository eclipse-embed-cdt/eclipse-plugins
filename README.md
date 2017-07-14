[![GitHub release](https://img.shields.io/github/release/gnu-mcu-eclipse/eclipse-plugins.svg)](https://github.com/gnu-mcu-eclipse/eclipse-plugins/releases/latest) [![Github Releases](https://img.shields.io/github/downloads/gnu-mcu-eclipse/eclipse-plugins/latest/total.svg)](https://github.com/gnu-mcu-eclipse/eclipse-plugins/releases/latest) [![Github Releases](https://img.shields.io/github/downloads/gnu-mcu-eclipse/eclipse-plugins/total.svg)](https://github.com/gnu-mcu-eclipse/eclipse-plugins/releases/latest) [![SourceForge](https://img.shields.io/sourceforge/dt/gnuarmeclipse.svg?label=SF%20downloads)](https://sourceforge.net/projects/gnuarmeclipse/files/) [![Build Status](https://travis-ci.org/gnu-mcu-eclipse/eclipse-plugins.svg?branch=develop)](https://travis-ci.org/gnu-mcu-eclipse/eclipse-plugins) 

# The GNU MCU Eclipse plug-ins

These are the Eclipse projects used to build the [GNU MCU Eclipse](http://gnu-mcu-eclipse.github.io) plug-ins.

## How to install

The recommended install method is via Eclipse Marketplace.

For manual installs, the stable version update site is:

- http://gnu-mcu-eclipse.netlify.com/v4-neon-updates

## How to build

### Prerequisites

- JavaSE-1.8
- Maven 3

### Command line

```
$ git clone --branch=master https://github.com/gnu-mcu-eclipse/eclipse-plugins.git gnu-mcu-eclipse/eclipse-plugins.git
$ cd gnu-mcu-eclipse/eclipse-plugins.git
$ mvn -Dtycho.localArtifacts=ignore clean verify
```

The result is a p2 repository, in `ilg.gnumcueclipse-repository/target/repository`.

### Eclipse

The Eclipse build is described in the [project web](http://gnu-mcu-eclipse.github.io/developer/build-procedure/).

## Run-time dependencies

The minimum requirements for running the GNU MCU Eclipse plug-ins are:

- JavaSE-1.8-111 or later
- CDT 9.2.1 (Neon.3)

Recommended:

- JavaSE-1.8-111 or later
- Eclipse Neon.3

## Developer

https://www.eclipse.org/cdt/developers.php

### Formatting

https://wiki.eclipse.org/CDT/policy

- use tabs, at 4
- code line size: 120 chars
- comments line size: 80 chars
