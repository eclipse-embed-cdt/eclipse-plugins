[![Build Status](https://travis-ci.org/gnuarmeclipse/plug-ins.svg?branch=develop)](https://travis-ci.org/gnuarmeclipse/plug-ins)

# The GNU ARM Eclipse plug-ins

These are the Eclipse projects used to build the [GNU ARM Eclipse](http://gnuarmeclipse.github.io) plug-ins.

## How to install

The recommended install method is via Eclipse Marketplace.

For manual installs, the stable version update site is:

- http://gnuarmeclipse.sourceforge.net/updates

## How to build

### Prerequisites

- JavaSE-1.8
- Maven 3

### Command line

```
$ git clone --branch=master https://github.com/gnuarmeclipse/plug-ins.git gnuarmeclipse/plug-ins.git
$ cd gnuarmeclipse/plug-ins.git
$ mvn clean install
```

The result is a p2 repository, in `ilg.gnuarmeclipse-repository/target/repository`.

### Eclipse

The Eclipse build is described in the [project web](http://gnuarmeclipse.github.io/developer/build-procedure/).

## Run-time dependencies

The minimum requirements for running the GNU ARM Eclipse plug-ins are:

- JavaSE-1.7
- CDT 8.6.0 (Luna SR2)

Recommended:

- JavaSE-1.8-111 or later
- Eclipse Mars.2

## Developer

https://www.eclipse.org/cdt/developers.php

### Formatting

https://wiki.eclipse.org/CDT/policy

- use tabs, at 4
- code line size: 120 chars
- comments line size: 80 chars
