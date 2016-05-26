package com.intel.JACW.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.exception.TestRuntimeErrorException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;
import com.intel.mttest.util.GR;
import com.intel.mttest.util.MTTestResourceManager;


/**
 * <p>Setup password protection a PDF file.</p>
 * 
 */
public class Encryption extends AbstractTestCase {

	/**
	 * PDF-file to be encrypted.
	 */
	@XMLParameter(defaultValue = "sample_pdf.pdf")
	public String goldenFileName;

	@XMLParameter(defaultValue = "123")
	protected String ownerPass;

	@XMLParameter(defaultValue = "456")
	protected String userPass;

	ByteArrayInputStream in;
	ByteArrayOutputStream out;

	ProtectionPolicy policy;
	
	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		File file = new File(GR.getGoldenDir(), goldenFileName);
		try {
			in = new ByteArrayInputStream(MTTestResourceManager.goldenFileToByteArray(file.getPath()));
			out = new ByteArrayOutputStream();
			AccessPermission ap = new AccessPermission();
			policy = new StandardProtectionPolicy(ownerPass, userPass, ap);
		} catch (IOException e) {
			throw new InvalidTestFormatException ("file not found " + e.getMessage() + " " + file.getAbsolutePath(), this.getClass());
		}
	}

	@Override
	public long iteration() throws TestRuntimeErrorException {
		long count = 0;
		try {
			for (int i = 0; i < repeats; i++) {
				in.reset();
				out.reset();
				PDDocument doc = PDDocument.load(in);
				doc.protect(policy);
				doc.save(out);
				doc.close();
				count++;
			}
		} catch (IOException e) {
			throw new TestRuntimeErrorException("IO exception in " + this.getClass() + " | " + e.getMessage());
		}
		return count;
	}

	@Override
	public void done() {
		try {
                        in.close();
                        out.close();
                } catch (Exception e) {}
        }
}
