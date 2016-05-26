package com.intel.mttest.launchers;

import java.util.List;

import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.representation.OS;
import com.intel.mttest.representation.TestCase;
import com.intel.mttest.representation.TestCaseSummary;
import com.intel.mttest.representation.TestSet;
import com.intel.mttest.representation.TestSetSummary;


public final class TestSetThread extends Thread {

	protected TestSet rootTestSet;
	protected TestSetSummary rootSetSummary;
	protected OS os;
	protected volatile TestCaseLauncher caseLauncher = null;
	
	
	public TestSetThread(OS os, TestSet testSet) throws MTTestException {
		this.rootTestSet = testSet;
		this.os = os;
		this.rootSetSummary = new TestSetSummary(rootTestSet, null);
	}
	
	public TestSetSummary getSummary() {
		return rootSetSummary;
	}
	
	public final void run() {
		run(rootTestSet, rootSetSummary);
	}
	
	synchronized protected void run(TestSet testSet, TestSetSummary testSummary) {
		testSummary.setInProgress();
		try {
			{
				List<TestSet> subsets = testSet.getTestSubsets();
				if(subsets != null && subsets.size() != 0) {
					for(TestSet subSet : subsets) {
						if(isInterrupted()) {
							testSummary.setInterrupted();
							break;
						}
						TestSetSummary subSummary = testSummary.getSubSummary(subSet);
						run(subSet, subSummary);
					}
				}
			}
	
			{
				List<TestCase> subcases = testSet.getTestSubcases();
				if(subcases != null && subcases.size() != 0) {
					for(TestCase subcase : subcases) {
						if(isInterrupted()) {
							testSummary.setInterrupted();
							break;
						}
						caseLauncher = new TestCaseLauncher(os, subcase);
						TestCaseSummary caseSummary = testSummary.getSubSummary(subcase);
						caseLauncher.run(caseSummary);
					}
				}
			}
			testSummary.setSuccesful();
		}  catch (Throwable e) {
			e.printStackTrace();
			testSummary.setFailed(e.getMessage());
		} finally {
			testSummary.setDone();
		}
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {
			caseLauncher.stopChilds();
		} catch (Throwable e) {
		}
	}


/*
	class TestSetThreadState {
		protected volatile boolean flagStop, flagRunning, flagFinished;
		public TestSetThreadState() {
			flagStop = false;
			flagRunning = false;
			flagFinished = false;
		}
		
		public void started() {
			flagStop = false;
			flagRunning = true;
			flagFinished = false;
		}
		public void stopped() {
			flagStop = false;
			flagRunning = false;
			flagFinished = false;
		}
		
		public void completed() {
			flagStop = false;
			flagRunning = false;
			flagFinished = true;
		}
		
		public void requestStop() {
			flagStop = true;
		}
		
		public boolean isRunning() { 
			return flagRunning;
		}
		public boolean isStopping() {
			return flagRunning & flagStop;
		}
		public boolean isFinished() {
			return flagFinished;
		}
	};
	protected TestSetThreadState state;
	*/


}
