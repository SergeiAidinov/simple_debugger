package com.gmail.aydinov.sergey.simpledebugger.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import javax.xml.stream.util.EventReaderDelegate;

import com.gmail.aydinov.sergey.simpledebugger.dto.ReferenceInfo;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;

public class SimpleDebuggerWorkFlow {

	private VirtualMachine virtualMachine = null;
	private final Map<ReferenceType, ReferenceInfo> referencesAtClasses = new HashMap<ReferenceType, ReferenceInfo>();
	//private List<ReferenceType> fields;
	private String host;
	private Integer port;
	private static final Map<SimpleDebuggerWorkFlowIdentifier, SimpleDebuggerWorkFlow> CACHE = new WeakHashMap<>();

	private SimpleDebuggerWorkFlow(String host, int port) throws IllegalStateException {
		this.host = host;
		this.port = port;
		try {
			configureVirtualMachine();
		} catch (IOException e) {
			throw new IllegalStateException();
		}
		createReferencesToClassesOfTargetApplication();
	}

	public static synchronized SimpleDebuggerWorkFlow instanceOfHostAndPort(String host, Integer port) {
		SimpleDebuggerWorkFlowIdentifier simpleDebuggerWorkFlowidentifier = 
				new SimpleDebuggerWorkFlowIdentifier(host, port);
		return CACHE.computeIfAbsent(simpleDebuggerWorkFlowidentifier, k -> new SimpleDebuggerWorkFlow(host, port));
	}

	public void debug() throws IOException, AbsentInformationException {
		EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
		System.out.println(">>>" + referencesAtClasses.size());
		Method method = null;
		for (Entry<ReferenceType, ReferenceInfo> entry : referencesAtClasses.entrySet()) {
			System.out.println("==> " + entry);
			entry.getValue().getFields().forEach(v -> System.out.println(v));
			entry.getValue().getMethods().forEach(v -> System.out.println(v));
			method = 
					//entry.getValue().getMethods().stream().forEach(m -> System.out.println(m.toString()));
					entry.getValue().getMethods().stream().filter(m -> m.name().contains("sayHello")).findAny().get();
		}
		/*
		 * fields.stream().forEach(f -> System.out.println(f)); ReferenceType
		 * targetClass = referencesAtClasses.get(0);
		 */
		
		
		//Method method = targetClass.methodsByName("sayHello").get(0);
		Location location = method.location();
		BreakpointRequest bpReq = eventRequestManager.createBreakpointRequest(location);
		bpReq.enable();
		EventQueue queue = virtualMachine.eventQueue();
		System.out.println("Waiting for events...");

		while (true) {
			EventSet eventSet = null;
			try {
				eventSet = queue.remove();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (Event event : eventSet) {
				if (event instanceof BreakpointEvent breakpointEvent) {
					System.out.println("Breakpoint hit at method: " + breakpointEvent.location().method().name());
					virtualMachine.resume(); // продолжить Target
				}
			}
		}
	}

	private void createReferencesToClassesOfTargetApplication() {
		System.out.println("Target class not loaded yet. Waiting...");
		List<ReferenceType> referenceTypes = new ArrayList<ReferenceType>();
		while (referenceTypes.isEmpty()) {
			referenceTypes.addAll(virtualMachine.allClasses());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				continue;
			}
		}
		System.out.println("Loaded " + referenceTypes.size() + " classes.");
		Set<ClassLoaderReference> classLoaderReferencesSet = referenceTypes.stream()
				.filter(clr -> Objects.nonNull(clr))
				.map(clr -> clr.classLoader())
				.filter(c -> Objects.nonNull(c))
				//.filter(cl -> cl.toString().contains("target"))
				.collect(Collectors.toSet());
//		Set<ClassLoaderReference> classLoaderReferenceSet = classLoaderReferencesSet.stream().filter(c -> Objects.nonNull(c))
//				.collect(Collectors.toSet());
		List<ReferenceType> targetClasses = new ArrayList<ReferenceType>();
		for (ClassLoaderReference classLoaderReference : classLoaderReferencesSet) {
//			targetClasses.addAll(classLoaderReference.visibleClasses().stream()
//					.filter(cl -> cl.toString().contains("target")).collect(Collectors.toList()));
			if (classLoaderReference.visibleClasses().stream()
			.filter(cl -> cl.toString().contains("target")).collect(Collectors.toList()).isEmpty()) continue;
			
			List<ReferenceType> references = classLoaderReference.definedClasses();
			for (ReferenceType referenceType : references) {
				Set<Field> fields = referenceType.allFields().stream().collect(Collectors.toSet());
				Set<Method> methods = referenceType.allMethods().stream().collect(Collectors.toSet());
				referencesAtClasses.put(referenceType, new ReferenceInfo(methods, fields));
				
			}
		}
		System.out.println("referencesAtClasses: " + referencesAtClasses.size());
		//this.referencesAtClasses = targetClasses;
	}

	private void configureVirtualMachine() throws IOException{
		VirtualMachineManager virtualMachineManager = Bootstrap.virtualMachineManager();
		AttachingConnector connector = virtualMachineManager.attachingConnectors().stream()
				.filter(c -> c.name().equals("com.sun.jdi.SocketAttach")).findAny().orElseThrow();
		Map<String, Connector.Argument> arguments = connector.defaultArguments();
		arguments.get("hostname").setValue(host);
		arguments.get("port").setValue(String.valueOf(port));
		System.out.println("Connecting to " + host + ":" + port + "...");
		VirtualMachine virtualMachine = null;
		try {
			virtualMachine = connector.attach(arguments);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalConnectorArgumentsException e) {
			e.printStackTrace();
		}
		if (Objects.isNull(virtualMachine))
			throw new IOException("Could not attach to VM on port " + port);
		System.out.println("Connected to VM: " + virtualMachine.name());
		this.virtualMachine = virtualMachine;
	}
	
	@Override
	public String toString() {
		return "SimpleDebuggerWorkFlow [virtualMachine=" + virtualMachine + ", referencesAtClasses="
				+ referencesAtClasses + ", host=" + host + ", port=" + port + "]";
	}

	private static class SimpleDebuggerWorkFlowIdentifier {
		private String host;
		private Integer port;
		public SimpleDebuggerWorkFlowIdentifier(String host, Integer port) {
			this.host = host;
			this.port = port;
		}
		@Override
		public int hashCode() {
			return Objects.hash(host, port);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SimpleDebuggerWorkFlowIdentifier other = (SimpleDebuggerWorkFlowIdentifier) obj;
			return Objects.equals(host, other.host) && Objects.equals(port, other.port);
		}
	}

}
