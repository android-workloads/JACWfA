package com.intel.mttest.exception;

import java.io.IOException;

import org.xml.sax.SAXException;

public class InvalidMTTestConfFileException extends MTTestException {

	private static final long serialVersionUID = 1L;

	public InvalidMTTestConfFileException(IOException cause,
			String fileName) {
		this((Exception) cause, fileName);
	}

	public InvalidMTTestConfFileException(SAXException cause, String fileName) {
		this((Exception) cause, fileName);
	}

	public InvalidMTTestConfFileException(InvalidTestParameterException cause,
			String fileName) {
		this((Exception) cause, fileName);
	}

	public InvalidMTTestConfFileException(WorkloadNotFoundException cause,
			String fileName) {
		this((Exception) cause, fileName);
	}

	private InvalidMTTestConfFileException(Exception cause, String fileName) {
		super(cause.getMessage() + " File: '" + fileName + "'.", cause);
	}

}
