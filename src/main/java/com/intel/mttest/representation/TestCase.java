package com.intel.mttest.representation;

import com.intel.mttest.config.ConfigParams;
import com.intel.mttest.config.TestParams;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.exception.WorkloadNotFoundException;


public class TestCase extends MttestTest {

	private Class<? extends TestInterface> testClass; // TestInterface test = testClass.newInstance();
	
	public Class<? extends TestInterface> getTestClass() {
		return testClass;
	}
	
	public TestCase(String className, TestSet owner, ConfigParams config, TestParams params) throws MTTestException {
		super(className, owner, config, params);
		testClass = loadClass(this.name);
	}
		
	public TestCase(TestCase test, TestSet owner) throws MTTestException {
		super(test.getName(), owner, test.getConfigParams(), test.getTestParams());
		testClass = loadClass(this.name);
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends TestInterface> loadClass(String testClassName) throws MTTestException {
		try {
			return (Class<? extends TestInterface>) Class.forName(testClassName);
		} catch (ClassNotFoundException e) {
			throw new WorkloadNotFoundException(testClassName);
		} catch (ClassCastException e) {
			throw new MTTestException("Class " + testClassName + " doesn't implement testInterface");
		}
	}
	
	@Override
	public int getTestCount() {
		return 1;
	}
}
