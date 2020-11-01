# Contributions to Eclipse Embedded CDT

The Eclipse Embedded CDT project is hosted on GitHub as 
[eclipse-embed-cdt/eclipse-plugins](https://github.com/eclipse-embed-cdt/eclipse-plugins)

TODO: update URLs

There are many ways you can contribute to this project, and all
contributions are highly appreciated.

## Post your findings & questions

If you have interesting use cases, if you have custom configurations,
and generally if you have any experience that you want to share with
others, please use the
[Tapatalk forums](https://www.tapatalk.com/groups/xpack/).

## Submit bug reports & enhancement requests

If you are convinced you identified a bug (if you have doubts,
use the forum), or you have a pertinent suggestion how to enhance
the **Eclipse Embedded CDT plug-ins**, please use the
[GitHub Issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues)
tracker.

Be sure you report only issues related to the **Eclipse Embedded CDT plug-ins**.
**DO NOT** use it for issues related to the tools (QEMU, OpenOCD, Build
tools, etc), which should be addressed to their corresponding trackers.
Web content issues should be addressed to the
[Web Issues](https://github.com/gnu-mcu-eclipse/gnu-mcu-eclipse.github.io/issues/)
tracker. J-Link specific questions should be addresses to SEGGER support.

Before reporting issues, please check the list of
[Known Issues](http://gnu-mcu-eclipse.github.io/support/known-issues/)
and the [FAQ](http://gnu-mcu-eclipse.github.io/support/faq/).

When entering a new issue, be sure you provide:

* plug-ins version
* Eclipse version
* Java version
* operating system
* toolchain version
* the **detailed and complete list of steps to reproduce the bug** (mandatory)

Please understand that without being able to reproduce the bug we cannot
identify your problem.

To be allowed to enter issues, you need to first login to GitHub.

Note: the text should respect the markdown syntax; preview the message
before posting and correct if it does not look as  expected, especially
inline code or other quoted text.

## Submit pull requests

The most useful contribution to the project is to submit code.
GitHub greatly simplifies this process, by using pull requests.

For those not familiar with GitHub workflow, reading the official
[Using pull requests](https://help.github.com/articles/using-pull-requests/)
page can be of great help.

In short, the process is something like this:

* fork the Git repository to your own account
* create a custom branch
* make the desired changes
* commit to the custom branch
* submit the pull request to the `develop` branch

## How to build

### Prerequisites

* JavaSE-11 or later
* Maven 3

### Command line

```bash
$ git clone --branch=master --recurse-submodules https://github.com/eclipse-embed-cdt/eclipse-plugins.git eclipse-plugins.git
$ cd eclipse-plugins.git
$ mvn clean verify
```

The result is a p2 repository:

* `repositories/org.eclipse.embedcdt-repository/target/repository`

Note: in some older versions, maven complained something about local
artefacts; in case this happens, add `-Dtycho.localArtifacts=ignore` when
invoking maven.

### Eclipse

The Eclipse build is described in the
[project web](http://gnu-mcu-eclipse.github.io/developer/build-procedure/).

## Developer

https://www.eclipse.org/cdt/developers.php

### Formatting

https://wiki.eclipse.org/CDT/policy

* use tabs, at 4
* code line size: 120 chars
* comments line size: 80 chars

Currently these settings are provided by the default Eclipse Java formatter.

## Legal

Contributors should agree to the Eclipse Contributor Agreement
(https://www.eclipse.org/legal/ECA.php).

