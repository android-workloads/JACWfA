package com.intel.mttest.exception;

public class WorkloadNotFoundException extends MTTestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String className;
	
	public WorkloadNotFoundException(String className){
		this.className = className;
	}
	
	@Override
	public String getMessage(){
		return "Workload with name '" + className  + "' is not found in the system.";
	}
	
}
