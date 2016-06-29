package com.intel.mttest.representation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import com.intel.mttest.config.ConfigParams;
import com.intel.mttest.config.TestParams;
import com.intel.mttest.exception.MTTestException;

public class TestSet extends MttestTest {

	private List<TestSet> subsets;
	private List<TestCase> tests;
	private int cntTests;
	public static String sys = System.getProperty("os.name").toLowerCase();
	
	public TestSet(String name, TestSet owner, ConfigParams config, TestParams params) throws MTTestException {
		super(name, owner, config, params);
		this.subsets = new ArrayList<TestSet>();
		this.tests = new ArrayList<TestCase>();
	}
	
	public TestSet(TestSet test, TestSet owner) throws MTTestException {
		super(test.getName(), owner, test.getConfigParams(), test.getTestParams());
		this.alternativeName = test.getAlternativeName();
		this.subsets = new ArrayList<TestSet>();
		this.tests = new ArrayList<TestCase>();
		for(TestSet sub : test.getTestSubsets()) {
			addMttestTest(new TestSet(sub, this));
		}
		for(TestCase sub : test.getTestSubcases()) {
			addMttestTest(new TestCase(sub, this));
		}
		Collections.sort(subsets);
		Collections.sort(tests);
	}

	public TestSet(String name, ConfigParams config, ArrayList<TestSet> tests) throws MTTestException {
		super(name, null, config, null);
		this.subsets = new ArrayList<TestSet>();
		this.tests = new ArrayList<TestCase>();
		for(TestSet test : tests) {
			addMttestTest(new TestSet(test, this));
		}
		Collections.sort(subsets);		
	}
	
	public void addMttestTest(MttestTest test) throws MTTestException {
		if(this != test.getOwner()) throw new IllegalArgumentException();
		cntTests += test.getTestCount();
		if(test instanceof TestSet) {
			subsets.add((TestSet) test);
			Collections.sort(subsets);
		} else if (test instanceof TestCase) {
			tests.add((TestCase) test);
			Collections.sort(tests);
		} else {
			throw new MTTestException("Unsupported MttestTest implementation: " + test.getClass());
		}
	}

	public List<TestSet> getTestSubsets() {
		return subsets;
	}
	public List<TestCase> getTestSubcases() {
		return tests;
	}
	
	@Override
	public int getTestCount() {
		return cntTests;
	}

	HashMap<String, ArrayList<StringTokenizer>> buildMap(ArrayList<StringTokenizer> tokenizers) throws MTTestException {
		HashMap<String, ArrayList<StringTokenizer>> map = new HashMap<String, ArrayList<StringTokenizer>>();
		for(StringTokenizer st : tokenizers) {
			String s = st.nextToken();
			if(s == null)
				throw new MTTestException("test subsets are incorrect");
			if(!map.containsKey(s))
				map.put(s, new ArrayList<StringTokenizer>());
			
			map.get(s).add(st);
		}
		return map;
	}
	
	public TestSet extractTestSet(ArrayList<String> paths) throws MTTestException {
		if(paths.isEmpty()) {
			return this;
		}
		
		HashMap<String, ArrayList<StringTokenizer>> map;
		ArrayList<StringTokenizer> tokenizers = new ArrayList<StringTokenizer>();
		for(String path : paths) {
			StringTokenizer st = new StringTokenizer(path, ":", false);
			tokenizers.add(st);
		}
		map = buildMap(tokenizers);
		if(map.keySet().size() != 1 || !map.keySet().iterator().next().equals(name)) throw new MTTestException("Unexpected testset roots: " + map.keySet() + "instead of " + name);
		return extractTestSet(map.get(name), null);
	}
	protected String debug() {
		return this.getFullName() + "[" + this.getId() + "/" + getTestCount() + "]";
	}
	protected TestSet extractTestSet(ArrayList<StringTokenizer> sts, TestSet owner) throws MTTestException {
		TestSet ret = new TestSet(name, owner, this.getConfigParams(), this.getTestParams());
		for(StringTokenizer st : sts) {
			if(!st.hasMoreTokens()) {
				for(TestCase test : tests) {
					ret.addMttestTest(new TestCase(test, ret));
				}
				
				for(TestSet test : subsets) {
					ret.addMttestTest(new TestSet(test, ret));
				}
				return ret;
			}
		}
		HashMap<String, ArrayList<StringTokenizer>> map = buildMap(sts);
		HashSet<String> unusedKeys = new HashSet<String>();
		HashSet<String> possibilities = new HashSet<String>();
		unusedKeys.addAll(map.keySet());
		
		for(String ss : map.keySet()) {
			String token = ss.toLowerCase();
			boolean used = false;
			for(TestCase test : tests) {
				if(test.getName().toLowerCase().equals(token)) {
					ret.addMttestTest(new TestCase(test, ret));
					used = true;
				}
				possibilities.add(test.getName());
			}
			
			for(TestSet test : subsets) {
				if(test.getName().toLowerCase().equals(token)) {
					TestSet extr = test.extractTestSet(map.get(ss), ret);
					ret.addMttestTest(extr);
					used = true;
				}
				possibilities.add(test.getName());
			}
			if(used) {
				unusedKeys.remove(ss);
			}
		}
		if(!unusedKeys.isEmpty()) {
			throw new MTTestException("Unknown test specification path - " + this.getFullName() + ":" + unusedKeys + " expected :" + possibilities);
		}
		return ret;
	}
	
	public TestSet applyConfig(ConfigParams config) throws MTTestException {
		return applyConfigs(config, null);
	}
	
	protected TestSet applyConfigs(ConfigParams cfg, TestSet owner) throws MTTestException {
		TestSet ret = new TestSet(this.getName(), owner, cfg, this.getTestParams());
		ret.setAlternativeName(this.getAlternativeName());
		
		for(TestCase test : tests) {
			TestCase tc = new TestCase(test.getName(), ret, cfg, test.getTestParams());
			ret.addMttestTest(tc);
		}
		
		for(TestSet test : subsets) {
			ret.addMttestTest(test.applyConfigs(cfg, ret));
		}
		return ret;
	}

}



















