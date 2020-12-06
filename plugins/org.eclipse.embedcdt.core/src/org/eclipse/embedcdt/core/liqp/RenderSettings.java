package org.eclipse.embedcdt.core.liqp;


public class RenderSettings {

    public final boolean strictVariables;

    public static class Builder {

        boolean strictVariables;

        public Builder() {
            this.strictVariables = false;
        }

        public Builder withStrictVariables(boolean strictVariables) {
            this.strictVariables = strictVariables;
            return this;
        }

        public RenderSettings build() {
            return new RenderSettings(this.strictVariables);
        }
    }

    private RenderSettings(boolean strictVariables) {
        this.strictVariables = strictVariables;
    }
}
