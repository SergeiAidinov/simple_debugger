package simpledebugger;

import com.sun.jdi.*;
import com.sun.jdi.connect.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class SimpleDebugger {
	public static void main(String[] args) {
		String host = args.length > 0 ? args[0] : "localhost";
		int port = args.length > 1 ? Integer.parseInt(args[1]) : 5005;

		VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
		AttachingConnector connector = vmm.attachingConnectors().stream()
		        .filter(c -> c.name().equals("com.sun.jdi.SocketAttach"))
		        .findFirst()
		        .orElseThrow();

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
		vm.resume();
	}
}
