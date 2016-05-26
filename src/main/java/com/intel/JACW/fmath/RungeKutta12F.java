package com.intel.JACW.fmath;

import com.intel.JACW.util.Resources;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>Test solves system of 12 differential equations using Runge-Kutta method [float].</p>
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
public class RungeKutta12F extends AbstractTestCase {

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
	@XMLParameter(defaultValue = "1")
	protected float x9_0;
	@XMLParameter(defaultValue = "1")
	protected float x10_0;
	@XMLParameter(defaultValue = "1")
	protected float x11_0;
	@XMLParameter(defaultValue = "1")
	protected float x12_0;

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
			float x9 = x9_0;
			float x10 = x10_0;
			float x11 = x11_0;
			float x12 = x12_0;

			for (int j = 0; j < MAX_ITERS; j++) {
				count++;
				float ax1 = fx1(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax2 = fx2(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax3 = fx3(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax4 = fx4(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax5 = fx5(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax6 = fx6(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax7 = fx7(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax8 = fx8(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax9 = fx9(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax10 = fx10(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax11 = fx11(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
				float ax12 = fx12(t, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);

				float bx1 = fx1(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx2 = fx2(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx3 = fx3(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx4 = fx4(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx5 = fx5(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx6 = fx6(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx7 = fx7(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx8 = fx8(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx9 = fx9(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx10 = fx10(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx11 = fx11(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);
				float bx12 = fx12(t + halfH, x1 + halfH * ax1, x2 + halfH * ax2, x3 + halfH * ax3, x4 + halfH * ax4, x5 + halfH * ax5, x6 + halfH * ax6, x7 + halfH * ax7, x8 + halfH * ax8, x9 + halfH * ax9, x10 + halfH * ax10, x11 + halfH * ax11, x12 + halfH * ax12);

				float cx1 = fx1(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx2 = fx2(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx3 = fx3(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx4 = fx4(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx5 = fx5(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx6 = fx6(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx7 = fx7(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx8 = fx8(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx9 = fx9(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx10 = fx10(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx11 = fx11(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);
				float cx12 = fx12(t + halfH, x1 + halfH * bx1, x2 + halfH * bx2, x3 + halfH * bx3, x4 + halfH * bx4, x5 + halfH * bx5, x6 + halfH * bx6, x7 + halfH * bx7, x8 + halfH * bx8, x9 + halfH * bx9, x10 + halfH * bx10, x11 + halfH * bx11, x12 + halfH * bx12);

				float dx1 = fx1(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx2 = fx2(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx3 = fx3(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx4 = fx4(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx5 = fx5(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx6 = fx6(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx7 = fx7(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx8 = fx8(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx9 = fx9(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx10 = fx10(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx11 = fx11(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);
				float dx12 = fx12(t + h, x1 + h * cx1, x2 + h * cx2, x3 + h * cx3, x4 + h * cx4, x5 + h * cx5, x6 + h * cx6, x7 + h * cx7, x8 + h * cx8, x9 + h * cx9, x10 + h * cx10, x11 + h * cx11, x12 + h * cx12);

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
		Resources.writeHelperBuffer(RungeKutta12F.class, String.valueOf(accumulator));
	}

	private static float fx1(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + 5*x1 + x2;
	}

	private static float fx2(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x1 + 5*x2 + x3;
	}

	private static float fx3(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x2 + 5*x3 + x4;
	}

	private static float fx4(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x3 + 5*x4 + x5;
	}

	private static float fx5(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x4 + 5*x5 + x6;
	}

	private static float fx6(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x5 + 5*x6 + x7;
	}

	private static float fx7(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x6 + 5*x7 + x8;
	}

	private static float fx8(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x7 + 5*x8 + x9;
	}

	private static float fx9(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x8 + 5*x9 + x10;
	}

	private static float fx10(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x9 + 5*x10 + x11;
	}

	private static float fx11(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x10 + 5*x11 + x12;
	}

	private static float fx12(float t, float x1, float x2, float x3, float x4, float x5, float x6, float x7, float x8, float x9, float x10, float x11, float x12) {
		return t + x11 + 5*x12;
	}
}
