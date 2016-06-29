package com.intel.mttest.launchers;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.representation.OS;
import com.intel.mttest.representation.TestCase;
import com.intel.mttest.representation.TestCaseSummary;

public class TestCaseLauncher {

	protected TestCase testCase;
	protected OS runningOS;
	protected RunConfig config;
	
	public TestCaseLauncher(OS mode, TestCase testCase) throws MTTestException {
		this.testCase = testCase;
		this.runningOS = mode;
		config = new RunConfig(testCase);
	}
	
	private TestCaseThread[] initThreads() {
		if(!checkOS()) {
			return null;
		}
		int threadCount = config.threads[0];
		TestCaseThread[] testThreads = new TestCaseThread[threadCount];
		
		for (int i = 0; i < threadCount; i++) {
			try {
				testThreads[i] = new TestCaseThread(testCase);
			} catch (Throwable e) {
				return null;
			}
		}
		return testThreads;
	}
	
	public boolean checkOS() {
		OS os = config.os;
		return (os == null || os.equals(OS.ALL)) ? true : os.equals(runningOS);
	}
	TestCaseThread[] threads;
	TestCaseSummary summary;
	
	public TestCaseSummary run(TestCaseSummary summary) throws MTTestException {

		
		System.gc();
		threads = initThreads();
		try {
			summary.setInProgress();
			if(threads == null) {
				summary.setFailed("OS mismatch");
			} else {
				if(Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
				
				for(TestCaseThread thread : threads) {
					thread.start();
				}
				for(TestCaseThread thread : threads) {
					thread.join();
				}
				for(TestCaseThread thread : threads) {
					thread.done();
				}
				for(TestCaseThread thread : threads) {
					TestCaseSummary thSummary = thread.getSummary();	
					
					if(!summary.isDone())
						thSummary.setInterrupted();
					summary.join(thSummary);
				}
				summary.setSuccesful();
			}
		} catch (InterruptedException e) {
			stopChilds();
			for(TestCaseThread thread : threads) {
				thread.interrupt();
			}
			summary.setInterrupted();
			Thread.currentThread().interrupt();
		} catch (Throwable e) {
			e.printStackTrace();
			summary.setFailed(e.getMessage());
		} finally {
			summary.setDone();
		}
		return summary; 
	}
	
	public void stopChilds() {
		try {
			for(TestCaseThread th : threads)
				th.interrupt();
		} catch (Throwable e) {
		}
	}
}
