package org.eclipse.embedcdt.core.liqp.exceptions;

public class ExceededMaxIterationsException extends RuntimeException {

    public ExceededMaxIterationsException(int maxIterations) {
        super("exceeded maxIterations: " + maxIterations);
    }
}
