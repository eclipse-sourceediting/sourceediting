/*******************************************************************************
 * Copyright (c) 2009, 2010 Standard for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - bug 271784 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.tests.plugin;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestPluginXMLRequirements extends TestCase{

	private static final String PLUGIN_XML = "plugin.xml";
	
	Document pluginDoc = null;
	XPath xpath = null;

	public TestPluginXMLRequirements() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		loadPluginXML();
		initXPath();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		pluginDoc = null;
		xpath = null;
	}

	private void initXPath() {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();
	}

	private void loadPluginXML() throws Exception {
		URL url = getTestFile(PLUGIN_XML);
		url.openStream();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
		pluginDoc = domBuilder.parse(url.openStream());
	}
	
	protected URL getInstallLocation(String path) {
		URL installLocation = DTDUIPlugin.getDefault().getBundle().getEntry(path);
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

	protected URL getTestFile(String filepath) {
		URL installURL = getInstallLocation("/" + filepath);
		return installURL;
	}
	
	
	public void testDTDToolTipExists() throws Exception {
		String xpathString = "//menuContribution[@locationURI = 'toolbar:org.eclipse.wst.xml.ui.perspective.NewFileToolBar']/command[@id = 'org.eclipse.wst.dtd.ui.newDTDFile']";
		XPathExpression xpathExpr = xpath.compile(xpathString);
		Element element = (Element) xpathExpr.evaluate(pluginDoc.getDocumentElement(), XPathConstants.NODE);
		String toolTip = element.getAttribute("tooltip");
		assertEquals("Unexpected value for DTD tooltip", "%DTD_New_File.tooltip", toolTip);
	}

}
