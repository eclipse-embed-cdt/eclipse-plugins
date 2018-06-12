[![GitHub release](https://img.shields.io/github/release/gnu-mcu-eclipse/eclipse-plugins.svg)](https://github.com/gnu-mcu-eclipse/eclipse-plugins/releases/latest) 
[![Github Releases](https://img.shields.io/github/downloads/gnu-mcu-eclipse/eclipse-plugins/latest/total.svg)](https://github.com/gnu-mcu-eclipse/eclipse-plugins/releases/latest) 
[![Github Releases](https://img.shields.io/github/downloads/gnu-mcu-eclipse/eclipse-plugins/total.svg)](https://github.com/gnu-mcu-eclipse/eclipse-plugins/releases/latest) 
[![SourceForge](https://img.shields.io/sourceforge/dt/gnuarmeclipse.svg?label=SF%20downloads)](https://sourceforge.net/projects/gnuarmeclipse/files/) 
[![Build Status](https://travis-ci.org/gnu-mcu-eclipse/eclipse-plugins.svg?branch=develop)](https://travis-ci.org/gnu-mcu-eclipse/eclipse-plugins) 

# The GNU MCU Eclipse plug-ins

These are the Eclipse projects used to build the [GNU MCU Eclipse](http://gnu-mcu-eclipse.github.io) plug-ins.



## How to install

For new installs, the preferred method is via [GNU MCU Eclipse IDE for C/C++ Developers ](https://github.com/gnu-mcu-eclipse/org.eclipse.epp.packages/releases), which packs the official **Eclipse IDE for C/C++ Developers** release with all **GNU MCU Eclipse plug-ins** already installed. 

For existing Eclipse instances, the recommended install method is via Eclipse Marketplace; search for **GNU MCU Eclipse**.

## Update Sites (for manual installs)

 * Stable: http://gnu-mcu-eclipse.netlify.com/v4-neon-updates  
    This is the official release; it is also referred by the Eclipse Marketplace and the same content is packed as **Eclipse IDE for C/C++ Developers**.
 * Pre-release versions: http://gnu-mcu-eclipse.netlify.com/v4-neon-updates-test  
    Usually this site should be safe to use, but use it with caution.
 * Experimental versions: http://gnu-mcu-eclipse.netlify.com/v4-neon-updates-experimental  
    Sometimes you can use this site to test some features that are not completely implemented, or that might change before a final version is released.  
**Note:** This site is not always updated; use it carefully, back up your workspace, and do not use it for production projects.

## How to build

### Prerequisites

* JavaSE-1.8
* Maven 3

### Command line

```bash
$ git clone --branch=master --recurse-submodules https://github.com/gnu-mcu-eclipse/eclipse-plugins.git eclipse-plugins.git
$ cd eclipse-plugins.git
$ mvn clean verify
```

The result are two p2 repositories:

* `repositories/ilg.gnumcueclipse.repository/target/repository`
* `repositories/ilg.gnumcueclipse.riscv.repository/target/repository`

Note: in some older versions, maven complained something about local artefacts; in case this happens, add `-Dtycho.localArtifacts=ignore` when invoking maven.

### Eclipse

The Eclipse build is described in the [project web](http://gnu-mcu-eclipse.github.io/developer/build-procedure/).

## Run-time dependencies

The minimum requirements for running the GNU MCU Eclipse plug-ins are:

* JavaSE-1.8-111 or later
* CDT 9.2.1 (Neon.3)

Recommended:

* JavaSE-1.8-111 or later
* Eclipse Neon.3

## Developer

https://www.eclipse.org/cdt/developers.php

### Formatting

https://wiki.eclipse.org/CDT/policy

* use tabs, at 4
* code line size: 120 chars
* comments line size: 80 chars

Currently these settings are provided by the default Eclipse Java formatter.


## Contributors

This project exists thanks to all the people who contribute. [[Contribute](CONTRIBUTING.md)].

<a href="graphs/contributors"><img src="https://opencollective.com/gnu-mcu-eclipse/contributors.svg?width=890&button=false" /></a>



