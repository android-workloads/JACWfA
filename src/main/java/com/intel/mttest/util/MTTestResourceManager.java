package com.intel.mttest.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import com.intel.mttest.representation.OS;

public class MTTestResourceManager {
	
	public static File getGoldenFile(String fileName) {
		return new File(GR.getGoldenDir(), fileName);
	}

	public static byte[] goldenFileToByteArray(String fileName) throws IOException {
		InputStream in = openFileAsInputStream(fileName);
		byte[] res = IOUtils.toByteArray(in);
		in.close();
		return res;
	}

	public static InputStream openFileAsInputStream(File file) throws IOException {
		return openFileAsInputStream(file.getPath());
	}

	public static InputStream openFileAsInputStream(String fileName) throws IOException {
	    
		if (GR.getOS().equals(OS.ANDROID)) {
			if(fileName.startsWith("/")) {
				fileName = fileName.substring(1);
			}
			return GR.getContext().getAssets().open(fileName);
		} else {
		    FileInputStream in = new FileInputStream(fileName);
		    byte[] buffer = IOUtils.toByteArray(in);
		    in.close();
		    return new ByteArrayInputStream(buffer);
		}
	}

	public static ByteBuffer readFileToByteBuffer(File file) throws IOException {
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			byte[] bytes = new byte[(int) randomAccessFile.length()];
			randomAccessFile.readFully(bytes);
			return ByteBuffer.wrap(bytes);
		} finally {
			if (randomAccessFile != null)
				randomAccessFile.close();
		}
	}

	public static ByteBuffer downloadPageAsByteBuffer(String strURL)
			throws IOException {
		URL url = new URL(strURL);
		InputStream is = url.openStream();
		byte[] bytes = IOUtils.toByteArray(is);
		ByteBuffer byteData = ByteBuffer.wrap(bytes);
		if (is != null) {
			is.close();
		}
		return byteData;
	}
}
