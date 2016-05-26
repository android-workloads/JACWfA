package com.intel.JACW.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.GoldenFileNotFoundException;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;

import boofcv.abst.denoise.FactoryImageDenoise;
import boofcv.abst.denoise.WaveletDenoiseFilter;
import boofcv.alg.misc.GImageMiscOps;
import boofcv.core.image.ConvertImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

/**
 * <p>Test performs image denoising using Sure algorithm</p>
 * <p>It uses wavelet based Sure algorithm to add and remove noise from image.
 * Often noise is modeled as Gaussian noise being added to each pixel independently.
 * And finally filter creates a smoother image.
 * Commonly used in image processing. </p>
 */
public class DenoiseSure extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;

	private ImageFloat32 denoisy, noisy;
	private Random rnd;
	private int levels;
	private WaveletDenoiseFilter<ImageFloat32> denoiser;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			rnd = new Random(repeats);
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			ImageUInt8 inImage = UtilImageIO.loadPGM_U8(inImageStream, (ImageUInt8) null);
			ImageFloat32 convImage = ConvertImage.convert(inImage, (ImageFloat32) null);
			levels = 4;
			denoiser = FactoryImageDenoise.waveletSure(ImageFloat32.class, levels, 0, 255);
			noisy = convImage.clone();
			denoisy = convImage.clone();
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			GImageMiscOps.addGaussian(noisy, rnd, 20, 0, 255);
			denoiser.process(noisy, denoisy);
			count++;
		}
		return count;

	}
}
