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
			testsSpecificator = "masterset:all"; // default value
		}

		if(args.contains("-c")) {
			int index = args.indexOf("-c");
			extractValue(args, index);
			configsSpecificator = extractValue(args, index);
		} else {
			configsSpecificator = "medium.xml"; // default value
		}
		
		if(args.contains("-t")) {
			int index = args.indexOf("-t");
			extractValue(args, index);
			threadsSpecificator = extractThreadNumValue(args, index);
		} else {
			// default value
			threadsSpecificator = Integer.toString(1)
					+ "," 
					+ Integer.toString(Runtime.getRuntime().availableProcessors());
		}
		
		if(args.contains("-n")) {
            int index = args.indexOf("-n");
            extractValue(args, index);
            numRunsSpecificator = Integer.toString(extractIntValue(args, index));
        } else {
            numRunsSpecificator = "1"; // default value
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

	private static String extractThreadNumValue(ArrayList<String> args, int index) {
		String s = extractValue(args, index);
		String[] tVals = s.split(",");
		String result = "";
		for (int i = 0; i < tVals.length; i++) {
			if (i > 0) {
				result += ",";
			}
			try {
				int tVal = Integer.parseInt(tVals[i]);
				if (tVal > Runtime.getRuntime().availableProcessors()
						|| tVal < 1) {
					throw new IllegalArgumentException(
							"Command line arguments: expected int in range 1.."
									+ Runtime.getRuntime()
											.availableProcessors()
									+ " or 'sysCores' instead of '" + tVals[i]
									+ "' in '" + s + "'");
				}
				result += tVal;
			} catch (Throwable e) {
				if (tVals[i].equals("sysCores")) {
					result += "" + Runtime.getRuntime().availableProcessors();
				} else {
					throw new IllegalArgumentException(
							"Command line arguments: expected int in range 1.."
									+ Runtime.getRuntime()
											.availableProcessors()
									+ " or 'sysCores' instead of '" + tVals[i]
									+ "' in '" + s + "'");
				}
			}
		}
		return result.toString();
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
