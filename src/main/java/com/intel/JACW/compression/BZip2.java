package com.intel.JACW.compression;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.GoldenFileNotFoundException;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.exception.TestRuntimeErrorException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;


/**
 * Test performs file compression\decompression with BZip2 utility
 * from org.apache.commons.compress.archivers package
 *  
 */
public class BZip2 extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_txt.txt")
	public String goldenFileName;
	
	private byte[] data;
	private ByteArrayInputStream dataBuffer;
	private ByteArrayOutputStream outBuffer;
	private ByteArrayInputStream compressedBuffer;
	
	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		
		outBuffer = new ByteArrayOutputStream();
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			data = MTTestResourceManager.goldenFileToByteArray(file.getPath());
			dataBuffer = new ByteArrayInputStream(data);
			compressZip(dataBuffer, outBuffer);
			compressedBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
		} catch (IOException e) {
			throw new GoldenFileNotFoundException(file, this.getClass());
		}

	}
	
	@Override
	public long iteration() throws TestRuntimeErrorException {
		long counter = 0;
		for (int i = 0; i < repeats; i++) {
			try {
				dataBuffer.reset();
				outBuffer.reset();
				compressZip(dataBuffer, outBuffer);
				
				compressedBuffer.reset();
				outBuffer.reset();
				decompressZip(compressedBuffer, outBuffer);
				
				counter++;
				
				if(config.isValidating && !Arrays.equals(data, outBuffer.toByteArray())) {
					setError("Validation failed");
				}
			} catch (IOException e) {
				throw new TestRuntimeErrorException("compressor cannot write\read stream (" + e.getMessage() + ")");
			}
		}
		return counter;
	}
	
	@SuppressWarnings("resource")
	private void compressZip(ByteArrayInputStream in, ByteArrayOutputStream out) throws IOException {
		CompressorOutputStream wrapperOS = new BZip2CompressorOutputStream(out, 1); 
		IOUtils.copy(in, wrapperOS);
		wrapperOS.close();
	}

	@SuppressWarnings("resource")
	private void decompressZip(ByteArrayInputStream in, ByteArrayOutputStream out) throws IOException {
		CompressorInputStream wrapperIS = new BZip2CompressorInputStream(in);
		IOUtils.copy(wrapperIS, out);
		wrapperIS.close();
	}
}