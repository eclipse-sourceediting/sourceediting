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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jst.jsp.core.internal.text.rules.StructuredTextPartitionerForJSP;
import org.eclipse.jst.jsp.ui.style.LineStyleProviderForJSP;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.wst.css.core.internal.text.rules.StructuredTextPartitionerForCSS;
import org.eclipse.wst.css.ui.style.LineStyleProviderForEmbeddedCSS;
import org.eclipse.wst.html.core.internal.text.rules.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.html.ui.style.LineStyleProviderForHTML;
import org.eclipse.wst.javascript.common.ui.style.LineStyleProviderForJavaScript;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.style.Highlighter;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

/**
 * This class is a performance test for the sed Highlighter class
 */
public class TestHighlighterPerformance extends TestCase {

	// (pa)
	// this flag determines wether or not to test the GA code
	//true > (the patched old code in com.ibm.sed.style.Highlighter)
	// otherwise it will try the newer test
	//false > (the code in com.ibm.sed.structured.style.Highlighter)
	private boolean fDoGATest = false;

	public TestHighlighterPerformance(String name) {
		super(name);
	}

	public void testAllFiles() throws IOException {
		// this just does the whole file line by line
		doHighlighterTest("bigStyle.jsp"); //$NON-NLS-1$
		doHighlighterTest("testfiles/company300k.jsp"); //$NON-NLS-1$
		doHighlighterTest("testfiles/company300k.html"); //$NON-NLS-1$
		doHighlighterTest("testfiles/company300k.xml"); //$NON-NLS-1$
	}

	protected void doHighlighterTest(String filename) throws IOException {
		doHighligtherTest(filename, 0);
	}

	protected void doHighligtherTest(String filename, int start) throws IOException {
		// create model
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) org.eclipse.core.runtime.Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		InputStream inStream = getClass().getResourceAsStream(filename);

