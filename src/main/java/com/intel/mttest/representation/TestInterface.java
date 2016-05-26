package com.intel.mttest.representation;

import java.util.ArrayList;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.config.TestParam;
import com.intel.mttest.config.TestParams;
import com.intel.mttest.config.TimeUnits;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.exception.TestRuntimeErrorException;

public interface TestInterface {

	/**
	 * Called once to initialize the test with the given parameters before
	 * running the first iteration. Can be used to allocate resources for the
	 * run.
	 * 
	 * @param runConfig
	 *            test configuration
	 */
	public void init(RunConfig params) throws InvalidTestFormatException;

	/**
	 * Execute a number of pay load operations. One iteration is checked to last
	 * from 0.2 to 2.0 seconds.
	 * 
	 * @return number of executed operations
	 */
	public long iteration() throws TestRuntimeErrorException;

	/**
	 * Called once after the last iteration. Can be used to reclaim resources.
	 */
	public void done();

	public TimeUnits getTimeUnits();

	public OS getOS();

	public int getReps();

	public void setReps(int reps);

	public String getWarning();

	public String getError();

	public void setWarning(String message);

	public void setError(String message);

	public String getParam(String name) throws MTTestException;

	public void setTestParam(TestParam param) throws MTTestException;

	public void setTestParams(TestParams params) throws MTTestException;

	public ArrayList<String> getTestParamsNames();

}
