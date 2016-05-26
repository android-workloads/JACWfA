package com.intel.mttest.util;

public class Identifiable implements Comparable<Identifiable> {
	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object e) {
		return (e instanceof Identifiable) ? (id == ((Identifiable) e).getId()) : false;
	}

	@Override
	public int compareTo(Identifiable e) {
		return id - e.getId();
	}
	
	static private volatile int counter = 0;

	static private synchronized int generateId() {
		return counter++;
	}

	protected int id;

	protected Identifiable() {
		id = generateId();
	}
}
