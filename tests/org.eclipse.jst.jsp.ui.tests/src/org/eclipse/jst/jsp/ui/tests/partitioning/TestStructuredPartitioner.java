/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.partitioning;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.text.DecimalFormat;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jst.jsp.core.internal.text.rules.StructuredTextPartitionerForJSP;
import org.eclipse.wst.css.core.internal.text.rules.StructuredTextPartitionerForCSS;
import org.eclipse.wst.html.core.internal.text.rules.StructuredTextPartitionerForHTML;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;

public class TestStructuredPartitioner extends TestCase {

	private boolean DEBUG_PRINT_RESULT = false;
	protected ITypedRegion[] partitions = null;

	private boolean useFormatter = true;
	protected DecimalFormat formatter;

	public TestStructuredPartitioner(String name) {
		super(name);
		if (DEBUG_PRINT_RESULT && useFormatter)
			formatter = new DecimalFormat();
	}

	public void testHTML1() throws IOException, BadLocationException {
		int expectedPartitions = 4;
		int nPartitions = doComputePartitioningTest("testfiles/html/example01.html");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForHTML.ST_HTML_DECLARATION, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForHTML.ST_HTML_COMMENT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML});
	}

	public void testHTML2() throws IOException, BadLocationException {
		int expectedPartitions = 7;
		//TODO Nitin, we need to review the Unknown to Script change (8/1/03, 9:00PM)
		int nPartitions = doComputePartitioningTest("testfiles/html/example02.html");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForHTML.ST_SCRIPT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForHTML.ST_SCRIPT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForHTML.ST_SCRIPT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML});
	}

	public void testHTML3() throws IOException, BadLocationException {
		int expectedPartitions = 1;
		String file = "testfiles/html/example03.html";
		int nPartitions = doComputePartitioningTest(file);
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForHTML.ST_DEFAULT_HTML});

		// 121 if CRLF
		//  ITypedRegion scriptPartition = getPartitionTest(file, 121);
		// 116 if LF or CR only
		//  ITypedRegion scriptPartition = getPartitionTest(file, 121);

		ITypedRegion part1 = getPartitionTest(file, 121);
		ITypedRegion part2 = getPartitionTest(file, 116);

