# Maintainer info

## Project repository

The project is hosted on GitHub:

- https://github.com/eclipse-embed-cdt/eclipse-plugins

To clone it, be sure the submodules are also cloned:

```
git clone --recurse-submodule https://github.com/eclipse-embed-cdt/eclipse-plugins \
  eclipse-plugins.git
```

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

- https://download.eclipse.org/embed-cdt/releases/5.2.1/org.eclipse.embedcdt.repository-5.2.1-202010292017.zip
- https://download.eclipse.org/embed-cdt/releases/5.2.1/p2/

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
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/)
page, click the
[Milestones](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/)
button and add a
[new](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/new/)
milestone. As title, use the current version, like _v5.2.1_.

### Fix issues

- be sure the `develop` branch is selected
- scan the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/)
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
[https://download.eclipse.org/embed-cdt/builds/develop/p2/](https://download.eclipse.org/embed-cdt/builds/develop/p2/)

### Install on a separate Eclipse

Test if the new build can be used as an update site, by installing it
on a separate Eclipse (not the one used for development); use the URL:

- https://download.eclipse.org/embed-cdt/builds/develop/p2/

## How to make a new release

### Merge to master

When ready, merge the `develop` branch into `master`, and push them to GitHub.

Wait for the CI to confirm that the build passed.

Add a tag like `v5.2.1` (with `v`).

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
Jenkins job to copy the files from `builds/master` to `updates/neon-test`,
which is the public location for the pre-release
- click the **Build Now** link

Announce the pre-release to the **embed-cdt-dev@eclipse.org** list;
use a subject like **Eclipse Embedded CDT v5.2.1 released**, and
pass a link to the release page.

Beta testers can install the pre-release from:

- https://download.eclipse.org/embed-cdt/updates/neon-test/

### Create a pre-release record

In the official
[iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/)
page, click the
[Create a new release](https://projects.eclipse.org/node/18638/create-release/) link in the right sidebar.

Name it like `5.2.1` (no v).

Click on Edit, The Basics; switch to Source mode

Start with _Pre-release_ (Header 3).

```html
<p>Version <strong>5.2.1</strong> is a maintenance release; if fixes XXX.</p>

<h3>Pre-release</h3>

<p>For those who want to beta test, the pre-release is available via <strong>Install New Software</strong> from:</p>

<ul>
	<li>https://download.eclipse.org/embed-cdt/updates/neon-test/</li>
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

<p>More details at GitHub:<br />
<br />
- <a href="https://github.com/eclipse-embed-cdt/eclipse-plugins/milestone/19">https://github.com/eclipse-embed-cdt/eclipse-plugins/milestone/19</a></p>

<h3>Release content</h3>

<p>The following features and plugins are included in this release:</p>

<pre>
features:
org.eclipse.embedcdt.codered.feature_1.1.2.202010292017.jar
...
org.eclipse.embedcdt.templates.stm.feature.source_2.7.2.202010292017.jar

plugins:
org.eclipse.embedcdt.codered_1.1.2.202010292017.jar
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

### Publish the final release

When the plug-ins are considered stable:

- go to https://ci.eclipse.org/embed-cdt/
- login (otherwise the next link is not visible!)
- use the [make-release-from-master](https://ci.eclipse.org/embed-cdt/job/make-release-from-master/)
Jenkins job to copy from `builds/master` to `updates/neon` and
`releases/<version>`
- click the **Build with Parameters** link
- enter _yes_
- click the **Build** link

The `releases` folder includes both the release archives and the expanded
p2 repository.

The `updates/neon` includes only the expanded p2 repository, for the archives
see the `releases` folder.

Both can be used in Eclipse to **Install New Software**.

The public update URLs are:

- https://download.eclipse.org/embed-cdt/updates/neon/
- https://download.eclipse.org/embed-cdt/releases/5.2.1/p2/

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
	<li><a href="https://download.eclipse.org/embed-cdt/updates/neon/">https://download.eclipse.org/embed-cdt/updates/neon/</a></li>
</ul>

<p>To get exactly this version, <strong>Install New Software</strong> from:</p>

<ul>
	<li><a href="https://download.eclipse.org/embed-cdt/releases/5.2.1/p2/">https://download.eclipse.org/embed-cdt/releases/5.2.1/p2/</a></li>
</ul>

<h3>Local install</h3>

<p>For manual installs, the plug-ins are also available as regular archives, that can be downloaded locally and installed.</p>

<ul>
	<li><a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/5.2.1/org.eclipse.embedcdt.repository-5.2.1-202010292017.zip">org.eclipse.embedcdt.repository-5.2.1-202010292017.zip</a> <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/5.2.1/org.eclipse.embedcdt.repository-5.2.1-202010292017.zip.sha">(SHA)</a></li>
</ul>
```

As links for the latest two, open https://download.eclipse.org/embed-cdt/releases/ and get something like:

- https://download.eclipse.org/embed-cdt/releases/5.2.1/org.eclipse.embedcdt.repository-5.2.1-202010292017.zip
- https://download.eclipse.org/embed-cdt/releases/5.2.1/org.eclipse.embedcdt.repository-5.2.1-202010292017.zip.sha

Update the URLs to use the download redirect:

- https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/5.2.1/org.eclipse.embedcdt.repository-5.2.1-202010292017.zip
- https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/5.2.1/org.eclipse.embedcdt.repository-5.2.1-202010292017.zip.sha

Remove the _Pre-release_ top header.

### Create the test package (deprecated)

TODO: explain how to edit/update the EPP project.

- switch to the `embed-cdt-develop` branch
- in the `parent/pom.xml` file, edit the Embed CDT specifics
- commit
- push `embed-cdt-develop` to GitHub

- go to https://ci.eclipse.org/embed-cdt/
- login (otherwise the next link is not visible!)
- use the [make-packages](https://ci.eclipse.org/embed-cdt/job/make-packages/)
Jenkins job to maven build the `embed-cdt-develop` branch and
copy the result in `packages-test/<version>
- click the **Build Now** link

The direct download URL is:

- https://download.eclipse.org/embed-cdt/packages-test/

Check if everything is fine and test.

Test on various platforms.

### Create the final package (deprecated)

- merge `embed-cdt-develop` int `embed-cdt`
- push `embed-cdt` to GitHub
- tag `2020-09_R-ecdt`

- go to https://ci.eclipse.org/embed-cdt/
- login (otherwise the next link is not visible!)
- use the [make-packages](https://ci.eclipse.org/embed-cdt/job/make-packages/)
Jenkins job to maven build the `embed-cdt` branch and
copy the result in `packages/<version>
- click the **Build Now** link

The direct download URL is:

- `https://download.eclipse.org/embed-cdt/packages/`

### Update the release record (deprecated)

- go to [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/governance/) and select the new release
- click Edit -> The Basics
- switch to Source mode

```html
<h3>Eclipse IDE for Embedded C/C++ Developers</h3>

<p>For convenience, this version of the plug-ins is also available as Eclipse packages, which pack together the <strong>Eclipse IDE for C/C++ Developers</strong> standard distribution with the <strong>Eclipse Embedded CDT plug-ins</strong>:</p>

<h4>2020-06 (Eclipse 4.16)</h4>

<ul>
	<li>Windows <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/packages/2020-06/eclipse-embedcdt-2020-06-R-win32.win32.x86_64.zip">64-bit</a> <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/packages/2020-06/eclipse-embedcdt-2020-06-R-win32.win32.x86_64.zip.sha">SHA</a></li>
	<li>macOS <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/packages/2020-06/eclipse-embedcdt-2020-06-R-macosx.cocoa.x86_64.tar.gz">64-bit</a> <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/packages/2020-06/eclipse-embedcdt-2020-06-R-macosx.cocoa.x86_64.tar.gz.sha">SHA</a></li>
	<li>Intel Linux <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/packages/2020-06/eclipse-embedcdt-2020-06-R-linux.gtk.x86_64.tar.gz">64-bit</a> <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/packages/2020-06/eclipse-embedcdt-2020-06-R-linux.gtk.x86_64.tar.gz.sha">SHA</a></li>
	<li>Arm Linux <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/packages/2020-06/eclipse-embedcdt-2020-06-R-linux.gtk.aarch64.tar.gz">64-bit</a> <a href="https://www.eclipse.org/downloads/download.php?file=/embed-cdt/packages/2020-06/eclipse-embedcdt-2020-06-R-linux.gtk.aarch64.tar.gz.sha">SHA</a></li>
</ul>

<h4>macOS security notice</h4>

<p>On macOS, if you download the archive with the browser, the strict security checks on recent macOS will prevent it to run, and complain that the program is damaged. That&#39;s obviously not true, and the fix is simple, you need to remove the <strong>com.apple.quarantine</strong> extended attribute.</p>

<pre>
$ xattr -d com.apple.quarantine ~/Downloads/eclipse-embedcdt-2020-06-R-macosx.cocoa.x86_64.tar.gz</pre>

<p>After un-archiving, if the Eclipse.app still does not run, check/remove the attribute from the Eclipse.app folder too:</p>

<pre>
$ xattr -dr com.apple.quarantine ~/Downloads/Eclipse.app
</pre>

```

### Update the Downloads page

- go to [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/) and select the new release
- click Edit -> The Basics
- switch to Source mode

Use copy/paste/edit.

### Share on Twitter

- in a separate browser windows, open [TweetDeck](https://tweetdeck.twitter.com/)
- using the `@embedCDT` account, enter a message like
**Eclipse Embedded CDT v5.2.1 released** and on the next line
paste the link to the release
- click the Tweet button
