package com.intel.mttest.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.loaders.CmdArgs;
import com.intel.mttest.representation.OS;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * GlobalResources. This class contains some constants.
 */
public class GR {
	
	public synchronized static void init(OS os, Context context, CmdArgs args) {
		GR.os = os;
		GR.context = context;
		if(os.equals(OS.ANDROID))
			initAndroid();
		else
			initJava();
		resultsDataDir = args.resultsDir;
        initialized = true;
	}
	
	public static Context getContext() {
		check();
		return context;
	}
	
	public static OS getOS() {
		check();
		return os;
	}
	
	public static String getGoldenDir() {
		check();
		return goldensDir;
	}
	public static String getTestsetDir() {
		check();
		return testsetsDir;
	}
	public static String getConfigDir() {
		check();
		return configsDir;
	}
	
	protected static File[] getSubFiles(String src) throws IOException {
		check();		
		if(os.equals(OS.JAVA)) {
			File dir = new File(src);
			return dir.listFiles(new FileFilter() {	
				@Override
				public boolean accept(File elem) {
					return elem.isFile();
				}
			});
		}
		if(os.equals(OS.ANDROID)) {
			AssetManager fs = context.getAssets();
			String[] subNames = fs.list(src);
			ArrayList<File> files = new ArrayList<File>();
			for(String subName : subNames) {
				File file = new File(src, subName);
				String[] mark = fs.list(file.getPath());
				if(mark.length == 0)
					files.add(file);
			}
			File ret[] = new File[files.size()];
			for(int i = 0; i < ret.length; i++)
				ret[i] = files.get(i);
			return ret;
		}
		
		throw new RuntimeException("Functionallity was not implemented");
	}
	
	public static ArrayList<String> getSubFilesName(String src) throws MTTestException {
		check();
		ArrayList<String> names = new ArrayList<String>();
		File[] files = null;;
		try {
			files = getSubFiles(src);
		} catch (IOException e) {
			throw new MTTestException(e.getMessage(), e);
		}
		for(File f : files)
			names.add(f.getName());
		return names;
	}

	volatile protected static boolean initialized = false;
	protected static volatile OS os;
	protected static volatile Context context;
	protected static volatile String goldensDir;
	protected static volatile String configsDir;
	protected static volatile String testsetsDir;
	protected static volatile String resultsDataDir;
	
	
	public static File createResultsFile(String fileName) throws IOException {
	    File folder = new File(resultsDataDir);
	    folder.mkdirs();
	    File f = new File(folder, fileName);
	    f.createNewFile();
	    return f;
	}
	
	protected static void initAndroid() {
		goldensDir = "goldens";
		testsetsDir = "testsets";
		configsDir = "configs";
	}
	
	protected static void initJava() {
		goldensDir = System.getProperty("mttestGoldensDir");
		testsetsDir = System.getProperty("mttestTestSetDir");
		configsDir = System.getProperty("mttestConfigDir");
//		resultsDataDir = System.getProperty("mttestResultDir") ;
		if(goldensDir == null || testsetsDir == null || configsDir == null) {
			throw new RuntimeException("Environment was not properly setted: goldens=" + goldensDir + " suite=" + testsetsDir + " cfg=" + configsDir);
		}
	}
	
	protected static void check() {
		if(!initialized) {
			throw new IllegalArgumentException("GlobalResources was not initialized");
		}	
	}
}
