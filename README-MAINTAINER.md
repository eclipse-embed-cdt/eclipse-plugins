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

Development builds are automatically published as p2 sub-folders like:

- <https://download.eclipse.org/embed-cdt/builds/develop/p2/>
- <https://download.eclipse.org/embed-cdt/builds/master/p2/>

When the content is stable, it is promoted as a pre-release and published as:

- <https://download.eclipse.org/embed-cdt/updates/v6-test/>

The final release is published in the main update site:

- <https://download.eclipse.org/embed-cdt/updates/v6/>

For archiving purposes, the release is also published in a separate folder
for each version, with the archive in the top folder and the p2 repo as
a sub-folder

- <https://download.eclipse.org/embed-cdt/releases/6.6.0/org.eclipse.embedcdt.repository-6.6.0-202404031712.zip>
- <https://download.eclipse.org/embed-cdt/releases/6.6.0/p2/>

The official download page is

- <https://projects.eclipse.org/projects/iot.embed-cdt/downloads/>

## Prepare release

### Clone SimRel

Install the **CBI Aggregator Editor** from:

- <https://download.eclipse.org/cbi/updates/aggregator/ide/4.13>

At first use, fork the SimRel Git repo from:

- <https://github.com/eclipse-simrel/simrel.build>

to

- <https://github.com/embed-cdt/simrel.build>

### SimRel deadline

The deadline for SimRel changes is **Wed 5pm Ottawa time**.

### Create a new milestone

If not already done, create a new milestone.

- in the
[plug-ins issues](https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/)
page, click the
[Milestones](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/)
button and add a
[new](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones/new/)
milestone. As title, use the current version, like _6.6.0_.

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

Commit the MANIFEST.MF files as _update versions..._

For all features, increase the common version (like _6.6.0_); be sure
the composite records are not updated at this moment, they require
new lines, added later.

For all branding, edit the MANIFEST.MF and increase the
`Bundle-Version` (like _6.6.0_).

For all other occurrences, except `compositeArtifacts.xml` and
`compositeContent.xml`.

Commit all with _update package version 6.6.0_.

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

- <https://github.com/eclipse-embed-cdt/eclipse-plugins/actions>

### Trigger the Jenkins development build

Note: this happens now automatically, at each push.

