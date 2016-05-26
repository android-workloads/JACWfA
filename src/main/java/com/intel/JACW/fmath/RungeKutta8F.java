package com.intel.JACW.fmath;

import com.intel.JACW.util.Resources;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>Test solves system of 8 differential equations using Runge-Kutta method [float].</p>
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
public class RungeKutta8F extends AbstractTestCase {

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
	@XMLParameter(defaultValue = "1")
	protected float x2_0;
	@XMLParameter(defaultValue = "1")
	protected float x3_0;
	@XMLParameter(defaultValue = "1")
	protected float x4_0;
	@XMLParameter(defaultValue = "1")
	protected float x5_0;
	@XMLParameter(defaultValue = "1")
	protected float x6_0;
	@XMLParameter(defaultValue = "1")
	protected float x7_0;
	@XMLParameter(defaultValue = "1")
	protected float x8_0;

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
		float halfH = h/2;
		float onesixthH = h/6;
		for (int i = 0; i < repeats; i++) {
			float t = 0;
			float x1 = x1_0;
			float x2 = x2_0;
			float x3 = x3_0;
			float x4 = x4_0;
			float x5 = x5_0;
			float x6 = x6_0;
			float x7 = x7_0;
			float x8 = x8_0;

			for (int j = 0; j < MAX_ITERS; j++) {
				count++;
				float ax1 = fx1(t, x1, x2, x3, x4, x5, x6, x7, x8);
				float ax2 = fx2(t, x1, x2, x3, x4, x5, x6, x7, x8);
				float ax3 = fx3(t, x1, x2, x3, x4, x5, x6, x7, x8);
				float ax4 = fx4(t, x1, x2, x3, x4, x5, x6, x7, x8);
				float ax5 = fx5(t, x1, x2, x3, x4, x5, x6, x7, x8);
				float ax6 = fx6(t, x1, x2, x3, x4, x5, x6, x7, x8);
				float ax7 = fx7(t, x1, x2, x3, x4, x5, x6, x7, x8);
				float ax8 = fx8(t, x1, x2, x3, x4, x5, x6, x7, x8);

				float bx1 = fx1(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8);
				float bx2 = fx2(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8);
				float bx3 = fx3(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8);
				float bx4 = fx4(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8);
				float bx5 = fx5(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8);
				float bx6 = fx6(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8);
				float bx7 = fx7(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8);
				float bx8 = fx8(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8);

				float cx1 = fx1(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8);
				float cx2 = fx2(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8);
				float cx3 = fx3(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8);
				float cx4 = fx4(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8);
				float cx5 = fx5(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8);
				float cx6 = fx6(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8);
				float cx7 = fx7(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8);
				float cx8 = fx8(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8);

				float dx1 = fx1(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8);
				float dx2 = fx2(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8);
				float dx3 = fx3(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8);
				float dx4 = fx4(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8);
				float dx5 = fx5(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8);
				float dx6 = fx6(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8);
				float dx7 = fx7(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8);
				float dx8 = fx8(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8);

				x1 = onesixthH * (ax1 + 2*bx1 + 2*cx1 + dx1);
				x2 = onesixthH * (ax2 + 2*bx2 + 2*cx2 + dx2);
				x3 = onesixthH * (ax3 + 2*bx3 + 2*cx3 + dx3);
				x4 = onesixthH * (ax4 + 2*bx4 + 2*cx4 + dx4);
				x5 = onesixthH * (ax5 + 2*bx5 + 2*cx5 + dx5);
				x6 = onesixthH * (ax6 + 2*bx6 + 2*cx6 + dx6);
				x7 = onesixthH * (ax7 + 2*bx7 + 2*cx7 + dx7);
				x8 = onesixthH * (ax8 + 2*bx8 + 2*cx8 + dx8);

				accumulator += x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8;
				t += h;
			}
		}
		return count;
	}

	@Override
	public void done() {
		Resources.writeHelperBuffer(RungeKutta8F.class, String.valueOf(accumulator));
	}

	private static float fx1(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8) {
		return t + 5*x1 + x2;
	}

	private static float fx2(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8) {
		return t + x1 + 5*x2 + x3;
	}

	private static float fx3(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8) {
		return t + x2 + 5*x3 + x4;
	}

	private static float fx4(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8) {
		return t + x3 + 5*x4 + x5;
	}

	private static float fx5(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8) {
		return t + x4 + 5*x5 + x6;
	}

	private static float fx6(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8) {
		return t + x5 + 5*x6 + x7;
	}

	private static float fx7(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8) {
		return t + x6 + 5*x7 + x8;
	}

	private static float fx8(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8) {
		return t + x7 + 5*x8;
	}
}
