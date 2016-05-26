package com.intel.mttest.loaders;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import com.intel.mttest.config.ConfigParams;
import com.intel.mttest.config.TestParam;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.representation.TestCase;
import com.intel.mttest.representation.TestSet;
import com.intel.mttest.util.GR;

public class CmdParser {

	static public String getConfigParamsString(TestCase testCase) {
		return getConfigParamsString(testCase.getConfigParams());
	}
	static public String getConfigParamsString(ConfigParams cfg) {
		String ret = "";
		ret += "Config:";
		try {
			for(TestParam param : cfg.getParams()) {
				if(param.isSpecialForConfig())
					ret += param.toString() + "   ";
			}
		} catch (MTTestException e) {
			ret += "err";
		}
		return ret;
	}
	
	static public String getTestParams(TestCase testCase) {
		String ret = "";
		Collection<TestParam> all = testCase.getTestParams().getParams();
		ret += "Test:";
		for(TestParam param : all) {
			if(!param.isSpecialForConfig())
				ret += param.toString() + "   ";
		}
		return ret;
	}
	

	private static final String defaultTestSet = "masterset:all";
	private static final String masterSetName = "masterset";
	
	static public TestSet extractTestSet(TestSet root, String arg) throws MTTestException {
		ArrayList<String> testSpecificators = new ArrayList<String>();
		if(arg == null)
			arg = defaultTestSet;
		{
			StringTokenizer st = new StringTokenizer(arg.trim(), ",", false);
			while(st.hasMoreTokens()) {
				String spec = st.nextToken();
				if(!spec.startsWith(masterSetName)) {
					throw new MTTestException("Please start test specification [" + spec + "] with " + masterSetName);
				}
				testSpecificators.add(spec);
			}
			root = root.extractTestSet(testSpecificators);
		}
		return root;
	}
	
	static public TestSet loadTests(XMLParser parser) throws MTTestException {
		return parser.parseTestSet((new File(GR.getTestsetDir(), masterSetName + ".xml")).getAbsolutePath());
	}
	
	static public final String defaultCfg = "medium.xml"; 
	public static ConfigParams pickConfig(ArrayList<ConfigParams> allConfigs, String spec) throws MTTestException {
		String possibilities = "";
		if(spec == null) {
			spec = defaultCfg;
		}
		for(ConfigParams cfg : allConfigs) {
			String name = cfg.getSourceFileName();
			possibilities += name + " ";
			if(name.startsWith(spec) && (name.length() == spec.length() || name.charAt(spec.length()) == '.')) {
				return cfg;
			}
		}
		throw new MTTestException("No [" + spec + "] config file among: " + possibilities);
	}
	
	static public ArrayList<ConfigParams> loadConfigs(XMLParser parser, String threadsStr, String repeatsStr) throws MTTestException {
		int threadsCnt = Runtime.getRuntime().availableProcessors();
		int repeatsCnt = 1;
		try {
			threadsCnt = Integer.parseInt(threadsStr);
		} catch (Throwable e) {
		}
		
		try {
			repeatsCnt = Integer.parseInt(repeatsStr);
		} catch (Throwable e) {
		}

		ArrayList<ConfigParams> testConfigs = new ArrayList<ConfigParams>();
		ArrayList<String> configSpecificators = new ArrayList<String>();
		configSpecificators = GR.getSubFilesName(GR.getConfigDir());
		
		for(String name : configSpecificators) {
			File src = new File(GR.getConfigDir(), name);
			testConfigs.add(parser.parseConfig(src.getPath(), threadsCnt, repeatsCnt));
		}
		return testConfigs;
	}
	
	
	
}
