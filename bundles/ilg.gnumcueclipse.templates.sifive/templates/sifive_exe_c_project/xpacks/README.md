## xPacks

Each xPack is a separate GitHub project, referred here as a git submodule.

To link new submodules, use:

```bash
$ git submodule add -b xpack https://github.com/micro-os-plus/sifive-coreplex-templates.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-coreplex-templates
$ git submodule add -b xpack https://github.com/micro-os-plus/diag-trace.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-diag-trace
$ git submodule add -b xpack https://github.com/micro-os-plus/startup.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-startup
$ git submodule add -b xpack https://github.com/micro-os-plus/c-libs.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-c-libs
$ git submodule add -b xpack https://github.com/micro-os-plus/cpp-libs.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-cpp-libs
$ git submodule add -b xpack  https://github.com/micro-os-plus/riscv-arch.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/micro-os-plus-riscv-arch
$ git submodule add -b xpack https://github.com/micro-os-plus/sifive-coreplex-devices.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-coreplex-devices
$ git submodule add -b xpack https://github.com/micro-os-plus/sifive-coreplex-arty-boards.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-coreplex-arty-boards
$ git submodule add -b xpack https://github.com/micro-os-plus/sifive-hifive1-board.git bundles/ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks/sifive-hifive1-board
$ git submodule init
```

To pull all submodules after cloning the main repo:

```bash
$ git submodule update --init --remote
```

For further updates to new submodule commits:

```bash
$ git submodule update --remote
```


