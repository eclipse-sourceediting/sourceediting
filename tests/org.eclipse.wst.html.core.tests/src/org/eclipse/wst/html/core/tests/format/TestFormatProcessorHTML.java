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
package org.eclipse.wst.html.core.tests.format;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.tests.utils.StringCompareUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.format.AbstractStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.xml.core.internal.provisional.format.StructuredFormatPreferencesXML;

public class TestFormatProcessorHTML extends TestCase {

	private static final boolean SPLIT_MULTI_ATTRS = false;

	private static final String INDENT = "\t";

	private static final boolean CLEAR_ALL_BLANK_LINES = false;

	private static final int MAX_LINE_WIDTH = 72;

	private static final String UTF_8 = "UTF-8";

	private StringCompareUtil fStringCompareUtil;

	public TestFormatProcessorHTML(String name) {
		super(name);
	}

	private HTMLFormatProcessorImpl formatProcessor;

	protected void setUp() throws Exception {
		formatProcessor = new HTMLFormatProcessorImpl();
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
		formatAndAssertEquals(beforePath, afterPath, true, 1);
	}

	private void formatAndAssertEquals(String beforePath, String afterPath, boolean resetPreferences) throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals(beforePath, afterPath, resetPreferences, 1);
	}
	private void formatAndAssertEquals(String beforePath, String afterPath, boolean resetPreferences, int formatCycles) throws UnsupportedEncodingException, IOException, CoreException {
		IStructuredModel beforeModel = null, afterModel = null;
		try {
			beforeModel = getModelForEdit(beforePath);
			assertNotNull("could not retrieve structured model for : " + beforePath, beforeModel);

			afterModel = getModelForEdit(afterPath);
			assertNotNull("could not retrieve structured model for : " + afterPath, afterModel);

			if (resetPreferences)
				resetPreferencesToDefault();

			((AbstractStructuredFormatProcessor) formatProcessor).refreshFormatPreferences = false;
			for(int i=0; i < formatCycles; i++)
			{
				formatProcessor.formatModel(beforeModel);
			}
			((AbstractStructuredFormatProcessor) formatProcessor).refreshFormatPreferences = true;

			ByteArrayOutputStream formattedBytes = new ByteArrayOutputStream();
			beforeModel.save(formattedBytes); // "beforeModel" should now be
			// after the formatter

			ByteArrayOutputStream afterBytes = new ByteArrayOutputStream();
			afterModel.save(afterBytes);

			String expectedContents = new String(afterBytes.toByteArray(), UTF_8);
			String actualContents = new String(formattedBytes.toByteArray(), UTF_8);
			assertTrue("Formatted document differs from the expected.\nExpected Contents:\n" + expectedContents + "\nActual Contents:\n" + actualContents, fStringCompareUtil.equalsIgnoreLineSeperator(expectedContents, actualContents));
		}
		finally {
			if (beforeModel != null)
				beforeModel.releaseFromEdit();
			if (afterModel != null)
				afterModel.releaseFromEdit();
		}
	}

	private void resetPreferencesToDefault() {
		IStructuredFormatPreferences formatPreferences = formatProcessor.getFormatPreferences();
		formatPreferences.setLineWidth(MAX_LINE_WIDTH);
		formatPreferences.setClearAllBlankLines(CLEAR_ALL_BLANK_LINES);
		formatPreferences.setIndent(INDENT);
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(SPLIT_MULTI_ATTRS);
	}

	public void testTableFormat() throws UnsupportedEncodingException, IOException, CoreException {
		formatAndAssertEquals("testfiles/html/tableformat.html", "testfiles/html/tableformat-fmt.html");
	}

	public void testBasicFormatting() throws UnsupportedEncodingException, IOException, CoreException {
		// Test that the basic elements of HTML are properly aligned. Don't indent children of <html> or <head>
		formatAndAssertEquals("testfiles/html/format-basic.html", "testfiles/html/format-basic-fmt.html");
	}

	public void testNestedDivs() throws UnsupportedEncodingException, IOException, CoreException {
		// Test that the basic elements of HTML are properly aligned. Don't indent children of <html> or <head>
		formatAndAssertEquals("testfiles/html/format-divs.html", "testfiles/html/format-divs-fmt.html");
	}

	public void testEmbeddedCSSFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG92957
		formatAndAssertEquals("testfiles/html/htmlwithcss.html", "testfiles/html/htmlwithcss-fmt.html");
	}
	
	public void testAttributeFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG113584
		IStructuredFormatPreferences formatPreferences = formatProcessor.getFormatPreferences();
		((StructuredFormatPreferencesXML) formatPreferences).setSplitMultiAttrs(true);
		((StructuredFormatPreferencesXML) formatPreferences).setAlignEndBracket(true);
		formatAndAssertEquals("testfiles/html/attributesformat.html", "testfiles/html/attributesformat-fmt.html", false);
	}
	
	public void testScriptFormat() throws UnsupportedEncodingException, IOException, CoreException {
		//BUG128813
		formatAndAssertEquals("testfiles/html/scriptformat.html", "testfiles/html/scriptformat-fmt.html", true, 3);
	}
}
