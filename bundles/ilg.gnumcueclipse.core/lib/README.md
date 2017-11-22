## Liquid

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

## JSON

The JSON.simple library `json-simple-1.1.1.json` is downloaded from:

https://code.google.com/archive/p/json-simple/downloads

* copy `json-simple-1.1.1.json` to `lib`
* in plugin.xml editor -> **Runtime**
  * **Classpath**
    * **Add...** `lib/json-simple-1.1.1.json`
  * **Exported Packages**
    * **Add...** `org.json.simple`
     