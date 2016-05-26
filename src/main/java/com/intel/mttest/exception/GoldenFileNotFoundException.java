package com.intel.mttest.exception;

import java.io.File;

import com.intel.mttest.representation.TestInterface;

public class GoldenFileNotFoundException extends InvalidTestFormatException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File goldenPath;

	public GoldenFileNotFoundException(File path,
			Class<? extends TestInterface> testClass) {
		super(testClass);
		goldenPath = path;
	}

	@Override
	public String getMessage() {
		return "Golden file " + goldenPath.getAbsolutePath() + " is missing. Class: "
				+ testClass.getName();
	}
}