		//TODO_future: seems silly to create a document here, just to get
		// length,
		// we should refactor that out.
		IStructuredDocument sDoc = modelManager.createStructuredDocumentFor(filename, inStream, null);
		int end = sDoc.getText().length();
		doHighlighterTest(filename, start, end);
	}

	protected void doHighlighterTest(String filename, int start, int end) throws IOException {

		doNewHighlighterTest(filename, start, end);

	}

	/**
	 * exactly the same as doOldHighlighterTest except uses the old
	 * com.ibm.sed.structured.style.Highlighter class
	 * 
	 * @param filename
	 * @param start
	 * @param end
	 * @throws IOException
	 */
	protected void doNewHighlighterTest(String filename, int start, int end) throws IOException {
		Runtime rt = Runtime.getRuntime();

		// create model
		IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) org.eclipse.core.runtime.Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager modelManager = modelManagerPlugin.getModelManager();
		InputStream inStream = getClass().getResourceAsStream(filename);
		IStructuredModel sModel = null;
		try {
			sModel = modelManager.getModelForEdit(filename, inStream, null);

			//		IStructuredDocumentRegion startNode =
			// sModel.getStructuredDocument().getNodeAtCharacterOffset(start);
			//		IStructuredDocumentRegion endNode =
			// sModel.getStructuredDocument().getNodeAtCharacterOffset(end);

			// ==> // ITypedRegion[] partitions =
			// sModel.getStructuredDocument().getDocumentPartitioner().computePartitioning(start,
			// end - start);
			ITextViewer nullViewer = null;
			Highlighter fHighlighter = (Highlighter) getAppropriateHilighter(filename);
			fHighlighter.setDocument(sModel.getStructuredDocument());

			// lineGetStyle, line by line
			try {
				IStructuredDocument structuredDocument = sModel.getStructuredDocument();

				int startLine = structuredDocument.getLineOfOffset(start);
				int i = startLine;
				int lineStart = start;
				int lineLength = end - start;

				long totalTime = 0;
				long totalMemory = 0;
				int totalRanges = 0;


				IRegion region = structuredDocument.getLineInformation(i++);
				StyleRange[] ranges = null;

				//			long totalStartTime = System.currentTimeMillis();
				while (i <= structuredDocument.getLineOfOffset(end)) {
					lineStart = region.getOffset();
					lineLength = region.getLength();

					long startMem = rt.totalMemory() - rt.freeMemory();
					long startTime = System.currentTimeMillis();
					ranges = fHighlighter.lineGetStyle(lineStart, lineLength);
					long endTime = System.currentTimeMillis();
					long endMem = rt.totalMemory() - rt.freeMemory();
					long diffTime = endTime - startTime;
					long diffMem = endMem - startMem;
					totalTime += diffTime;
					totalMemory += diffMem;
					totalRanges += ranges.length;

					//				System.out.println("++");
					//				System.out.println("line number > " + i);
					//				System.out.println("from > " + lineStart + " length of
					// > "
					// + lineLength);
					//				System.out.println("time > " + diffTime);
					//				System.out.println("style ranges > " + ranges.length);
					//				System.out.println("");
					region = structuredDocument.getLineInformation(i++);
				}
				//			long totalEndTime = System.currentTimeMillis();

				System.out.println(""); //$NON-NLS-1$
				System.out.println("---------------------------------------"); //$NON-NLS-1$
				System.out.println(""); //$NON-NLS-1$
				System.out.println("V51 com.ibm.sed.structured.style.Highlighter > " + filename); //$NON-NLS-1$
				System.out.println("total lines >			" + (i - startLine)); //$NON-NLS-1$
				System.out.println("total time >			" + totalTime); //$NON-NLS-1$
				System.out.println("total memory >			" + totalMemory); //$NON-NLS-1$
				//			System.out.println("accurate total time > " + (totalEndTime
				// -
				// totalStartTime));
				System.out.println("total style ranges  >	" + totalRanges); //$NON-NLS-1$
				System.out.println("ave time/line >			" + ((double)totalTime / (double)(i - startLine))); //$NON-NLS-1$
			}
			catch (BadLocationException ble) {
				System.out.println("no region at that line number..."); //$NON-NLS-1$
			}
		}
		finally {
			if (sModel != null) {
				sModel.releaseFromEdit();
			}

		}
	}

	/**
	 * @param filename
	 * @return
	 */
	private IHighlighter getAppropriateHilighter(String filename) {
		IHighlighter result = null;
		if (filename.endsWith(".html")) { //$NON-NLS-1$
			StructuredTextViewerConfiguration configuration = new StructuredTextViewerConfigurationHTML();
			result = configuration.getHighlighter(null);
		} else if (filename.endsWith(".jsp")) { //$NON-NLS-1$
			result = getHighlighterJSP();
		} else if (filename.endsWith(".xml")) { //$NON-NLS-1$
			StructuredTextViewerConfiguration configuration = new StructuredTextViewerConfigurationXML();
			result = configuration.getHighlighter(null);
		} 
		return result;
	}

	/**
	 * This file should be kept "in snych" with what's in viewer configuration
	 * It was found the "java" part caused loading of plugins which then 
	 * would throw exception automatically if workbench UI hadn't been started. 
	 */
	private IHighlighter getHighlighterJSP() {
		IHighlighter highlighter = new Highlighter();
	
		if (highlighter != null) {
			// HTML
			LineStyleProvider htmlLineStyleProvider = new LineStyleProviderForHTML();
			highlighter.addProvider(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, htmlLineStyleProvider);
			highlighter.addProvider(StructuredTextPartitionerForHTML.ST_HTML_COMMENT, htmlLineStyleProvider);
			highlighter.addProvider(StructuredTextPartitionerForHTML.ST_HTML_DECLARATION, htmlLineStyleProvider);
	
			// HTML JavaScript
			LineStyleProvider jsLineStyleProvider = new LineStyleProviderForJavaScript();
			highlighter.addProvider(StructuredTextPartitionerForHTML.ST_SCRIPT, jsLineStyleProvider);
	
			// CSS
			LineStyleProvider cssLineStyleProvider = new LineStyleProviderForEmbeddedCSS();
			highlighter.addProvider(StructuredTextPartitionerForCSS.ST_STYLE, cssLineStyleProvider);
	
			// JSP
			LineStyleProvider jspLineStyleProvider = new LineStyleProviderForJSP();
			highlighter.addProvider(StructuredTextPartitionerForJSP.ST_DEFAULT_JSP, jspLineStyleProvider);
			highlighter.addProvider(StructuredTextPartitionerForJSP.ST_JSP_COMMENT, jspLineStyleProvider);
			highlighter.addProvider(StructuredTextPartitionerForJSP.ST_JSP_DIRECTIVE, jspLineStyleProvider);
			highlighter.addProvider(StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, jspLineStyleProvider);
	
			// JSP Java or JSP JavaScript
			//highlighter.addProvider(StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, new LineStyleProviderForJava());
			highlighter.addProvider(StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT, new LineStyleProviderForJavaScript());
		}
	
		return highlighter;
	}
}