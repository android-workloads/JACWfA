package com.intel.JACW.dmath;


import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>Test performs computing an [integer] arithmetic series and stops computing when sum reaches specified bound.
 * This is general math algorithm which has various applications. Bit it's pretty hard to find particular android application built on it.</p>
 * 
 */
public class ArithProgressionI extends AbstractTestCase {

	@XMLParameter(defaultValue = "100000000")
	public int argSeqSumLimit;

	@Override
	public long iteration() {
		long counter = 0;
		for (int i = 0; i < repeats; i++) {
			int base = i;
			int step = i + 1;
			int seqSum = 0;
			int elemIndex;
			for (elemIndex = 0; seqSum < argSeqSumLimit; elemIndex++) {
				seqSum += base + elemIndex * step;
			}
			counter += elemIndex;
		}
		return counter;
	}
}