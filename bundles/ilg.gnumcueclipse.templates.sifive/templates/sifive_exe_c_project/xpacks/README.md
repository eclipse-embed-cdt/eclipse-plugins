## xPacks

Each xPack is a separate GitHub project, referred here as a git submodule.

### Initial link submodules

To link new submodules, use:

```console
$ git submodule add -b xpack https://github.com/micro-os-plus/sifive-templates-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-templates
$ git submodule add -b xpack https://github.com/micro-os-plus/diag-trace-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-diag-trace
$ git submodule add -b xpack https://github.com/micro-os-plus/startup-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-startup
$ git submodule add -b xpack https://github.com/micro-os-plus/c-libs-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-c-libs
$ git submodule add -b xpack https://github.com/micro-os-plus/cpp-libs-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-cpp-libs
$ git submodule add -b xpack  https://github.com/micro-os-plus/riscv-arch-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-riscv-arch
$ git submodule add -b xpack https://github.com/micro-os-plus/sifive-devices-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-devices
$ git submodule add -b xpack https://github.com/micro-os-plus/sifive-arty-boards-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-arty-boards
$ git submodule add -b xpack https://github.com/micro-os-plus/sifive-hifive1-board-xpack.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-hifive1-board
$ git submodule init
```

To pull all submodules after cloning the main repo:

```console
$ git submodule update --init --remote
```

### Further updates

For get further updates:

```console
$ git submodule foreach 'git pull'
```

### Remove

To remove the submodules

```console
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-templates
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-diag-trace
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-startup
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-c-libs
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-cpp-libs
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-riscv-arch
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-devices
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-arty-boards
$ git rm --cached bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-hifive1-board
```

