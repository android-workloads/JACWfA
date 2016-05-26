package com.intel.JACW.compression;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
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
 * Test performs translation between source file and its tar representation with tar utility
 * from org.apache.commons.compress.archivers package
 * 
 */
public class Tar extends AbstractTestCase {

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
			compressTar(outBuffer);
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
				compressTar(outBuffer);
				
				compressedBuffer.reset();
				outBuffer.reset();
				decompressTar(compressedBuffer, outBuffer);
				
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

	@SuppressWarnings("resource")
	private void compressTar(ByteArrayOutputStream out) throws IOException { 
		TarArchiveOutputStream wrapperOS = new TarArchiveOutputStream(out);
		TarArchiveEntry entry = new TarArchiveEntry(ArchiveStreamFactory.TAR);
		entry.setSize(data.length);
		wrapperOS.putArchiveEntry(entry);
		wrapperOS.write(data);
		wrapperOS.closeArchiveEntry();
	}

	private void decompressTar(ByteArrayInputStream in, ByteArrayOutputStream out) throws IOException {
		TarArchiveInputStream wrapperIS = new TarArchiveInputStream(in);
		wrapperIS.getNextEntry();
		IOUtils.copy(wrapperIS, out);
	}
}