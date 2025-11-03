package com.gmail.aydinov.sergey.simpledebugger.dto;

import java.util.Set;

import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.Method;

public class TargetApplicationClassRepresentation implements TargetApplicationElementRepresentation {

	private final String targetApplicationElementName;
	private final TargetApplicationElementType targetApplicationElementType;
	private final Set<com.sun.jdi.Method> methods;
	private final Set<com.sun.jdi.Field> fields;

	public TargetApplicationClassRepresentation(String targetApplicationElementName,
			TargetApplicationElementType targetApplicationElementType, Set<Method> methods, Set<Field> fields) {
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

	@Override
	public String toString() {
		return "ReferenceInfo [methods=" + methods + ", fields=" + fields + ", getMethods()=" + getMethods()
				+ ", getFields()=" + getFields() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	public String prettyPrint() {
		String meth = methods.stream().map(m -> m.name() + "(" + String.join(", ", m.argumentTypeNames()) + ")")
				.sorted().collect(java.util.stream.Collectors.joining("\n    "));

		String fld = fields.stream().map(f -> f.typeName() + " " + f.name()).sorted()
				.collect(java.util.stream.Collectors.joining("\n    "));

		StringBuilder sb = new StringBuilder();
		// sb.append("Class: ").append(className).append("\n");
		// sb.append("Loader: ").append(classLoaderName).append("\n\n");

		sb.append("Methods(").append(methods.size()).append("):\n");
		sb.append("    ").append(meth).append("\n\n");

		sb.append("Fields(").append(fields.size()).append("):\n");
		sb.append("    ").append(fld);

		return sb.toString();
	}

}
