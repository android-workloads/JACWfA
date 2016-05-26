package com.intel.JACW.image;

import georegression.struct.shapes.EllipseRotated_F64;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.GoldenFileNotFoundException;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;

import boofcv.alg.feature.shapes.FitData;
import boofcv.alg.feature.shapes.ShapeFittingOps;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

/**
 * <p>
 * Test fits ellipses to object contours in a binary image. It is a common task
 * in industrial and scientific settings.
 * </p>
 * 
 */
public class FitEllipse extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_picture.jpg")
	public String goldenFileName;

	private ImageFloat32 image;
	private ImageUInt8 filtered;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);

		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			InputStream inImageStream = MTTestResourceManager.openFileAsInputStream(file.getPath());
			ImageUInt8 inImage = UtilImageIO.loadPGM_U8(inImageStream, (ImageUInt8) null);
			image = ConvertImage.convert(inImage, (ImageFloat32) null);
			ImageUInt8 bin = new ImageUInt8(image.width, image.height);
			double mean = ImageStatistics.mean(image);
			ThresholdImageOps.threshold(image, bin, (float) mean, true);
			filtered = BinaryImageOps.erode8(bin, 1, null);
			filtered = BinaryImageOps.dilate8(filtered, 1, null);
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	public FitData<EllipseRotated_F64> result;

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			List<Contour> contours = BinaryImageOps.contour(filtered,
					ConnectRule.EIGHT, null);
			for (Contour c : contours) {
				result = ShapeFittingOps.fitEllipse_I32(c.external, 0, false,
						null);
			}
			count++;
		}
		return count;
	}
}
