package com.intel.JACW.util;

/**
 * This class contains some constants.
 */
public class Resources {

	public static final String passerJNIParamLibName = "passerJNIParam";
	private static String lastResult;
	
	public static String getLastResult() {
		return lastResult;
	}
	
	public static void writeHelperBuffer(Class<?> src, String result) {
		lastResult = result;
	}
}
