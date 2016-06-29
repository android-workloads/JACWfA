package com.intel.mttest.representation;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.config.TimeUnits;
import com.intel.mttest.exception.MTTestException;

public class TestCaseSummary extends Summary {

	protected volatile long durationNano;
	protected volatile long scoreSum;
	protected volatile int cntRuns;
	protected volatile RunConfig runConfig;
	protected volatile int joinedCount = 0;
	
	public TestCaseSummary(TestCase test, TestSetSummary owner) throws MTTestException {
		super(test, owner);
		runConfig = new RunConfig(test);
		this.durationNano = 0;
		this.scoreSum = 0;
		this.cntRuns = 0;
	}
	
	public RunConfig getRunConfig() {
		return runConfig;
	}
	@Override
	public int getDurationMs() {
		return (int) (convertTime(durationNano, TimeUnits.OPS_PER_MILLISECOND));
	}

	@Override
	public int getTestsCompletedCount() {
		return getExecutionStatus().isDone() ? 1 : 0;
	}

	@Override
	public double getMeanIterationMs() {
		return  (double) getDurationMs() / cntRuns;
	}

	@Override
	public double getScore() {
		double result = Double.NEGATIVE_INFINITY;
		try {
			result = getTestsCompletedCount() == 0 ? Double.NaN : ((double) scoreSum / convertTime(durationNano, runConfig.timeUnits));
		} catch (Throwable e) {
		}
		return result;
	}

	public void collect(long score, long durationNano, long count) {
		joinedCount = 1;
		this.cntRuns += count;
		this.scoreSum += score;
		this.durationNano += durationNano;
	}
	
	public static double convertTime(long time, TimeUnits units) {
		double timeConverted = time;
		// don't use the break!
		switch (units) {
			case OPS_PER_MINUTE: timeConverted /= 60;
			case OPS_PER_SECOND: timeConverted /= 1000;
			case OPS_PER_MILLISECOND: timeConverted /= 1000 * 1000;
			case OPS_PER_NANOSECOND: timeConverted /= 1;
			break;
		}
		return (double) timeConverted;
	}
	
	public void join(TestCaseSummary sum) {
		updateAgregatedExecutionStatus(sum.getExecutionStatus());
		durationNano = getArithmeticMean(durationNano, joinedCount, sum.durationNano, sum.joinedCount);
		scoreSum = scoreSum + sum.scoreSum;
		cntRuns = getArithmeticMean(cntRuns, joinedCount, sum.cntRuns, sum.joinedCount);
		joinedCount += sum.getJoined();
	}
	public int getJoined() {
		return joinedCount;
	}
	
	protected long getArithmeticMean(long score1, int weight1, long score2, int weight2) {
		return (score1 * weight1 + score2 * weight2) / (weight1 + weight2);
	}
	protected int getArithmeticMean(int score1, int weight1, int score2, int weight2) {
		return (score1 * weight1 + score2 * weight2) / (weight1 + weight2);
	}
	
	@Override
	public String getShortName() {
		String ret = super.getShortName();
		if(getRunConfig().hasGoldenFile()) {
			ret += " (" + getRunConfig().getGoldenFile() + ")";
		}
		return ret;
	}

	@Override
	public String getAlternativeName() {
		String ret = super.getAlternativeName();
		if(getRunConfig().hasGoldenFile()) {
			ret += " (" + getRunConfig().getGoldenFile() + ")";
		}
		return ret;
	}
	
    public TestSetSummary getRoot() {
        return this.owner.getRoot();
    }	
}
