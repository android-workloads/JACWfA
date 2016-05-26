package com.intel.mttest.representation;

public class TestExecutionStatus {
	public enum Status {
		NONE(0) {
			@Override
			public String toString() {
				return "skipped";
			}
		},
		IN_PORGRESS(1) {
			@Override
			public String toString() {
				return "executing";
			}
		}, 
		SUCCESS(2) {
			@Override
			public String toString() {
				return "success";
			}
		}, 
		WARNING(3) {
			@Override
			public String toString() {
				return "warning";
			}
		}, 
		FAIL(4) {
			@Override
			public String toString() {
				return "failed";
			}
		}, 
		INTERRUPT(5) {
			@Override
			public String toString() {
				return "interrupted";
			}
		};
		private final int priority;
		
		private Status(int priority) {
			this.priority = priority;
		}
		
		public int getPriority() {
			return priority;
		}
		
		public String toShortString() {
			return toString().charAt(0) + "";
		}

		public boolean equals(TestExecutionStatus e) {
			return this.equals(e.status);
		}
	}
	
	protected final Status status;
	protected final String message;
	
	
	public TestExecutionStatus(Status status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public String getMessage() {
		return message == null ? "-" : message;
	}
	
	public int getPriority() {
		return status.getPriority();
	}		
	
	public String toString() {
		return status.toString();
	}
	
	public String toShortString() {
		return status.toShortString();
	}
	public boolean isPassed() {
		return status.equals(Status.SUCCESS) || status.equals(Status.WARNING);
	}

	public static TestExecutionStatus getWorst(TestExecutionStatus s1, TestExecutionStatus s2) {
		return s1.getPriority() > s2.getPriority() ? s1 : s2;
	}

	public boolean isDone() {
		return !status.equals(Status.NONE) && !status.equals(Status.IN_PORGRESS);
	}

	public boolean inProgress() {
		return status.equals(Status.IN_PORGRESS);
	}
	
	public boolean equals(Status e) {
		return status.equals(e);
	}
}
