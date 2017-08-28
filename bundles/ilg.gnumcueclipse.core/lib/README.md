I could not find an easy way to bring those JARs automatically, so I copied
them manually from ${HOME}/.m2/repository.

I got the actual versions from the `pom.xml` editor, the _Dependency
Hierarchy_ view.

To add them to the project, in the `plugin.xml` editor, the _Runtime_ tab, 
_Classpath_ -> _Add..._; then, to export the `liqp` class, in the 
_Exported Packages_ -> _Add..._.

Note: The Liquid parser is patched, to allow for more convenient white space
processing. Only `{% %}` tags are affected.