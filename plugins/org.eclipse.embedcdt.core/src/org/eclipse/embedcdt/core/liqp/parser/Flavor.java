package org.eclipse.embedcdt.core.liqp.parser;

public enum Flavor {

    LIQUID("snippets"),
    JEKYLL("_includes");

    public final String snippetsFolderName;

    Flavor(String snippetsFolderName) {
        this.snippetsFolderName = snippetsFolderName;
    }
}
