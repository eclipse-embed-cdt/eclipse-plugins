
## Prerequisites

### Clone EPP

At first use, clone the EPP Git repo:

```bash
git clone ssh://lionescu@git.eclipse.org:29418/epp/org.eclipse.epp.packages.git org.eclipse.epp.packages.git
mkdir -p org.eclipse.epp.packages.git/.git/hooks/
scp -p -P 29418 lionescu@git.eclipse.org:hooks/commit-msg org.eclipse.epp.packages.git/.git/hooks/
```

### EPP deadline 

The deadline for EPP changes is **Thu 9am Ottawa time**.

## Check & update EPP

If the list of features changed, it is necessary to
update the EPP project.

Pull new commits.

- edit `packages/org.eclipse.epp.package.embedcpp.product/epp.product`
- update the list of features

### package.embedcpp

To change the default preferences, edit the
`packages/org.eclipse.epp.package.embedcpp/plugin_customization.ini`

### package.embedcpp.feature

If necessary, update the text displayed in the Downloads page, it is in
`packages/org.eclipse.epp.package.embedcpp.feature/epp.website.xml` file, the
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
git commit -m 'embedcpp: ...'
git push ssh://lionescu@git.eclipse.org:29418/epp/org.eclipse.epp.packages.git HEAD:refs/for/master
```

In Gerrit, click **CODE_REVIEW+2** and then **SUBMIT** to merge the changes.

The commit will trigger the [EPP](https://ci.eclipse.org/packaging/)
Jenkins job:

- <https://ci.eclipse.org/packaging/job/simrel.epp-tycho-build/>

In 1.5 hours the new test versions of the integrated epp builds are
available from:

- <https://ci.eclipse.org/packaging/job/simrel.epp-tycho-build/lastSuccessfulBuild/artifact/org.eclipse.epp.packages/archive/>

