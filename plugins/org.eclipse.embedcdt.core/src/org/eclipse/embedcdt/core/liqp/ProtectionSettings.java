package org.eclipse.embedcdt.core.liqp;

import org.eclipse.embedcdt.core.liqp.exceptions.ExceededMaxIterationsException;

public class ProtectionSettings {

    public final int maxIterations;
    public final int maxSizeRenderedString;
    public final long maxRenderTimeMillis;
    public final long maxTemplateSizeBytes;

    // A global counter that keeps track of the amount of iterations
    private int iterations = 0;

    public static class Builder {

        private int maxIterations;
        private int maxSizeRenderedString;
        private long maxRenderTimeMillis;
        private long maxTemplateSizeBytes;

        public Builder() {
            this.maxIterations = Integer.MAX_VALUE;
            this.maxSizeRenderedString = Integer.MAX_VALUE;
            this.maxRenderTimeMillis = Long.MAX_VALUE;
            this.maxTemplateSizeBytes = Long.MAX_VALUE;
        }

        public Builder withMaxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        public Builder withMaxSizeRenderedString(int maxSizeRenderedString) {
            this.maxSizeRenderedString = maxSizeRenderedString;
            return this;
        }

        public Builder withMaxRenderTimeMillis(long maxRenderTimeMillis) {
            this.maxRenderTimeMillis = maxRenderTimeMillis;
            return this;
        }

        public Builder withMaxTemplateSizeBytes(long maxTemplateSizeBytes) {
            this.maxTemplateSizeBytes = maxTemplateSizeBytes;
            return this;
        }

        public ProtectionSettings build() {
            return new ProtectionSettings(this.maxIterations, this.maxSizeRenderedString, this.maxRenderTimeMillis, this.maxTemplateSizeBytes);
        }
    }

    private ProtectionSettings(int maxIterations, int maxSizeRenderedString, long maxRenderTimeMillis, long maxTemplateSizeBytes) {
        this.maxIterations = maxIterations;
        this.maxSizeRenderedString = maxSizeRenderedString;
        this.maxRenderTimeMillis = maxRenderTimeMillis;
        this.maxTemplateSizeBytes = maxTemplateSizeBytes;
    }

    public void incrementIterations() {

        this.iterations++;

        if (this.iterations > this.maxIterations) {
            throw new ExceededMaxIterationsException(this.maxIterations);
        }
    }
}
