package com.gmail.aydinov.sergey.simpledebugger.dto;

import java.util.Set;

import com.sun.jdi.Field;
import com.sun.jdi.Method;

public class ReferenceInfo {
	
	Set<com.sun.jdi.Method> methods;
	Set<com.sun.jdi.Field> fields;
	
	public ReferenceInfo(Set<Method> methods, Set<Field> fields) {
		super();
		this.methods = methods;
		this.fields = fields;
	}

	public Set<com.sun.jdi.Method> getMethods() {
		return methods;
	}

	public Set<com.sun.jdi.Field> getFields() {
		return fields;
	}
	
}
