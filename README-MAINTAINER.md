# Maintainer info

## Project repository

The project is hosted on GitHub:

- https://github.com/eclipse-embed-cdt/eclipse-plugins

To clone it, be sure the submodules are also cloned:

```
git clone --recurse-submodule https://github.com/eclipse-embed-cdt/eclips
e-plugins \
  eclipse-plugins.git
```

## Prerequisites

Production builds are run via
[Jenkins](https://ci.eclipse.org/embed-cdt/)
on the Eclipse Foundation infrastructure (CBI); for local builds, use the
[Adopt OpenJDK 11](https://adoptopenjdk.net) and maven 3.6.

## Development workflow

The plug-ins are published on the Eclipse download servers both as update
sites and as archives.

Development builds are published as p2 sub-folders like:

- https://download.eclipse.org/embed-cdt/builds/develop/p2/
- https://download.eclipse.org/embed-cdt/builds/master/p2/

When the content is stable, it is promoted as a pre-release and published as:

- https://download.eclipse.org/embed-cdt/updates/v6-test/

The final release is published in the main update site:

- https://download.eclipse.org/embed-cdt/updates/v6/

For archiving purposes, the release is also published in a separate folder
for each version, with the archive in the top folder and the p2 repo as
a sub-folder

- https://download.eclipse.org/embed-cdt/releases/6.0.0/org.eclipse.embedcdt.repository-6.0.0-202010292017.zip
- https://download.eclipse.org/embed-cdt/releases/6.0.0/p2/

Packages are published at:

- https://download.eclipse.org/embed-cdt/packages/
- https://download.eclipse.org/embed-cdt/packages-test/

The official download page is

- https://projects.eclipse.org/projects/iot.embed-cdt/downloads/

## Prepare release

### Prepare EPP

Clone the EPP Git project:

```
git clone "ssh://lionescu@git.eclipse.org:29418/epp/org.eclipse.epp.packages.git"
scp -p -P 29418 lionescu@git.eclipse.org:hooks/commit-msg "epp/org.eclipse.epp.packages.git/.git/hooks/"
```

### Prepare SimRel

Use the CBI Aggregator installed from:

- https://download.eclipse.org/cbi/updates/aggregator/ide/4.13

Clone the SimRel Git project:

```
git clone "ssh://lionescu@git.eclipse.org:29418/simrel/org.eclipse.simrel.build"
scp -p -P 29418 lionescu@git.eclipse.org:hooks/commit-msg "org.eclipse.simrel.build/.git/hooks/"
```

### Create a new milestone

If not already done, create a new milestone.

- in the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/)
page, click the
[Milestones](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/)
button and add a
[new](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/new/)
milestone. As title, use the current version, like _v6.0.0_.

### Fix issues

Normally all changes should be done as a result of a ticket registered as
a GitHub Issue.

- be sure the `develop` branch is selected
- scan the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/)
list, and fix them. The commit message should be prefixed with the issue
number, like `[#122]`;
- mark all fixed issues as part of the new milestone;
- add a message like _fixed on 2020-01-10_;
- close the issues

## Build locally

### Run maven

After fixing issues, run the maven build locally:

```
$ mvn clean verify
```

Start a Debug/Run session and try the result in a child Eclipse.

## How to make the pre-release

### Push to GitHub

Be sure the repo is clean and push the `develop` branch to GitHub.

This will also trigger a CI job that will run a maven build.

### Trigger the Jenkins development build

