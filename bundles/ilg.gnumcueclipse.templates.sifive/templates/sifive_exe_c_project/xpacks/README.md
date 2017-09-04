## xPacks

Each xPack is a separate GitHub project, referred here as a git submodule.

To link new submodules, use:

```bash
$ cd ilg.gnumcueclipse.templates.sifive/templates/sifive_exe_c_project/xpacks
$ git submodule add -b xpack https://github.com/micro-os-plus/diag-trace.git micro-os-plus-diag-trace
$ git submodule init
```

To update to new submodule commits:

```bash
$ git submodule update --remote
```

