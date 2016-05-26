package com.intel.mttest.exception;

import com.intel.mttest.representation.TestInterface;

public class NoSuchTestParamException extends InvalidTestFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String paramName;

	public NoSuchTestParamException(String paramName) {
		super(null);
	}
	
	public NoSuchTestParamException(String paramName,
			Class<? extends TestInterface> testClass) {
		super(testClass);
		this.paramName = paramName;
	}

	@Override
	public String getMessage() {
		if(testClass == null){
			return "Test parameter named '" + paramName
					+ "' does not exist.";
		}
		return "Test parameter named '" + paramName
				+ "' does not exist in the class '" + testClass.getName()
				+ "'.";
	}
}
