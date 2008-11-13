/*******************************************************************************
 * Copyright (c) 2008 Jesper Steen Møller
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Møller - initial XSL launching test
 *******************************************************************************/

package org.eclipse.wst.xsl.launching.tests.testcase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.xsl.launching.tests.TestEnvironment;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XSLLaunchingTests extends TestCase {

	private TestEnvironment _env;
	private IProject _testProject;
	private String launchXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
		+ "<launchConfiguration type=\"org.eclipse.wst.xsl.launching.launchConfigurationType\">"
		+ "<stringAttribute key=\"org.eclipse.wst.xsl.jaxp.launching.ATTR_ATTRIBUTES\" value=\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;Attributes/&gt;&#13;&#10;\"/>"
		+ "<stringAttribute key=\"org.eclipse.wst.xsl.jaxp.launching.ATTR_OUTPUT_PROPERTIES\" value=\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;Properties/&gt;&#13;&#10;\"/>"
		+ "<booleanAttribute key=\"org.eclipse.wst.xsl.jaxp.launching.ATTR_USE_DEFAULT_PROCESSOR\" value=\"true\"/>"
		+ "<stringAttribute key=\"org.eclipse.wst.xsl.jaxp.launching.INVOKER_DESCRIPTOR\" value=\"org.eclipse.wst.xsl.launching.jaxp.invoke\"/>"
		+ "<booleanAttribute key=\"org.eclipse.wst.xsl.launching.ATTR_FORMAT_FILE\" value=\"false\"/>"
		+ "<stringAttribute key=\"org.eclipse.wst.xsl.launching.ATTR_INPUT_FILE\" value=\"${workspace_loc:/XSLTestProject/input.xml}\"/>"
		+ "<booleanAttribute key=\"org.eclipse.wst.xsl.launching.ATTR_OPEN_FILE\" value=\"true\"/>"
		+ "<stringAttribute key=\"org.eclipse.wst.xsl.launching.ATTR_OUTPUT_FILENAME\" value=\"output.xml\"/>"
		+ "<stringAttribute key=\"org.eclipse.wst.xsl.launching.ATTR_PIPELINE\" value=\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;no&quot;?&gt;&#13;&#10;&lt;Pipeline&gt;&#13;&#10;&lt;OutputProperties/&gt;&#13;&#10;&lt;Transform path=&quot;/XSLTestProject/transform.xsl&quot; pathType=&quot;resource&quot;&gt;&#13;&#10;&lt;Parameters/&gt;&#13;&#10;&lt;/Transform&gt;&#13;&#10;&lt;/Pipeline&gt;&#13;&#10;\"/>"
		+ "<booleanAttribute key=\"org.eclipse.wst.xsl.launching.ATTR_USE_DEFAULT_OUTPUT_FILE\" value=\"false\"/>"
		+ "<stringAttribute key=\"org.eclipse.wst.xsl.launching.ATTR_OUTPUT_FOLDER\" value=\"${workspace_loc:/XSLTestProject}\"/>"
		+ "</launchConfiguration>";

	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		_testProject = _env.createProject("XSLTestProject");
	}

	private void launchConfiguration(IPath folder, String launchXml)
			throws CoreException, UnsupportedEncodingException,
			InterruptedException {
		String name = "launch" + (int) (Math.random() * 1000);
		_env.addFile(folder, name + ".launch", launchXml);

		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] allConfigs = mgr.getLaunchConfigurations();
		ILaunchConfiguration mine = null;
		boolean found = false;
		for (ILaunchConfiguration lc : allConfigs) {
			if (lc.getName().equals(name)) {
				found = true;
				mine = lc;
				assertEquals(
						"bad launch config type?",
						"org.eclipse.wst.xsl.launching.launchConfigurationType",
						lc.getType().getIdentifier());
				assertEquals("bad launch config plugin?",
						"org.eclipse.wst.xsl.launching", lc.getType()
								.getPluginIdentifier());
			}
		}
		assertTrue("expected to find the launch config we just added", found);

		ILaunch launch = mine.launch(ILaunchManager.RUN_MODE,
				new NullProgressMonitor());
		for (int i = 0; i < 200 && !launch.isTerminated(); ++i) {
			System.out.println("waiting");
			Thread.sleep(100);
		}
		assertTrue("Launch did not complete within a 20 second time period",
				launch.isTerminated());

		_testProject.refreshLocal(2, null);
	}

	public void testBuildConfig() throws CoreException, InterruptedException,
			ParserConfigurationException, SAXException, IOException {
		IPath folder = _testProject.getFullPath();
		_env.addFileFromResource(folder, "input.xml", "1-input.xml");
		_env.addFileFromResource(folder, "transform.xsl", "1-transform.xsl");

		launchConfiguration(folder, launchXml);
		IFile output = _testProject.getFile("output.xml");
		Document doc = parseXml(output.getContents(true));

		assertEquals("root-out", doc.getDocumentElement().getNodeName());
	}

	/**
	 * Test to make sure comments are being copied out to the output file.
	 * bug 253703
	 * @throws CoreException
	 * @throws InterruptedException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void testTransformComments() throws CoreException, InterruptedException,
			ParserConfigurationException, SAXException, IOException {
		IPath folder = _testProject.getFullPath();
		_env.addFileFromResource(folder, "input.xml", "testCommentInput.xml");
		_env.addFileFromResource(folder, "transform.xsl", "testComments.xsl");
		_env.addFileFromResource(folder, "expected.xml", "testCommentsExpected.xml");

		launchConfiguration(folder, launchXml);
		IFile output = _testProject.getFile("output.xml");
		IFile expected = _testProject.getFile("expected.xml");
		
		String result = readFile(output.getContents());
		String wanted = readFile(expected.getContents());
		
		assertEquals("Unexpected results:", wanted, result);
	}

	private String readFile(InputStream input) {
		String str;
		String finalString = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			while ((str = in.readLine()) != null) {
				finalString = finalString + str + "\n";
			}
			in.close();
		} catch (IOException e) {
		}
		return finalString;
	}

	static private Document parseXml(InputStream contents)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		return builder.parse(contents);
	}

	protected void tearDown() throws Exception {
		_env.dispose();
		super.tearDown();
	}
}
