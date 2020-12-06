package org.eclipse.embedcdt.core.liqp.exceptions;


public class VariableNotExistException extends RuntimeException {
    private final String variableName;

    public VariableNotExistException(String variableName) {
        super(String.format("Variable '%s' does not exists", variableName));

        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }
}
