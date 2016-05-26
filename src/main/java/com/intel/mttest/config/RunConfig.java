package com.intel.mttest.config;

import java.util.ArrayList;

import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.representation.OS;
import com.intel.mttest.representation.TestCase;

public class RunConfig {

	public long rampUpMillis;
	public long durationMillis;
	public long rampDownMillis;
	public boolean isValidating;
	public TimeUnits timeUnits;
	public int threads;
	public OS os;

	public TestParams commonParams;
	public TestParams specialParams;
	
	public ConfigParams config;
	

	public TestParams getCommonParams() {
		return commonParams;
	}
	
	public TestParams getSpecialParams() {
		return specialParams;
	}
	
	public TestParams getAllParams() {
		TestParams ret = new TestParams(TestParams.defaultName);
		ret.add(commonParams.getParams());
		ret.add(specialParams.getParams());
		return ret;
	}
	
	public String get(String paramName) {
		String ret = specialParams.getValue(paramName);
		if(ret == null) {
			ret = commonParams.getValue(paramName); 
		}
		return ret;
	}
	
	public RunConfig(TestCase test) throws MTTestException {
		init(test.getConfigParams(), test.getTestParams());
	}

	private void init(ConfigParams config, TestParams test) throws MTTestException {
		this.config = config;
		
		this.commonParams = new TestParams(TestParams.defaultName);
		this.specialParams = new TestParams(TestParams.defaultName);
		
		ArrayList<TestParam> input = new ArrayList<TestParam>();
		input.addAll(test.getParams());
		input.addAll(config.getParams());
		
		for (TestParam param : input) {
			if (param.isSpecial()) {
				this.specialParams.add(param);
			} else {
				this.commonParams.add(param);
			}
		}
		if(!specialParams.contains("OS")) {
			specialParams.add(new TestParam("OS", OS.ALL.toString(), true));
		}

		ArrayList<String> cfgNames = TestParam.getSpecialAll();
		for(String s : cfgNames) {
			if(!specialParams.contains(s)) {
				throw new MTTestException("Test required param " + s + " was not defined");
			}
		}

		isValidating = Boolean.parseBoolean(config.getValue(ConfigParams.Field.isValidating));
		threads = Integer.parseInt(config.getValue(ConfigParams.Field.threads));
		rampUpMillis = Long.parseLong(config.getValue(ConfigParams.Field.rampUp));
		durationMillis = Long.parseLong(config.getValue(ConfigParams.Field.duration));
		rampDownMillis = Long.parseLong(config.getValue(ConfigParams.Field.rampDown));
		timeUnits = TimeUnits.getUnitsByRepresentation(get("timeUnits"));
		os = OS.getOSByRepresentation(get("OS"));
	}
	public static final String goldenFileFieldName = "goldenfilename";
	
	public boolean hasGoldenFile() {
		return commonParams.contains(goldenFileFieldName);
	}
	
	public String getGoldenFile() {
		try {
			return commonParams.getValue(goldenFileFieldName);
		} catch (Throwable e) {
			return "n/a";
		}
	}
	
	@Override
	public String toString() {
		return "common: " + commonParams.toString() + " special: " + specialParams.toString();
	}
}