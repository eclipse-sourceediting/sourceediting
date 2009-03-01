/*******************************************************************************
 * Copyright (c) 2008 Jesper Steen Møller
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Møller - initial XSL launching test
 *     David Carver (STAR) - bug 262046 - refactored for better reliability.
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.tests.AbstractLaunchingTest;
import org.eclipse.wst.xsl.launching.tests.TestEnvironment;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XSLLaunchingTests extends AbstractLaunchingTest {


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		IPath path = folder.getFullPath();
		addLaunchConfiguration(path, "SimpleTransform.launch");
		addLaunchConfiguration(path, "TransformComments.launch");
		testProject.refreshLocal(2, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {
		env.dispose();
		super.tearDown();
	}
	
	
	private ILaunch launch(String name)  throws Exception {
		ILaunchConfiguration configuration =  getLaunchConfiguration(name);
		ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		return launch;
	}
	
	/**
	 * Returns the launch configuration for the given main type
	 * 
	 * @param mainTypeName program to launch
	 * @see ProjectCreationDecorator
	 */
	protected ILaunchConfiguration getLaunchConfiguration(String mainTypeName)  throws Exception {
		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		IFile file = testProject.getProject().getFolder("launchConfigs").getFile(mainTypeName + ".launch");
		ILaunchConfiguration mine =	mgr.getLaunchConfiguration(file);
		assertEquals("Wrong type found: ", XSLLaunchConfigurationConstants.ID_LAUNCH_CONFIG_TYPE, mine.getType().getIdentifier());

		return mine;
	}	

	public synchronized void testSimpleTransformation() throws Exception{
		IPath folder = testProject.getFullPath();
		env.addFileFromResource(folder, "1-input.xml", "1-input.xml");
		env.addFileFromResource(folder, "1-transform.xsl", "1-transform.xsl");
		refreshProject();

		launchConfiguration("SimpleTransform");
		IFile output = testProject.getFile("1-input.out.xml");
		Document doc = parseXml(output.getContents(true));
		assertEquals("root-out", doc.getDocumentElement().getNodeName());
	}

	private void refreshProject() throws CoreException, InterruptedException {
		testProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		while (testProject.isSynchronized(IResource.DEPTH_INFINITE) == false) {
			Thread.sleep(1000);
		}
	}

	private void launchConfiguration(String launchConfigName) throws Exception, CoreException {
		ILaunch launch = launch(launchConfigName);
		// Wait until the launch configuration terminates.
		while (launch.isTerminated() == false) {
			Thread.sleep(1000);
		}
		refreshProject();
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
	public synchronized void testTransformComments() throws Exception{
		IPath folder = testProject.getFullPath();
		env.addFileFromResource(folder, "testCommentInput.xml", "testCommentInput.xml");
		env.addFileFromResource(folder, "testComments.xsl", "testComments.xsl");
		env.addFileFromResource(folder, "expected.xml", "testCommentsExpected.xml");
		refreshProject();

		launchConfiguration("TransformComments");
		IFile output = testProject.getFile("testCommentInput.out.xml");
		IFile expected = testProject.getFile("expected.xml");
		
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

}
