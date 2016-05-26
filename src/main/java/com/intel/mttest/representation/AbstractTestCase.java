package com.intel.mttest.representation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.config.TestParam;
import com.intel.mttest.config.TestParams;
import com.intel.mttest.config.TimeUnits;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.exception.NoSuchTestParamException;
import com.intel.mttest.exception.TestRuntimeErrorException;
import com.intel.mttest.loaders.XMLParameter;

abstract public class AbstractTestCase implements TestInterface {

	protected RunConfig config;
	private String error;
	private String warning;

	/**
	 * Number of repetitions of algorithm, which is used to provide enough heavy
	 * loading of test. Default value is one.
	 */
	@XMLParameter(defaultValue = "100")
	protected int repeats;

	private Map<String, Field> testParams;
	private ArrayList<String> testParamsNames;
	private TestParams defaults;

	protected AbstractTestCase() {
		findAllTestParams();
	}

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		this.config = config;
		setDefault();
		setTestParams(config.getCommonParams());
	}

	@Override
	abstract public long iteration() throws TestRuntimeErrorException;

	@Override
	public void done() {
	}

	@Override
	public final int getReps() {
		return repeats;
	}

	@Override
	public final void setReps(int reps) {
		this.repeats = reps;
	}

	@Override
	public String getWarning() {
		return warning;
	}

	@Override
	public String getError() {
		return error;
	}

	@Override
	public final void setWarning(String message) {
		warning = message;
	}

	@Override
	public final void setError(String message) {
		error = message;
	}

	@Override
	public final TimeUnits getTimeUnits() {
		return config.timeUnits;
	}

	@Override
	public final OS getOS() {
		return config.os;
	}

	@Override
	public final String getParam(String name) throws MTTestException {
		try {
			Field testParam = testParams.get(name.toLowerCase());
			if (testParam == null) {
				throw new NoSuchTestParamException(name, this.getClass());
			}
			testParam.setAccessible(true);
			return testParam.get(this).toString();
		} catch (IllegalArgumentException e) {
			throw new MTTestException(
					"The specified object is not a valid instance for this operation.",
					e);
		} catch (IllegalAccessException e) {
			throw new MTTestException(
					"The underlying field is either inaccessible or final.", e);
		}
	}

	@Override
	public final void setTestParam(TestParam param) throws InvalidTestFormatException {
		Field testParam = testParams.get(param.getName());
		if (testParam == null) {
			throw new NoSuchTestParamException(param.getName(), this.getClass());
		}
		setField(testParam, param.getValue());
	}

	private void setField(Field testParam, String value) throws InvalidTestFormatException {
		try {
			String type = testParam.getType().getName();
			testParam.setAccessible(true);
			if (type.equals("long")) {
				testParam.setLong(this, Long.parseLong(value));
			} else if (type.equals("int")) {
				testParam.setInt(this, Integer.parseInt(value));
			} else if (type.equals("float")) {
				testParam.setFloat(this, Float.parseFloat(value));
			} else if (type.equals("double")) {
				testParam.setDouble(this, Double.parseDouble(value));
			} else if (type.equals("boolean")) {
				testParam.setBoolean(this, Boolean.parseBoolean(value));
			} else {
				testParam.set(this, value);
			}
		} catch (NumberFormatException e) {
			throw new InvalidTestFormatException("The String does not contain a parsable value.", this.getClass());
		} catch (IllegalArgumentException e) {
			throw new InvalidTestFormatException("The specified object is not a valid instance for this operation.", this.getClass());
		} catch (IllegalAccessException e) {
			throw new InvalidTestFormatException("The underlying field is either inaccessible or final.", this.getClass());
		}
	}

	@Override
	public final void setTestParams(TestParams params) throws InvalidTestFormatException {
		Collection<TestParam> collection = params.getParams();
		for (TestParam param : collection) {
			if (!param.isSpecial()) {
				setTestParam(param);
			}
		}
	}

	@Override
	public final ArrayList<String> getTestParamsNames() {
		return testParamsNames;
	}

	private void findAllTestParams() {
		testParamsNames = new ArrayList<>();
		testParams = new HashMap<>();
		defaults = new TestParams(TestParams.defaultName);
		ArrayList<Field> fields = getAllFields();
		for (Field field : fields) {
			String name = field.getName().toLowerCase();
			XMLParameter paramAnnot = (XMLParameter) field.getAnnotation(XMLParameter.class);
			if (paramAnnot != null) {
				testParamsNames.add(name);
				testParams.put(name, field);
				TestParam defaultParam = new TestParam(name,
						paramAnnot.defaultValue(), paramAnnot.mustBeNotEmpty());
				defaults.add(defaultParam);
			}
		}
		Collections.reverse(testParamsNames);
	}

	private ArrayList<Field> getAllFields() {
		Class<?> current = this.getClass();
		ArrayList<Field> fields = new ArrayList<Field>();
		while (current.getSuperclass() != null) {
			fields.addAll(Arrays.asList(current.getDeclaredFields()));
			current = current.getSuperclass();
		}
		return fields;
	}

	private void setDefault() throws InvalidTestFormatException {
		Collection<TestParam> defaultParams = defaults.getParams();
		for (TestParam defaultParam : defaultParams) {
			setTestParam(defaultParam);
		}
	}
}
