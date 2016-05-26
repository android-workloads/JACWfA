package com.intel.mttest.exception;

import com.intel.mttest.representation.TestInterface;

public class InvalidTestFormatException extends MTTestException {

	private static final long serialVersionUID = 1L;
	private String message;
	Class<? extends TestInterface> testClass;
	

	public InvalidTestFormatException(Class<? extends TestInterface> testClass) {
		super();
		this.testClass = testClass;
	}

	public InvalidTestFormatException(String message,
			Class<? extends TestInterface> testClass) {
		super(message);
		this.message = message;
		this.testClass = testClass;
	}

	public InvalidTestFormatException(String message, Throwable cause,
			Class<? extends TestInterface> testClass) {
		super(message, cause);
		this.message = message;
		this.testClass = testClass;
	}

	@Override
	public String getMessage() {
		if (message == null) {
			return "Error appeared because of wrong test settings or it's source code. Caused "
					+ getCause().getClass().getName()
					+ "("
					+ getCause().getMessage()
					+ ")"
					+ " . Class: "
					+ testClass.getName();
		} else {
			return message + ". Class: " + testClass.getName();
		}
	}

}
