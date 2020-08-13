# Maintainer info

## How to make a new release

The plug-ins are published on the Eclipse download servers both as update
sites and as archives.

Development builds are published as p2 sub-folders like:

- https://download.eclipse.org/embed-cdt/builds/develop
- https://download.eclipse.org/embed-cdt/builds/master

When the content is stable, it is promoted as a pre-release and published as

- https://download.eclipse.org/embed-cdt/updates/neon-test

The final release is published in the main update site:

- https://download.eclipse.org/embed-cdt/updates/neon

For archiving purposes, the release is also published in a separate folder
for each version, with the archive in the top folder and the p2 repo as
a sub-folder

- https://download.eclipse.org/embed-cdt/releases/5.1.1/ilg.gnumcueclipse.repository-5.1.1-202007271621.zip
- https://download.eclipse.org/embed-cdt/releases/5.1.1/p2

## Create a new milestone

If not already done, create a new milestone.

- in the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues)
page, click the
[Milestones](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones)
button and add a
[new](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/new)
milestone. As title, use the current version, like _v5.1.2_.

## Fix issues

- be sure the `develop` branch is selected
- scan the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues)
list, and fix them. The commit message should be prefixed with the issue
number, like `[#122]`;
- mark all fixed issues as part of the new milestone;
- add a message like _fixed on 2020-01-10_;
- close the issues

## Build locally

When all issues are fixed, run the maven build locally:

```
$ mvn clean verify
```

Try the result in a child Eclipse.

## Push to GitHub

Be sure the repo is clean and push the `develop` branch to GitHub.

This will also trigger a CI job that will run a maven build.

## Trigger the Jenkins development build

- go to [https://ci.eclipse.org/embed-cdt/job/build-plug-ins/](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/)
- login (otherwise the next link is not visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds/develop](https://download.eclipse.org/embed-cdt/builds/develop)

## Install on a separate Eclipse

Test if the new build can be used as an update site, by installing from
`https://download.eclipse.org/embed-cdt/builds/develop` on a separate
Eclipse (not the one used for development).

## Merge to master

When ready, merge the develop commits to master.

## Trigger the Jenkins master build

- go to [https://ci.eclipse.org/embed-cdt/job/build-plug-ins/](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/)
- login (otherwise the next link is not visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds/master](https://download.eclipse.org/embed-cdt/builds/master)

## Publish the pre-release

Use the [make-pre-release-from-master]('https://ci.eclipse.org/embed-cdt/job/make-pre-release-from-master')
Jenkins job to copy the files from `builds/master` to `updates/neon-test`,
which is the public address for the pre-release.

Beta testers can install the pre-release from:

- `https://download.eclipse.org/embed-cdt/updates/neon-test`

## Publish the release

Use the [make-release-from-neon-test](https://ci.eclipse.org/embed-cdt/job/make-release-from-neon-test/)
Jenkins job to copy from `updates/neon-test` to `updates/neon` and `releases/<version>`.

In the versioned `releases` folder are stored both the release archives and
the expanded p2 repository,
which can be used in Eclipse to install new software.

The public update URL is:

- `https://download.eclipse.org/embed-cdt/updates/neon`

## Create a release record

In the official [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt)
page, click the **Create a new release** link in the right sidebar.

Copy/paste content from the previous release.

To get the changes, scan the Git log and add new entries, grouped by days.

```console
$ git log --pretty='%cd * %h - %s' --date=short
```

To get the release content, download the archive and do a
`ls -L features plugins`.

## Update the Eclipse Marketplace records

- go to [Eclipse Marketplace](https://marketplace.eclipse.org/content/eclipse-embedded-cdt)
- log in
- click **Edit**
- update version number, minimum Eclipse versions.

---

TODO: update

## Build a local test version of the Eclipse packages

The Eclipse Packaging Project allows to create complete Eclipse packages
for all relevant platforms.

- clone the `gnu-mcu-eclipse/org.eclipse.epp.packages` project
- select the `master` branch
- merge from upstream
- merge into the local GME branch (like `master-gme`)
- edit `org.eclipse.epp.packages.git/releng/org.eclipse.epp.config/parent/pom.xml`
  - update the latest version `<gnumcueclipse.version>4.3.1</gnumcueclipse.version>`
  - temporarily update the site URL to the test site `<gnumcueclipse.repository>http://gnu-mcu-eclipse.netlify.com/v4-neon-updates-test</gnumcueclipse.repository>`
- **remove** the `archive` folder
- run the `org.eclipse.epp.packages.git/scripts/build.mac.command` script
- check if the retrieved plug-ins versions match the expected versions; if not, check if the update site was published correctly and the URL is right;
- the result should be in the `archive` folder, including the computed `.sha` values.

## Test the resulted packages

- install the packages on macOS, Windows & GNU/Linux
- create ARM and RISC-V projects 
- build & run.

## Eclipse Packaging

The Eclipse Packaging Project allows to create complete Eclipse packages for all relevant platforms.

### Build

- in the `gnu-mcu-eclipse/org.eclipse.epp.packages` project
- edit `org.eclipse.epp.packages.git/releng/org.eclipse.epp.config/parent/pom.xml`
  - update the site URL to the release site `<gnumcueclipse.repository>http://gnu-mcu-eclipse.netlify.com/v4-neon-updates</gnumcueclipse.repository>`
- **remove** the `archive` folder
- run the `org.eclipse.epp.packages.git/scripts/build.mac.command` script
- check if the retrieved plug-ins versions match the expected versions; if not, check if the update site was published correctly and the URL is right
- the result should be in the `archive` folder, including the computed `.sha` values
- push the `gnu-mcu-eclipse/org.eclipse.epp.packages` project.

### Create the EPP GitHub release

- be sure the `org.eclipse.epp.packages.git` is up to date and pushed
- go to the [GitHub Releases](https://github.com/eclipse-embed-cdt/org.eclipse.epp.packages/releases) page
- click **Draft a new release**
- name the tag like **v4.4.2-20180930-2018-09**; mind the `-` in the middle, the short date and the 2018-09 which is the official Eclipse release name 
- select the corresponding GME branch, like **master-gme**
- name the release like **GNU MCU Eclipse IDE for C/C++ Developers 2018-09 20180930**; mind the short date
- as first line of the description, copy the download badge
```
[![Github Releases (by Release)](https://img.shields.io/github/downloads/eclipse-embed-cdt/org.eclipse.epp.packages/v4.3.1-20180110-o2/total.svg)]({{ site.baseurl }}/blog/2018/01/10/plugins-v4.3.1-201801092051-released/)
```
- update the URL to the actual release post
- copy the description from a previous release, and update 
- copy the SHA numbers from the maven console
- **attach binaries** (drag and drop from the archives folder will do it)
- click the **Publish Release** button

### Update the main release

- go to the [GitHub Releases](https://github.com/eclipse-embed-cdt/eclipse-plugins/releases) page
- copy the text related to the EPP package from a previous release
- paste to the current release, updating the link

## Share on Twitter

- go to the new post and follow the Tweet link
- copy the content to the clipboard
- DO NOT click the Tweet button here, it'll not use the right account
- in a separate browser windows, open [TweetDeck](https://tweetdeck.twitter.com/)
- using the `@gnu_mcu_eclipse` account, paste the content
- click the Tweet button
