/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.text.IXMLPartitions;

import com.ibm.icu.text.DecimalFormat;

public class TestStructuredPartitionerHTML extends TestCase {

	private boolean DEBUG_PRINT_RESULT = false;
	protected ITypedRegion[] partitions = null;

	private boolean useFormatter = true;
	protected DecimalFormat formatter;

	public TestStructuredPartitionerHTML(String name) {
		super(name);
		if (DEBUG_PRINT_RESULT && useFormatter)
			formatter = new DecimalFormat();
	}
	
	/**
	 * must release model (from edit) after
	 * @param filename relative to this class (TestStructuredPartitioner)
	 */
	private IStructuredModel getModelForEdit(String filename) {

		IStructuredModel model = null;
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			InputStream inStream = getClass().getResourceAsStream(filename);
			if (inStream == null)
				inStream = new StringBufferInputStream("");
			model = modelManager.getModelForEdit(filename, inStream, null);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return model;
	}
	
	public void testDisconnectConnect() {
		IStructuredModel model = null;
		try {
			model =getModelForEdit("testfiles/html/example01.xml");
			if(model != null) {
				IStructuredDocument sDoc = model.getStructuredDocument();
				assertTrue("sDoc implementation not instance of IDocumentExtension3", sDoc instanceof IDocumentExtension3);
			
				IDocumentPartitioner partitioner = ((IDocumentExtension3)sDoc).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
				assertTrue("partitioner doesn't implement IStructuredTextPartitioner", partitioner instanceof IStructuredTextPartitioner);
				
				IStructuredTextPartitioner stp = (IStructuredTextPartitioner)partitioner;
				assertNotNull("partitioner was null for sDoc:" + sDoc, partitioner);
				try {
					stp.disconnect();
				}
				catch(Exception e) {
					assertTrue("problem disconnecting w/:" +sDoc + "/n" + e, false);
				}
				try {
					stp.connect(sDoc);
				}
				catch(Exception e) {
					assertTrue("problem connecting w/:" + sDoc + "/n" + e, false);
				}
			}
			else {
				assertTrue("could not retrieve structured model", false);
			}
		}
		finally  {
			if(model != null)
				model.releaseFromEdit();
		}	
	}
	
	public void testGetDefaultPartitionType() {
		IStructuredModel model = null;
		try {
			model =getModelForEdit("testfiles/html/example01.xml");
			if(model != null) {
				IStructuredDocument sDoc = model.getStructuredDocument();
				assertTrue("sDoc implementation not instance of IDocumentExtension3", sDoc instanceof IDocumentExtension3);
			
				IDocumentPartitioner partitioner = ((IDocumentExtension3)sDoc).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
				assertTrue("partitioner doesn't implement IStructuredTextPartitioner", partitioner instanceof IStructuredTextPartitioner);
				
				IStructuredTextPartitioner stp = (IStructuredTextPartitioner)partitioner;
				String defaultPartitionType = stp.getDefaultPartitionType();
				assertTrue("wrong default partition type was: [" + defaultPartitionType + "] should be: [" + IXMLPartitions.XML_DEFAULT + "]", defaultPartitionType.equals(IXMLPartitions.XML_DEFAULT));
			}
			else {
				assertTrue("could not retrieve structured model", false);
			}
		}
		finally  {
			if(model != null)
				model.releaseFromEdit();
		}	
	}
	
	public void testGetPartitionType() {
		IStructuredModel model = null;
		try {
			model =getModelForEdit("testfiles/html/example01.xml");
			if(model != null) {
				IStructuredDocument sDoc = model.getStructuredDocument();
				assertTrue("sDoc implementation not instance of IDocumentExtension3", sDoc instanceof IDocumentExtension3);
			
				IDocumentPartitioner partitioner = ((IDocumentExtension3)sDoc).getDocumentPartitioner(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
				assertTrue("paritioner doesn't implement IStructuredTextPartitioner", partitioner instanceof IStructuredTextPartitioner);
				
				IStructuredTextPartitioner stp = (IStructuredTextPartitioner)partitioner;
				String defaultPartitionType = stp.getDefaultPartitionType();
				assertTrue("wrong default partition type was: [" + defaultPartitionType + "] should be: [" + IXMLPartitions.XML_DEFAULT + "]", defaultPartitionType.equals(IXMLPartitions.XML_DEFAULT));
			}
			else {
				assertTrue("could not retrieve structured model", false);
			}
		}
		finally  {
			if(model != null)
				model.releaseFromEdit();
		}	
	}
	
	public void testHTML1() throws IOException, BadLocationException {
		int expectedPartitions = 4;
		int nPartitions = doComputePartitioningTest("testfiles/html/example01.html");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{IHTMLPartitions.HTML_DECLARATION, IHTMLPartitions.HTML_DEFAULT, IHTMLPartitions.HTML_COMMENT, IHTMLPartitions.HTML_DEFAULT});
	}

	public void testHTML2() throws IOException, BadLocationException {
		int expectedPartitions = 7;
		//TODO Nitin, we need to review the Unknown to Script change (8/1/03, 9:00PM)
		int nPartitions = doComputePartitioningTest("testfiles/html/example02.html");
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{IHTMLPartitions.HTML_DEFAULT, IHTMLPartitions.SCRIPT, IHTMLPartitions.HTML_DEFAULT, IHTMLPartitions.SCRIPT, IHTMLPartitions.HTML_DEFAULT, IHTMLPartitions.SCRIPT, IHTMLPartitions.HTML_DEFAULT});
	}

	public void testHTML3() throws IOException, BadLocationException {
		int expectedPartitions = 1;
		String file = "testfiles/html/example03.html";
		int nPartitions = doComputePartitioningTest(file);
		assertTrue("wrong number of partitions", nPartitions == expectedPartitions);
		checkSeams();
		verifyPartitionTypes(partitions, new String[]{IHTMLPartitions.HTML_DEFAULT});

		// 121 if CRLF
		//  ITypedRegion scriptPartition = getPartitionTest(file, 121);
		// 116 if LF or CR only
		//  ITypedRegion scriptPartition = getPartitionTest(file, 121);

		ITypedRegion part1 = getPartitionTest(file, 121);
		ITypedRegion part2 = getPartitionTest(file, 116);

//		assertEquals("partition is wrong type!", scriptPartition.getType(), IHTMLPartitions.ST_SCRIPT);
		// the critical position is surrounded with HTML > 5 characters in either direction; this should be good enough
		assertTrue("partition is wrong type! :(" + part1.getType() + "|" + part2.getType() + ")", part1.getType().equals(IHTMLPartitions.SCRIPT) || part2.getType().equals(IHTMLPartitions.SCRIPT));
		assertTrue("partition is not zero length!", (part1.getLength() == 0 || part2.getLength() == 0));
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
