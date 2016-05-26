package com.intel.JACW.jni;

import com.intel.JACW.util.Resources;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * This test performs computing jni function with 4 arguments. 
 * Where it is used in android applications:
 * <ol>
 * <li>There are many JNI interfaces of popular libraries which is written in
 * C/C++ (OpenCV, OpenGL, Qt, CUDA, zlib, Bullet)</li>
 * </ol>
 * Native function returns sum of arguments.
 * 
 * 
 * @see <a href="http://qt-project.org/doc/qt-5/androidgs.html">Getting started
 *      with Qt on android</a>
 * @see <a
 *      href="https://developer.nvidia.com/tegra-android-development-pack">NVIDIA
 *      Tegra android development pack</a>
 * @see <a href="http://www.zlib.net/">Zlib</a>
 * @see <a href="https://code.google.com/p/android-2d-engine/">Bullet engine</a>
 * 
 */
public class Passer4ParamJNI extends AbstractTestCase {
	static {
		System.loadLibrary(Resources.passerJNIParamLibName);
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			count += passer(1, 1, 1, 1);
		}
		return count / 4;
	}

	public native long passer(long p0, long p1, long p2, long p3);
}
