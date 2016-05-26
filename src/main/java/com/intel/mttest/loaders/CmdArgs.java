package com.intel.mttest.loaders;

import java.util.ArrayList;

public class CmdArgs {
	public final int verbose;
	public final String configsSpecificator;
	public final String testsSpecificator;
	public final String threadsSpecificator;
    public final String numRunsSpecificator;

    public final String resultsDir;
	
	
	public static String[] getKeys() {
		return new String[] {"-v", "-s", "-c", "-t", "-n", "-output"};
	}
	
	
	public CmdArgs(ArrayList<String> args) {
		if (args.contains("-v")) {
			int index = args.indexOf("-v");
			extractValue(args, index);
			verbose = extractIntValue(args, index);
		} else {
			verbose = 10;
		}
		
		if (args.contains("-s")) {
			int index = args.indexOf("-s");
			extractValue(args, index);
			testsSpecificator = extractValue(args, index);
		} else {
			testsSpecificator = null;
		}

		if(args.contains("-c")) {
			int index = args.indexOf("-c");
			extractValue(args, index);
			configsSpecificator = extractValue(args, index);
		} else {
			configsSpecificator = null;
		}
		
		if(args.contains("-t")) {
			int index = args.indexOf("-t");
			extractValue(args, index);
			threadsSpecificator = extractIntValue(args, index) + "";
		} else {
			threadsSpecificator = null;
		}
		
		if(args.contains("-n")) {
            int index = args.indexOf("-n");
            extractValue(args, index);
            numRunsSpecificator = extractIntValue(args, index) + "";
        } else {
            numRunsSpecificator = null;
        }
		
		if(args.contains("-output")) {
            int index = args.indexOf("-output");
            extractValue(args, index);
            resultsDir = extractValue(args, index) + "";
        } else {
            resultsDir = null;
        }
		
		if (!args.isEmpty()) {
			throw new IllegalArgumentException("Unknown options." + args);
		}
	}
	
	private static int extractIntValue(ArrayList<String> args, int index) {
		String s = extractValue(args, index);
		int ret = 0;
		try {
			ret = Integer.parseInt(s);
		} catch (Throwable e) {
			throw new IllegalArgumentException("Command line arguments: expected int instead of '" + s + "'");
		}
		return ret;
	}
	private static String extractValue(ArrayList<String> args, int index) {
		if (index >= args.size()) {
			throw new IllegalArgumentException("Command line arguments: value was not defined.");
		}
		String ret = args.get(index);
		args.remove(index);
		return ret;
	}

	
}
