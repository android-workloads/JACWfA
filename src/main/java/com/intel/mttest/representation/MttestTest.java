package com.intel.mttest.representation;

import com.intel.mttest.config.ConfigParams;
import com.intel.mttest.config.TestParams;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.util.Identifiable;


public abstract class MttestTest extends Identifiable {
 
	protected String name;
	protected String alternativeName;
	protected TestSet owner;
	protected TestParams params;
	protected ConfigParams config;

	public MttestTest(String name, TestSet owner, ConfigParams config, TestParams params) throws MTTestException{
		this.name = name;
		this.owner = owner;
		this.config = config;
		this.params = params;
		this.alternativeName = this.getShortName();
	}
	
	public MttestTest(String name, String alternativeName, TestSet owner, ConfigParams config, TestParams params) throws MTTestException{
		this(name, owner, config, params);
		this.alternativeName = alternativeName;
	}

	public boolean isRoot() {
		return owner == null;
	}

	public void setAlternativeName(String alternativeName) {
		this.alternativeName = alternativeName;
	}
	
	public String getAlternativeName() {
		return alternativeName;
	}
	
	public String getName() {
		return name;
	}

	public TestParams getTestParams() {
		return params;
	}

	public ConfigParams getConfigParams() {
		return config;
	}
	
	public TestSet getOwner() {
		return owner;
	}
	
	public final String getFullName() {
		return (owner == null ? "" : (owner.getFullName() + ":")) + getName();
	}

	public final String getAlternativeFullName() {
		return (owner == null ? "" : (owner.getFullName() + ":")) + getAlternativeName();
	}
	
	public abstract int getTestCount();

	public String getShortName() {
		String s = getName();
		if(s != null) {
			s = s.substring(Math.max(0, s.lastIndexOf('.') + 1));
		}
		return s;
	}
	
	@Override
	public int compareTo(Identifiable ie) {
		if(ie instanceof MttestTest){
			MttestTest e = (MttestTest) ie;
			if(!name.equals(e.name)) {
				return name.compareTo(e.name);
			}
			int x = params.compareTo(e.getTestParams());
			if(x != 0) {
				return x;
			}
		}
		return super.compareTo(ie);
	}
}
