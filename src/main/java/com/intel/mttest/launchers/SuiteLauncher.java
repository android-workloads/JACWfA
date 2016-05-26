package com.intel.mttest.launchers;

import java.util.ArrayList;
import java.util.List;

import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.reporter.EventType;
import com.intel.mttest.reporter.Observer;
import com.intel.mttest.representation.OS;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestSet;
import com.intel.mttest.representation.TestSetSummary;

public class SuiteLauncher {
	
	static public void init(OS os) {
		SuiteLauncher.os = os;
		observers = new ArrayList<Observer>();
	}
	static public void addStaticObserver(Observer observer) {
		check();
		if(!observers.contains(observer)) {
			observers.add(observer);
		}
		if(runner != null)
			runner.getSummary().addObserver(observer);
	}

	public static void clearStaticObservers() {
		if(observers != null) {
			observers.clear();
		}
	}
	
	static public Summary waitForResults() {
		check();
		if(runner == null || !runner.isAlive())
			return getSummary();
		try {
			runner.join();
		} catch (InterruptedException e) {
		}
		return getSummary();
	}
	
	static public TestSetSummary getSummary() {
		check();
		return runner == null ? null : runner.getSummary();
	}
	
	
	
	static public TestSetSummary runTestSet(TestSet testSet) throws MTTestException {
		if(testSet == null) {
			throw new IllegalArgumentException("Cannot start. TestSeti is null");
		}
		check();	
		stopTestingBlocked();
		runner = new TestSetThread(os, testSet);
		TestSetSummary summary = runner.getSummary();
		for(Observer obs : observers) {
			summary.addObserver(obs);
			obs.update(summary, EventType.STATE_CHANGED);
		}
		runner.start();
		return summary;
	}
	
	static public boolean isRunning() {
		check();
		return runner != null && runner.isAlive();
	}
	
	static protected OS os;
	static protected SuiteLauncher inst; 
	static protected List<Observer> observers;
	static protected TestSetThread runner;
	
	static public void stopTestingNonBlocked() {
		check();
		try {
			if(runner != null)
				runner.interrupt();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void stopTestingBlocked() {
		check();
		try {
			stopTestingNonBlocked();
			runner.join();
		} catch (Throwable e) {
		}
	}
	
	static protected void check() {
		if(os == null) {
			throw new IllegalArgumentException("OS was not defined: " + SuiteLauncher.class);
		}
	}
}
