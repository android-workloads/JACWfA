package com.intel.JACW.util;

import java.util.Arrays;

/**
 * This consists of useful operations on arrays.
 *
 * @see com.intel.JACW.util.ArrayFillerInt
 */
public class ArrayLib {

	/**
	 * This function give an information about array, it is sorted or not.
	 * @param array array to check
	 * @return if was returned true - array is sorted, otherwise - not sorted
	 */
	public static final boolean arrayIsSorted(int[] array) {
		int result = 0;
		for (int i = 0; i < array.length - 1; ++i) {
			if(array[i] > array[i+1]) {
				result |= 1;
			}
			if(array[i] < array[i+1]) {
				result |= 2;
			}
		}
		return result != 3;
	}
	
	public static final boolean arrayIsSorted(long[] array) {
		int result = 0;
		for (int i = 0; i < array.length - 1; ++i) {
			if(array[i] > array[i+1]) {
				result |= 1;
			}
			if(array[i] < array[i+1]) {
				result |= 2;
			}
		}
		return result != 3;
	}

	/**
	 * It takes an array and two indexes. As result it swap two elements of the array.
	 * @param array array to make transformation
	 * @param i index of one element of array
	 * @param j index of another element of array
	 */
	public static final void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public static final void swap(long[] array, int i, int j) {
		long temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	/**
	 * Clones array
	 * @param array instance array
	 * @return copied array
	 */
	public static final int[] copyArray(int[] array) {
		return Arrays.copyOf(array, array.length);
	}
	
	public static final long[] copyArray(long[] array) {
		return Arrays.copyOf(array, array.length);
	}
}
