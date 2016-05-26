package com.intel.mttest.reporter;

public interface Observer {

	public abstract void update(Observable subject, EventType eventType);
	public abstract void update(Observable subject, MttestMessage eventMsg);

}
