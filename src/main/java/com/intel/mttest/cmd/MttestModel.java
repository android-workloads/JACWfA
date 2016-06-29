package com.intel.mttest.cmd;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import android.content.Context;

import com.intel.mttest.config.ConfigParams;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.launchers.SuiteLauncher;
import com.intel.mttest.loaders.CmdArgs;
import com.intel.mttest.loaders.CmdParser;
import com.intel.mttest.loaders.XMLParser;
import com.intel.mttest.reporter.CSVReporter;
import com.intel.mttest.reporter.CmdReporter;
import com.intel.mttest.reporter.EventType;
import com.intel.mttest.reporter.ILog;
import com.intel.mttest.reporter.MttestMessage;
import com.intel.mttest.reporter.Observable;
import com.intel.mttest.reporter.Observer;
import com.intel.mttest.reporter.PlainTableReporter;
import com.intel.mttest.representation.OS;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestSet;
import com.intel.mttest.representation.TestSetSummary;
import com.intel.mttest.util.GR;

public class MttestModel implements Observable, Observer{
	
	
	static public void init(OS os, Context context, PrintStream logOut, ArrayList<String> args) {
		if(instance != null) {
			instance.logger.w("Attepmt to reinit " + instance.getClass());
		} else {
			instance = new MttestModel(os, context, logOut, args);
		}
	}
	static public MttestModel instance() {
		return instance;
	}
		
	synchronized public void setPickedTestSet(TestSet pick) {
		pickedTestset = pick;
	}

	
	synchronized public void setPickedTestSet(String specificator) {
		pickedTestset = extractTestSet(specificator);
		
	}


	synchronized public void setRunningTestSet(String specificator) {
		runningTestSet = extractTestSet(specificator);
	}
	

	synchronized public TestSet getRootTestSet() {
		return rootTestSet;
	}
	synchronized public TestSet getPickedTestSet() {
		return pickedTestset;
	}
	
	synchronized public void setPickedConfig(ConfigParams pick) {
		pickedConfig = pick;
	}

	synchronized public ConfigParams getPickedConfig() {
		return pickedConfig;
	}
	synchronized public ArrayList<ConfigParams> getAllConfig() {
		return allConfigs;
	}
	synchronized public boolean isRunning() {
		return SuiteLauncher.isRunning();
	}

	synchronized public boolean start() {
		try {
			ArrayList<TestSet> testsToExecute = new ArrayList<>();
			TestSet all = runningTestSet.getTestSubsets().get(0);
			
			for (int i : pickedConfig.getThreadsNumConfig()) {
				ConfigParams testConfig = new ConfigParams(pickedConfig);
				testConfig.setAbsValue(ConfigParams.Field.threads, i + "");
				all.setAlternativeName(all.getShortName() + " {" + i + "T}");
				testsToExecute.add(all.applyConfig(testConfig));
			}
			TestSet execute = new TestSet("masterset", pickedConfig, testsToExecute);
			SuiteLauncher.runTestSet(execute);
		} catch (Throwable e) {
			logger.e(e.getMessage());
			return false;
		}
		notifyObservers(EventType.STARTED);
		return true;
	}
	
	synchronized public TestSetSummary getSummary() {
		return SuiteLauncher.getSummary();
	}

	synchronized public void stop() {
		SuiteLauncher.stopTestingBlocked();
		notifyObservers(EventType.FINISHED);
	}

	synchronized public CmdArgs getArgs() {
		return cmdArgs;
	}

	public Summary waitForResults() {
		return SuiteLauncher.waitForResults();
	}
	
	static protected volatile MttestModel instance = null;
	ILog logger;
	XMLParser parser;
	CmdArgs cmdArgs;
	OS os;
	protected MttestModel(OS os, Context context, PrintStream logOut, ArrayList<String> args) {
		this.os = os;
		cmdArgs = new CmdArgs(args);
		GR.init(os, context, cmdArgs);
		logger = new ILog(os, logOut);
		try {
			loadResources(os, context);
		} catch (MTTestException e) {
			exitWithException(e.getShortMessage());
		}
		addSuiteObserver(this);
	}
	
	protected void loadResources(OS os, Context context) throws MTTestException {
		SuiteLauncher.init(os);	
		SuiteLauncher.addStaticObserver(new CmdReporter(logger, cmdArgs.verbose));	

		parser = new XMLParser(os);
		rootTestSet = CmdParser.loadTests(parser);
		allConfigs = CmdParser.loadConfigs(parser, cmdArgs);
		
		pickedConfig = CmdParser.pickConfig(allConfigs, cmdArgs.configsSpecificator);
		
		pickedTestset = CmdParser.extractTestSet(rootTestSet, cmdArgs.testsSpecificator);
		runningTestSet = CmdParser.extractTestSet(rootTestSet, cmdArgs.testsSpecificator);
	}
	
	protected TestSet extractTestSet(String specificator) {
		TestSet ret = null; 
		try {
			ret = CmdParser.extractTestSet(rootTestSet, specificator);
		} catch (MTTestException e) {
			exitWithException(e.getMessage());
		}
		return ret;
	}

	volatile protected TestSet rootTestSet;
	volatile protected ArrayList<ConfigParams> allConfigs;
	
	volatile protected TestSet pickedTestset, runningTestSet;
	volatile protected ConfigParams pickedConfig;
	
	private void exitWithException(String msg) {
		logger.e("EXIT :" + msg);
		System.exit(1);
	}
	synchronized public void addSuiteObserver(Observer observer) {
		SuiteLauncher.addStaticObserver(observer);
	}
	synchronized public void addObserver(Observer obs) {
		if(!observers.contains(obs)) {
			observers.add(obs);
		}
	}
	protected void notifyObservers(EventType event) {
		for(Observer obs : observers)
			obs.update(this, event);
	}
	ArrayList<Observer> observers = new ArrayList<Observer>();

	@Override
	public void update(Observable subject, EventType eventType) {
		if(subject instanceof TestSetSummary) {
			if(eventType.equals(EventType.FINISHED) && ((TestSetSummary) subject).isRoot()) {
				PlainTableReporter reporter = new PlainTableReporter(cmdArgs.verbose, SuiteLauncher.getSummary());
				ILog logger = new ILog(os, null);
				logger.appendTAG("-res");
				if(cmdArgs.verbose > 0) {
    				try {
    					reporter.report(logger, true);
    				} catch (MTTestException e) {
    					exitWithException(e.getMessage());
    				}
				}
				try {
				    {
                        File tableOut = GR.createResultsFile("table.txt");
                        PrintStream tableStream = new PrintStream(tableOut);
                        ILog tableLogger = new ILog(os, tableStream);
                        PlainTableReporter tableReporter = new PlainTableReporter(getSummary());
                        tableReporter.report(tableLogger, true);
                        tableStream.close();
                        logger.i("Please find results in:" + tableOut.getAbsolutePath());
				    }
				    {
    				    File csvOut = GR.createResultsFile("results.csv");
    				    PrintStream csvStream = new PrintStream(csvOut);
    				    ILog csvLogger = new ILog(os, csvStream);
    				    CSVReporter csvReporter = new CSVReporter(getSummary());
    	                csvReporter.report(csvLogger, true);
    	                csvStream.close();
    	                logger.i("Please find results in:" + csvOut.getAbsolutePath());
				    }
				} catch (Throwable e) {
                    logger.i("Unable to save result files : " + e.getMessage());
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					//nothing
				}
				logger.i("MTTest testing finished.");
			}
		}
	}
	@Override
	public void update(Observable subject, MttestMessage eventMsg) {
		// do nothing
	}
}
