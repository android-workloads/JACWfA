package com.intel.JACW.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.GoldenFileNotFoundException;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;

import boofcv.abst.transform.fft.DiscreteFourierTransform;
import boofcv.alg.transform.fft.DiscreteFourierTransformOps;
import boofcv.core.image.ConvertImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.InterleavedF32;

/**
 * <p>
 * Test computes the Discrete Fourier Transform.
 * </p>
 * 
 */
public class DFT extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;

	private ImageFloat32 image;
	private InterleavedF32 transform;
	private DiscreteFourierTransform<ImageFloat32, InterleavedF32> dft;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);

		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			ImageUInt8 inImage = UtilImageIO.loadPGM_U8(inImageStream, (ImageUInt8) null);
			image = ConvertImage.convert(inImage, (ImageFloat32) null);
			dft = DiscreteFourierTransformOps.createTransformF32();
			transform = new InterleavedF32(image.width, image.height, 2);
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			dft.forward(image, transform);
			count++;
		}
		return count;

	}
}
