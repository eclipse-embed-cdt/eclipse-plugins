# The GNU ARM Eclipse plug-ins

## Build status

[![Build Status](https://travis-ci.org/gnuarmeclipse/plug-ins.svg?branch=master)](https://travis-ci.org/gnuarmeclipse/plug-ins)

These are the Eclipse projects used to build the [GNU ARM Eclipse](http://gnuarmeclipse.github.io) plug-ins.

## Update site

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

The resulted plug-ins minimum requirements are

- JavaSE-1.7
- CDT 8.6.0 (Luna SR2)

