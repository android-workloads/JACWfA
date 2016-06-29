package com.intel.mttest.android.adapters.data;

import java.util.ArrayList;

import com.intel.mttest.reporter.ActivityReporter.Result;

public class GroupData {
	
	private Result result;
	public ArrayList<Result> testCases;

	public GroupData (Result result, ArrayList<Result> testCases) {
		this.result = result;
		this.testCases = testCases;
	}
	
	public Result getTestCaseResult(int i) {
		return testCases.get(i);
	}
	
	public Result get() {
		return result;
	}

}
