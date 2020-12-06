package org.eclipse.embedcdt.core.liqp;

import org.eclipse.embedcdt.core.liqp.parser.Flavor;

public class ParseSettings {

    public final Flavor flavor;
    public final boolean stripSpacesAroundTags;

    public static class Builder {

        Flavor flavor;
        boolean stripSpacesAroundTags;

        public Builder() {
            this.flavor = Flavor.LIQUID;
            this.stripSpacesAroundTags = false;
        }

        public Builder withFlavor(Flavor flavor) {
            this.flavor = flavor;
            return this;
        }

        public Builder withStripSpaceAroundTags(boolean stripSpacesAroundTags) {
            this.stripSpacesAroundTags = stripSpacesAroundTags;
            return this;
        }

        public ParseSettings build() {
            return new ParseSettings(this.flavor, this.stripSpacesAroundTags);
        }
    }

    private ParseSettings(Flavor flavor, boolean stripSpacesAroundTags) {
        this.flavor = flavor;
        this.stripSpacesAroundTags = stripSpacesAroundTags;
    }
}
