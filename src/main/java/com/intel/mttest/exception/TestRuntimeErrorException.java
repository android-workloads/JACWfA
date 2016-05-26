package com.intel.mttest.exception;

public class TestRuntimeErrorException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public TestRuntimeErrorException(String msg){
		super(msg);
	}
}
