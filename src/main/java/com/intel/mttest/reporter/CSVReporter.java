package com.intel.mttest.reporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import com.intel.mttest.config.ConfigParams.Field;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestCaseSummary;
import com.intel.mttest.representation.TestSetSummary;
import com.intel.mttest.util.StringProcessor;

public class CSVReporter {

	protected int verbose;
	protected TestSetSummary rootSummary;

	public CSVReporter(TestSetSummary summary) {
        this.rootSummary = summary;
        this.verbose = Integer.MAX_VALUE;
        loadProperties();
    }

	private void loadProperties() {
		final Properties props = new Properties();
		try {
			final InputStream in = new FileInputStream(new File("mttest.properties"));
			props.load(in);
			System.setProperties(props);
			in.close();
		} catch (IOException e) {
			
		}
	}
	
	private String getHeader(TestSetSummary summary) {
	    String ret = "";
	    ret = summary.getSource().getFullName();
	    ret = ret.replaceAll("masterset", "JACW");
	    ret = ret.replaceAll(":", "_");
	    try {
            ret += "_" + summary.getSource().getConfigParams().getValue(Field.threads) + "T";
        } catch (MTTestException e) {
            e.printStackTrace();
        }
	    // TODO the default version is hardcoded, approach is OK for java/host, not for device
	    String version = System.getProperty("mttest_version", "1.2");
	    ret += "/" + version;
	    return ret;
	}
	
	
    public void report(ILog logger, boolean recursive) throws MTTestException {
		ArrayList<String> results = build(rootSummary, recursive);
		for(String s : results)
			logger.noTag(s);
	}
	
    String buildString(String header, String name, Summary summary) {
        
        String score = summary.getExecutionStatus().isPassed() ? (summary.getScore() + "") : summary.getExecutionStatus().toString(); 
        
        try {
            score = StringProcessor.formatDouble(Double.parseDouble(score), 3);
        } catch (Throwable e) {
        }
        return header + ";" + name + ";original (1);" + score + ";"; 
        
    }
	public ArrayList<String> build(Summary summary, boolean recursive) throws MTTestException {
		ArrayList<String> result = new ArrayList<String>();
		if(!(summary instanceof TestSetSummary)) {
			return result;
		}
		TestSetSummary setSummary = (TestSetSummary) summary;
		
		String header = getHeader(setSummary);
		if(!recursive || setSummary.getSubSummaries().size() > 1) {
		    String self=buildString(header, "Overall", setSummary);
		    result.add(self);
		}
		
		for(Summary subSummary : setSummary.getSubSummaries()) {
            String row = "";
	        if(subSummary instanceof TestSetSummary) {
	            TestSetSummary inst = (TestSetSummary) subSummary;
	            String name = subSummary.getShortName();
	            row = buildString(header, name, inst);
	        } else {
	            TestCaseSummary inst = (TestCaseSummary) subSummary;
	            String name = inst.getSource().getShortName() + "(" + inst.getRunConfig().getGoldenFile() + ")";
                row = buildString(header, name, inst);
	        }
	        
	        result.add(row);
		}
	
		if(recursive) {
			for(Summary subSummary : setSummary.getSubSummaries()) {
				if(!(subSummary instanceof TestSetSummary)) {
					continue;
				}
				ArrayList<String> rec =  build(subSummary, recursive);
				for(String s : rec)
					result.add(s);
			}
		}
		return result;
	}
}
