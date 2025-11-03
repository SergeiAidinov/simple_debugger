package com.gmail.aydinov.sergey.simpledebugger.dto;

import java.util.Set;

import com.sun.jdi.Field;
import com.sun.jdi.Method;

public class TargetApplicationInterfaceRepresentation implements TargetApplicationElementRepresentation {
	
	private final String targetApplicationElementName;
	private final TargetApplicationElementType targetApplicationElementType;
	private final Set<com.sun.jdi.Method> methods;
	private final Set<com.sun.jdi.Field> fields;
	
	public TargetApplicationInterfaceRepresentation(String targetApplicationElementName,
			TargetApplicationElementType targetApplicationElementType, Set<Method> methods, Set<Field> fields) {
		super();
		this.targetApplicationElementName = targetApplicationElementName;
		this.targetApplicationElementType = targetApplicationElementType;
		this.methods = methods;
		this.fields = fields;
	}
	
	public Set<com.sun.jdi.Method> getMethods() {
		return methods;
	}

	public Set<com.sun.jdi.Field> getFields() {
		return fields;
	}
	
	public String getTargetApplicationElementName() {
		return targetApplicationElementName;
	}

	public TargetApplicationElementType getTargetApplicationElementType() {
		return targetApplicationElementType;
	}

}
