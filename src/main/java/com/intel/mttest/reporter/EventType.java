package com.intel.mttest.reporter;

public enum EventType {
	STARTED {
		@Override
		public String toString() {
			return "test started";
		}

		@Override
		public boolean isCritical() {
			return true;
		}
	},
	FINISHED {
		@Override
		public String toString() {
			return "test finished";
		}

		@Override
		public boolean isCritical() {
			return true;
		}
	},
	STATE_CHANGED {
		@Override
		public String toString() {
			return "changed";
		}

		@Override
		public boolean isCritical() {
			return false;
		}
	};
	abstract public boolean isCritical();
}
