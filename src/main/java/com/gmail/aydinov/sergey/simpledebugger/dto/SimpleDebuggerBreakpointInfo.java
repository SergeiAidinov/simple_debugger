package com.gmail.aydinov.sergey.simpledebugger.dto;


public class SimpleDebuggerBreakpointInfo {
    private final String methodName;
    private final int lineNumber;

    public SimpleDebuggerBreakpointInfo(String methodName, int lineNumber) {
        this.methodName = methodName;
        this.lineNumber = lineNumber;
    }


    public String getMethodName() {
        return methodName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return methodName + ":" + lineNumber;
    }
}

