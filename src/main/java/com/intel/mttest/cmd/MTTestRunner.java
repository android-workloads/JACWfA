package com.intel.mttest.cmd;

import java.util.ArrayList;
import java.util.Arrays;

import com.intel.mttest.representation.OS;
import com.intel.mttest.util.GR;


/**
 * Main class which runs one specified test with specified parameters and prints results.
 */
public class MTTestRunner {

	private enum ModeOfOperation {
		RunWorkload, ShowUsage, ShowParametersForClass, ShowXMLFileExample
	}

	private static ModeOfOperation mode;
	
	/**
	 * <p>This method is used to launch workload from command line and passing options to it. <br>
	 * You have different ways to run your tests in various modes.</p>
	 * <ul>
	 * 	<li>java MTTest -h - for help information.</li>
	 * 	<li>java MTTest -e - to get example of configuration xml-file.</li>
	 * 	<li>java MTTest -p test-class-name - to get list of accepted parametersfor test class with defaults.</li>
	 * 	<li>java options MTTest test-class-name - run one test with specifiedparameters.</li>
	 * 	<li>java MTTest -f configuration-file-name - run test suit described inspecified configuration file.</li>
	 * </ul>
	 * Option set up as -DoptName=value.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		try {
			ArrayList<String> listArgs = new ArrayList<String>(Arrays.asList(args)); 
			parseComandlineArgs(listArgs);
			switch (mode) {
				case RunWorkload:
					runWorkload(listArgs);
					break;
				case ShowUsage:
					printUsage();
					System.exit(0);
				case ShowParametersForClass:
					printParametersForClass();
					System.exit(0);
				case ShowXMLFileExample:
					printXMLFileExample();
					System.exit(0);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println(String.format("Error: " + e.getMessage()));
			printUsage();
			System.exit(1);
		}
	}

	/**
	 * <p>
	 * This method can be called, for example, from main method of workload
	 * class and it redirects call to main method of MTTest.
	 * </p>
	 * 
	 * @param testClass
	 *            class of workload to launch
	 * @param args
	 *            command line arguments
	 */
	/**
	 * @param testClass
	 * @param args
	 */
	public static void main(Class<?> testClass, String[] args) {
		args = new String[1];
		args[0] = testClass.getName();
		main(args);
	}


	/**
	 * @param testSpecificator
	 */
	public static void runWorkload(ArrayList<String> args) {
		MttestModel.init(OS.JAVA, null, System.out, args);
		MttestModel model = MttestModel.instance();
		model.start();
		model.waitForResults();
	}

	private static void parseComandlineArgs(ArrayList<String> args) {
		mode = ModeOfOperation.RunWorkload;
		if (args.contains("-h")) {
			mode = ModeOfOperation.ShowUsage;
			return;
		}
		
		if (args.contains("-e")) {
			mode = ModeOfOperation.ShowXMLFileExample;
			return;
		}

		if (args.contains("-p")) {
			mode = ModeOfOperation.ShowParametersForClass;
			return;
		}

	}
	


	private static void printUsage() {
		System.out
				.println("Usage:\n"
						+ "    java MTTest -h - print this message.\n"
						+ "    java MTTest -e - see example of configuration xml-file.\n"
						+ "    java MTTest -p test-class-name - to get list of accepted parameters for test class with defaults.\n"
						+ "    java <options> MTTest <test-class-name> - run one test with specified parameters.\n"
						+ "    java MTTest -f <configuration-file> - run test suit described in specified configuration file.\n"
						+ "Option set up as -DoptName=value.\n\n");
	}

	private static void printXMLFileExample() {
		System.out.println(
					"Example of configuration xml-file:\n" +
				"<?xml version=\"1.0\"?>" +
				"<mttest>" +
				"    <name value=\"default\" />" +
				"    <conf name=\"rampUp\" value=\"2000\" />" +
				"    <conf name=\"duration\" value=\"6000\" />" +
				"    <conf name=\"rampDown\" value=\"1000\" />" +
				"    <conf name=\"isValidating\" value=\"0\"/>" +
				"</mttest>");
	}

	private static void printParametersForClass() {
		System.out.println("List of parameters'"
						+  "':\n"
						+  "Property 'name' of 'option' tag can be:\n"
						+  "    rampUp   - Time to warm up. (default: 2000 ms)\n"
						+  "    duration - Duration of measuring phase. (default: 5000 ms)\n"
						+  "    rampDown - Time to wait other threads. (default: 2000 ms)\n"
						+  "    any public field by its name in a class in lower case. (refer to the documentation of particular test for more information)\n"
						+  "\n");
	}
}
