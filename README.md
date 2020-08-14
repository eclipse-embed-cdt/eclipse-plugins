[![Eclipse License](https://img.shields.io/badge/license-EPL--2.0-brightgreen.svg)](https://github.com/eclipse-embed-cdt/eclipse-plugins/blob/master/LICENSE)
[![Build Status](https://github.com/eclipse-embed-cdt/eclipse-plugins/workflows/CI/badge.svg)](https://github.com/eclipse-embed-cdt/eclipse-plugins/actions)

# The Eclipse Embedded CDT plug-ins

These are the Eclipse projects used to build the
[Eclipse Embedded CDT](http://www.eclipse.org/embed-cdt) plug-ins,
formerly the [GNU MCU/ARM Eclipse](http://gnu-mcu-eclipse.github.io) plug-ins.

## How to install

### Eclipse Packages

For new installs, the preferred method is via
[Eclipse IDE for Embedded C/C++ Developers](https://github.com/gnu-mcu-eclipse/org.eclipse.epp.packages/releases),
which packs the official **Eclipse IDE for C/C++ Developers** release with
all **Eclipse Embedded CDT plug-ins** already installed.

### Eclipse Marketplace

For existing Eclipse instances, the recommended install method is via
Eclipse Marketplace; search for _Embedded CDT_.

### Eclipse Update Sites

- Stable: `https://download.eclipse.org/embed-cdt/updates/neon`  
   This is the official release; it is also referred by the Eclipse
   Marketplace and the same content is packed as **Eclipse IDE for Embedded C/C++ Developers**.
- Pre-release versions: `https://download.eclipse.org/embed-cdt/updates/neon-test`  
   Usually this site should be safe, but use it with caution.
- Development versions: `https://download.eclipse.org/embed-cdt/builds/develop/p2`
   Sometimes you can use this site to test features that are not
   completely implemented, or that might change before a final version is released.  
   **Note:** This site is not always functional; use it carefully,
   back up your workspace, and do not use it for production projects.

## Maintainer info

Plese read the separate [README-MAINTAINER.md](README-MAINTAINER.md) page.

## Run-time dependencies

The minimum requirements for running the Eclipse Embedded CDT plug-ins are:

- JavaSE-1.8 or later
- CDT 9.2.1 (Neon.3)

The recommended:

- JavaSE-11 or later
- Eclipse 2019-12 or later

Please note that since 2020-03, installing CDT requires Java 11.

## License

Copyright (c) 2012, 2020 Liviu Ionescu and others.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
[https://www.eclipse.org/legal/epl-2.0/](https://www.eclipse.org/legal/epl-2.0/).

SPDX-License-Identifier: EPL-2.0
