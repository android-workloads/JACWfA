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

import boofcv.alg.color.ColorXyz;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

/**
 * <p>
 * Test performs conversion between CIE XYZ and RGB color models. The XYZ color
 * model is based on on three hypothetical primaries, XYZ, and all visible
 * colors can be represented by using only positive values of X, Y, and Z. This
 * model is completly device-independent.
 * </p>
 * 
 */

public class RGBtoXYZ extends AbstractTestCase {

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
			ColorXyz.rgbToXyz_U8(image, convert);
			count++;
		}
		return count;

	}
}
