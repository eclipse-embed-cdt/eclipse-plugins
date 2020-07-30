# How to build & publish

## Build the plug-ins

- go to [https://ci.eclipse.org/embed-cdt/job/build/](https://ci.eclipse.org/embed-cdt/job/build/)
- click the **Scan Multibranch Pipeline Now**
- the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds](https://download.eclipse.org/embed-cdt/builds)

Developer builds will result in a [builds/develop](https://download.eclipse.org/embed-cdt/builds) folder.

When the code is ready for a release, build the master branch.


## Publish the pre-release

Use the [make-pre-release-from-master]('https://ci.eclipse.org/embed-cdt/job/make-pre-release-from-master') 
Jenkins job to copy the files from `builds/master` to `updates/neon-test`, which is the public address 
for the pre-release.

Users can test the pre-release via

- https://download.eclipse.org/embed-cdt/updates/neon-test

## Publish the release

Use the [make-release-from-neon-test](https://ci.eclipse.org/embed-cdt/job/make-release-from-neon-test/) 
Jenkins job to copy from `updates/neon-test` to `updates/neon` and `releases/<version>`.

In the versioned `releases` folder are stored both the release archives and the expanded p2 repository,
which can be used in Eclipse to install new software.

The public update URL is:

- https://download.eclipse.org/embed-cdt/updates/neon
