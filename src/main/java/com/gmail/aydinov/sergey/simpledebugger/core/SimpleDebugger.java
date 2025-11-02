package com.gmail.aydinov.sergey.simpledebugger.core;

import java.io.IOException;

import com.sun.jdi.AbsentInformationException;

public class SimpleDebugger {
	public static void main(String[] args) throws InterruptedException, AbsentInformationException, IOException {
		String host = args.length > 0 ? args[0] : "localhost";
		Integer port = args.length > 1 ? Integer.parseInt(args[1]) : 5005;
		SimpleDebuggerWorkFlow.instanceOfHostAndPort(host, port).debug();
	}
}
