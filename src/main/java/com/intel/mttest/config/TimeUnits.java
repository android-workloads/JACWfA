package com.intel.mttest.config;

public enum TimeUnits {
	OPS_PER_MINUTE("minute") {
		@Override
		public String toShortString() {
			return "min";
		}
	},
	OPS_PER_SECOND("second") {
		@Override
		public String toShortString() {
			return "sec";
		}
	},
	OPS_PER_MILLISECOND("millisecond") {
		@Override
		public String toShortString() {
			return "ms";
		}
	},
	OPS_PER_NANOSECOND("nanosecond") {
		@Override
		public String toShortString() {
			return "ns";
		}
	};

	private String representation;

	TimeUnits(String representation) {
		this.representation = representation;
	}

	public String getRepresentation() {
		return representation;
	}

	public static TimeUnits getUnitsByRepresentation(String representation) throws IllegalArgumentException {
		TimeUnits [] units = TimeUnits.values();
		int index;
		for (index = 0; index < units.length; index++) {
			if (units[index].getRepresentation().equals(representation))
				return units[index];
		}
		throw new IllegalArgumentException();
	}

	abstract public String toShortString();
}
