package com.gmail.aydinov.sergey.simpledebugger.core;

import java.util.Collection;
import java.util.List;

import com.gmail.aydinov.sergey.simpledebugger.dto.SimpleDebuggerBreakpointInfo;

public interface SimpleDebuggerBreakpointProvider {

    List<SimpleDebuggerBreakpointInfo> getBreakpoints();

    void addBreakpoint(SimpleDebuggerBreakpointInfo breakpoint);

    void addBreakpoints(Collection<SimpleDebuggerBreakpointInfo> breakpoints); // <- массовое добавление

    void removeBreakpoint(SimpleDebuggerBreakpointInfo breakpoint);

    void removeBreakpoints(Collection<SimpleDebuggerBreakpointInfo> breakpoints); // <- массовое удаление
}

