package com.intel.JACW.sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.intel.JACW.util.ArrayLib;
import com.intel.JACW.util.PseudoRandom;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.exception.TestRuntimeErrorException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;


/**
 * Non-recursive Qsort implementation for [long] arrays.
 * 
 * In java it is implemented in Arrays.sort(...) method of
 * java.util package to sort arrays.
 * 
 * 
 * @see com.intel.JACW.sort.QSortRecursiveL
 */
public class QSortIterativeL extends AbstractTestCase {

	@XMLParameter(defaultValue = "sort_2000.txt")
	public String goldenFileName;

	private long[] initArray;
	private long[] data;
	private PseudoRandom rnd;

	private int STACK_SIZE, top;
	private int[] stack;
	
	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);

		rnd = new PseudoRandom();
		File file = new File(GR.getGoldenDir(), goldenFileName);
		BufferedReader in = null; 
		try {
			InputStream inStream = MTTestResourceManager.openFileAsInputStream(file);
			in = new BufferedReader(new InputStreamReader(inStream));
			String s = in.readLine();
			int length = Integer.parseInt(s);
			initArray = new long[length];
			data = new long[length];
			StringTokenizer st = new StringTokenizer(in.readLine());
			for (int i = 0; i < length; i++)
				initArray[i] = Long.parseLong(st.nextToken());
			in.close();
		} catch (IOException e) {
			try {
				in.close();
			} catch (Throwable ee) {
			}
			throw new InvalidTestFormatException("Malformed " + file, this.getClass());
		}

		STACK_SIZE = initArray.length;
		stack = new int[STACK_SIZE];
	}

	@Override
	public long iteration() throws TestRuntimeErrorException {
		long counter = 0;
		rnd.setSeed(data.length);
		for (int i = 0; i < repeats; i++) {
			for (int j = 0; j < initArray.length; j++) {
				data[j] = initArray[j];
			}
			qsort(data);
			counter++;
			if(config.isValidating && !ArrayLib.arrayIsSorted(data)) {
				setError("Validation failed");
			}
		}
		return counter;
	}

	/**
	 * Iterative implementation of quick sort algorithm.
	 * 
	 * @param array
	 *            array to be sorted
	 * @return number of business operations
	 */
	public void qsort(long[] array) throws TestRuntimeErrorException {
		if (array.length > 0) {
			int left = 0;
			int right = array.length - 1;

			top = -1;
			stack[++top] = left;
			stack[++top] = right;
			while (top >= 0) {
				if (top + 4 > STACK_SIZE) {
					throw new TestRuntimeErrorException("Default stack size is too small: " + this.getClass());
				}
				right = stack[top--];
				left = stack[top--];
				int pivotIndex = randInt(left, right);
				long pivotValue = array[pivotIndex];
				ArrayLib.swap(array, pivotIndex, right);
				int p = left;
				for (int i = left; i < right; ++i) {
					if (array[i] < pivotValue) {
						ArrayLib.swap(array, i, p);
						p++;
					}
				}
				ArrayLib.swap(array, p, right);

				if (p - 1 > left) {
					stack[++top] = left;
					stack[++top] = p - 1;
				}

				if (p + 1 < right) {
					stack[++top] = p + 1;
					stack[++top] = right;
				}
			}
		}
	}

	private int randInt(int min, int max) {
		int result = rnd.nextInt((max - min) + 1) + min;
		return result;
	}
}
