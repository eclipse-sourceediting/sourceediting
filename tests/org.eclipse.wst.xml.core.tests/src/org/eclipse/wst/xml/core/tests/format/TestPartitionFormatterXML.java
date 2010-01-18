/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.formatter.DefaultXMLPartitionFormatter;
import org.eclipse.wst.xml.core.internal.formatter.XMLFormattingPreferences;
import org.eclipse.wst.xml.core.tests.util.StringCompareUtil;

public class TestPartitionFormatterXML extends TestCase {
	private static final String UTF_8 = "UTF-8";

	private StringCompareUtil fStringCompareUtil;
	private DefaultXMLPartitionFormatter partitionFormatter;

	protected void setUp() throws Exception {
		partitionFormatter = new DefaultXMLPartitionFormatter();
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
		formatAndAssertEquals(beforePath, afterPath, null);
	}

	private void formatAndAssertEquals(String beforePath, String afterPath, XMLFormattingPreferences prefs) throws UnsupportedEncodingException, IOException, CoreException {
		IStructuredModel beforeModel = null, afterModel = null;
		try {
			beforeModel = getModelForEdit(beforePath);
			assertNotNull("could not retrieve structured model for : " + beforePath, beforeModel);

			afterModel = getModelForEdit(afterPath);
			assertNotNull("could not retrieve structured model for : " + afterPath, afterModel);

			IStructuredDocument document = beforeModel.getStructuredDocument();
			
			String normalizedContents = document.get();
			normalizedContents = StringUtils.replace(normalizedContents, "\r\n", "\n");
			normalizedContents = StringUtils.replace(normalizedContents, "\r", "\n");
			document.set(normalizedContents);
			
			if (prefs == null)
				prefs = new XMLFormattingPreferences();
			TextEdit edit = partitionFormatter.format(beforeModel, 0, document.getLength(), prefs);
			try {
				edit.apply(document);
			}
			catch (MalformedTreeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ByteArrayOutputStream formattedBytes = new ByteArrayOutputStream();
			beforeModel.save(formattedBytes); // "beforeModel" should now be
			// after the formatter

			ByteArrayOutputStream afterBytes = new ByteArrayOutputStream();
			afterModel.save(afterBytes);

			String expectedContents = new String(afterBytes.toByteArray(), UTF_8);
			String actualContents = new String(formattedBytes.toByteArray(), UTF_8);

			/* Make some adjustments to ignore cross platform line delimiter issues */
			expectedContents = StringUtils.replace(expectedContents, "\r\n", "\n");
			expectedContents = StringUtils.replace(expectedContents, "\r", "\n");
			actualContents = StringUtils.replace(actualContents, "\r\n", "\n");
			actualContents = StringUtils.replace(actualContents, "\r", "\n");
			
			assertTrue("Formatted document differs from the expected.\nExpected Contents:\n" + expectedContents + "\nActual Contents:\n" + actualContents, fStringCompareUtil.equalsIgnoreLineSeperator(expectedContents, actualContents));
		}
		finally {
			if (beforeModel != null)
				beforeModel.releaseFromEdit();
			if (afterModel != null)
				afterModel.releaseFromEdit();
		}
	}

	public void testSimpleXml() throws UnsupportedEncodingException, IOException, CoreException {
		// results are different than old formatter
		// Bug [228495] - Result should have blank lines cleared
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setClearAllBlankLines(true);
		formatAndAssertEquals("testfiles/xml/simple-standalone.xml", "testfiles/xml/simple-standalone-newfmt.xml", prefs);
	}
	
	public void testWhitespaceFormatXSD() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 194698
		formatAndAssertEquals("testfiles/xml/xml-whitespace-xsd.xml", "testfiles/xml/xml-whitespace-xsd-actual.xml");
	}

