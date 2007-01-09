/*******************************************************************************
 * Copyright (c) 2006, 2007 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jesper Steen Møller - jesper@selskabet.org
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.format;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xml.core.internal.provisional.format.IStructuredFormatPreferencesXML;
import org.eclipse.wst.xml.core.tests.util.StringCompareUtil;

public class TestFormatProcessorXML extends TestCase {

	private static final boolean SPLIT_MULTI_ATTRS = false;

	private static final String INDENT = " ";

	private static final boolean CLEAR_ALL_BLANK_LINES = false;

	private static final int MAX_LINE_WIDTH = 72;

	private static final String UTF_8 = "UTF-8";
	
	private StringCompareUtil fStringCompareUtil;

	public TestFormatProcessorXML(String name) {
		super(name);
	}

	private FormatProcessorXML formatProcessor;

	protected void setUp() throws Exception {
		formatProcessor = new FormatProcessorXML();
		fStringCompareUtil = new StringCompareUtil();
	}

	/**
	 * must release model (from edit) after
	 * 
	 * @param filename
	 *            relative to this class (TestStructuredPartitioner)
	 */
	private IStructuredModel getModelForEdit(final String filename) {

		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			InputStream inStream = getClass().getResourceAsStream(filename);
			if (inStream == null)
				throw new FileNotFoundException("Can't file resource stream " + filename);
			final String baseFile = getClass().getResource(filename).toString();
			model = modelManager.getModelForEdit(baseFile, inStream, new URIResolver() {

				String fBase = baseFile;

				public String getFileBaseLocation() {
					return fBase;
				}

				public String getLocationByURI(String uri) {
					return getLocationByURI(uri, fBase);
				}

				public String getLocationByURI(String uri, boolean resolveCrossProjectLinks) {
					return getLocationByURI(uri);
				}

				public String getLocationByURI(String uri, String baseReference) {
					int lastSlash = baseReference.lastIndexOf("/");
					if (lastSlash > 0)
						return baseReference.substring(0, lastSlash + 1) + uri;
					return baseReference;
				}

				public String getLocationByURI(String uri, String baseReference, boolean resolveCrossProjectLinks) {
					return getLocationByURI(uri, baseReference);
				}

				public IProject getProject() {
					return null;
				}

				public IContainer getRootLocation() {
					return null;
				}

				public InputStream getURIStream(String uri) {
					return getClass().getResourceAsStream(getLocationByURI(uri));
				}

				public void setFileBaseLocation(String newLocation) {
					this.fBase = newLocation;
				}

				public void setProject(IProject newProject) {
				}
			});
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return model;
	}

	protected void formatAndAssertEquals(String beforePath, String afterPath) throws UnsupportedEncodingException, IOException, CoreException {
		IStructuredModel beforeModel = null, afterModel = null;
		try {
			beforeModel = getModelForEdit(beforePath);
			assertNotNull("could not retrieve structured model for : " + beforePath, beforeModel);

			afterModel = getModelForEdit(afterPath);
			assertNotNull("could not retrieve structured model for : " + afterPath, afterModel);

			IStructuredFormatPreferences formatPreferences = formatProcessor.getFormatPreferences();
			formatPreferences.setLineWidth(MAX_LINE_WIDTH);
			formatPreferences.setClearAllBlankLines(CLEAR_ALL_BLANK_LINES);
			formatPreferences.setIndent(INDENT);
			((IStructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(SPLIT_MULTI_ATTRS);

			formatProcessor.formatModel(beforeModel);

			ByteArrayOutputStream formattedBytes = new ByteArrayOutputStream();
			beforeModel.save(formattedBytes); // "beforeModel" should now be
			// after the formatter

			ByteArrayOutputStream afterBytes = new ByteArrayOutputStream();
			afterModel.save(afterBytes);

			String formattedContents = new String(afterBytes.toByteArray(), UTF_8);
			String expectedContents = new String(formattedBytes.toByteArray(), UTF_8);
			assertTrue("Formatted document differs from the expected", fStringCompareUtil.equalsIgnoreLineSeperator(formattedContents, expectedContents));
		}
		finally {
			if (beforeModel != null)
				beforeModel.releaseFromEdit();
			if (afterModel != null)
				afterModel.releaseFromEdit();
		}
	}

	public void testSimpleXml() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/xml/simple-standalone.xml", "testfiles/xml/simple-standalone-fmt.xml");
	}

	public void testPreserveFormat() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/xml/xml-space-preserve-standalone.xml", "testfiles/xml/xml-space-preserve-standalone-fmt.xml");
	}

	public void testPreserveFormatDTD() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/xml/xml-space-preserve-dtd.xml", "testfiles/xml/xml-space-preserve-dtd-fmt.xml");
	}

	public void testOneLineFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG115716
		formatAndAssertEquals("testfiles/xml/oneline.xml", "testfiles/xml/oneline-fmt.xml");
	}
	
	public void testOneLineTextNodeFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG166441
		formatAndAssertEquals("testfiles/xml/onelineTextNode.xml", "testfiles/xml/onelineTextNode-fmt.xml");
	}

}
