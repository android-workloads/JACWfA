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
import boofcv.alg.misc.ImageStatistics;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageUInt8;

/**
 * <p>Test performs intensity modification depends on image histogram. </p>   
 * <p>Test uses Histogram adjustment algorithms to
 * change pixel intensity across the allowed range. It helps to view images and find out more details.
 * It's usually used to processing images in mobile applications</p>
 */
public class EnhancementHistogram extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;

	private ImageUInt8 image, result;
	int histo[], trans[];

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			image = UtilImageIO.loadPGM_U8(inImageStream, (ImageUInt8) null);
			result = new ImageUInt8(image.width, image.height);
			histo = new int[256];
			trans = new int[256];
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() {
		long count = 0;
		for(int i = 0; i < repeats; i++){
			ImageStatistics.histogram(image, histo);
			EnhanceImageOps.equalize(histo, trans);
			EnhanceImageOps.applyTransform(image, trans, result);
			count++;		
		}
		return count;
	}

}
