package com.intel.mttest.config;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class represents a name-value pair.
 */
public class TestParam {
	
	public TestParam(String name, String value, boolean isFinal) {
		this.name = name.toLowerCase();
		this.value = value;
		this.isFinal = isFinal;
		if(!isValid()) {
			throw new IllegalArgumentException("Config error: value must be non-empty for " + name);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean isFinal() {
		return isFinal;
	}

	public boolean isSpecial() {
		return isSpecialForConfig() || isSpecialForTest();
	}
	
	public boolean isSpecialForConfig() {
		boolean result = false;
		for (String specialName : specialForConfig) {
			result |= this.nameIs(specialName);
		}
		return result;
	}
	
	public boolean isSpecialForTest() {
		boolean result = false;
		for (String specialName : specialForTest) {
			result |= this.nameIs(specialName);
		}
		return result;
	}
	
	@Override
	public String toString() {
		return name + " [" + value + "]";
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof TestParam) {
			return nameIs(((TestParam)o).getName());
		}
		return false;
	}

	protected static final ArrayList<String> specialForTest = new ArrayList<String>();
	protected static final ArrayList<String> specialForConfig = new ArrayList<String>();
	static {
		specialForTest.add("timeUnits");
		specialForTest.add("OS");
		
		ConfigParams.Field cfgs[] = ConfigParams.Field.values();
		for(ConfigParams.Field cfg : cfgs) {
			specialForConfig.add(cfg.getName());
		}
	}
	
	public static ArrayList<String> getSpecialAll() {
		ArrayList<String> ret = new ArrayList<String>(specialForConfig);
		ret.addAll(specialForTest);
		return ret;
	}
	
	public static ArrayList<String> getSpecialForConfig() {
		return new ArrayList<String>(specialForConfig);
	}
	public static ArrayList<String> getSpecialForTest() {
		return new ArrayList<String>(specialForTest);
	}

	protected String name;
	protected String value;
	protected boolean isFinal;

	/**
	 * name must be non empty
	 * values for special must should be non empty
	 * @return
	 */
	protected boolean isValid() {
		return name != null && name.trim().length() != 0 && (!isSpecialForConfig() || value == null || !value.equals(""));
	}
	
	protected boolean nameIs(String name) {
		return this.name.equalsIgnoreCase(name);
	}

	public List<TestParam> splitValues() {
		StringTokenizer st = new StringTokenizer(value, " \t,", false);
		ArrayList<TestParam> list = new ArrayList<TestParam>();
		while(st.hasMoreTokens()) {
			String subValue = st.nextToken();
			list.add(new TestParam(getName(), subValue, isFinal()));
		}
		return list;
	}
}

















