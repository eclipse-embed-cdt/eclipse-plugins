[![Build Status](https://github.com/eclipse-embed-cdt/eclipse-plugins/workflows/CI/badge.svg)](https://github.com/eclipse-embed-cdt/eclipse-plugins/actions)

# The Eclipse Embedded CDT plug-ins

These are the Eclipse projects used to build the
[Eclipse Embedded CDT](http://www.eclipse.org/embed-cdt) plug-ins,
formerly the [GNU MCU Eclipse](http://gnu-mcu-eclipse.github.io) plug-ins.

## How to install

### Eclipse Packages

For new installs, the preferred method is via
[Eclipse IDE for Embedded C/C++ Developers](https://github.com/gnu-mcu-eclipse/org.eclipse.epp.packages/releases),
which packs the official **Eclipse IDE for C/C++ Developers** release with
all **Eclipse Embedded CDT plug-ins** already installed.

### Eclipse Marketplace

For existing Eclipse instances, the recommended install method is via
Eclipse Marketplace; search for **GNU MCU Eclipse**.

## Manual (Update Sites)

- Stable: https://download.eclipse.org/embed-cdt/updates/neon  
   This is the official release; it is also referred by the Eclipse
   Marketplace and the same content is packed as **Eclipse IDE for Embedded C/C++ Developers**.
- Pre-release versions: https://download.eclipse.org/embed-cdt/updates/neon-test
   Usually this site should be safe to use, but use it with caution.
- Development versions: https://download.eclipse.org/embed-cdt/builds/develop
   Sometimes you can use this site to test some features that are not
   completely implemented, or that might change before a final version is released.  
   **Note:** This site is not always functional; use it carefully,
   back up your workspace, and do not use it for production projects.

## Run-time dependencies

The minimum requirements for running the Eclipse Embedded CDT plug-ins are:

- JavaSE-1.8-111 or later
- CDT 9.2.1 (Neon.3)

Recommended:

- JavaSE-1.11 or later
- Eclipse 2019-12

Please note that since 2020-03, installing CDT requires Java 11.
