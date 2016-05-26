package com.intel.JACW.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class MTTestErrorHandler implements ErrorHandler {
	
	public StringBuilder log;
	public int warnings;
	public int errors;
	public int fatalErrors;	
	 
	public MTTestErrorHandler() {
		this.log = new StringBuilder();
        this.warnings = 0;
    	this.errors = 0;
    	this.fatalErrors = 0;
    }

	public void reset(){
		this.log = new StringBuilder();
        this.warnings = 0;
    	this.errors = 0;
    	this.fatalErrors = 0;		
	}
	
	@Override
    public void warning(SAXParseException e) {
    	log.append(String.format("warning: %s\n", e.getMessage()));
        warnings++;
    }

	@Override
    public void error(SAXParseException e) {
    	log.append(String.format("error: %s\n", e.getMessage()));
        errors++;
    }

	@Override
    public void fatalError(SAXParseException e) {
    	log.append(String.format("fatalError: %s\n", e.getMessage()));
        fatalErrors++;
    }
}