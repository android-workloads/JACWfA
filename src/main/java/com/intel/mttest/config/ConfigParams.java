package com.intel.mttest.config;

import java.util.ArrayList;
import java.util.HashMap;

import com.intel.mttest.exception.MTTestException;

public class ConfigParams {
	public enum Field {
		name ("name") {
			@Override
			public boolean fromFile() {
				return false;
			}
		},
		rampUp("rampUp") {
			@Override
			public boolean fromFile() {
				return true;
			}
			@Override
			public void checkValue(String val) {
				super.checkValue(val);
				Integer.parseInt(val);
			}
		},
		duration("duration") {
			@Override
			public boolean fromFile() {
				return true;
			}
			@Override
			public void checkValue(String val) {
				super.checkValue(val);
				Integer.parseInt(val);
			}
		},
		rampDown("rampDown") {
			@Override
			public boolean fromFile() {
				return true;
			}
			@Override
			public void checkValue(String val) {
				super.checkValue(val);
				Integer.parseInt(val);
			}
		},
		isValidating("isValidating") {
			@Override
			public boolean fromFile() {
				return true;
			}
			@Override
			public void checkValue(String val) {
				super.checkValue(val);
				Boolean.parseBoolean(val);
			}
		},
		//----------- should be passed as args
		threads("threads") {
			@Override
			public boolean fromFile() {
				return false;
			}
			@Override
			public void checkValue(String val) {
				super.checkValue(val);
				//Integer.parseInt(val);
			}
		},
		numRuns("numRuns") {
			@Override
			public boolean fromFile() {
				return false;
			}
			@Override
			public void checkValue(String val) {
				super.checkValue(val);
				Integer.parseInt(val);
			}
		};
		
		
		static public boolean isConfigParamName(String name) {
			for(Field inst : Field.values())
				if(inst.isAcceptable(name)) {
					return true;
				}
			return false;
		}
		
		
		private String fieldName;
		private Field(String fieldName) {
			this.fieldName = fieldName;
		}

		public void checkValue(String val) {
			return;
		}
		
		public boolean isAcceptable(String paramName) {
			return fieldName.toLowerCase().equals(paramName.toLowerCase());
		}
		
		final public String getName() {
			return this.toString();
		}

		final public String toString() {
			return fieldName;
		}
		
		public abstract boolean fromFile();
	}
	
	HashMap<Field, String> values = new HashMap<Field, String>();
	
	public boolean setFileParam(Field field, TestParam param) {
		return field.fromFile() && setValue(field, param.name, param.value);
	}

	public boolean setAbsValue(Field field, String paramValue) {
		return !field.fromFile() && setValue(field, field.getName(), paramValue); 
	}

	public String getValue(Field field) throws MTTestException {
		if(!isSetted(field))
			throw new MTTestException("Config param '" + field.toString() + "' was not defined in " + srcFileName);
		return values.get(field);
	}
	
	public int[] getThreadsNumConfig() throws MTTestException{
		String[] threadsStr = getValue(ConfigParams.Field.threads).split(",");
		int[] threads = new int[threadsStr.length];
		for (int i = 0; i < threadsStr.length; i++) {
			threads[i] = Integer.parseInt(threadsStr[i]);
		}
		return threads;
	}
	
	public void setFileParam(TestParam param) throws MTTestException {
		boolean accepted = false;
		for(Field field : Field.values()) {
			accepted |= setFileParam(field, param);
		}
		if(!accepted) {
			throw new MTTestException("Unexpected param '" + param.name + "=" + param.value + "' in config : " + srcFileName);
		}
	}
	
	public boolean isSetted(Field field) {
		return values.containsKey(field);
	}
	
	private boolean setValue(Field field, String paramName, String paramValue) {
		if(!field.isAcceptable(paramName)) {
			return false;
		}
		try {
			field.checkValue(paramValue);
		} catch (Throwable e) {
			return false;
		}
		values.put(field, paramValue);
		return true;
	}
	
	private String srcFileName;
	

	@SuppressWarnings("unchecked")
	public ConfigParams(ConfigParams confs) {
		this.values = (HashMap<Field, String>) confs.values.clone();
		this.srcFileName = confs.srcFileName;
	}
	
	public ConfigParams(String srcName) {
		this.srcFileName = srcName;
	}
	
	public String getName() {
		String ret = null;
		try {
			ret = getValue(Field.name);
		} catch (MTTestException e) {
			ret = getSourceFileName();
		}
		return ret;
	}
	
	public String getSourceFileName() {
		return srcFileName;
	}
	

	public ArrayList<TestParam> getParams() throws MTTestException {
		ArrayList<TestParam> ret = new ArrayList<TestParam>();
		for(Field field : Field.values()) {
			if(!isSetted(field)) {
				throw new MTTestException("Config'" + srcFileName + "' param '" + field.name() + "' was not defined '");
			}
			ret.add(new TestParam(field.getName(), getValue(field) + "", true));
		}
		return ret;
	}
}
