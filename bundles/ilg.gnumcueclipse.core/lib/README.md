I could not find an easy way to bring those JARs automatically, so I copied
them manually from ${HOME}/.m2/repository.

I got the actual versions from the `pom.xml` editor, the _Dependency
Hierarchy_ view.

To add them to the project, in the `plugin.xml` editor, the _Runtime_ tab, 
_Classpath_ -> _Add..._; then, to export the `liqp` class, in the 
_Exported Packages_ -> _Add..._.

Note: The [Liquid parser](https://github.com/bkiers/Liqp) is [patched](https://github.com/gnu-mcu-eclipse/Liqp), to allow for more convenient white space processing, i.e. `-%}` should not be greedy and stop after the first line terminator.

To build the liqp library

- go to the `libq-fork.git` folder
- add maven to PATH (amaven)
- mvn clean package
- copy target/liqp-0.6.8.jar

