package com.intel.JACW.util;

public class PseudoRandom {
	
	public PseudoRandom() {
		init(defaultSeed);
	}
	
	public PseudoRandom(long seed) {
		init(seed);
	}
	
	public void setSeed(long seed) {
		init(seed);
	}
	
	public int nextInt(int bound) {
		return (int) (rand() % bound);
	}
	
	private long defaultSeed = 8912759817292181L;
	private long lastValue;
	private final long mod = (1L << 31) - 1;
	private void init(long seed) {
		lastValue = seed & mod;
		rand();
	}
	
	private long rand() {
		return lastValue = (lastValue * 1103515245 + 12345) & mod;
	}
}
