package com.mama100.monitorcenter.client;

public class LogUtil {
	
	public static final String level_info = "info";
	public static final String level_error = "error";
	
	
	public static String getStackTrace(Throwable e) {

		StringBuffer stack = new StringBuffer();
		stack.append(e);
		stack.append("\r\n");
		stack.append(e.getMessage());
		stack.append("\r\n");

		Throwable rootCause = e.getCause();

		while (rootCause != null) {
			stack.append("Root Cause:\r\n");
			stack.append(rootCause);
			stack.append("\r\n");
			stack.append(rootCause.getMessage());
			stack.append("\r\n");
			stack.append("StackTrace:\r\n");
			stack.append(rootCause);
			stack.append("\r\n");
			rootCause = rootCause.getCause();
		}

		for (int i = 0; i < e.getStackTrace().length; i++) {
			stack.append(e.getStackTrace()[i].toString());
			stack.append("\r\n");
		}

		return stack.toString();
	}

}
