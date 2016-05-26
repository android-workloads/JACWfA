package com.intel.JACW.sort;

import static java.lang.Math.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.intel.JACW.util.ArrayLib;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;


/**
 * Introsort algorithm which uses combination of Quick and Insertion sorts.
 * 
 * It performs recursive operation of qsort algorithm while subrange length is greater than threshold=16.
 * Next it switches on insertion sorting algorithm. 
 * 
 */
public class IntroSortInserts extends AbstractTestCase {

	@XMLParameter(defaultValue = "sort_2000.txt")
	public String goldenFileName;

	private int[] initArray;
	private int[] data;
	private static final int threshold = 16;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		
		File file = new File(GR.getGoldenDir(), goldenFileName);
		BufferedReader in = null; 
		try {
			InputStream inStream = MTTestResourceManager.openFileAsInputStream(file);
			in = new BufferedReader(new InputStreamReader(inStream));
			String s = in.readLine();
			int length = Integer.parseInt(s);
			initArray = new int[length];
			data = new int[length];
			StringTokenizer st = new StringTokenizer(in.readLine());
			for (int i = 0; i < length; i++)
				initArray[i] = Integer.parseInt(st.nextToken());
			in.close();
		} catch (IOException e) {
			try {
				in.close();
			} catch (Throwable ee) {
			}
			throw new InvalidTestFormatException("Malformed " + file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			for (int j = 0; j < initArray.length; j++) {
				data[j] = initArray[j];
			}
			sorting(0, data.length - 1, (int) log(data.length));
			count++;
			if(config.isValidating && !ArrayLib.arrayIsSorted(data)) {
				setError("Validation failed");
			}
		}
		return count;
	}

	private int median(int first, int mid, int last) {
		if (data[first] > data[mid]) {
			if (data[last] < data[mid]) {
				return data[mid];
			} else {
				if (data[first] < data[last]) {
					return data[first];
				} else {
					return data[last];
				}
			}
		} else {
			if (data[last] < data[mid]) {
				if (data[last] > data[first]) {
					return data[last];
				} else {
					return data[first];
				}
			} else {
				return data[mid];
			}
		}
	}

	private void sorting(int first, int last, int depth) {
		if (last - first + 1 > threshold) {
			int left = first, right = last;
			int key_elem = median(first, (left + right) / 2, last);
			int split = 0;
			while (true) {
				while (data[left] < key_elem) {
					left++;
				}
				while (data[right] > key_elem) {
					right--;
				}
				if (left >= right) {
					split = (left + right) / 2;
					break;
				}
				swap(data, left, right);
				left++;
			}
			sorting(first, split, depth - 1);
			sorting(split + 1, last, depth - 1);
		} else
			insertionSort(first, last);
	}

	private void insertionSort(int first, int last) {
		for (int i = first; i <= last; i++) {
			int position = i;
			int tmpData = data[i];
			while (position != first && tmpData < data[position - 1]) {
				data[position] = data[position - 1];
				position--;
			}
			data[position] = tmpData;
		}
	}

	private static void swap(int[] a, int i, int j) {
		int temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
}