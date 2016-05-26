package com.intel.JACW.fmath;

import com.intel.JACW.util.Resources;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>Test solves system of 12 differential equations using Runge-Kutta method [double].</p>
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
public class RungeKutta12D extends AbstractTestCase {

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
	@XMLParameter(defaultValue = "1")
	protected double x5_0;
	@XMLParameter(defaultValue = "1")
	protected double x6_0;
	@XMLParameter(defaultValue = "1")
	protected double x7_0;
	@XMLParameter(defaultValue = "1")
	protected double x8_0;
	@XMLParameter(defaultValue = "1")
	protected double x9_0;
	@XMLParameter(defaultValue = "1")
	protected double x10_0;
	@XMLParameter(defaultValue = "1")
	protected double x11_0;
	@XMLParameter(defaultValue = "1")
	protected double x12_0;

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
			double x5 = x5_0;
			double x6 = x6_0;
			double x7 = x7_0;
			double x8 = x8_0;
			double x9 = x9_0;
			double x10 = x10_0;
			double x11 = x11_0;
			double x12 = x12_0;

			for (int j = 0; j < MAX_ITERS; j++) {
				count++;
				double ax1 = fx1(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax2 = fx2(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax3 = fx3(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax4 = fx4(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax5 = fx5(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax6 = fx6(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax7 = fx7(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax8 = fx8(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax9 = fx9(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax10 = fx10(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax11 = fx11(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				double ax12 = fx12(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);

				double bx1 = fx1(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx2 = fx2(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx3 = fx3(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx4 = fx4(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx5 = fx5(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx6 = fx6(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx7 = fx7(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx8 = fx8(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx9 = fx9(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx10 = fx10(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx11 = fx11(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				double bx12 = fx12(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);

				double cx1 = fx1(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx2 = fx2(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx3 = fx3(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx4 = fx4(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx5 = fx5(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx6 = fx6(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx7 = fx7(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx8 = fx8(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx9 = fx9(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx10 = fx10(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx11 = fx11(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				double cx12 = fx12(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);

				double dx1 = fx1(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx2 = fx2(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx3 = fx3(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx4 = fx4(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx5 = fx5(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx6 = fx6(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx7 = fx7(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx8 = fx8(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx9 = fx9(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx10 = fx10(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx11 = fx11(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				double dx12 = fx12(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);

				x1 = onesixthH * (ax1 + 2*bx1 + 2*cx1 + dx1);
				x2 = onesixthH * (ax2 + 2*bx2 + 2*cx2 + dx2);
				x3 = onesixthH * (ax3 + 2*bx3 + 2*cx3 + dx3);
				x4 = onesixthH * (ax4 + 2*bx4 + 2*cx4 + dx4);
				x5 = onesixthH * (ax5 + 2*bx5 + 2*cx5 + dx5);
				x6 = onesixthH * (ax6 + 2*bx6 + 2*cx6 + dx6);
				x7 = onesixthH * (ax7 + 2*bx7 + 2*cx7 + dx7);
				x8 = onesixthH * (ax8 + 2*bx8 + 2*cx8 + dx8);
				x9 = onesixthH * (ax9 + 2*bx9 + 2*cx9 + dx9);
				x10 = onesixthH * (ax10 + 2*bx10 + 2*cx10 + dx10);
				x11 = onesixthH * (ax11 + 2*bx11 + 2*cx11 + dx11);
				x12 = onesixthH * (ax12 + 2*bx12 + 2*cx12 + dx12);

				accumulator += x1 + x2 + x3 + x4 + x5 + x6 + x7 + x8 + x9 + x10 + x11 + x12;
				t += h;
			}
		}
		return count;
	}

	@Override
	public void done() {
		Resources.writeHelperBuffer(RungeKutta12D.class, String.valueOf(accumulator));
	}

	private static double fx1(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + 5*x1 + x2;
	}

	private static double fx2(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x1 + 5*x2 + x3;
	}

	private static double fx3(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x2 + 5*x3 + x4;
	}

	private static double fx4(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x3 + 5*x4 + x5;
	}

	private static double fx5(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x4 + 5*x5 + x6;
	}

	private static double fx6(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x5 + 5*x6 + x7;
	}

	private static double fx7(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x6 + 5*x7 + x8;
	}

	private static double fx8(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x7 + 5*x8 + x9;
	}

	private static double fx9(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x8 + 5*x9 + x10;
	}

	private static double fx10(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x9 + 5*x10 + x11;
	}

	private static double fx11(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x10 + 5*x11 + x12;
	}

	private static double fx12(double t, double x1, double x2, double x3, double x4, double x5, double x6, double x7, double x8, double x9, double x10, double x11, double x12) {
		return t + x11 + 5*x12;
	}
}
