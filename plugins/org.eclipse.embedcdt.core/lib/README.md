# Dependencies

## org.jsoup

The `org.jsoup_1.7.2.v201411291515.jar` archive is downloaded from
[Orbit R20201130205003](https://download.eclipse.org/tools/orbit/downloads/drops/R20201130205003/).

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

From [Bugzilla](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=21415#c27)

Already approved

```console
> ./plugins/org.eclipse.embedcdt.core/lib/json-simple-1.1.1.jar
Already approved. CQ#9858
```

Submitted as separate CQ

```console
> ./plugins/org.eclipse.embedcdt.core/lib/org.eclipse.embedcdt.core.liqp-0.6.8.jar

Good contender for first submission. It appears to be MIT license and I don't
think there is a CQ about it already.
https://github.com/bkiers/Liqp/releases/tag/org.eclipse.embedcdt.core.liqp-0.6.8
```

To be upgraded:

```console
> ./plugins/org.eclipse.embedcdt.core/lib/ST4-4.0.7.jar
Similar versions already approved - so if you can use one of them it will be a
little easier. CQ#14504 is for 4.0.8

> ./plugins/org.eclipse.embedcdt.core/lib/antlr-runtime-3.5.1.jar
Similar versions already approved - so if you can use one of them it will be a
little easier. CQ#9433 is for 3.5.2 (https://dev.eclipse.org/ipzilla/show_bug.cgi?id=9433)

Jackson has already been approved - however not the exact versions you are
using today.
https://download.eclipse.org/tools/orbit/downloads/drops/R20200224183213/ has
2.9.9 version and 2.2.2 was previously approved. So if it needs to be 2.2.3 it
should be easy. Even easier is to use the version in Orbit as nothing else
needs to be done.

> ./plugins/org.eclipse.embedcdt.core/lib/jackson-databind-2.2.3.jar
Jackson comment as per above.

> ./plugins/org.eclipse.embedcdt.core/lib/jackson-core-2.2.3.jar
Jackson comment as per above.

> ./plugins/org.eclipse.embedcdt.core/lib/jackson-annotations-2.2.3.jar
Jackson comment as per above.

```

### antlr ST4-*.jar

Was ST4-4.0.7.jar.

[CQ#14504](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=14504) for 4.0.8

org.antlr.ST4 Version:4.0.8, StringTemplate is a java template engine ...

https://mvnrepository.com/artifact/org.antlr/ST4/4.0.8
https://repo1.maven.org/maven2/org/antlr/ST4/4.0.8/ST4-4.0.8.jar

### antlr-runtime-*.jar

Was antlr-runtime-3.5.1.jar.

[CQ#9433](https://dev.eclipse.org/ipzilla/show_bug.cgi?id=9433) for 3.5.2

https://mvnrepository.com/artifact/org.antlr/antlr-runtime/3.5.2
https://repo1.maven.org/maven2/org/antlr/antlr-runtime/3.5.2/antlr-runtime-3.5.2.jar

### jackson

https://download.eclipse.org/tools/orbit/downloads/drops/R20200224183213/

https://mvnrepository.com/artifact/com.fasterxml.jackson.core

https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core/2.9.9
https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.9.9/jackson-core-2.9.9.jar

https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.9.9.3
https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.9.9.3/jackson-databind-2.9.9.3.jar

https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations/2.9.9
https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.9.9/jackson-annotations-2.9.9.jar
