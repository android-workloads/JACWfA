
package com.intel.JACW.html;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.GoldenFileNotFoundException;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.exception.TestRuntimeErrorException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;


/**
 * Test parses .html page into Document using JSoup.
 * 
 */
public class Parser extends AbstractTestCase {

	@XMLParameter(defaultValue = "wiki_en.html")
	public String goldenFileName;
	@XMLParameter(defaultValue = "https://en.wikipedia.org/wiki/Android_(operating_system)")
	public String baseURI;
	
	private String charsetName = "UTF-8";
	private ByteArrayInputStream input;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			byte[] data = MTTestResourceManager.goldenFileToByteArray(file.getPath());
			input = new ByteArrayInputStream(data);
		} catch (IOException e) {
			e.printStackTrace();
			throw new GoldenFileNotFoundException(file, this.getClass());
		}
	}

	@Override
	public long iteration() throws TestRuntimeErrorException {
		long count = 0;
		try {
		for (int k = 0; k < repeats; k++) {
			input.reset();
			Document doc = Jsoup.parse(input, charsetName, baseURI);
			count++;
		}
		} catch (IOException e) {
			throw new TestRuntimeErrorException("IO error on page parsing");
		}
		return count;
	}
}
