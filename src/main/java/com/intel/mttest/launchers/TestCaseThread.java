package com.intel.mttest.launchers;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.exception.TestRuntimeErrorException;
import com.intel.mttest.representation.TestCase;
import com.intel.mttest.representation.TestCaseSummary;
import com.intel.mttest.representation.TestInterface;
import com.intel.mttest.util.StringProcessor;


/**
 * One TestThread instance is created for every running thread and runs all stages.
 */
public final class TestCaseThread extends Thread {

	protected static final long MIN_ITER_DURATION = 50;
	protected static final long MAX_ITER_DURATION = 500;
	
	private RunConfig config;
	protected TestCaseSummary summary;
	protected TestInterface test;
	protected TestCase testCase;
	protected volatile int stage = 0; // 0 - initial, 1 on init(), 2 on run(), 3 on done()
	protected volatile boolean inited = false;
	
	
	public TestCaseThread(TestCase testCase) throws MTTestException {
		summary = new TestCaseSummary(testCase, null);
		config = new RunConfig(testCase);
		this.testCase = testCase;
	}
	
	public void init() {
		if(stage != 0) {
			summary.setFailed("Attempt to re-init " + this.getClass());
			return;
		}
		stage = 1;
		try {
			test = createInstance();
			test.init(config);
		} catch (Throwable e) {
			e.printStackTrace();
			summary.setFailed("Fail on " + this.getClass() + ".init()" + e.getMessage());
		}
		inited = true;
	}
	
	public void done() {
		if(stage == 0) {
			summary.setFailed("call init() before done() in TestThread" + this.getClass());
			return;
		} 
		stage = 3;		
		test.done();
	}
	
	public TestCaseSummary getSummary() {
		if(stage != 3) {
			summary.setFailed("call done() before getSummary() in " + this.getClass());
		}	
		return summary;
	}

	/**
	 * <p>
	 * This method makes all measurements. It consists of three phases:
	 * </p>
	 * <ol>
	 * <li>warm up - starting thread and wait for others.</li>
	 * <li>steady state - making measurements.</li>
	 * <li>ramp down - continuing running threads until every other end steady
	 * state.</li>
	 * </ol>
	 */
	public final void run() {
		if(!inited) {
			init();
		}
		if(stage != 1) {
			summary.setFailed("call init() before run() int " + this.getClass());
			return;
		}
		summary.setInProgress();
		stage = 2;
		
		try {
			runStage(config.rampUpMillis, null);
			runStage(config.durationMillis, summary);
			runStage(config.rampDownMillis, null);			
			if (test.getError() != null) {
				throw new TestRuntimeErrorException(test.getError());
			}
			if(isInterrupted()) {
				summary.setInterrupted();
			} else {
				String warning = getWarning();
				if(warning == null) {
					summary.setSuccesful();
				} else {
					summary.setWarning(warning);
				}							
			}			
		} catch (Throwable e) {
			e.printStackTrace();
			summary.setFailed(e.getMessage());
		} finally {
			summary.setDone();
		}

		return;
	}

	/**
	 * Runs test which lasts specified time and collects statistics
	 * 
	 * @param durationMilis
	 *            limit on duration of stage
	 * @param log
	 *            statistics accumulator
	 */
	protected void runStage(long durationMilis, TestCaseSummary log) throws TestRuntimeErrorException {
		long durationTime = durationMilis * 1000 * 1000;
		long count = 0;
		long score = 0;
		long startTime = System.nanoTime();
		do {
			count++;
			score += test.iteration();
		} while(System.nanoTime() < startTime + durationTime && !isInterrupted());
		durationTime = System.nanoTime() - startTime;
		if(log != null) {
			log.collect(score, durationTime, count);
		}
	}
		
	protected TestInterface createInstance() throws MTTestException {
		try {
			TestInterface test = testCase.getTestClass().newInstance();
			return test;
		} catch (InstantiationException | IllegalAccessException e) { 
			throw new MTTestException("Internal error, invalid instantiation of class '"+ testCase.getTestClass().getName() + "'", e);
		}
	}
	
	protected String getWarning() {
		String ret = null;
		double meanMs = summary.getMeanIterationMs(); 
		if(meanMs > MAX_ITER_DURATION) {
			ret = "duration " + StringProcessor.formatDouble(meanMs, 0) + " > " + MAX_ITER_DURATION;
		}
		if(meanMs < MIN_ITER_DURATION) {
			ret = "duration " + StringProcessor.formatDouble(meanMs, 0) + " < " + MIN_ITER_DURATION;
		}
		return ret;
	}
}
