# Maintainer info

## Project repository

The project is hosted on GitHub:

- <https://github.com/eclipse-embed-cdt/eclipse-plugins>

To clone it, be sure the submodules are also cloned:

```sh
git clone --recurse-submodule https://github.com/eclipse-embed-cdt/eclipse-plugins \
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

- <https://download.eclipse.org/embed-cdt/builds/develop/p2/>
- <https://download.eclipse.org/embed-cdt/builds/master/p2/>

When the content is stable, it is promoted as a pre-release and published as:

- <https://download.eclipse.org/embed-cdt/updates/v6-test/>

The final release is published in the main update site:

- <https://download.eclipse.org/embed-cdt/updates/v6/>

For archiving purposes, the release is also published in a separate folder
for each version, with the archive in the top folder and the p2 repo as
a sub-folder

- <https://download.eclipse.org/embed-cdt/releases/6.2.1/org.eclipse.embedcdt.repository-6.2.1-202204041943.zip>
- <https://download.eclipse.org/embed-cdt/releases/6.2.1/p2/>

The official download page is

- <https://projects.eclipse.org/projects/iot.embed-cdt/downloads/>

## Prepare release

### Clone SimRel

Use the CBI Aggregator installed from:

- <https://download.eclipse.org/cbi/updates/aggregator/ide/4.13>

At first use, clone the SimRel Git repo:

```bash
git clone "ssh://lionescu@git.eclipse.org:29418/simrel/org.eclipse.simrel.build" "org.eclipse.simrel.build.git"
scp -p -P 29418 lionescu@git.eclipse.org:hooks/commit-msg "org.eclipse.simrel.build.git/.git/hooks/"
```

### SimRel deadline

The deadline for SimRel changes is **Wed 5pm Ottawa time**.

### Clone EPP

At first use, clone the EPP Git repo:

```bash
git clone "ssh://lionescu@git.eclipse.org:29418/epp/org.eclipse.epp.packages.git"
scp -p -P 29418 lionescu@git.eclipse.org:hooks/commit-msg "epp/org.eclipse.epp.packages.git/.git/hooks/"
```

### EPP deadline

The deadline for EPP changes is **Thu 9am Ottawa time**.

### Create a new milestone

If not already done, create a new milestone.

- in the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/)
page, click the
[Milestones](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/)
button and add a
[new](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/new/)
milestone. As title, use the current version, like _v6.2.1_.

### Update version in README-MAINTENANCE

Perform a search & replace to update the version.

Push the develop branch.

### Fix issues

Normally all changes should be done as a result of a ticket registered as
a GitHub Issue.

- be sure the `develop` branch is selected
- scan the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/)
list, and fix them. The commit message should be prefixed with the issue
number, like `[#122]`;
- mark all fixed issues as part of the new milestone;
- add a message like _Fixed on 2022-01-10_;
- close the issues

### Update plug-ins/features versions

For the plug-ins that were modified, increase the specific version
(like _1.2.3_).

For all features, increase the common version (like _6.2.1_); be sure
the composite records are not updated at this moment, they require
new lines, added later.

## Build locally

### Run maven

After fixing issues, run the maven build locally:

```sh
mvn clean verify
```

Start a Debug/Run session and try the result in a child Eclipse.

## How to make a release candidate

### Push to GitHub

Be sure the repo is clean and push the `develop` branch to GitHub.

This will also trigger a GitHub Actions CI job that will run a maven build.

### Trigger the Jenkins development build

- go to [https://ci.eclipse.org/embed-cdt/job/build-plug-ins/](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/)
- **login** (otherwise the next link is not visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds/develop/p2/](https://download.eclipse.org/embed-cdt/builds/develop/p2/)

### Install on a separate Eclipse

Test if the new build can be used as an update site, by installing it
on a separate Eclipse (not the one used for development); use the URL:

- <https://download.eclipse.org/embed-cdt/builds/develop/p2/>

### Merge to master

When ready, merge the `develop` branch into `master`, and push them to GitHub.

Wait for the GitHub Actions CI job to confirm that the build passed.

### Trigger the Jenkins master build

- go to [https://ci.eclipse.org/embed-cdt/job/build-plug-ins/](https://ci.eclipse.org/embed-cdt/job/build-plug-ins/)
- **login** (otherwise the next link will not be visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
[https://download.eclipse.org/embed-cdt/builds/master/p2/](https://download.eclipse.org/embed-cdt/builds/master/p2/)

### Publish the release candidate

- go to <https://ci.eclipse.org/embed-cdt/>
- **login** (otherwise the next link is not visible!)
- use the [make-release-candidate-from-master](https://ci.eclipse.org/embed-cdt/job/make-release-candidate-from-master/)
Jenkins job to copy the files from `builds/master` to `updates/v6-test/` and
`release-candidates/<version>-<date>`,
which is the public location for the release candidates until the final
release is out
- click the **Build Now** link

### Update timestamp in README-MAINTENANCE

Go to the release candidate folder

- <https://download.eclipse.org/embed-cdt/release-candidates/>

Get the timestamp and update the README-MAINTENANCE.md file.

### Create a release candidate record

This applies only for the first release candidate, or for the final release.

In the official
[iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/)
page, login and click the
[Create a new release](https://projects.eclipse.org/node/18638/create-release/)
link in the right side bar, below Releases (it shows only after login).

Name it like `6.2.1` (no v).

Click on Edit, The Basics; switch to Source mode

Start with _Release candidate_ (Header 3).

```html
<p>Version <strong>6.2.1</strong> is a maintenance release; it ....</p>

<h3>Release candidate</h3>

<p>For those who want to beta test, the release candidate is available via <strong>Install New Software</strong> from:</p>

<ul>
	<li>https://download.eclipse.org/embed-cdt/updates/v6-test/</li>
</ul>
```

Select the **Release Type** (major, minor, service).

### Test

Install the plug-ins on several platforms.

### Update SimRel for the release candidate

If everything is fine, update SimRel.

With a Git client:

- open `org.eclipse.simrel.build.git`
- pull new commits

Go to the release candidate folder

- [https://download.eclipse.org/embed-cdt/release-candidates/](https://download.eclipse.org/embed-cdt/release-candidates/)

In Eclipse:

- import existing project `org.eclipse.simrel.build`
- open `simrel.aggr`
- expand the 'Contribution: Embedded CDT'
- select **Mapped Repository**
- right click: **Show Properties View**
- in the right side, edit the **Location** field to the new release
candidate p2 URL (like
`https://download.eclipse.org/embed-cdt/release-candidates/6.2.1-202204041943/p2/`
and press Enter
- select all the features in the contribution, right-click and choose
**Fix Versions**
- select the Contribution and **Validate**
- select the Aggregation and **Validate**
- Save
- commit _embedcdt: update for 6.2.1-202204041943'_
- _Signed-off-by: Liviu Ionescu <ilg@livius.net>_
- _Signed-off-by: Liviu Ionescu &lt;ilg@livius.net&gt;_

```bash
git push ssh://lionescu@git.eclipse.org:29418/simrel/org.eclipse.simrel.build HEAD:refs/for/master
```

This will trigger a Gerrit run.

Check the console output, for the Gerrit link. If missed, it'll be
later sent by e-mail, when the run completes.

In Gerrit web page, if the check is successful and Verified+1 is shown,
click **CODE_REVIEW+2** and then **SUBMIT** to merge the changes.

The commit will trigger the [SimRel](https://ci.eclipse.org/simrel/)
Jenkins aggregator pipeline:

- <https://ci.eclipse.org/simrel/job/simrel.runaggregator.pipeline/>

In about one hour it'll automatically rebuild the staging repo:

- <https://download.eclipse.org/staging/>

### Announce release candidate

Announce the release candidate to the **embed-cdt-dev@eclipse.org** list;
use a subject like
**Embed CDT v6.2.1-202204041943 release candidate**,
and pass a link to the release page, available at:

- <https://projects.eclipse.org/projects/iot.embed-cdt/>

Beta testers can install the release candidate from:

- <https://download.eclipse.org/embed-cdt/updates/v6-test/>

## Add Git tag for pre-release

Go to the release candidate folder

- [https://download.eclipse.org/embed-cdt/release-candidates/](https://download.eclipse.org/embed-cdt/release-candidates/)

Copy the tag and enter it in Git, like `v6.2.1-202204041943` (with `v`).

## Publish the final release

When the plug-ins are considered stable:

- in `eclipse-plugins.git`, the master branch, edit both .xml files
  - `repositories/org.eclipse.embededcdt-repository/composite/compositeArtifacts.xml`
  - `repositories/org.eclipse.embededcdt-repository/composite/compositeContent.xml`
  - add new child like `<child location='../../releases/6.2.1/p2'/>`
  - update `p2.timestamp` to the value shown at the end of the `make-release-candidate-from-master`
- **push** master with a message like _add 6.2.1 to composite_

- go to <https://ci.eclipse.org/embed-cdt/>
- **login** (otherwise the next link is not visible!)
- use the
[make-release-from-master](https://ci.eclipse.org/embed-cdt/job/make-release-from-master/)
Jenkins job to copy from `builds/master` to `updates/v6/` and `releases/<version>`
- click the **Build with Parameters** link
- enter _yes_
- click the **Build** link

The `releases` folder includes both the release archives and the expanded
p2 repository.

The `updates/v6/` includes only the expanded p2 repository, for the archives
see the `releases` folder.

Both can be used in Eclipse to **Install New Software**.

The public update URLs are:

- <https://download.eclipse.org/embed-cdt/updates/v6/>
- <https://download.eclipse.org/embed-cdt/releases/6.2.1/p2/>

### Update the Eclipse Marketplace records

- go to [Eclipse Marketplace](https://marketplace.eclipse.org/content/eclipse-embedded-cdt/)
- **login**
- click **Edit**
- update version number, minimum Eclipse versions
- click the **Save item to list** button (very important!)
- click the bottom page **Save**.

### Add a new release in the project web

Edit the `eclipse-embed-cdt/web-jekyll.git` project.

In the `develop` branch, in `_posts/release`, add a new release page.

As links for the latest two, open <https://download.eclipse.org/embed-cdt/releases/>
and get the archive URL, like:

- <https://download.eclipse.org/embed-cdt/releases/6.2.1/org.eclipse.embedcdt.repository-6.2.1-202204041943.zip>

Isolate the part starting with `/embed-cdt/...` and update the URLs to use the download redirect:

- <https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/6.2.1/org.eclipse.embedcdt.repository-6.2.1-202204041943.zip>
- <https://www.eclipse.org/downloads/download.php?file=//embed-cdt/releases/6.2.1/org.eclipse.embedcdt.repository-6.2.1-202204041943.zip.sha>

Update the milestone URL.

Copy/paste the features/plug-ins from the console output of the
[make-release-from-master](https://ci.eclipse.org/embed-cdt/job/make-release-from-master/)
Jenkins job.

- commit with a message like _Eclipse Embedded CDT plug-ins v6.2.1 released_.
- push the `develop` branch
- wait for GitHub Actions job to complete
  (<https://github.com/eclipse-embed-cdt/web-jekyll/actions>)
- check the result at <https://eclipse-embed-cdt.github.io/web-preview/>
- when ok, merge `develop` into `master`
- push the `master` branch
- wait for GitHub Actions job to complete
- check the result at <https://eclipse-embed-cdt.github.io/>

### Add GitHub release

In [GitHub releases](https://github.com/eclipse-embed-cdt/eclipse-plugins/releases) add a new release

- tag: _v6.2.1_ (with `v`)
- title _Eclipse Embedded CDT plug-ins v6.2.1_
- copy/paste from the release page
- add `[Continue reading »](https://eclipse-embed-cdt.github.io/blog/YYYY/MM/DD/plugins-v6.2.1-released/)` with a link to the web page
- do not attach files
- click **Publish release**

### Close milestone

In [GitHub milestones](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones):

- close the v6.2.1 milestone

### Update the Eclipse release record

- go to [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/governance/)
and select the new release
- click Edit -> The Basics
- switch to Source mode
- replace the entire content with links to GitHub:

```html
<p>Version <strong>6.2.1</strong> is a new major/minor/service release; it updates ...</p>

<p>Fore more details, please read the project web release pages:</p>

<ul>
 <li><a href="https://eclipse-embed-cdt.github.io/blog/YYYY/MM/DD/package-2020-12-released/">Eclipse IDE for Embedded C/C++ Developers 2020-12 released</a>&nbsp;(for installing a new Eclipse)</li>
 <li><a href="https://eclipse-embed-cdt.github.io/blog/YYYY/MM/DD/plugins-v6.2.1-released/">Eclipse Embedded CDT plug-ins v6.2.1 released</a>&nbsp;(for updating the plug-ins on an existing Eclipse)</li>
</ul>
```

### Update the Downloads page

For packages releases:

- go to [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/)
- click Edit -> The Basics
- switch to Source mode

Use copy/paste/edit.

## Check & update SimRel

**Pull** new commits.

In Eclipse:

- import existing project `org.eclipse.simrel.build`
- open `simrel.aggr`
- expand the 'Contribution: Embedded CDT'
- select **Mapped Repository**
- right click: **Show Properties View**
- in the right side, edit the **Location** field to the new release p2 URL
(like `https://download.eclipse.org/embed-cdt/releases/6.2.1/p2/`
and press Enter
- select all the features in the contribution, right-click and choose
**Fix Versions**
- select the Contribution and **Validate**
- select the Aggregation and **Validate**
- commit with a message like:
  - _embedcdt: update for 6.2.1_
  - _Signed-off-by: Liviu Ionescu <ilg@livius.net>_

```bash
git push ssh://lionescu@git.eclipse.org:29418/simrel/org.eclipse.simrel.build HEAD:refs/for/master
```

This will trigger a Gerrit run.

Check the console output, for the Gerrit link. If missed, it'll be
later sent by e-mail, when the run completes.

In Gerrit, click **CODE_REVIEW+2** and then **SUBMIT** to merge the changes.

The commit will trigger the [SimRel](https://ci.eclipse.org/simrel/)
Jenkins aggregator pipeline:

- <https://ci.eclipse.org/simrel/job/simrel.runaggregator.pipeline/>

In about one hour it'll automatically rebuild the staging repo:

- <https://download.eclipse.org/staging/>

## Check & update EPP

If the list of features changed, it is necessary to
update the EPP project.

Pull new commits.

- edit `packages/org.eclipse.epp.package.embedcpp.product/epp.product`
- update the list of features

### package.embedcpp

To change the default preferences, edit the
`pacakges/org.eclipse.epp.package.embedcpp/plugin_customization.ini`

### package.embedcpp.feature

If necessary, update the text displayed in the Downloads page, it is in
`pacakges/org.eclipse.epp.package.embedcpp.feature/epp.website.xml` file, the
`<description>` element.

Update the version in **NewAndNoteworthy**, in the same file.

### Compare to package.cpp

Compare the three packages with the similar ones from CPP:

```sh
cd org.eclipse.epp.packages.git

diff packages/org.eclipse.epp.package.cpp packages/org.eclipse.epp.package.embedcpp
diff packages/org.eclipse.epp.package.cpp.feature packages/org.eclipse.epp.package.embedcpp.feature
diff packages/org.eclipse.epp.package.cpp.product packages/org.eclipse.epp.package.embedcpp.product
```

### Commit & push

Commit and push to Gerrit:

```bash
git commit -m 'embedcpp ...'
git push ssh://lionescu@git.eclipse.org:29418/epp/org.eclipse.epp.packages.git HEAD:refs/for/master
```

In Gerrit, click **CODE_REVIEW+2** and then **SUBMIT** to merge the changes.

The commit will trigger the [EPP](https://ci.eclipse.org/packaging/)
Jenkins job:

- <https://ci.eclipse.org/packaging/job/simrel.epp-tycho-build/>

In 1.5 hours the new test versions of the integrated epp builds are
available from:

- <https://ci.eclipse.org/packaging/job/simrel.epp-tycho-build/lastSuccessfulBuild/artifact/org.eclipse.epp.packages/archive/>

### Announce release

Announce the release to the **embed-cdt-dev@eclipse.org** list;
use a subject like **Eclipse Embedded CDT plug-ins v6.2.1 released**, and
pass a link to the release page.

## Share on Twitter

- in a separate browser windows, open [TweetDeck](https://tweetdeck.twitter.com/)
- using the `@EmbedCDT` account, enter a message like
  **Eclipse Embedded CDT plug-ins v6.2.1 released** and on the next line
  paste the link to the release
- click the Tweet button
