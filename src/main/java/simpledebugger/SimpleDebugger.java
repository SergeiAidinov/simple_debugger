package simpledebugger;

import com.sun.jdi.*;
import com.sun.jdi.connect.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleDebugger {
	public static void main(String[] args) throws InterruptedException, AbsentInformationException, IOException {
		String host = args.length > 0 ? args[0] : "localhost";
		int port = args.length > 1 ? Integer.parseInt(args[1]) : 5005;
		SimpleDebuggerWorkFlow.instance().debug(host, port);
	}
}
