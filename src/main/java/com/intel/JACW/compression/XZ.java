package com.intel.JACW.compression;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
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
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;


/**
 * Test performs file compression\decompression with XZ utility
 * from org.apache.commons.compress.archivers package
 * 
 */
public class XZ extends AbstractTestCase {

	@XMLParameter(defaultValue = "sample_txt.txt")
	public String goldenFileName;

	private byte[] data;
	private ByteArrayOutputStream outBuffer;
	private ByteArrayInputStream compressedBuffer;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		outBuffer = new ByteArrayOutputStream();
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			data = MTTestResourceManager.goldenFileToByteArray(file.getPath());
			outBuffer.reset();
			compressXZ(outBuffer);
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
				outBuffer.reset();
				compressXZ(outBuffer);
				
				compressedBuffer.reset();
				outBuffer.reset();
				decompressXZ(compressedBuffer, outBuffer);
				
				counter++;

				if(config.isValidating && !Arrays.equals(data, outBuffer.toByteArray())) {
					setError("Validation failed");
				}
			} catch (IOException e) {
				throw new TestRuntimeErrorException("compressor cannot access buffer (" + e.getMessage() + ")");
			}
		}
		return counter;
	}

	void compressXZ(ByteArrayOutputStream out) throws IOException {
		LZMA2Options opts = new LZMA2Options();
		// preset manage compression level at all, DictSize limit allow to rn test on low-memory devices
		opts.setPreset(3);
		opts.setDictSize(1 << 18);
		XZOutputStream wrapperOS = new XZOutputStream(out, opts);
		wrapperOS.write(data);
		wrapperOS.close();
	}

	void decompressXZ(ByteArrayInputStream in, ByteArrayOutputStream out) throws IOException {
		XZCompressorInputStream wrapperIS = new XZCompressorInputStream(in); 
		IOUtils.copy(wrapperIS, out);
		wrapperIS.close();
	}
}