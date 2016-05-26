package com.intel.mttest.representation;


public enum OS {
	ANDROID("Android"),
	ALL("All"), 
	JAVA("Java");

	private String representation;

	OS(String representation) {
		this.representation = representation.toLowerCase();
	}

	@Override
	public String toString() {
		return representation;
	}

	public static OS getOSByRepresentation(String name) throws IllegalArgumentException {
		for (OS os : OS.values()) {
			if (os.toString().toLowerCase().equals(name.toLowerCase()))
				return os;
		}
		throw new IllegalArgumentException();
	}
}

