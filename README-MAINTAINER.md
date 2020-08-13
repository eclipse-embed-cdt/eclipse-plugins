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

## Create a package

TODO

## Share on Twitter

- go to the new post and follow the Tweet link
- copy the content to the clipboard
- DO NOT click the Tweet button here, it'll not use the right account
- in a separate browser windows, open [TweetDeck](https://tweetdeck.twitter.com/)
- using the `@gnu_mcu_eclipse` account, paste the content
- click the Tweet button