//		assertEquals("partition is wrong type!", scriptPartition.getType(), StructuredTextPartitionerForHTML.ST_SCRIPT);
		// the critical position is surrounded with HTML > 5 characters in either direction; this should be good enough
		assertTrue("partition is wrong type! :(" + part1.getType() + "|" + part2.getType() + ")", part1.getType().equals(StructuredTextPartitionerForHTML.ST_SCRIPT) || part2.getType().equals(StructuredTextPartitionerForHTML.ST_SCRIPT));
		assertTrue("partition is not zero length!", (part1.getLength() == 0 || part2.getLength() == 0));
	}

	public void testJSP1() throws IOException, BadLocationException { // DW 05/01/2003 I modified this test, after creating test6, 
		// and changing partioner to pick up end *> as part of page directive
		// partition (for highlighting to work right).
		int expectedPartitions = 8;
		int nPartitions = doComputePartitioningTest("testfiles/jsp/example01.jsp");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForJSP.ST_JSP_DIRECTIVE, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_DIRECTIVE, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML});
	}

	public void testJSP2() throws IOException, BadLocationException {
		int expectedPartitions = 11;
		int nPartitions = doComputePartitioningTest("testfiles/jsp/example02.jsp");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForHTML.ST_HTML_COMMENT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML,});
	}

	public void testJSP3() throws IOException, BadLocationException {
		int expectedPartitions = 5;
		int nPartitions = doComputePartitioningTest("testfiles/jsp/example03.jsp");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForHTML.ST_SCRIPT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForCSS.ST_STYLE, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML,});
	}

	public void testJSP4() throws IOException, BadLocationException {
		int expectedPartitions = 8;
		//TODO Nitin, we need to review the Unknown to Script change (8/1/03, 9:00PM)
		int nPartitions = doComputePartitioningTest("testfiles/jsp/example04.jsp");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, "SCRIPT.language:MYLANGUAGE", StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_COMMENT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML});
	}

	public void testJSP5() throws IOException, BadLocationException {
		int expectedPartitions = 17;
		//TODO Nitin, we need to review the Unknown to Script change (8/1/03, 9:00PM)
		int nPartitions = doComputePartitioningTest("testfiles/jsp/example05.jsp");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForHTML.ST_SCRIPT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_COMMENT, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML});
	}

	public void testJSP6() throws IOException, BadLocationException {
		int expectedPartitions = 1;
		int nPartitions = doComputePartitioningTest("testfiles/jsp/example06.jsp");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForJSP.ST_JSP_DIRECTIVE,});
	}

	public void testXML1() throws IOException, BadLocationException {
		int expectedPartitions = 6;
		int nPartitions = doComputePartitioningTest("testfiles/xml/example01.xml");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForXML.ST_XML_PI, StructuredTextPartitionerForXML.ST_DEFAULT_XML, StructuredTextPartitionerForXML.ST_XML_DECLARATION, StructuredTextPartitionerForXML.ST_DEFAULT_XML, StructuredTextPartitionerForXML.ST_XML_COMMENT, StructuredTextPartitionerForXML.ST_DEFAULT_XML});
	}
	
	public void testPerfXML() throws IOException, BadLocationException {
		//int expectedPartitions = 6;
		int nPartitions = doTimedComputePartitioningTest("testfiles/xml/company300k.xml");
		//assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		//checkSeams();
		//verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForXML.ST_XML_PI, StructuredTextPartitionerForXML.ST_DEFAULT_XML, StructuredTextPartitionerForXML.ST_XML_DECLARATION, StructuredTextPartitionerForXML.ST_DEFAULT_XML, StructuredTextPartitionerForXML.ST_XML_COMMENT, StructuredTextPartitionerForXML.ST_DEFAULT_XML});
	}
	public void testPerfJSP() throws IOException, BadLocationException {
		//int expectedPartitions = 6;
		int nPartitions = doTimedComputePartitioningTest("testfiles/jsp/company300k.jsp");
		//assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		//checkSeams();
		//verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForXML.ST_XML_PI, StructuredTextPartitionerForXML.ST_DEFAULT_XML, StructuredTextPartitionerForXML.ST_XML_DECLARATION, StructuredTextPartitionerForXML.ST_DEFAULT_XML, StructuredTextPartitionerForXML.ST_XML_COMMENT, StructuredTextPartitionerForXML.ST_DEFAULT_XML});
	}

	public void testEmpty() throws IOException, BadLocationException {
		int expectedPartitions = 1;
		int nPartitions = doComputePartitioningTest("testfiles/xml/empty.xml");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{StructuredTextPartitionerForXML.ST_DEFAULT_XML});
	}

	/**
	 * Ensure that the current list of partitions are all adjacent to one another
	 */
	protected void checkSeams() {
		if (partitions == null)
			return;
		int offset = 0;
		for (int i = 0; i < partitions.length; i++) {
			assertEquals("partitions are not contiguous!", partitions[i].getOffset(), offset);
			offset = partitions[i].getOffset() + partitions[i].getLength();
		}
	}

	/**
	 * Compute the partitions for the given filename using the default partitioner
	 * for that file type.
	 * 
	 * @param filename
	 * @return int
	 * @throws IOException
	 * @throws BadLocationException
	 */
	protected int doComputePartitioningTest(String filename) throws IOException, BadLocationException {

		IModelManager modelManager = StructuredModelManager.getModelManager();
		InputStream inStream = getClass().getResourceAsStream(filename);
		if (inStream == null)
			inStream = new StringBufferInputStream("");
		IStructuredModel model = modelManager.getModelForEdit(filename, inStream, null);

		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (DEBUG_PRINT_RESULT && useFormatter) {
			double baseTen = Math.log(10);
			formatter.setMinimumIntegerDigits((int) (Math.log(structuredDocument.getLength()) / baseTen) + 1);
			formatter.setGroupingUsed(false);
		}

		partitions = structuredDocument.computePartitioning(0, structuredDocument.getLength());
		if (DEBUG_PRINT_RESULT) {
			String contents = null;

			System.out.println("\nfilename: " + filename);
			for (int i = 0; i < partitions.length; i++) {
				try {
					contents = structuredDocument.get(partitions[i].getOffset(), partitions[i].getLength());
				}
				catch (BadLocationException e) {
					contents = "*error*";
				}
				if (useFormatter)
					System.out.println(formatter.format(partitions[i].getOffset()) + ":" + formatter.format(partitions[i].getLength()) + " - " + partitions[i].getType() + " [" + StringUtils.escape(contents) + "]");
				else
					System.out.println(partitions[i] + " [" + StringUtils.escape(contents) + "]");
			}
		}
		checkSeams();
		model.releaseFromEdit();
		inStream.close();

		if (partitions == null)
			return -1;
		return partitions.length;
	}

	/**
	 * Compute the partitions for the given filename using the default partitioner
	 * for that file type.
	 * 
	 * @param filename
	 * @return int
	 * @throws IOException
	 * @throws BadLocationException
	 */
	protected int doTimedComputePartitioningTest(String filename) throws IOException, BadLocationException {
	
		IModelManager modelManager = StructuredModelManager.getModelManager();
		InputStream inStream = getClass().getResourceAsStream(filename);
		if (inStream == null)
			inStream = new StringBufferInputStream("");
		IStructuredModel model = modelManager.getModelForEdit(filename, inStream, null);
	
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (DEBUG_PRINT_RESULT && useFormatter) {
			double baseTen = Math.log(10);
			formatter.setMinimumIntegerDigits((int) (Math.log(structuredDocument.getLength()) / baseTen) + 1);
			formatter.setGroupingUsed(false);
		}
		long startTime = System.currentTimeMillis();
		partitions = structuredDocument.computePartitioning(0, structuredDocument.getLength());
		long endTime = System.currentTimeMillis();
		if (DEBUG_PRINT_RESULT) {
			
			String contents = null;
	
			System.out.println("\nfilename: " + filename);
			System.out.println("Time to compute " + partitions.length + ": " + (endTime-startTime) + " (msecs)");
			for (int i = 0; i < partitions.length; i++) {
				try {
					contents = structuredDocument.get(partitions[i].getOffset(), partitions[i].getLength());
				}
				catch (BadLocationException e) {
					contents = "*error*";
				}
				if (useFormatter)
					System.out.println(formatter.format(partitions[i].getOffset()) + ":" + formatter.format(partitions[i].getLength()) + " - " + partitions[i].getType() + " [" + StringUtils.escape(contents) + "]");
				else
					System.out.println(partitions[i] + " [" + StringUtils.escape(contents) + "]");
			}
		}
		checkSeams();
		model.releaseFromEdit();
		inStream.close();
	
		if (partitions == null)
			return -1;
		return partitions.length;
	}

	/**
	 * Retrieves the single partition at the given offset for the given file, using the
	 * default partitioner for that file type.  This test allows for verifying the zero-length
	 * partitioning behavior.
	 * 
	 * @param filename
	 * @param offset
	 * @return ITypedRegion
	 * @throws IOException
	 * @throws BadLocationException
	 */
	protected ITypedRegion getPartitionTest(String filename, int offset) throws IOException, BadLocationException {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		InputStream inStream = getClass().getResourceAsStream(filename);
		IStructuredModel model = modelManager.getModelForEdit(filename, inStream, null);

		IStructuredDocument structuredDocument = model.getStructuredDocument();

		partitions = null;
		ITypedRegion partition = structuredDocument.getPartition(offset);
		if (DEBUG_PRINT_RESULT) {
			String contents = null;

			System.out.println("\nfilename: " + filename);
			try {
				contents = structuredDocument.get(partition.getOffset(), partition.getLength());
			}
			catch (BadLocationException e) {
				contents = "*error*";
			}
			System.out.println(partition + " [" + StringUtils.escape(contents) + "]");
		}
		model.releaseFromEdit();
		inStream.close();

		return partition;
	}

	/**
	 * Verifies that the given partitions are of the given partition types
	 * @param typedRegions
	 * @param types
	 */
	protected void verifyPartitionTypes(ITypedRegion actualTypedRegions[], String expectedTypes[]) {
		for (int i = 0; i < expectedTypes.length; i++)
			assertEquals("partition type mismatched", expectedTypes[i], actualTypedRegions[i].getType());
	}

	/**
	 * Verifies that the given partitions begin at the given offsets
	 * @param typedRegions
	 * @param types
	 */
	protected void verifyPartitionRegions(ITypedRegion typedRegions[], int offsets[]) {
		for (int i = 0; i < offsets.length; i++) {
			assertEquals("offset mismatched", typedRegions[i].getOffset(), offsets[i]);
			if (i > 0) {
				assertEquals("lengths misaligned", typedRegions[i].getOffset(), typedRegions[i - 1].getOffset() + typedRegions[i - 1].getLength());
			}
		}
	}
}