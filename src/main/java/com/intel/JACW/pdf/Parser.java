package com.intel.JACW.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.exception.TestRuntimeErrorException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;


/**
 * <p>Parse PDF document into logical tokens in terms of pdfbox library</p>
 * 
 */
public class Parser extends AbstractTestCase {

	/**
	 * PDF-file to be encrypted.
	 */
	@XMLParameter(defaultValue = "sample_pdf.pdf")
	public String goldenFileName;

	ArrayList<byte[]> content;
	
	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			PDDocument doc = PDDocument.load(MTTestResourceManager.goldenFileToByteArray(file.getPath()));
			Iterator<PDPage> pageIt = doc.getPages().iterator();
			content = new ArrayList<byte[]>();
			while (pageIt.hasNext()) {
				PDPage page = (PDPage) pageIt.next();
				Iterator<PDStream> streamIt = page.getContentStreams();
				while (streamIt.hasNext()) {
				    PDStream stream = (PDStream) streamIt.next();
				    content.add(stream.toByteArray());
				}
			}
			doc.close();
		} catch (IOException e) {
			throw new InvalidTestFormatException ("file not found " + e.getMessage() + " " + file.getAbsolutePath(), this.getClass());
		}
	}

	@Override
	public long iteration() throws TestRuntimeErrorException {
		long count = 0;
		try {
			for (int i = 0; i < repeats; i++) {
				for(byte[] part : content) {
					PDFStreamParser parser = new PDFStreamParser(part);
					parser.parse();
				}
				count++;
			}
		} catch (IOException e) {
			throw new TestRuntimeErrorException("IO exception in " + this.getClass() + " | " + e.getMessage());
		}
		return count;
	}
}
