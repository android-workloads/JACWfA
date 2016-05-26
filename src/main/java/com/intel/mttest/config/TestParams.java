package com.intel.mttest.config;

import java.util.Collection;
import java.util.HashMap;

/**
 * This class represents a set of name-value pairs.
 */
public class TestParams implements Comparable<TestParams> {
	public static final String defaultName = "unknown";
	protected HashMap<String, TestParam> map;
	protected String name = defaultName;
	
	public String getName() {
		return name;
	}
	
	public TestParams(String name) {
		if(name != null) {
			this.name = name;
		}
		map = new HashMap<String, TestParam>();
	}

	public TestParams(TestParams inst) {
		this(inst.getName(), inst.getParams());
	}

	protected TestParams(String name, Collection<TestParam> source) {
		this.name = name;
		this.map = new HashMap<String, TestParam>();
		for (TestParam param : source) {
			add(param);
		}
	}

	public void add(TestParam... params){
		for (TestParam param : params) {
			add(param);
		}
	}
	public void add(Collection<TestParam> params){
		for (TestParam param : params) {
			add(param);
		}
	}
	
	public void add(TestParam param) {
		String name = param.getName().toLowerCase();
		if(map.containsKey(name) && map.get(name).isFinal) {
			return;
		}
		map.put(name, param);
	}
	
	public Collection<TestParam> getParams() {
		return map.values();
	}
	
	public TestParam get(String name) {
		name = name.toLowerCase();
		if(map.containsKey(name)) {
			return map.get(name);
		}
		return null;
	}


	public String getValue(String name) {
		String value = get(name.toLowerCase()).value;
		return value;
	}
	
	public boolean contains(String name) {
		return map.containsKey(name.toLowerCase());
	}

	@Override
	public int compareTo(TestParams e) {
		String n = RunConfig.goldenFileFieldName;
		if(contains(n) && e.contains(n)) {
			int x = getValue(n).compareTo(e.getValue(n));
			if(x != 0) return x;
		}
		return name.compareTo(e.name);
	}
	
	@Override
	public String toString() {
		return getName() + " : " + map.values();
	}
}
