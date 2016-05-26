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

import boofcv.alg.color.ColorLab;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

/**
 * <p>
 * Test performs conversion between RGB and LAB color space. It is designed to
 * approximate human vision.
 * </p>
 * 
 */
public class RGBtoLAB extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;

	MultiSpectral<ImageUInt8> image;
	MultiSpectral<ImageFloat32> convert;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);

		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			image = UtilImageIO.loadPPM_U8(inImageStream,
					(MultiSpectral<ImageUInt8>) null, null);
			convert = new MultiSpectral<ImageFloat32>(ImageFloat32.class,
					image.width, image.height, 3);
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			ColorLab.rgbToLab_U8(image, convert);
			count++;
		}
		return count;

	}
}
