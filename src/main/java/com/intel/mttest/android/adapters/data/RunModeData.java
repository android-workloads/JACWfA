package com.intel.mttest.android.adapters.data;

import java.util.ArrayList;

import com.intel.mttest.reporter.ActivityReporter.Result;

public class RunModeData {
	
	private Result result;
	public ArrayList<GroupData> groups;

	public RunModeData (Result result, ArrayList<GroupData> groups) {
		this.result = result;
		this.groups = groups;
	}
	
	public GroupData getGroupResult(int i) {
		return groups.get(i);
	}
	
	public Result get() {
		return result;
	}

}
