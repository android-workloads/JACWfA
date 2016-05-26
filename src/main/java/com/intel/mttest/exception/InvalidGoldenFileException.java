package com.intel.mttest.exception;

import java.io.File;

import com.intel.mttest.representation.TestInterface;

public class InvalidGoldenFileException extends InvalidTestFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File goldenPath;
	private String expectedExt;
	private String actualExt;

	public InvalidGoldenFileException(File path, String expectedExt,
			String actualExt, Class<? extends TestInterface> testClass) {
		super(testClass);
		goldenPath = path;
		this.expectedExt = expectedExt;
		this.actualExt = actualExt;
	}

	@Override
	public String getMessage() {
		return "Format of the file " + goldenPath.getAbsolutePath()
				+ ". Expected extention " + expectedExt + ", actual "
				+ actualExt + ". Class: " + testClass.getName();
	}
}
