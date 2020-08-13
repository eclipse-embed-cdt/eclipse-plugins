# The Jenkins scripts

The main Jenkins build script is `builds.Jenkins`, which also refers to
`builds-upload.sh`.

The job does not start automatically, but needs to be triggered manually
when the content is ready for a public release.(the
**Scan Multibranch Pipeline Now** link in the
[build-plug-ins](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/) page).

For the build and publish procedure, please see the
../../README-MAINTAINER.md file.
