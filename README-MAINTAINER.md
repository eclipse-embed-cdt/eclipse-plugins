# Maintainer info

## Development workflow

The plug-ins are published on the Eclipse download servers both as update
sites and as archives.

Development builds are published as p2 sub-folders like:

- https://download.eclipse.org/embed-cdt/builds/develop/p2/
- https://download.eclipse.org/embed-cdt/builds/master/p2/

When the content is stable, it is promoted as a pre-release and published as:

- https://download.eclipse.org/embed-cdt/updates/neon-test/

The final release is published in the main update site:

- https://download.eclipse.org/embed-cdt/updates/neon/

For archiving purposes, the release is also published in a separate folder
for each version, with the archive in the top folder and the p2 repo as
a sub-folder

- https://download.eclipse.org/embed-cdt/releases/5.1.1/ilg.gnumcueclipse.repository-5.1.1-202007271621.zip
- https://download.eclipse.org/embed-cdt/releases/5.1.1/p2/

Packages are published at:

- https://download.eclipse.org/embed-cdt/packages/
- https://download.eclipse.org/embed-cdt/packages-test/

The official download page is

- https://projects.eclipse.org/projects/iot.embed-cdt/downloads/

## How to fix issues

Normally all changes should be done as a result of a ticket registered as
a GitHub Issue.

### Create a new milestone

If not already done, create a new milestone.

- in the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues)
page, click the
[Milestones](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones)
button and add a
[new](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/new)
milestone. As title, use the current version, like _v5.1.2_.

### Fix issues

- be sure the `develop` branch is selected
- scan the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues)
list, and fix them. The commit message should be prefixed with the issue
number, like `[#122]`;
- mark all fixed issues as part of the new milestone;
- add a message like _fixed on 2020-01-10_;
- close the issues

### Build locally

After fixing issues, run the maven build locally:

```
$ mvn clean verify
```

Start a Debug/Run session and try the result in a child Eclipse.

### Push to GitHub

Be sure the repo is clean and push the `develop` branch to GitHub.

This will also trigger a CI job that will run a maven build.

### Trigger the Jenkins development build

- go to [https://ci.eclipse.org/embed-cdt/job/build-plug-ins/](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/)
- login (otherwise the next link is not visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds/develop/p2](https://download.eclipse.org/embed-cdt/builds/develop/p2)

### Install on a separate Eclipse

Test if the new build can be used as an update site, by installing it
on a separate Eclipse (not the one used for development); use the URL:

- `https://download.eclipse.org/embed-cdt/builds/develop/p2`

## How to make a new release

### Merge to master

When ready, merge the `develop` branch into `master`, and push them to GitHub.

Wait for the CI to confirm that the build passed.

### Trigger the Jenkins master build

- go to [https://ci.eclipse.org/embed-cdt/job/build-plug-ins/](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/)
- login (otherwise the next link will not be visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds/master/p2](https://download.eclipse.org/embed-cdt/builds/master/p2)

### Publish the pre-release

- go to https://ci.eclipse.org/embed-cdt/job/build-plug-ins/
- login (otherwise the next link is not visible!)
- use the [make-pre-release-from-master]('https://ci.eclipse.org/embed-cdt/job/make-pre-release-from-master')
Jenkins job to copy the files from `builds/master` to `updates/neon-test`,
which is the public location for the pre-release
- click the **Build Now** link

Beta testers can install the pre-release from:

- `https://download.eclipse.org/embed-cdt/updates/neon-test`

### Publish the release

- go to https://ci.eclipse.org/embed-cdt/job/build-plug-ins/
- login (otherwise the next link is not visible!)
- use the [make-release-from-master](https://ci.eclipse.org/embed-cdt/job/make-release-from-master/)
Jenkins job to copy from `builds/master` to `updates/neon` and
`releases/<version>`
- click the **Build Now** link

The `releases` folder includes both the release archives and the expanded
p2 repository.

The `updates/neon` includes only the expanded p2 repository, for the archives
see the `releases` folder.

Both can be used in Eclipse to Install New Software.

The public update URLs are:

- `https://download.eclipse.org/embed-cdt/updates/neon`
- `https://download.eclipse.org/embed-cdt/releases/<version>/p2`

### Create a release record

In the official [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt)
page, click the **Create a new release** link in the right sidebar.

Copy/paste content from the previous release.

To get the list oc changes, scan the Git log and add new entries,
grouped by days.

```console
$ git log --pretty='%cd * %h - %s' --date=short
```

To get the release content, download the archive and do a
`ls -L features plugins`.

### Update the Eclipse Marketplace records

- go to [Eclipse Marketplace](https://marketplace.eclipse.org/content/eclipse-embedded-cdt)
- log in
- click **Edit**
- update version number, minimum Eclipse versions.

### Create a package

TODO: explain how to edit/update the EPP project.

- go to https://ci.eclipse.org/embed-cdt/job/build-plug-ins/
- login (otherwise the next link is not visible!)
- use the [make-packages](https://ci.eclipse.org/embed-cdt/job/make-packages/)
Jenkins job to maven build the `embed-cdt` and `embed-cdt-develop` branches and
copy the result in `packages/<version>.

The direct download URL is

- `https://download.eclipse.org/embed-cdt/packages`

### Share on Twitter

- go to the new post and follow the Tweet link
- copy the content to the clipboard
- DO NOT click the Tweet button here, it'll not use the right account
- in a separate browser windows, open [TweetDeck](https://tweetdeck.twitter.com/)
- using the `@embed-cdt` account, paste the content
- click the Tweet button
