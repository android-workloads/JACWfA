package com.intel.mttest.representation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.reporter.EventType;

public class TestSetSummary extends Summary {

	protected volatile int expectedTestResultsCount;
	protected volatile List<Summary> subsummaries;
	protected volatile int durationMs;
	protected volatile DescriptiveStatistics results;
	protected volatile int cntTestsCompleted;
	
	public TestSetSummary(MttestTest test, TestSetSummary owner) throws MTTestException {
		super(test, owner);
		results = new DescriptiveStatistics();
		durationMs = 0;
		cntTestsCompleted = 0;
		subsummaries = new ArrayList<Summary>();
		if(test instanceof TestSet) {
			expectedTestResultsCount = test.getTestCount(); 
		}
		if(test instanceof TestSet) {
			TestSet tSet = (TestSet) test;
			for(TestSet elem : tSet.getTestSubsets()) {
				TestSetSummary add = new TestSetSummary(elem, this);
				subsummaries.add(add);
			}
			for(TestCase elem : tSet.getTestSubcases()) {
				TestCaseSummary add = new TestCaseSummary(elem, this);
				subsummaries.add(add);
			}
		}
		if(test instanceof TestCase) {
			TestCaseSummary add = new TestCaseSummary((TestCase) test, this);
			subsummaries.add(add);
		}
		
	}

	public double getProgress() {
		return 100.0 * getTestsCompletedCount() / getTestExpectedCount();
	}
	@Override
	public int getTestsCompletedCount() {
		return cntTestsCompleted;
	}
	
	public int getTestExpectedCount() {
		return expectedTestResultsCount;
	}

	@Override
	public int getDurationMs() {
		return (int) durationMs;
	}
	
	@Override
	public double getMeanIterationMs() {
		return (double) getDurationMs() / subsummaries.size();
	}

	@Override
	public double getScore() {
		return results.getGeometricMean();
	}

	public void addResult(Summary sum) {
		if(subsummaries.contains(sum)) return;
		if(sum.getOwner() != this) {
			throw new IllegalArgumentException("Attempt to add summary which has antoher owner");
		}
		subsummaries.add(sum);
		processSubersult(sum);
		notifyObservers(EventType.STATE_CHANGED);
		recalcOwner();
	}
	
	public List<Summary> getSubSummaries() {
		return subsummaries;
	}
	
	protected void processSubersult(Summary sum) {
		results.addValue(sum.getScore());
		durationMs += sum.getDurationMs();
		cntTestsCompleted += sum.getTestsCompletedCount();
		updateAgregatedExecutionStatus(sum.getExecutionStatus());
	}
	
	public void recalc(Summary sum) {
		if(!subsummaries.contains(sum)) subsummaries.add(sum);
		results = new DescriptiveStatistics();
		durationMs = 0;
		cntTestsCompleted = 0;
		for(Summary subSum : subsummaries)
			processSubersult(subSum);
		notifyObservers(EventType.STATE_CHANGED);
		recalcOwner();
	}

	public TestCaseSummary getCaseInProgress() {
		if(!isInProgress()) {
			return null;
		}
		for(Summary sub : subsummaries) {
			if(sub instanceof TestCaseSummary && sub.isInProgress()) {
				return (TestCaseSummary) sub;
			}
		}
		for(Summary sub : subsummaries) {
			if(sub instanceof TestSetSummary) {
				TestCaseSummary ret = (((TestSetSummary) sub).getCaseInProgress());
				if(ret != null)
					return ret;
			}
		}
		return null;
	}

	public TestSetSummary getSubSummary(TestSet inst) throws MTTestException {
		return (TestSetSummary) findSubSummary(inst);
	}

	public TestCaseSummary getSubSummary(TestCase inst) throws MTTestException {
		return (TestCaseSummary) findSubSummary(inst);
	}
	
	private Summary findSubSummary(MttestTest test) throws MTTestException {
		for(Summary s : subsummaries) {
			if(s.getSource() == test) {
				return s;
			}
		}
		throw new MTTestException("Cannot find subsammary for specified  test");
	}

    public TestSetSummary getRoot() {
        return owner == null ? this : owner.getRoot();
    }
}
