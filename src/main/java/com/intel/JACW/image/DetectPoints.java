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

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.detect.interest.InterestPointDetector;
import boofcv.factory.feature.detect.interest.FactoryInterestPoint;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageUInt8;

/**
 * <p>
 * Test performs points recognition
 * </p>
 * <p>
 * Test uses a special algorithm for matching features observed in two or more
 * images. It associates the two sets of images so that the relationship can be
 * found. This is done by computing descriptors for each detected feature and
 * associating them together.
 * </p>
 * 
 */
public class DetectPoints extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;

	private ImageUInt8 image;
	private InterestPointDetector<ImageUInt8> detector;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);

		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			image = UtilImageIO.loadPGM_U8(inImageStream, (ImageUInt8) null);
			detector = FactoryInterestPoint.fastHessian(new ConfigFastHessian(
					10, 2, 100, 2, 9, 3, 4));
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			detector.detect(image);
			count++;
		}
		return count;
	}
}
