package com.intel.JACW.fmath;


import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>
 * Test performs computing a [float] arithmetic series and stops computing when
 * sum reaches specified bound. This is general math algorithm which has various
 * applications. Bit it's pretty hard to find particular android application
 * built on it.
 * </p>
 * 
 */
public class ArithProgressionF extends AbstractTestCase {

	@XMLParameter(defaultValue = "100000000")
	public float argSeqSumLimit;

	@Override
	public long iteration() {
		long counter = 0;
		for (int i = 0; i < repeats; i++) {
			float base = i;
			float step = i + 1;
			float seqSum = 0;
			int elemIndex;
			for (elemIndex = 0; seqSum < argSeqSumLimit; elemIndex++) {
				seqSum += base + elemIndex * step;
			}
			counter += elemIndex;
		}
		// validation is not supported
		return counter;
	}
}