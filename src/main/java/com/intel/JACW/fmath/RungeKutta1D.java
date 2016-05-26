package com.intel.JACW.fmath;

import com.intel.JACW.util.Resources;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>Test solves system of 1 differential equations using Runge-Kutta method [double].</p>
 * Where it is used in android applications:
 * <ol>
 * <li>Games to simulate physics processes</li>
 * </ol>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Runge-Kutta_methods">Runge-Kutta methods (Wikipedia)</a>
 * @see <a href="https://play.google.com/store/apps/details?id=com.mathstools.rungekutta">Runge-Kutta methods (Google Play)</a>
 * @see <a href="https://play.google.com/store/apps/details?id=com.tss.android">Particle simulation (Google Play)</a>
 * @see <a href="http://stackoverflow.com/questions/24922524/android-java-calculating-coupled-differential-equations-effectively">Stackoverflow (it's recommended method for some tasks)</a>
 * @see <a href="https://java.net/projects/cougarsquared/sources/svn/show/trunk/core/libraries/flanagan/integration">Some java library which uses this method</a>
 * 
 */
public class RungeKutta1D extends AbstractTestCase {

	/**
	 * Step of mesh.
	 */
	@XMLParameter(defaultValue = "0.01")
	protected double h;
	/**
	 * Size of solution area.
	 */
	@XMLParameter(defaultValue = "100")
	protected double solArea;
	/*
	 * initial values, Cauchy condition
	 */
	@XMLParameter(defaultValue = "1")
	protected double x1_0;

	private int MAX_ITERS;

	protected double accumulator;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		accumulator = 0.0;
		MAX_ITERS = (int) (solArea / h);
	}

	@Override
	public long iteration() {
		long count = 0;
		double halfH = h/2;
		double onesixthH = h/6;
		for (int i = 0; i < repeats; i++) {
			double t = 0;
			double x1 = x1_0;

			for (int j = 0; j < MAX_ITERS; j++) {
				count++;
				double ax1 = fx1(t, x1);

				double bx1 = fx1(t + halfH, x1 + halfH * ax1);

				double cx1 = fx1(t + halfH, x1 + halfH * bx1);

				double dx1 = fx1(t + h, x1 + h * cx1);

				x1 = onesixthH * (ax1 + 2*bx1 + 2*cx1 + dx1);

				accumulator += x1;
				t += h;
			}
		}
		return count;
	}

	@Override
	public void done() {
		Resources.writeHelperBuffer(RungeKutta1D.class, String.valueOf(accumulator));
	}

	private static double fx1(double t, double x1) {
		return t + 5*x1;
	}
}
