package com.intel.mttest.reporter;

import java.util.ArrayList;

import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestCaseSummary;
import com.intel.mttest.representation.TestSetSummary;
import com.intel.mttest.util.StringProcessor;

public class PlainTableReporter {

	protected int verbose;
	protected Summary rootSummary;
	protected final String shiftString = "";
	protected final String splitLine = "------------------------------------";
	protected final String blankLine = "";
		
	public PlainTableReporter(int verbose, Summary summary) {
		this.rootSummary = summary;
		this.verbose = verbose;
	}

	public PlainTableReporter(TestSetSummary summary) {
        this.rootSummary = summary;
        this.verbose = Integer.MAX_VALUE;
    }

    public void report(ILog logger, boolean recursive) throws MTTestException {
		ArrayList<String> results = build(rootSummary, recursive);
		String prev = null;
		for(String s : results) {
		    if(s.trim().length() == 0) continue;
		    if(s.equals(prev)) continue;
		    logger.i(s);
		    prev = s;
		}
	}
	
	public ArrayList<String> build(Summary summary, boolean recursive) throws MTTestException {
		ArrayList<String> result = new ArrayList<String>();
		
		if(!(summary instanceof TestSetSummary)) {
			return result;
		}
		TestSetSummary setSummary = (TestSetSummary) summary;
		

		result.add(splitLine);
		{
			ArrayList<ArrayList<String>> linesSelf = new ArrayList<ArrayList<String>>();
			linesSelf.add(getHeaderString(setSummary.getClass()));
			linesSelf.add(getScoreString(setSummary));
			result.addAll(normolize(linesSelf));
		}
		
		
		int index = 0;
		
		ArrayList<ArrayList<String>> linesComponents = new ArrayList<ArrayList<String>>();
		for(Summary subSummary : setSummary.getSubSummaries()) {
			if(index++ == 0) {
				linesComponents.add(shift(getHeaderString(subSummary.getClass())));
			}
			linesComponents.add(shift(getScoreString(subSummary)));
		}
		if(linesComponents.size() > 0) {
			result.add(blankLine);
		}
		result.addAll(normolize(linesComponents));
		result.add(splitLine);
		
		if(recursive) {
			for(Summary subSummary : setSummary.getSubSummaries()) {
				if(!(subSummary instanceof TestSetSummary)) {
					continue;
				}
//				result.add("subresult of " + setSummary.getSource().getFullName() + " ---------------");
				ArrayList<String> rec =  build(subSummary, recursive);
				for(String s : rec)
					result.add("     " + s);
			}
		}
		return result;
	}
	
	ArrayList<String> getHeaderString(Class<? extends Summary> sourceClass) throws MTTestException {
		if(sourceClass.equals(TestSetSummary.class)) {
			return getTestSetSummaryHeader();
		} 
		if(sourceClass.equals(TestCaseSummary.class)) {
			return getTestCaseSummaryHeader();
		}
		throw new MTTestException("Reporter does not support " + sourceClass);
	}
		
	ArrayList<String> getScoreString(Summary summary) throws MTTestException {
		if(summary instanceof TestSetSummary) {
			return getTestSetSummaryScoreString((TestSetSummary) summary);
		} 
		if(summary instanceof TestCaseSummary) {
			return getTestCaseSummaryScoreString((TestCaseSummary) summary);
		}
		throw new MTTestException("Reporter does not support " + summary.getClass());
	}

		

	// test set
	ArrayList<String> getTestSetSummaryHeader() {
		ArrayList<String> ret = new ArrayList<String>();
		addField(ret, "name", 1);
		addField(ret, "score", 1);
		addField(ret, "status", 2);
		addField(ret, "total time", 3);
		addField(ret, "Tests", 3);
		return ret;
	}
	
	ArrayList<String> getTestSetSummaryScoreString(TestSetSummary summary) {
		ArrayList<String> ret = new ArrayList<String>();
		addField(ret, summary.getSource().getAlternativeFullName(), 1);
		addField(ret, StringProcessor.formatDouble(summary.getScore(), 5), 1);
		addField(ret, summary.getExecutionStatus().toString(), 2);
		addField(ret, StringProcessor.formatDouble(summary.getTotalTimeInProgressMs() / 1000.0, 2) + " sec", 3);
		addField(ret, summary.getTestsCompletedCount() + "", 3);
		return ret;
	}
	
	
	
	// test case
	ArrayList<String> getTestCaseSummaryHeader() {
		ArrayList<String> ret = new ArrayList<String>();
		addField(ret, "name", 1);
		addField(ret, "source", 1);
		addField(ret, "cfg", 1);
		addField(ret, "score", 1);
		
		addField(ret, "units", 3);
		
		addField(ret, "threads", 2);
		addField(ret, "status", 1);
		addField(ret, "Time per iter", 2);
		addField(ret, "total time", 3);
		addField(ret, "message", 4);
		return ret;
	}
	
	ArrayList<String> getTestCaseSummaryScoreString(TestCaseSummary summary) {
		ArrayList<String> ret = new ArrayList<String>();
		addField(ret, summary.getSource().getName(), 1);
		addField(ret, summary.getRunConfig().getGoldenFile(), 1);
		addField(ret, summary.getSource().getConfigParams().getName(), 1);
		addField(ret, StringProcessor.formatDouble(summary.getScore(), 5), 1);
		
		addField(ret, "ops/" + summary.getRunConfig().timeUnits.toShortString(), 3);

		addField(ret, summary.getJoined() + "", 2);
		addField(ret, summary.getExecutionStatus().toString(), 1);
		addField(ret, StringProcessor.formatDouble(summary.getMeanIterationMs(), 0) + " ms", 2);
		addField(ret, StringProcessor.formatDouble(summary.getTotalTimeInProgressMs() / 1000.0, 2) + " sec", 3);
		addField(ret, summary.getExecutionStatus().getMessage(), 4);
		return ret;
	}
	
	
	
	// utils
	
	protected ArrayList<String> shift(ArrayList<String> source) {
		source.add(0, shiftString);
		return source;
	}
	protected ArrayList<String> normolize(ArrayList<ArrayList<String>> set){
		ArrayList<String> result = new ArrayList<String>();
		if(set == null || set.size() == 0) {
			return result;
		}
		ArrayList<Integer> sz = new ArrayList<Integer>();
		
		for(ArrayList<String> list : set) {
			for(int i = 0; i < list.size(); i++) {
				if(sz.size() <= i){
					sz.add(list.get(i).length());
				} else {
					sz.set(i, Math.max(sz.get(i), list.get(i).length()));
				}				
			}
		}
		for(ArrayList<String> list : set) {
			StringBuilder tmp = new StringBuilder(); 
			for(int i = 0; i < list.size(); i++) {
				tmp.append(ext(sz.get(i) + 5, list.get(i)));
			}
			result.add(tmp.toString());
		}
		return result;
	}
	
	protected void addField(ArrayList<String> list, String field, int verbose) {
		if(this.verbose >= verbose) {
			list.add(field);
		}
	}
		
	private String ext(int length, String s){
		return String.format("%-" + length + "s", s);
	}
}
