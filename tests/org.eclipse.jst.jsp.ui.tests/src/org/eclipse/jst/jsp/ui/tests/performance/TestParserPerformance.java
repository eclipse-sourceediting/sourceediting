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
package org.eclipse.jst.jsp.ui.tests.performance;



import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;

/**
 * This class is a performance test for the sed XMLSourceParser class
 */
public class TestParserPerformance extends TestCase {

	public TestParserPerformance(String name) {
		super(name);
	}

	public void testBigStyle() throws IOException {
		doJSPParser("bigStyle.jsp"); //$NON-NLS-1$
		doJSPParser("testfiles/company300k.jsp"); //$NON-NLS-1$


		doXMLParser("bigStyle.jsp"); //$NON-NLS-1$
		doXMLParser("testfiles/company300k.html"); //$NON-NLS-1$
		doXMLParser("testfiles/company300k.xml"); //$NON-NLS-1$

		String[] yourCompanyFiles = {"testfiles/YourCoIndex.html", "testfiles/YourCoIntro.html", "testfiles/YourCoMenu.html", "testfiles/YourCoTitle.html"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		doXMLParser(yourCompanyFiles);
	}

	private void doXMLParser(String filename) throws IOException {
		String[] filenames = {filename};
		doXMLParser(filenames);
	}

	private void doJSPParser(String filename) throws IOException {
		String[] filenames = {filename};
		doJSPParser(filenames);
	}

	/**
	 * this test can time how long it takes to parse multiple files.
	 * @param filename
	 * @throws IOException
	 */
	private void doXMLParser(String[] filenames) throws IOException {
		Runtime rt = Runtime.getRuntime();

		XMLSourceParser[] parsers = new XMLSourceParser[filenames.length];
		String[] texts = new String[filenames.length];

		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) org.eclipse.core.runtime.Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();

		for (int k = 0; k < filenames.length; k++) {
			// create models
			InputStream inStream = getClass().getResourceAsStream(filenames[k]);
			IStructuredModel sModel = modelManager.getModelForEdit(filenames[k], inStream, null);
			IStructuredDocument structuredDocument = sModel.getStructuredDocument();
			//String structuredDocumentText = structuredDocument.getText();
			texts[k] = structuredDocument.getText();
			//XMLSourceParser parser = (XMLSourceParser) structuredDocument.getParser();
			parsers[k] = (XMLSourceParser) structuredDocument.getParser();
			sModel.releaseFromEdit();
		}

		int numTrials = 101;
		long totalTime = 0;
		long totalMemory = 0;

		for (int i = 0; i < numTrials; i++) {
			rt.gc();
			long startMem = rt.totalMemory() - rt.freeMemory();
			long startTime = System.currentTimeMillis();
			//			parser.reset(structuredDocumentText);
			//			parser.getNodes();
			for (int j = 0; j < filenames.length; j++) {
				parsers[j].reset(texts[j]);
				parsers[j].getDocumentRegions();
			}
			long endTime = System.currentTimeMillis();
			long endMem = rt.totalMemory() - rt.freeMemory();

			long diffTime = endTime - startTime;
			long diffMem = endMem - startMem;

			if (i > 0) {
				totalTime += diffTime;
				totalMemory += diffMem;
			}

			//			System.out.println("++ " + i);
			//			System.out.println("time > " + diffTime);
			//			System.out.println("mem  > " + diffMem);
		}

		System.out.println(""); //$NON-NLS-1$
		System.out.println("---------------------------"); //$NON-NLS-1$
		System.out.println("XMLParser performance for > "); //$NON-NLS-1$
		for (int l = 0; l < filenames.length; l++) {
			System.out.print(filenames[l] + " "); //$NON-NLS-1$
		}
		System.out.println("number of trials >		" + (numTrials - 1)); //$NON-NLS-1$
		System.out.println("getNodes() ave time >	" + totalTime / (numTrials - 1)); //$NON-NLS-1$
		System.out.println("ave memory >			" + totalMemory / (numTrials - 1)); //$NON-NLS-1$
	}

	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	protected void doJSPParser(String[] filenames) throws IOException {
		Runtime rt = Runtime.getRuntime();

		// create model
		JSPSourceParser[] parsers = new JSPSourceParser[filenames.length];
		String[] texts = new String[filenames.length];

		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) org.eclipse.core.runtime.Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();

		for (int k = 0; k < filenames.length; k++) {
			InputStream inStream = getClass().getResourceAsStream(filenames[k]);
			IStructuredModel sModel = modelManager.getModelForEdit(filenames[k], inStream, null);
			IStructuredDocument structuredDocument = sModel.getStructuredDocument();
			//String structuredDocumentText = structuredDocument.getText();
			texts[k] = structuredDocument.getText();
			//JSPSourceParser parser = (JSPSourceParser) structuredDocument.getParser();
			parsers[k] = (JSPSourceParser) structuredDocument.getParser();
			sModel.releaseFromEdit();
		}
		int numTrials = 101;
		long totalTime = 0;
		long totalMemory = 0;

		for (int i = 0; i < numTrials; i++) {
			rt.gc();
			long startMem = rt.totalMemory() - rt.freeMemory();
			long startTime = System.currentTimeMillis();
			//			parser.reset(structuredDocumentText);
			//			parser.getNodes();
			for (int j = 0; j < filenames.length; j++) {
				parsers[j].reset(texts[j]);
				parsers[j].getDocumentRegions();
			}
			long endTime = System.currentTimeMillis();
			long endMem = rt.totalMemory() - rt.freeMemory();

			long diffTime = endTime - startTime;
			long diffMem = endMem - startMem;

			if (i > 0) {
				totalTime += diffTime;
				totalMemory += diffMem;
			}

			//			System.out.println("++ " + i);
			//			System.out.println("time > " + diffTime);
			//			System.out.println("mem  > " + diffMem);
		}

		System.out.println(""); //$NON-NLS-1$
		System.out.println("---------------------------"); //$NON-NLS-1$
		System.out.println("JSPParser performance for > "); //$NON-NLS-1$
		for (int l = 0; l < filenames.length; l++) {
			System.out.print(filenames[l] + " "); //$NON-NLS-1$
		}
		System.out.println("number of trials >		" + (numTrials - 1)); //$NON-NLS-1$
		System.out.println("getNodes() ave time >	" + totalTime / (numTrials - 1)); //$NON-NLS-1$
		System.out.println("ave memory >			" + totalMemory / (numTrials - 1)); //$NON-NLS-1$
	}
}