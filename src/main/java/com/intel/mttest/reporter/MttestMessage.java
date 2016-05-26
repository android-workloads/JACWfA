package com.intel.mttest.reporter;

public class MttestMessage {
	public static enum Type {
		i, w, e
	}
	private int level;
	private Type type;
	private String message;
	
	public int getLevel() {
		return level;
	}
	
	public String getMessage(){
		return message;
	}
	
	public Type getType() {
		return type;
	}
	
	public MttestMessage(int level, Type type, String message) {
		super();
		this.level = level;
		this.type = type;
		this.message = message;
	}
}
