package com.intel.mttest.loaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.intel.mttest.config.ConfigParams;
import com.intel.mttest.config.TestParam;
import com.intel.mttest.config.TestParams;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.exception.InvalidMTTestConfFileException;
import com.intel.mttest.exception.MTTestRuntimeException;
import com.intel.mttest.representation.OS;
import com.intel.mttest.representation.TestCase;
import com.intel.mttest.representation.TestSet;
import com.intel.mttest.util.MTTestResourceManager;
import com.intel.mttest.util.StringProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 * This class contains methods to parse configuration xml file.
 * 
 * 
 */
public class XMLParser {

	/*
	private Document doc;

	private Properties globalProps;
	private Properties finalProps;
	private List<TestCaseRunner> testCases;
	private List<TestGroup> groups;
	private TestGroup currentGroup;
	*/

	/**
	 * Constructor passed file parses and on the fly creates groups of workloads
	 * 
	 * @param suite
	 *            test suite which tests should belong to
	 * @param xmlFile
	 *            file where information about suite is loaded
	 * @throws InvalidMTTestConfFileException
	 *             in case when file is missing or parses incorrectly
	 * @throws NoSuchTestParamException
	 *             in case when in workload specified parameter which is not a part of it
	 * @throws InvalidTestParameterException
	 *             when parameter's value is of incorrect type or something else
	 * @throws WorkloadNotFoundException
	 *             when as a workload to run specified missing class
	 */

	protected static final String confStr = "conf";
	protected static final String includeStr = "include";
	protected static final String workloadStr = "workload";
	protected static final String nameStr = "name";
	protected static final String valueStr = "value";
	protected static final String fileStr = "file";
	protected OS os;
	
	
	
	public XMLParser(OS os) throws MTTestException {
		this.os = os;
	}
	
	public TestSet parseTestSet(String source) throws MTTestException {
		File file = new File(source);
		return parseTestSet(file, os, new TestParams(TestParams.defaultName), null);
	}
	
	public ConfigParams parseConfig(String source, int threadsCnt, int repeatsCnt) throws MTTestException {
		File file = new File(source);
		ConfigParams ret = parseConfig(file);
		ret.setAbsValue(ConfigParams.Field.threads, threadsCnt + "");
		ret.setAbsValue(ConfigParams.Field.numRuns, repeatsCnt + "");
		return ret; 
	}
	
	protected ConfigParams parseConfig(File source) throws MTTestException {
		Document doc = loadDocument(source);
		
		String srcName = source.getName();
		ConfigParams tmp = new ConfigParams(srcName);
		updateConfProperties(doc, tmp);
		return tmp;
	}
	
	protected TestSet parseTestSet(File source, OS os, TestParams params, TestSet owner) throws MTTestException  {
		String path = source.getPath();
		String dir = StringProcessor.getDirectory(path);
		String name = StringProcessor.cutExtention(StringProcessor.getFileName(path));
		Document doc = loadDocument(source);
		return parseTestSet(doc, name, owner, params, dir, os);
	}
	
	protected TestSet parseTestSet(Document source, String sourceName, TestSet owner, TestParams preDefinedParams, String dir, OS os) throws MTTestException {
		TestParams params = new TestParams(preDefinedParams);
		updateConfProperties(source, params);
		
		if(hasWokloads(source) && hasINcludes(source)) {
			throw new MTTestException("Bad .xml format: file contains 'wokload' and 'include' simultaneously");
		}
		
		TestSet testSet = new TestSet(sourceName, owner, null, params);
		loadWorkloads(source, testSet, params);
		expandIncludes(source, testSet, params, dir, os);
	
		return testSet;
	}

	protected void updateConfProperties(Document source, ConfigParams params) throws MTTestException {
		{
			NodeList nodes = source.getElementsByTagName(nameStr);
			for(int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if(node.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String value = ((Element) node).getAttribute(valueStr);
				params.setAbsValue(ConfigParams.Field.name, value);
			}
		}
		
		
		NodeList nodes = source.getElementsByTagName(confStr);
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			TestParam param = parseOptionElement((Element) node);
			params.setFileParam(param);
		}
	}
	
	protected void updateConfProperties(Document source, TestParams params) {
		NodeList nodes = source.getElementsByTagName(confStr);
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			TestParam param = parseOptionElement((Element) node);
			params.add(param);
		}
	}
	
	protected boolean hasWokloads(Document source) {
		NodeList nodes = source.getElementsByTagName(workloadStr);
		return nodes.getLength() != 0;
	}
	protected boolean hasINcludes(Document source) {
		NodeList nodes = source.getElementsByTagName(includeStr);
		return nodes.getLength() != 0;
	}
	
	protected void loadWorkloads(Document source, TestSet owner, TestParams params) throws MTTestException {
		if(!hasWokloads(source)) {
			return;
		}
		
		NodeList nodes = source.getElementsByTagName(workloadStr);
		for(int i = 0; i < nodes.getLength(); i++) {
			List<TestParams> paramsList = new ArrayList<TestParams>();
			paramsList.add(params);
			Node node = nodes.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element elem = (Element) node;
			String className = elem.getAttribute(nameStr);
			
			NodeList argsList = elem.getElementsByTagName("option");
			for(int j = 0; j < argsList.getLength(); j++) {
				Node argNode = argsList.item(j);
				if(argNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				TestParam args = parseOptionElement((Element) argNode);
				List<TestParam> argValues = args.splitValues(); 
				paramsList = multiply(paramsList, argValues);
				
			}
			for(TestParams paramsCase : paramsList) {
				TestCase testCase = new TestCase(className, owner, null, paramsCase);
				owner.addMttestTest(testCase);
			}
		}
	}
	


	protected void expandIncludes(Document source, TestSet owner, TestParams params, String dir, OS os) throws MTTestException {
		if(!hasINcludes(source)) {
			return;
		}
		NodeList nodes = source.getElementsByTagName(includeStr);
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element elem = (Element) node;
			String filePath = elem.getAttribute(fileStr);
			TestSet subSet = parseTestSet(new File(dir, filePath), os, params, owner);
			owner.addMttestTest(subSet);
		}
	}

	
	
	// ----------- utils
	
	protected Document loadDocument(File xmlFile)
			throws InvalidMTTestConfFileException {
		try {
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputStream input = MTTestResourceManager.openFileAsInputStream(xmlFile);
			Document doc = dBuilder.parse(input);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (IOException e) {
			throw new InvalidMTTestConfFileException(e, xmlFile.getAbsolutePath());
		} catch (SAXException e) {
			throw new InvalidMTTestConfFileException(e, xmlFile.getAbsolutePath());
		} catch (ParserConfigurationException e) {
			throw new MTTestRuntimeException("Internal error, wrong parser config. File: " + xmlFile.getAbsolutePath(), e);
		}
	}
	
	protected static TestParam parseOptionElement(Element optionElement) {
		String name = optionElement.getAttribute(nameStr);
		String value = optionElement.getAttribute(valueStr);
		boolean isFinal = false;
		String strIsFinal = optionElement.getAttribute(valueStr);
		if(strIsFinal != null) {
			isFinal = Boolean.parseBoolean(strIsFinal);
		}
		TestParam option = new TestParam(name, value, isFinal);
		return option;
	}
	
	protected List<TestParams> multiply(List<TestParams> paramsList, List<TestParam> argList) {
		List<TestParams> result = new ArrayList<TestParams>();
		for(TestParams params : paramsList)
			for(TestParam arg : argList) {
				TestParams tmp = new TestParams(params);
				tmp.add(arg);
				result.add(tmp);
			}
		return result;
	}
}
