package com.gmail.aydinov.sergey.simpledebugger.dto;

import java.util.Set;

public interface TargetApplicationElementRepresentation {

	Set<com.sun.jdi.Method> getMethods();

	Set<com.sun.jdi.Field> getFields();
	
	String getTargetApplicationElementName();

	TargetApplicationElementType getTargetApplicationElementType();
	
	String prettyPrint();

}
