package com.intel.mttest.exception;

import java.util.Locale;

public class MTTestException extends Exception {

	private static final long serialVersionUID = 1L;

	public MTTestException() {
		super();
	}

	public MTTestException(String message) {
		super(message);
	}

	public MTTestException(Throwable cause) {
		super(cause);
	}

	public MTTestException(String message, Throwable cause) {
		super(message, cause);
	}
	public String getShortMessage() {
		return super.getMessage();
	}
	public String getMessage() {
		Throwable cause = getCause();
		String message = super.getMessage();
		StringBuilder errorMessage = new StringBuilder("Unclassified error ("
				+ this.getClass().getName() + ")");
		if (message != null) {
			errorMessage.append(": " + message + ".");
		}
		if (cause != null) {
			errorMessage.append(String.format(Locale.ENGLISH,
					" Caused by %s\n", cause.getClass().getName()));
		}
		StackTraceElement[] stackTrace = getStackTrace();
		for (StackTraceElement elem : stackTrace) {
			errorMessage.append("\t" + elem.toString() + "\n");
		}
		if (cause != null) {
			errorMessage.append(cause.getClass().getName() + ":");
			if (cause.getMessage() != null) {
				errorMessage.append(" " + cause.getMessage());
			}
			errorMessage.append("\n");
			stackTrace = cause.getStackTrace();
			for (StackTraceElement elem : stackTrace) {
				errorMessage.append("\t" + elem.toString() + "\n");
			}
		}
		return errorMessage.toString();
	}
}
