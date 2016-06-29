package com.intel.mttest.util;

import java.io.File;

public class StringProcessor {

	public static String cutExtention(String path) {
		String filename = getFileName(path);
		int dotPos = filename.lastIndexOf('.');
		if (dotPos < 0)
			return filename;
		return filename.substring(0, dotPos);
	}

	public static String getFileName(String path) {
		return new File(path).getName();
	}

	public static String getDirectory(String path) {
		String dir = new File(path).getParent();
		return dir == null ? path : dir;
	}

	public static String getClassnameWithoutPackage(String classname) {
		int lastDotPos = classname.lastIndexOf('.');
		if (lastDotPos == -1)
			return classname;
		return classname.substring(lastDotPos + 1, classname.length());
	}

	public static String cutHead(int newLength, String src) {
		return src.substring(Math.max(0, src.length() - newLength));
	}

	public static String formatDouble(double score, int cnt) {
		String ret = score + "";
		if(!Double.isInfinite(score) && !Double.isNaN(score)) {
			ret = String.format("%." + cnt + "f", score);
		}
		return ret;
	}
}
