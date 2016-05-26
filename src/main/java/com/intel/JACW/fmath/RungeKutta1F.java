package com.intel.JACW.fmath;

import com.intel.JACW.util.Resources;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>
 * Test solves system of 1 differential equations using Runge-Kutta method [float].
 * </p>
 * Where it is used in android applications:
 * <ol>
 * <li>Games to simulate physics processes</li>
 * </ol>
 * 
 *
 * @see <a href="http://en.wikipedia.org/wiki/Runge-Kutta_methods">Runge-Kuttamethods (Wikipedia)</a>
 * @see <ahref="https://play.google.com/store/apps/details?id=com.mathstools.rungekutta">Runge-Kuttamethods (Google Play)</a>
 * @see <ahref="https://play.google.com/store/apps/details?id=com.tss.android">Particlesimulation (Google Play)</a>
 * @see <a href="http://stackoverflow.com/questions/24922524/android-java-calculating-coupled-differential-equations-effectively">Stackoverflow (it's recommended method for some tasks)</a>
 * @see <ahref="https://java.net/projects/cougarsquared/sources/svn/show/trunk/core/libraries/flanagan/integration">Somejava library which uses this method</a>
 */
public class RungeKutta1F extends AbstractTestCase {

	/**
	 * Step of mesh.
	 */
	@XMLParameter(defaultValue = "0.01")
	protected float h;
	/**
	 * Size of solution area.
	 */
	@XMLParameter(defaultValue = "100")
	protected float solArea;
	/*
	 * initial values, Cauchy condition
	 */
	@XMLParameter(defaultValue = "1")
	protected float x1_0;

	private int MAX_ITERS;

	protected float accumulator;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		accumulator = 0.0f;
		MAX_ITERS = (int) (solArea / h);
	}

	@Override
	public long iteration() {
		long count = 0;
		float halfH = h / 2;
		float onesixthH = h / 6;
		for (int i = 0; i < repeats; i++) {
			float t = 0;
			float x1 = x1_0;

			for (int j = 0; j < MAX_ITERS; j++) {
				count++;
				float ax1 = fx1(t, x1);

				float bx1 = fx1(t + halfH, x1 + halfH * ax1);

				float cx1 = fx1(t + halfH, x1 + halfH * bx1);

				float dx1 = fx1(t + h, x1 + h * cx1);

				x1 = onesixthH * (ax1 + 2 * bx1 + 2 * cx1 + dx1);

				accumulator += x1;
				t += h;
			}
		}
		return count;
	}

	@Override
	public void done() {
		Resources.writeHelperBuffer(RungeKutta1F.class,String.valueOf(accumulator));
	}

	private static float fx1(float t, float x1) {
		return t + 5 * x1;
	}
}
