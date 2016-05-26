package com.intel.JACW.fmath;

import com.intel.JACW.util.Resources;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>Test solves system of 4 differential equations using Runge-Kutta method [double].</p>
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
public class RungeKutta4D extends AbstractTestCase {

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
	@XMLParameter(defaultValue = "1")
	protected double x2_0;
	@XMLParameter(defaultValue = "1")
	protected double x3_0;
	@XMLParameter(defaultValue = "1")
	protected double x4_0;

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
			double x2 = x2_0;
			double x3 = x3_0;
			double x4 = x4_0;

			for (int j = 0; j < MAX_ITERS; j++) {
				count++;
				double ax1 = fx1(t, x1, x2, x3, x4);
				double ax2 = fx2(t, x1, x2, x3, x4);
				double ax3 = fx3(t, x1, x2, x3, x4);
				double ax4 = fx4(t, x1, x2, x3, x4);

				double bx1 = fx1(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4);
				double bx2 = fx2(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4);
				double bx3 = fx3(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4);
				double bx4 = fx4(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4);

				double cx1 = fx1(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4);
				double cx2 = fx2(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4);
				double cx3 = fx3(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4);
				double cx4 = fx4(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4);

				double dx1 = fx1(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4);
				double dx2 = fx2(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4);
				double dx3 = fx3(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4);
				double dx4 = fx4(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4);

				x1 = onesixthH * (ax1 + 2*bx1 + 2*cx1 + dx1);
				x2 = onesixthH * (ax2 + 2*bx2 + 2*cx2 + dx2);
				x3 = onesixthH * (ax3 + 2*bx3 + 2*cx3 + dx3);
				x4 = onesixthH * (ax4 + 2*bx4 + 2*cx4 + dx4);

				accumulator += x1 + x2 + x3 + x4;
				t += h;
			}
		}
		return count;
	}

	@Override
	public void done() {
		Resources.writeHelperBuffer(RungeKutta4D.class, String.valueOf(accumulator));
	}

	private static double fx1(double t, double x1, double x2, double x3, double x4) {
		return t + 5*x1 + x2;
	}

	private static double fx2(double t, double x1, double x2, double x3, double x4) {
		return t + x1 + 5*x2 + x3;
	}

	private static double fx3(double t, double x1, double x2, double x3, double x4) {
		return t + x2 + 5*x3 + x4;
	}

	private static double fx4(double t, double x1, double x2, double x3, double x4) {
		return t + x3 + 5*x4;
	}
}