- go to <https://ci.eclipse.org/embed-cdt/job/build-plug-ins/>
- **login** (otherwise the next link is not visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
<https://download.eclipse.org/embed-cdt/builds/develop/p2/>

### Install on a separate Eclipse

Test if the new build can be used as an update site, by installing it
on a separate Eclipse (not the one used for development); use the URL:

- <https://download.eclipse.org/embed-cdt/builds/develop/p2/>

### Merge to master

When ready, merge the `develop` branch into `master`, and push them to GitHub.

Wait for the GitHub Actions CI job to confirm that the build passed.

- <https://github.com/eclipse-embed-cdt/eclipse-plugins/actions>

### Trigger the Jenkins master build

- go to <https://ci.eclipse.org/embed-cdt/job/build-plug-ins/>
- **login** (otherwise the next link will not be visible!)
- click the **Scan Multibranch Pipeline Now** link
- when ready, the p2 repository is published at
<https://download.eclipse.org/embed-cdt/builds/master/p2/>

### Publish the release candidate

- go to <https://ci.eclipse.org/embed-cdt/>
- **login** (otherwise the next link is not visible!)
- use the [make-release-candidate-from-master](https://ci.eclipse.org/embed-cdt/job/make-release-candidate-from-master/)
Jenkins job to copy the files from `builds/master` to `updates/v6-test/` and
`release-candidates/<version>-<date>`,
which is the public location for the release candidates until the final
release is out
- click the **Build Now** link

Check the console output and remember the timestamp shown at the end, it'll
be used later in the composite files.

### Update timestamp in README-MAINTENANCE

Go to the release candidate folder

- <https://download.eclipse.org/embed-cdt/release-candidates/>

Get the timestamp (like 202404031712) and update the README-MAINTENANCE.md file.

Commit with _README-MAINTAINER update timestamp_.

### Create a release candidate record

This applies only for the first release candidate, or for the final release.

In the official
[iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/)
page, login and click the
[Create a new release](https://projects.eclipse.org/node/18638/create-release/)
link in the right side bar, below Releases (it shows only after login).

Name it like `6.6.0` (no v).

Click the **Create and edit** button.

Click the **Edit** tab, **The Basics**; switch to Source mode

Start with _Release candidate_ (Header 3).

```html
<p>Version <strong>6.6.0</strong> is a maintenance/new minor release; it ....</p>

<h3>Release candidate</h3>

<p>For those who want to beta test, the release candidate is available via <strong>Install New Software</strong> from:</p>

<ul>
  <li>https://download.eclipse.org/embed-cdt/updates/v6-test/</li>
</ul>
```

Select the **Release Type** (major, minor, service).

Click the bottom **Save** button. Leave the page.

### Test

Install the plug-ins on several platforms.

### Update SimRel for the release candidate (optional)

Full instructions are here: <https://github.com/orgs/eclipse-simrel/discussions/3>

but the simple version is update and create a PR against <https://github.com/eclipse-simrel/simrel.build>

If everything is fine, sync the SimRel fork:

- <https://github.com/embed-cdt/simrel.build>

With a Git client:

- open `simrel.build-fork.git`
- pull new commits
- edit `embedcdt.aggrcon`
  - replace location to <https://download.eclipse.org/embed-cdt/release-candidates/6.6.0-202404031712/p2/>
  - replace full version to `6.6.0.202404031712`
  - replace short version to `6.6.0`
- commit with a message like:
  - _embedcdt: update for 6.6.0-202404031712 release candidate_,
- push
- create pull request

The commit will trigger the [SimRel](https://ci.eclipse.org/simrel/)
Jenkins aggregator pipeline:

- <https://ci.eclipse.org/simrel/job/simrel.runaggregator.pipeline/>

In about 7-8 minutes it'll automatically rebuild the staging repo:

- <https://download.eclipse.org/staging/>

### Announce release candidate (optional)

Announce the release candidate to the **embed-cdt-dev@eclipse.org** list;
use a subject like
**Embed CDT v6.6.0-202404031712 release candidate**,
and pass a link to the release page, available at:

- <https://projects.eclipse.org/projects/iot.embed-cdt/>

Beta testers can install the release candidate from:

- <https://download.eclipse.org/embed-cdt/updates/v6-test/>

## Add Git tag for pre-release

Go to the release candidate folder

- <https://download.eclipse.org/embed-cdt/release-candidates/>

Copy the tag and enter it in Git, like `v6.6.0-202404031712` (with `v`).

## Publish the final release

When the plug-ins are considered stable:

- in `eclipse-plugins.git`, the master branch, edit both .xml files
  - `repositories/org.eclipse.embededcdt-repository/composite/compositeArtifacts.xml`
  - `repositories/org.eclipse.embededcdt-repository/composite/compositeContent.xml`
  - add new child like `<child location='../../releases/6.6.0/p2'/>`
  - update `p2.timestamp` to the value shown at the end of the `make-release-candidate-from-master`
- **commit** master with a message like _add 6.6.0 to composite_
- **push** master

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
- <https://download.eclipse.org/embed-cdt/releases/6.6.0/p2/>

### Update the Eclipse Marketplace records

- go to [Eclipse Marketplace](https://marketplace.eclipse.org/content/eclipse-embedded-cdt/)
- **login**
- click **Edit**
- update version number, minimum Eclipse versions
- click the **Save item to list** button (very important!)
- click the bottom page **Save**.

### Add a new release in the project web

Edit the `eclipse-embed-cdt/web-jekyll.git` project.

In the `develop` branch, in `_posts/plugins/releases`, add a new release page.

As links for the latest two, open <https://download.eclipse.org/embed-cdt/releases/>
and get the archive URL, like:

- <https://download.eclipse.org/embed-cdt/releases/6.6.0/org.eclipse.embedcdt.repository-6.6.0-202404031712.zip>

Isolate the part starting with `/embed-cdt/...` and update the URLs to use the download redirect:

- <https://www.eclipse.org/downloads/download.php?file=/embed-cdt/releases/6.6.0/org.eclipse.embedcdt.repository-6.6.0-202404031712.zip>
- <https://www.eclipse.org/downloads/download.php?file=//embed-cdt/releases/6.6.0/org.eclipse.embedcdt.repository-6.6.0-202404031712.zip.sha>

Go to <https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones> and
update the fixed issues.

Update the milestone URL.

Copy/paste the features/plug-ins from the console output of the
[make-release-from-master](https://ci.eclipse.org/embed-cdt/job/make-release-from-master/)
Jenkins job.

- commit with a message like _Eclipse Embedded CDT plug-ins v6.6.0 released_.
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

- tag: _v6.6.0_ (with `v`)
- title _Eclipse Embedded CDT plug-ins v6.6.0_
- copy/paste from the release page
- add `[Continue reading »](https://eclipse-embed-cdt.github.io/blog/YYYY/MM/DD/plugins-v6.6.0-released/)` with a link to the web page
- do not attach files
- click **Publish release**

### Close milestone

In [GitHub milestones](https://github.com/eclipse-embed-cdt/eclipse-plugins/milestones):

- close the v6.6.0 milestone

### Update the Eclipse release record

- go to [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/governance/)
and select the new release
- click Edit -> The Basics
- switch to Source mode
- replace the entire content with links to GitHub:

```html
<p>Version <strong>6.6.0</strong> is a new major/minor/service release; it updates ...</p>

<p>Fore more details, please read the project web release pages:</p>

<ul>
 <li><a href="https://eclipse-embed-cdt.github.io/blog/YYYY/MM/DD/package-2020-12-released/">Eclipse IDE for Embedded C/C++ Developers 2020-12 released</a>&nbsp;(for installing a new Eclipse)</li>
 <li><a href="https://eclipse-embed-cdt.github.io/blog/YYYY/MM/DD/plugins-v6.6.0-released/">Eclipse Embedded CDT plug-ins v6.6.0 released</a>&nbsp;(for updating the plug-ins on an existing Eclipse)</li>
</ul>
```

Click the **Save** button.

### Update the Downloads page (deprecated)

For packages releases:

- go to [iot.embed-cdt](https://projects.eclipse.org/projects/iot.embed-cdt/)
- click Edit -> The Basics
- switch to Source mode

Use copy/paste/edit.

## Check & update SimRel

**Pull** new commits.

NOTE: to be updated for Pull Requests!

In Eclipse:

- import existing project `org.eclipse.simrel.build`
- open `simrel.aggr`
- expand the 'Contribution: Embedded CDT'
- select **Mapped Repository**
- right click: **Show Properties View**
- in the right side, edit the **Location** field to the new release p2 URL
  (like `https://download.eclipse.org/embed-cdt/releases/6.6.0/p2/`)
  and press Enter
- select all the features in the contribution, right-click and choose
**Fix Versions**
- select the Contribution and **Validate**
- select the Aggregation and **Validate**
- stage `embededcdt.aggrcom`
- commit with a message like:
  - _embedcdt: update for 6.6.0_
  - _Signed-off-by: Liviu Ionescu <ilg@livius.net>_
- click the **Commit** button (do not push yet)
- right click, Show in local Terminal

```bash
git push ssh://lionescu@git.eclipse.org:29418/simrel/org.eclipse.simrel.build HEAD:refs/for/master
```

This will trigger a Gerrit run.

Check the console output, for the Gerrit link. If missed, it'll be
later sent by e-mail, when the run completes.

In Gerrit, click **CODE_REVIEW+2** and then **SUBMIT** to merge the changes.

After a while (it might take about 10 minutes),
the commit will trigger the [SimRel](https://ci.eclipse.org/simrel/)
Jenkins aggregator pipeline:

- <https://ci.eclipse.org/simrel/job/simrel.runaggregator.pipeline/>

After another while (about 10 min) it'll automatically rebuild the staging repo:

- <https://download.eclipse.org/staging/>

## Announce release

Announce the release to the **embed-cdt-dev@eclipse.org** list;
use a subject like **Eclipse Embedded CDT plug-ins v6.6.0 released**, and
pass a link to the release page (<https://eclipse-embed-cdt.github.io/news/>).

## Share on Twitter

- in a separate browser windows, open [X/Twitter](https://twitter.com/)
- using the `@EmbedCDT` account, enter a message like
  **Eclipse Embedded CDT plug-ins v6.6.0 released** and on the next line
  paste the link to the release
- click the Tweet button
