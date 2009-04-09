/*******************************************************************************
 * Copyright (c) 2009 Standard for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - bug 271784 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.tests.XSLModelXMLTestsPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class TestPluginXMLRequirements extends TestCase{

	private static final String PLUGIN_XML = "plugin.xml";
	
	Document pluginDoc = null;
	XPath xpath = null;

	public TestPluginXMLRequirements() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		loadPluginXML();
		initXPath();
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		pluginDoc = null;
		xpath = null;
	}

	private void initXPath() {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();
	}

	private void loadPluginXML() throws Exception {
		File srcFile = getTestFile("/" + PLUGIN_XML);
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
		pluginDoc = domBuilder.parse(srcFile);
	}
	
	protected URL getInstallLocation() {
		URL installLocation = XSLUIPlugin.getDefault().getBundle().getEntry("/");
		URL resolvedLocation = null;
		try {
			resolvedLocation = FileLocator.resolve(installLocation);
		}
		catch (IOException e) {
			// impossible
			throw new Error(e);
		}
		return resolvedLocation;
	}

	protected File getTestFile(String filepath) {
		URL installURL = getInstallLocation();
		//String scheme = installURL.getProtocol();
		String path = installURL.getPath();
		String location = path + filepath;
		File result = new File(location);
		return result;
	}
	
	
	public void testDTDToolTipExists() throws Exception {
		String xpathString = "//menuContribution[@locationURI = 'toolbar:org.eclipse.ui.main.toolbar?after=additions']/toolbar/command[@id = 'org.eclipse.wst.xsl.ui.newDTDFile']";
		XPathExpression xpathExpr = xpath.compile(xpathString);
		Element element = (Element) xpathExpr.evaluate(pluginDoc.getDocumentElement(), XPathConstants.NODE);
		String toolTip = element.getAttribute("tooltip");
		assertEquals("Unexpected value for DTD tooltip", "%commandTooltipNewDTDFile", toolTip);
	}

}
