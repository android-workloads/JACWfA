package com.intel.mttest.reporter;

import java.util.ArrayList;

import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestCaseSummary;
import com.intel.mttest.representation.TestExecutionStatus;
import com.intel.mttest.representation.TestSetSummary;
import com.intel.mttest.util.Identifiable;


public class ActivityReporter {

	public class Result extends Identifiable {
		private String name, result;
		private TestExecutionStatus status;

		public Result(String name, String result, TestExecutionStatus status) {
			super();
			this.name = name;
			this.result = formatResult(result);
			this.status = status;
		}
		
		
		public String getName() {
			return name;
		}
		
		public String getResult() {
			return result;
		}
		
		private String formatResult(String res) {
			try {
				return String.format("%.3f", Double.parseDouble(res));
			} catch(Throwable e) {
				return res;
			}
		}


		public TestExecutionStatus getStatus() {
			return status;
		}
	}
	
	public ActivityReporter() {
	}
	
	public Result getGroup(TestSetSummary summary) {
        return getResult(summary);
    }
	
	public ArrayList<Result> getGroups(TestSetSummary summary) {
		ArrayList<Result> results = new ArrayList<Result>();
		if(summary != null) {
			for(Summary subSummary : summary.getSubSummaries()) {
				results.add(getResult(subSummary));
			}
		}
		return results;
	}
	
	public ArrayList<Result> getTests(TestSetSummary summary) {
		ArrayList<Result> results = new ArrayList<Result>();
		if(summary != null) {
			for(Summary subSummary : summary.getSubSummaries()) {
				if(subSummary instanceof TestCaseSummary) {
					results.add(getResult(subSummary));
				}
				if(subSummary instanceof TestSetSummary) {
					results.addAll(getTests((TestSetSummary) subSummary));
				}
			}
		}
		return results;
	}

	public ArrayList<ArrayList<Result>> getTestsHierachicaly(TestSetSummary summary) {
		ArrayList<ArrayList<Result>> results = new ArrayList<>();
		if(summary != null) {
			for(Summary subSummary : summary.getSubSummaries()) {
				if(subSummary instanceof TestSetSummary) {
					results.add(getTests((TestSetSummary) subSummary));
				}
			}
		}
		return results;
	}
	
	public ArrayList<Result> report(TestSetSummary summary) {
		ArrayList<Result> results = new ArrayList<Result>();
		results.add(getResult(summary));
		
		if(summary != null && !summary.getExecutionStatus().equals(TestExecutionStatus.Status.NONE)) {
			for(Summary subSummary : summary.getSubSummaries()) {
				results.add(shift(getResult(subSummary)));
			}
			
			for(Summary subSummary : summary.getSubSummaries()) {
				if(subSummary instanceof TestSetSummary)
					results.addAll(report((TestSetSummary) subSummary));
			}
		}
		return results;
	}

	
	Result shift(Result result) {
		return new Result("   " + result.getName(), result.getResult(), result.getStatus());
	}
	
	private Result getResult(Summary summary) {
		if(summary == null) return new Result("n/a", "n/a", null);
		
		//String name = summary.getShortName();
		String name = summary.getAlternativeName();
		String result = "";
		if(summary.isDone()) {
			TestExecutionStatus status = summary.getExecutionStatus();
			if(status.isPassed()) {
				result = summary.getScore() + "";
			} else {
				result = status.toString();
			}
		} else {
			if(summary.isInProgress()) {
				result = "in progress ";
				if(summary instanceof TestSetSummary)
					result += summary.getTestsCompletedCount() + "/" + summary.getSource().getTestCount();
			} else {
			    result = "waiting";
		        Summary root = summary.getRoot();
		        if(root != null && root.isDone())
		            result = "interrupted"; 
			}
		}
		return new Result(name, result, summary.getExecutionStatus());
	}
}
