package com.intel.mttest.reporter;

import com.intel.mttest.config.ConfigParams.Field;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.loaders.CmdParser;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestCase;
import com.intel.mttest.representation.TestCaseSummary;
import com.intel.mttest.representation.TestSetSummary;
import com.intel.mttest.util.StringProcessor;

public class CmdReporter implements Observer {

	protected final String blankLine = "";
	protected ILog logger;
	protected int verbose;
	
	public CmdReporter(ILog logger, int verbose) {
		this.logger = logger;
		this.logger.appendTAG("-cmd");
		this.verbose = verbose;
	}

	@Override
	public synchronized void update(Observable subject, EventType eventType) {
		boolean ok = false;
		if(subject instanceof TestSetSummary) {
			TestSetSummary summary = (TestSetSummary) subject;
			switch (eventType) {
				case STARTED:		ok = true; groupStarted(summary); break;
				case FINISHED:		ok = true; groupFinished(summary); break;
				case STATE_CHANGED:	ok = true; stateChanged(summary); break;
				default: break;
			}
		}
		if(subject instanceof TestCaseSummary) {
			TestCaseSummary summary = (TestCaseSummary) subject;
			switch (eventType) {
				case STARTED:		ok = true; testStarted(summary); break;
				case FINISHED:		ok = true; testFinished(summary); break;
				default: break;
			}
		}
		if(ok)
			log(blankLine, 1);
	}
	
	@Override
	public void update(Observable subject, MttestMessage msg) {
		msg(subject, msg.getMessage(), msg.getType());
	}
	
	protected void log(String msg, int priority) {
	    if(priority <= verbose)
	        logger.i(msg);
	}
	
	protected void msg(Object source, String msg, MttestMessage.Type type) {	
		switch(type) {
			case e: logger.e("[" + source.getClass() + "] " + msg); break; 
			case w: logger.w("[" + source.getClass() + "] " + msg); break;
			case i: logger.i("[" + source.getClass() + "] " + msg); break;
		}
	}
	
	

	//test set summary handling
	
	private void groupStarted(TestSetSummary arg) {
		log("-----", 1);
		String str = "group started:" + arg.getSource().getFullName();
		str += "   number of tests:" + arg.getSource().getTestCount();
		log(str, 1);
		log(getCommandLine(arg), 2);
	}

	private void groupFinished(TestSetSummary arg) {
		log("group finished:" + arg.getSource().getName(), 1);
		PlainTableReporter reporter = new PlainTableReporter(verbose, arg);
		try {
			reporter.report(logger, false);
		} catch (MTTestException e) {
			log("Fail on reprot constructing " + e.getMessage(), 1);
		}
		log("-----", 1);
	}

	private void stateChanged(TestSetSummary arg) {
		if(!arg.isRoot()) return;
//		log("Progress " + StringProcessor.formatDouble(arg.getProgress(), 2) + " %", 1);
	}
	
	
	//test case summary handling 
	private void testStarted(TestCaseSummary arg) {
		log("-----", 1);
		TestCase testCase = (TestCase) arg.getSource();
		String sourceFile = arg.getRunConfig().getGoldenFile();
		log("test started:", 0);
		log(testCase.getName() + " goldenFile=" + sourceFile, 1);
		log(CmdParser.getConfigParamsString(testCase), 1);
		log(CmdParser.getTestParams(testCase), 1);
		log(getCommandLine(arg), 0);
	}
	
	String getCommandLine(Summary arg) {
		String testSet = arg.getSource().getFullName();
		String testConfig = arg.getSource().getConfigParams().getSourceFileName();
		String threads = "0";
		try {
			threads = arg.getSource().getConfigParams().getValue(Field.threads);
		} catch (MTTestException e) {
		}
		return "cmd : -s " + testSet + " -c " + testConfig + " -t " + threads;
	}

	private void testFinished(TestCaseSummary arg) {
		TestCase testCase = (TestCase) arg.getSource();
		String sourceFile = arg.getRunConfig().getGoldenFile();
		log("test finished:", 0);
		log(testCase.getName() + "   goldenFile=" + sourceFile + "   score=" + StringProcessor.formatDouble(arg.getScore(), 5) + "   mean_duration=" + arg.getMeanIterationMs(), 0);
		TestSetSummary root = arg.getRoot();
        log("Progress " + StringProcessor.formatDouble(root.getProgress(), 2) + " %", 0);
		
		log("-----", 1);
	}
}
