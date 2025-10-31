package simpledebugger;

import com.sun.jdi.*;
import com.sun.jdi.connect.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SimpleDebugger {
	public static void main(String[] args) throws InterruptedException {
		String host = args.length > 0 ? args[0] : "localhost";
		int port = args.length > 1 ? Integer.parseInt(args[1]) : 5005;

		VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
		AttachingConnector connector = vmm.attachingConnectors().stream()
				.filter(c -> c.name().equals("com.sun.jdi.SocketAttach")).findFirst().orElseThrow();

		Map<String, Connector.Argument> arguments = connector.defaultArguments();
		arguments.get("hostname").setValue(host);
		arguments.get("port").setValue(String.valueOf(port));

		System.out.println("Connecting to " + host + ":" + port + "...");
		VirtualMachine vm = null;
		try {
			vm = connector.attach(arguments);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalConnectorArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to VM: " + vm.name());
		EventRequestManager erm = vm.eventRequestManager();
		// Найдём метод sayHello и поставим Breakpoint
		//List<ReferenceType> classes = vm.classesByName("target.Target");
		List<ReferenceType> classes = vm.allClasses();
		if (classes.isEmpty()) {
			System.out.println("Target class not loaded yet. Waiting...");
			vm.resume();
			Thread.sleep(1000);
			classes = vm.allClasses();
		}
		System.out.println(classes.size());
		classes.stream().forEach(c -> System.out.println(c.getClass()));
		List<ReferenceType> selectedClasses = classes.stream().filter(c -> c.getClass().toString().contains("tar")).toList();
		selectedClasses.stream().forEach(c -> System.out.println("SELECTED: " + c.getClass()));
		Optional<ReferenceType> targetClass = classes.stream().filter(c -> c.getClass().toString().contains("tar")).findAny();
		targetClass.ifPresentOrElse(c -> System.out.println(c + "  FOUND"), () -> System.out.println(" NOT FOUND"));
		//ReferenceType targetClass = classes.get(0);
		//Method method = targetClass.methodsByName("sayHello").get(0);
		Method method = targetClass.get().methodsByName("sayHello").get(0);
		Location location = method.location();
		BreakpointRequest bpReq = erm.createBreakpointRequest(location);
		bpReq.enable();

		EventQueue queue = vm.eventQueue();

		System.out.println("Waiting for events...");

		while (true) {
			EventSet eventSet = null;
			try {
				eventSet = queue.remove();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (Event event : eventSet) {
				if (event instanceof BreakpointEvent be) {
					System.out.println("Breakpoint hit at method: " + be.location().method().name());
					vm.resume(); // продолжить Target
				}
			}
		}
	}
}
