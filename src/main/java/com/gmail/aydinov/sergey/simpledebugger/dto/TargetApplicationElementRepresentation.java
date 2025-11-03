package com.gmail.aydinov.sergey.simpledebugger.dto;

import java.util.Collection;

import com.sun.jdi.Method;

public interface TargetApplicationElementRepresentation {

	Collection<Method> getMethods();

}
