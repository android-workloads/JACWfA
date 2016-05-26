package com.intel.mttest.util;

public class StringProcessor {
	
	public static final String pathSeparator = "/";

	public static String cutExtention(String path) {
		String filename = getFileName(path);
		int dotPos = filename.lastIndexOf('.');
		if (dotPos < 0)
			return filename;
		return filename.substring(0, dotPos);
	}

	public static String getFileName(String path) {
		int separatorPos = path.lastIndexOf(pathSeparator);
		if (separatorPos == -1)
			return path;
		return path.substring(separatorPos+1);
	}
	
	public static String getDirectory(String path) {
		int separatorPos = path.lastIndexOf(pathSeparator);
		if (separatorPos == -1)
			return path;
		return path.substring(0, separatorPos);
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
