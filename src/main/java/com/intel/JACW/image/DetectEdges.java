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

import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;

/**
 * <p>Test performs edges recognition</p>
 * <p>Test uses an algorithm for detecting edges in an image which uses hysteresis thresholding.
 * This is the basic computer vision problem.</p>
 */
public class DetectEdges extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;

	private ImageUInt8 image, canny;
	private CannyEdge<ImageUInt8, ImageSInt16> detector;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			image = UtilImageIO.loadPGM_U8(inImageStream, (ImageUInt8) null);
			canny = new ImageUInt8(image.width, image.height);
			detector = FactoryEdgeDetectors.canny(2, true, true, ImageUInt8.class,ImageSInt16.class);
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			detector.process(image, 0.1f, 0.3f, canny);
			count++;
		}
		return count;
	}
}
