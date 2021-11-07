[![Eclipse License](https://img.shields.io/badge/license-EPL--2.0-brightgreen.svg)](https://github.com/eclipse-embed-cdt/eclipse-plugins/blob/master/LICENSE)
[![Build Status](https://github.com/eclipse-embed-cdt/eclipse-plugins/workflows/CI/badge.svg)](https://github.com/eclipse-embed-cdt/eclipse-plugins/actions)

# The Eclipse Embedded CDT plug-ins

These are the Eclipse projects used to build the
[Eclipse Embedded CDT](https://eclipse-embed-cdt.github.io) plug-ins,
formerly the [GNU MCU/ARM Eclipse](http://gnu-mcu-eclipse.github.io) plug-ins.

The project is now part of the
[Eclipse Foundation](http://www.eclipse.org/embed-cdt/) and is hosted on
[GitHub](https://github.com/eclipse-embed-cdt/).

## How to install

### Eclipse Packages

For new installs, the preferred method is via
[Eclipse IDE for Embedded C/C++ Developers](https://www.eclipse.org/downloads/packages/)
which packs the official **Eclipse IDE for C/C++ Developers** release with
all **Eclipse Embedded CDT plug-ins** already installed.

Older packages are available from GitHub
[Releases](https://github.com/eclipse-embed-cdt/org.eclipse.epp.packages/releases/).

### Eclipse Marketplace

For existing Eclipse instances, the recommended install method is via
Eclipse Marketplace; search for _Embedded CDT_.

### Eclipse Update Sites

- Stable: `https://download.eclipse.org/embed-cdt/updates/v6/`  
   This is the official release; it is also referred by the Eclipse
   Marketplace and the same content is packed as **Eclipse IDE for Embedded C/C++ Developers**.
- Pre-release versions: `https://download.eclipse.org/embed-cdt/updates/v6-test/`  
   Usually this site should be safe, but use it with caution.
- Development versions: `https://download.eclipse.org/embed-cdt/builds/develop/p2`
   Sometimes you can use this site to test features that are not
   completely implemented, or that might change before a final version is released.  
   **Note:** This site is not always functional; use it carefully,
   back up your workspace, and do not use it for production projects.

## Run-time dependencies

Starting with v6.x, the minimum requirements for running the Eclipse
Embedded CDT plug-ins are:

- JavaSE-11 or later
- CDT 9.2.1 (Neon.3)

The recommended:

- JavaSE-11 or later
- Eclipse 2020-09 or later

## Support

For support, please check the
[web support](https://eclipse-embed-cdt.github.io/support/) page.

## Maintainer info

Plese read the separate [README-MAINTAINER](README-MAINTAINER.md) page.

## License

Copyright (c) 2012, 2021 Liviu Ionescu and others.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
[https://www.eclipse.org/legal/epl-2.0/](https://www.eclipse.org/legal/epl-2.0/).

SPDX-License-Identifier: EPL-2.0
