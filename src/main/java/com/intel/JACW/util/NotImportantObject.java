package com.intel.JACW.util;

/**
 * This object is used as mock to perform various workloads.
 * 
 */
public class NotImportantObject {
	public long a;
	public long b;
	
	
	public NotImportantObject(long a, long b){
		this.a = a;
		this.b = b;
	}
	
	public NotImportantObject(){
		this(0, 0);
	}
	
	public void setFields(long a, long b){
		this.a = a;
		this.b = b;		
	}
	
	public long getSummOfFields(){
		return a + b;
	}
}
