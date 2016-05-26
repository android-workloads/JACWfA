package com.intel.mttest.exception;

import com.intel.mttest.config.TestParam;

public class InvalidTestParameterException extends MTTestException {

	private static final long serialVersionUID = 1L;
	private TestParam param;
	private String expectedParamDesc;

	public InvalidTestParameterException(TestParam param,
			String expectedParamDesc) {
		this.param = param;
		this.expectedParamDesc = expectedParamDesc;
	}

	@Override
	public String getMessage() {
		return String.format("Value of '%s' is '%s', but expected %s.", param.getName(), param.getValue(), expectedParamDesc);
	}
}
