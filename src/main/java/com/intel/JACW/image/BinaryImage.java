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

import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.core.image.ConvertImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

/**
 * <p> Test performs image binarization. </p>
 * <p> It creates binary images by thresholding.
 * In binary images each pixel can have only 0 or 1 value so they are easy to compute, 
 * which makes them popular in many applications. </p>
 */
public class BinaryImage extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;
	
	private ImageFloat32 image;
	private ImageUInt8 binary;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			ImageUInt8 inImage = UtilImageIO.loadPGM_U8(inImageStream, (ImageUInt8) null);
			image = ConvertImage.convert(inImage, (ImageFloat32) null);
			binary = new ImageUInt8(image.width, image.height);
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			double threshold = GThresholdImageOps.computeOtsu(image, 0, 256);
			ThresholdImageOps.threshold(image, binary, (float) threshold, true);
			count++;
		}
		return count++;
	}
}
