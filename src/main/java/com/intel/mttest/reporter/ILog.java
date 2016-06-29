package com.intel.mttest.reporter;

import java.io.PrintStream;
import com.intel.mttest.representation.OS;
import android.util.Log;

public class ILog {

	protected PrintStream out;
	protected OS os;
	protected String TAG = "mttest";

	public PrintStream getOut() {
		return out;
	}
	public ILog(OS os, PrintStream out) {
		this.os = os;
		this.out = out;
		if(!OS.ANDROID.equals(os) && out == null) {
			this.out = System.out;
		}
	}
	
	public void i(String message) { 
		if(out != null) {
			out.println(TAG + "    " + message);
		} else { 
			Log.i(TAG, message);
		}
	}

	public void w(String message) { 
		if(out != null) {
			out.println(TAG + ":warning:    " + message);
		} else {
			Log.w(TAG, message);
		}
	}

	public void e(String message) { 
		if(out != null) {
			out.println(TAG + ":error:    " + message);
		} else {
			Log.e(TAG, message);
		}
	}

	public void appendTAG(String ext) {
		TAG += ext;
	}
    public void noTag(String message) {
        if(out != null) {
            out.println(message);
        } else {
            throw new IllegalArgumentException("Operation is not supported. The TAG required");
        }
    }
}
