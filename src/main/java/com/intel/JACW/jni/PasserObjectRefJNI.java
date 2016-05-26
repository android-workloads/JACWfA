package com.intel.JACW.jni;

import com.intel.JACW.util.NotImportantObject;
import com.intel.JACW.util.Resources;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * This test performs computing jni function with
 * {@link com.intel.JACW.util.NotImportantObject} as an argument.
 * Where it is used in android applications:
 * <ol>
 * <li>There are many JNI interfaces of popular libraries which is written in
 * C/C++ (OpenCV, OpenGL, Qt, CUDA, zlib)</li>
 * </ol>
 * Native function returns sum of [long] arguments.
 * 
 * 
 * @see <a href="http://qt-project.org/doc/qt-5/androidgs.html">Getting started
 *      with Qt on android</a>
 * @see <a
 *      href="https://developer.nvidia.com/tegra-android-development-pack">NVIDIA
 *      Tegra android development pack</a>
 * @see <a href="http://www.zlib.net/">Zlib</a>
 * 
 */
public class PasserObjectRefJNI extends AbstractTestCase {
	static {
		System.loadLibrary(Resources.passerJNIParamLibName);
	}

	private NotImportantObject obj;

	public PasserObjectRefJNI() {
		obj = new NotImportantObject();
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			count += passer(obj, 1, 1);
		}
		return count / 2;
	}

	public native long passer(NotImportantObject obj, long a, long b);
}
