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

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageUInt8;

/**
 * <p>
 * Test performs intensity modification with sharpen8 function.
 * </p>
 * <p>
 * Test uses sharpen-8 function, which increase intensity of edges but the other
 * parts stay the same. It's usually used to processing images in mobile
 * applications
 * </p>
 * 
 */
public class EnhancementSharpen8 extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;

	private ImageUInt8 image, result;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);

		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			image = UtilImageIO.loadPGM_U8(inImageStream, (ImageUInt8) null);
			result = new ImageUInt8(image.width, image.height);
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			EnhanceImageOps.sharpen8(image, result);
			count++;
		}
		return count;
	}
}