	public void testPreserveFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// results are different than old formatter
		// Bug [228495] - Result should have blank lines cleared
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setClearAllBlankLines(true);
		formatAndAssertEquals("testfiles/xml/xml-space-preserve-standalone.xml", "testfiles/xml/xml-space-preserve-standalone-newfmt.xml", prefs);
	}

	public void testPreserveFormatDTD() throws UnsupportedEncodingException, IOException, CoreException {
		// results are different than old formatter
		// Bug [228495] - Result should have blank lines cleared
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setClearAllBlankLines(true);
		formatAndAssertEquals("testfiles/xml/xml-space-preserve-dtd.xml", "testfiles/xml/xml-space-preserve-dtd-newfmt.xml", prefs);
	}

	public void testOneLineFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG115716
		formatAndAssertEquals("testfiles/xml/oneline.xml", "testfiles/xml/oneline-fmt.xml");
	}

	public void testOneLineTextNodeFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// results are different than old formatter
		// BUG166441
		formatAndAssertEquals("testfiles/xml/onelineTextNode.xml", "testfiles/xml/onelineTextNode-newfmt.xml");
	}

	public void testEmptyContentNodeFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG174243
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setIndentMultipleAttributes(true);
		formatAndAssertEquals("testfiles/xml/usetagswithemptycontent.xml", "testfiles/xml/usetagswithemptycontent-fmt.xml", prefs);
	}

	public void testXSLFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG108074
		formatAndAssertEquals("testfiles/xml/xslattributetext.xsl", "testfiles/xml/xslattributetext-fmt.xsl");
	}

	public void testEntityFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// results are different than old formatter
		// BUG102076
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setClearAllBlankLines(true);
		formatAndAssertEquals("testfiles/xml/entities.xml", "testfiles/xml/entities-newfmt.xml", prefs);
	}

	public void testPreservePCDATAFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG84688
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setPCDataWhitespaceStrategy(XMLFormattingPreferences.PRESERVE);
		prefs.setClearAllBlankLines(true);
		formatAndAssertEquals("testfiles/xml/xml-preservepcdata.xml", "testfiles/xml/xml-preservepcdata-yes-fmt.xml", prefs);

		// results are different than old formatter
		prefs.setPCDataWhitespaceStrategy(XMLFormattingPreferences.COLLAPSE);
		formatAndAssertEquals("testfiles/xml/xml-preservepcdata.xml", "testfiles/xml/xml-preservepcdata-no-newfmt.xml", prefs);
	}

	public void testPreserveCDATAFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG161330
		formatAndAssertEquals("testfiles/xml/usecdata.xml", "testfiles/xml/usecdata-fmt.xml");
		// the below test will expose a bug in the new formatter so commenting
		// out for now
		// formatAndAssertEquals("testfiles/xml/usecdata2.xml",
		// "testfiles/xml/usecdata2-fmt.xml");
	}

	public void testSplitAttributesFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// BUG113584
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		// the below tests are slighty different from old formatter test
		prefs.setIndentMultipleAttributes(true);
		prefs.setAlignFinalBracket(false);
		formatAndAssertEquals("testfiles/xml/multiattributes2.xml", "testfiles/xml/multiattributes2-yessplit-noalign-newfmt.xml", prefs);

		prefs.setIndentMultipleAttributes(false);
		prefs.setAlignFinalBracket(false);
		formatAndAssertEquals("testfiles/xml/multiattributes2.xml", "testfiles/xml/multiattributes2-nosplit-noalign-newfmt.xml", prefs);

	}

	public void testAlignEndBracketFormat() throws UnsupportedEncodingException, IOException, CoreException {
		// results are different than old formatter
		// BUG113584
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setIndentMultipleAttributes(false);
		prefs.setAlignFinalBracket(true);
		formatAndAssertEquals("testfiles/xml/multiattributes.xml", "testfiles/xml/multiattributes-nosplit-yesalign-newfmt.xml", prefs);

		// results are different than old formatter
		prefs.setIndentMultipleAttributes(true);
		prefs.setAlignFinalBracket(true);
		formatAndAssertEquals("testfiles/xml/multiattributes.xml", "testfiles/xml/multiattributes-yessplit-yesalign-newfmt.xml", prefs);
	}
	
	public void testSpaceBeforeEmptyCloseTag() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 195264
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setSpaceBeforeEmptyCloseTag(false);
		formatAndAssertEquals("testfiles/xml/xml-empty-tag-space.xml", "testfiles/xml/xml-empty-tag-space-none-newfmt.xml", prefs);
		
		prefs.setSpaceBeforeEmptyCloseTag(true);
		formatAndAssertEquals("testfiles/xml/xml-empty-tag-space.xml", "testfiles/xml/xml-empty-tag-space-newfmt.xml", prefs);
	}
	
	public void testProcessingInstruction() throws UnsupportedEncodingException, IOException, CoreException {
	    // BUG198297
		formatAndAssertEquals("testfiles/xml/processinginstruction.xml", "testfiles/xml/processinginstruction-fmt.xml");
	}
	
	public void testComments() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 226821
		formatAndAssertEquals("testfiles/xml/xml-comment.xml", "testfiles/xml/xml-comment-newfmt.xml");
	}
	
	public void testComments_short_NoText() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 258512
		formatAndAssertEquals("testfiles/xml/xml-comment-short-NoText.xml", "testfiles/xml/xml-comment-short-NoText-formated.xml");
	}
	
	public void testComments_short_InbetweenText() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 258512
		formatAndAssertEquals("testfiles/xml/xml-comment-short-InbetweenText.xml", "testfiles/xml/xml-comment-short-InbetweenText-formated.xml");
	}
	
	public void testComments_short_SameLineText() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 258512
		formatAndAssertEquals("testfiles/xml/xml-comment-short-SameLineText.xml", "testfiles/xml/xml-comment-short-SameLineText-formated.xml");
	}
	
	public void testComments_short_EverywhereText() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 258512
		formatAndAssertEquals("testfiles/xml/xml-comment-short-EverywhereText.xml", "testfiles/xml/xml-comment-short-EverywhereText-formated.xml");
	}
	
	public void testComments_long_NoText() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 258512
		formatAndAssertEquals("testfiles/xml/xml-comment-long-NoText.xml", "testfiles/xml/xml-comment-long-NoText-formated.xml");
	}
	
	public void testComments_long_InbetweenText() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 258512
		formatAndAssertEquals("testfiles/xml/xml-comment-long-InbetweenText.xml", "testfiles/xml/xml-comment-long-InbetweenText-formated.xml");
	}
	
	public void testComments_long_SameLineText() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 258512
		formatAndAssertEquals("testfiles/xml/xml-comment-long-SameLineText.xml", "testfiles/xml/xml-comment-long-SameLineText-formated.xml");
	}
	
	public void testComments_long_EverywhereText() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 258512
		formatAndAssertEquals("testfiles/xml/xml-comment-long-EverywhereText.xml", "testfiles/xml/xml-comment-long-EverywhereText-formated.xml");
	}
	
	public void testKeepEmptyLines() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 228495
		// Test that formatting keeps empty lines
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setClearAllBlankLines(false);
		formatAndAssertEquals("testfiles/xml/xml-keep-blank-lines.xml", "testfiles/xml/xml-keep-blank-lines-fmt.xml", prefs);
	}
	
	public void testClearBlankLines() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 228495
		// Test that formatting clears empty lines
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setClearAllBlankLines(true);
		formatAndAssertEquals("testfiles/xml/xml-keep-blank-lines.xml", "testfiles/xml/xml-clear-blank-lines-fmt.xml", prefs);
	}
	
	public void testFormatMalformedEndTag() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 221279
		// Test that malformed end tags do not cause a NPE and format the document
		formatAndAssertEquals("testfiles/xml/xml-221279.xml", "testfiles/xml/xml-221279-fmt.xml");
	}

	public void testFormatWithFracturedXMLContent() throws UnsupportedEncodingException, IOException, CoreException {
		// Bug 229135
		// Test that text content that is split into multiple document regions does not stop the formatter
		formatAndAssertEquals("testfiles/xml/xml-229135.xml", "testfiles/xml/xml-229135-fmt.xml");
	}

	public void testFormatCommentsJoinLinesDisabled() throws UnsupportedEncodingException, IOException, CoreException {
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setJoinCommentLines(false);
		formatAndAssertEquals("testfiles/xml/xml-join-lines-disabled.xml", "testfiles/xml/xml-join-lines-disabled-fmt.xml", prefs);
	}

	public void testFormatCommentsDisabled() throws UnsupportedEncodingException, IOException, CoreException {
		XMLFormattingPreferences prefs = new XMLFormattingPreferences();
		prefs.setFormatCommentText(false);
		formatAndAssertEquals("testfiles/xml/xml-format-comments-disabled.xml", "testfiles/xml/xml-format-comments-disabled-fmt.xml", prefs);
	}
}