- go to [https://ci.eclipse.org/embed-cdt/job/build-plug-ins/](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/)
- login (otherwise the next link is not visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds/develop/p2/](https://download.eclipse.org/embed-cdt/builds/develop/p2/)

### Install on a separate Eclipse

Test if the new build can be used as an update site, by installing it
on a separate Eclipse (not the one used for development); use the URL:

- https://download.eclipse.org/embed-cdt/builds/develop/p2/

## How to make a new release

### Merge to master

When ready, merge the `develop` branch into `master`, and push them to GitHub.

Wait for the CI to confirm that the build passed.

Add a tag like `v6.0.0` (with `v`).

### Trigger the Jenkins master build

- go to [https://ci.eclipse.org/embed-cdt/job/build-plug-ins/](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/)
- login (otherwise the next link will not be visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds/master/p2/](https://download.eclipse.org/embed-cdt/builds/master/p2/)

### Publish the pre-release

- go to https://ci.eclipse.org/embed-cdt/
- login (otherwise the next link is not visible!)
- use the [make-pre-release-from-master](https://ci.eclipse.org/embed-cdt/job/make-pre-release-from-master/)
Jenkins job to copy the files from `builds/master` to `updates/v6-test/`,
which is the public location for the pre-release
- click the **Build Now** link

### Announce pre-release

Announce the pre-release to the **embed-cdt-dev@eclipse.org** list;
use a subject like **Eclipse Embedded CDT v6.0.0 released**, and
pass a link to the release page.

Beta testers can install the pre-release from:

- https://download.eclipse.org/embed-cdt/updates/v6-test/

### Create a pre-release record

In the official
[iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/)
page, click the
[Create a new release](https://projects.eclipse.org/node/18638/create-release/) link in the right sidebar.

Name it like `6.0.0` (no v).

Click on Edit, The Basics; switch to Source mode

Start with _Pre-release_ (Header 3).

```html
<p>Version <strong>6.0.0</strong> is a maintenance release; if fixes XXX.</p>

<h3>Pre-release</h3>

<p>For those who want to beta test, the pre-release is available via <strong>Install New Software</strong> from:</p>

<ul>
	<li>https://download.eclipse.org/embed-cdt/updates/v6-test/</li>
</ul>

<h3>Changes</h3>

<p>The bug fixes are:</p>

<ul>
	<li>none</li>
</ul>

<p>The enhancements are:</p>

<ul>
	<li>none</li>
</ul>

<p>The developer changes are:</p>

<ul>
	<li>none</li>
</ul>

<p>More details at GitHub:</p>
<ul>
	<li><a href="https://github.com/eclipse-embed-cdt/eclipse-plugins/milestone/19">https://github.com/eclipse-embed-cdt/eclipse-plugins/milestone/19</a></li>
</ul>

<h3>Release content</h3>

<p>The following features and plugins are included in this release:</p>

<pre>
features:
org.eclipse.embedcdt.codered.feature_2.0.0.202010292017.jar
...
org.eclipse.embedcdt.templates.stm.feature.source_2.7.2.202010292017.jar

plugins:
org.eclipse.embedcdt.codered_2.0.0.202010292017.jar
...
org.eclipse.embedcdt.templates.stm.source_2.7.2.202010292017.jar
</pre>

```

To get the list of changes, scan the Git log:

```console
$ git log --pretty='%cd * %h - %s' --date=short
```

To get the release content, check the Jenkins output after the command
`ls -L features plugins`.

Select the **Release Type** (major, minor, service).

### Test

Install the plug-ins on several platforms.

### Update SimRel for pre-release

If everything fine, update SimRel.

With Sourcetree:

- open `org.eclipse.simrel.build.git`
- pull new commits

In Eclipse:

- import existing project `org.eclipse.simrel.build`
- open `simrel.aggr`
- expand the 'Contribution: Embedded CDT'
- select **Mapped Repository**
- right click: **Show Properties View**
- in the right side, edit the **Location** field to the new pre-release p2 URL
(like `https://download.eclipse.org/embed-cdt/pre-releases/6.0.0-202011221632/p2/`
and press Enter
- select all the features in the contribution, right-click and choose **Fix Versions**
- select the Contribution and **Validate**
- select the Aggregation and **Validate**
- Save

```bash
git commit -m 'embedcdt: update for 6.0.0-202011221632'
git push ssh://lionescu@git.eclipse.org:29418/simrel/org.eclipse.simrel.build HEAD:refs/for/master
```

In Gerrit, click **CODE_REVIEW+2** and then **SUBMIT** to merge the changes.

The commit will trigger the [SimRel](https://ci.eclipse.org/simrel/)
Jenkins aggregator pipeline:  

- https://ci.eclipse.org/simrel/job/simrel.runaggregator.pipeline/

In about one hour it'll automatically rebuild the staging repo:

- https://download.eclipse.org/staging/

## Publish the final release

When the plug-ins are considered stable:

- go to https://ci.eclipse.org/embed-cdt/
- login (otherwise the next link is not visible!)
- use the [make-release-from-master](https://ci.eclipse.org/embed-cdt/job/make-release-from-master/)
Jenkins job to copy from `builds/master` to `updates/v6/` and
`releases/<version>`
- click the **Build with Parameters** link
- enter _yes_
- click the **Build** link

The `releases` folder includes both the release archives and the expanded
p2 repository.

The `updates/v6/` includes only the expanded p2 repository, for the archives
see the `releases` folder.

Both can be used in Eclipse to **Install New Software**.

The public update URLs are:

- https://download.eclipse.org/embed-cdt/updates/v6/
- https://download.eclipse.org/embed-cdt/releases/6.0.0/p2/

### Update the Eclipse Marketplace records

- go to [Eclipse Marketplace](https://marketplace.eclipse.org/content/eclipse-embedded-cdt/)
- log in
- click **Edit**
- update version number, minimum Eclipse versions
- click the bottom page **Save**.

### Update the release record

- go to [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/governance/) and select the new release
- click Edit -> The Basics
- switch to Source mode
- replace the **Pre-release** section with the above:

```html
<h3>Eclipse Marketplace</h3>

<p>The recommended way to update to the latest plug-ins is via the <strong>Eclipse Marketplace</strong>; search for <em>Embedded CDT</em>.</p>

<h3>Eclipse Update Sites</h3>

<p>For those who prefer to do it manually, the latest version is available via <strong>Install New Software</strong> from:</p>

<ul>
	<li><a href="https://download.eclipse.org/embed-cdt/updates/v6/">https://download.eclipse.org/embed-cdt/updates/v6/</a></li>
</ul>

<p>To get exactly this version, <strong>Install New Software</strong> from:</p>

<ul>
	<li><a href="https://download.eclipse.org/embed-cdt/releases/6.0.0/p2/">https://download.eclipse.org/embed-cdt/releases/6.0.0/p2/</a></li>
</ul>

<h3>Local install</h3>

<p>For manual installs, the plug-ins are also available as regular archives, that can be downloaded locally and installed.</p>

<ul>
	<li><a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/6.0.0/org.eclipse.embedcdt.repository-6.0.0-202010292017.zip">org.eclipse.embedcdt.repository-6.0.0-202010292017.zip</a> <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/6.0.0/org.eclipse.embedcdt.repository-6.0.0-202010292017.zip.sha">(SHA)</a></li>
</ul>
```

As links for the latest two, open https://download.eclipse.org/embed-cdt/releases/ and get something like:

- https://download.eclipse.org/embed-cdt/releases/6.0.0/org.eclipse.embedcdt.repository-6.0.0-202010292017.zip
- https://download.eclipse.org/embed-cdt/releases/6.0.0/org.eclipse.embedcdt.repository-6.0.0-202010292017.zip.sha

Update the URLs to use the download redirect:

- https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/6.0.0/org.eclipse.embedcdt.repository-6.0.0-202010292017.zip
- https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/6.0.0/org.eclipse.embedcdt.repository-6.0.0-202010292017.zip.sha

Remove the _Pre-release_ top header.

### Update the Downloads page

- go to [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/) and select the new release
- click Edit -> The Basics
- switch to Source mode

Use copy/paste/edit.

## Check & update SimRel

Pull new commits.

In Eclipse:

- import existing project `org.eclipse.simrel.build`
- open `simrel.aggr`
- expand the 'Contribution: Embedded CDT'
- select **Mapped Repository**
- right click: **Show Properties View**
- in the right side, edit the **Location** field to the new pre-release p2 URL
(like `https://download.eclipse.org/embed-cdt/releases/6.0.0/p2/`
and press Enter
- select all the features in the contribution, right-click and choose **Fix Versions**
- select the Contribution and **Validate**
- select the Aggregation and **Validate**

```bash
git commit -m 'embedcdt: update for 6.0.0'
git push ssh://lionescu@git.eclipse.org:29418/simrel/org.eclipse.simrel.build HEAD:refs/for/master
```

In Gerrit, click **CODE_REVIEW+2** and then **SUBMIT** to merge the changes.

The commit will trigger the [SimRel](https://ci.eclipse.org/simrel/)
Jenkins aggregator pipeline:  

- https://ci.eclipse.org/simrel/job/simrel.runaggregator.pipeline/

In about one hour it'll automatically rebuild the staging repo:

- https://download.eclipse.org/staging/

## Check & update EPP

If the list of features changed, it is necessary to
update the EPP project.

Pull new commits.

- edit `org.eclipse.epp.package.embedcpp.product/epp.product`
- update the list of features

To change the default preferences, edit the
`org.eclipse.epp.package.embedcpp/plugin_customization.ini`

If necessary, update the text displayed in the Downloads page, it is in
`org.eclipse.epp.package.embedcpp.feature/epp.website.xml` file, the
`<description>` element.

Compare with `package.cpp.*`:

```
cd org.eclipse.epp.packages.git

diff packages/org.eclipse.epp.package.cpp packages/org.eclipse.epp.package.embedcpp
diff packages/org.eclipse.epp.package.cpp.feature packages/org.eclipse.epp.package.embedcpp.feature
diff packages/org.eclipse.epp.package.cpp.product packages/org.eclipse.epp.package.embedcpp.product
```

Commit and push to Gerrit:

```bash
git commit -m 'embedcpp ...'
git push ssh://lionescu@git.eclipse.org:29418/epp/org.eclipse.epp.packages.git HEAD:refs/for/master
```

In Gerrit, click **CODE_REVIEW+2** and then **SUBMIT** to merge the changes.

The commit will trigger the [EPP](https://ci.eclipse.org/packaging/)
Jenkins job:  

- https://ci.eclipse.org/packaging/job/simrel.epp-tycho-build/

In 1.5 hours the new test versions of the integrated epp builds are
available from:

- https://ci.eclipse.org/packaging/job/simrel.epp-tycho-build/lastSuccessfulBuild/artifact/org.eclipse.epp.packages/archive/ 

### Announce release

Announce the release to the **embed-cdt-dev@eclipse.org** list;
use a subject like **Eclipse Embedded CDT v6.0.0 released**, and
pass a link to the release page.

Beta testers can install the pre-release from:

- https://ci.eclipse.org/packaging/job/simrel.epp-tycho-build/lastSuccessfulBuild/artifact/org.eclipse.epp.packages/archive/ 

## Share on Twitter

- in a separate browser windows, open [TweetDeck](https://tweetdeck.twitter.com/)
- using the `@embedCDT` account, enter a message like
  **Eclipse Embedded CDT v6.0.0 released** and on the next line
  paste the link to the release
- click the Tweet button
