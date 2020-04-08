## Liquid

I could not find an easy way to bring those JARs automatically, so I copied
them manually from ${HOME}/.m2/repository.

I got the actual versions from the `pom.xml` editor, the _Dependency
Hierarchy_ view.

To add them to the project, in the `plugin.xml` editor, the _Runtime_ tab, 
_Classpath_ -> _Add..._; then, to export the `liqp` class, in the 
_Exported Packages_ -> _Add..._.

Note: The [Liquid parser](https://github.com/bkiers/Liqp) is [patched](https://github.com/eclipse-embed-cdt/Liqp), to allow for more convenient white space processing, i.e. `-%}` should not be greedy and stop after the first line terminator.

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
     
     
## Eclipse Orbit

[Downloads](https://download.eclipse.org/tools/orbit/downloads/)

From https://dev.eclipse.org/ipzilla/show_bug.cgi?id=21415#c27

```
Jackson has already been approved - however not the exact versions you are
using today.
https://download.eclipse.org/tools/orbit/downloads/drops/R20200224183213/ has
2.9.9 version and 2.2.2 was previously approved. So if it needs to be 2.2.3 it
should be easy. Even easier is to use the version in Orbit as nothing else
needs to be done.

> ./plugins/ilg.gnumcueclipse.core/lib/liqp-0.6.8.jar

Good contender for first submission. It appears to be MIT license and I don't
think there is a CQ about it already.
https://github.com/bkiers/Liqp/releases/tag/liqp-0.6.8

> ./plugins/ilg.gnumcueclipse.core/lib/antlr-runtime-3.5.1.jar

Similar versions already approved - so if you can use one of them it will be a
little easier. CQ#9433 is for 3.5.2 (https://dev.eclipse.org/ipzilla/show_bug.cgi?id=9433)

> ./plugins/ilg.gnumcueclipse.core/lib/jackson-databind-2.2.3.jar
Jackson comment as per above.

> ./plugins/ilg.gnumcueclipse.core/lib/jackson-core-2.2.3.jar
Jackson comment as per above.

> ./plugins/ilg.gnumcueclipse.core/lib/json-simple-1.1.1.jar
Already approved. CQ#9858

> ./plugins/ilg.gnumcueclipse.core/lib/ST4-4.0.7.jar
Similar versions already approved - so if you can use one of them it will be a
little easier. CQ#14504 is for 4.0.8

> ./plugins/ilg.gnumcueclipse.core/lib/jackson-annotations-2.2.3.jar
Jackson comment as per above.
```

### ST4-4.0.7.jar

[CQ#14504](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=14504)

org.antlr.ST4 Version:4.0.8, StringTemplate is a java template engine ...

https://mvnrepository.com/artifact/org.antlr/ST4/4.0.8
https://repo1.maven.org/maven2/org/antlr/ST4/4.0.8/ST4-4.0.8.jar





