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
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jst.jsp.core.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.text.rules.StructuredTextPartitionerForJSP;
import org.eclipse.jst.jsp.core.model.parser.DOMJSPRegionContexts;
import org.eclipse.wst.html.core.internal.text.rules.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.ui.internal.contentassist.NoRegionContentAssistProcessorForHTML;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;

/**
 * 
 * @author pavery
 */
public class NoRegionContentAssistProcessorForJSP extends NoRegionContentAssistProcessorForHTML {

	private String[] fJSPContexts = null;

	public NoRegionContentAssistProcessorForJSP() {
		super();
		initJSPContexts();
	}

	/* 
	 * @see org.eclipse.wst.html.ui.internal.contentassist.NoRegionContentAssistProcessorForHTML#initPartitionToProcessorMap()
	 */
	protected void initPartitionToProcessorMap() {
		super.initPartitionToProcessorMap();
		IContentAssistProcessor jspContentAssistProcessor = new JSPContentAssistProcessor();
		// JSP
		fPartitionToProcessorMap.put(StructuredTextPartitioner.ST_DEFAULT_PARTITION, jspContentAssistProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForXML.ST_DEFAULT_XML, jspContentAssistProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, jspContentAssistProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForHTML.ST_HTML_COMMENT, jspContentAssistProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForJSP.ST_DEFAULT_JSP, jspContentAssistProcessor);
		// JSP directives
		fPartitionToProcessorMap.put(StructuredTextPartitionerForJSP.ST_JSP_DIRECTIVE, jspContentAssistProcessor);
		// JSP delimiters
		fPartitionToProcessorMap.put(StructuredTextPartitionerForJSP.ST_JSP_CONTENT_DELIMITER, jspContentAssistProcessor);
		// JSP JavaScript
		fPartitionToProcessorMap.put(StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVASCRIPT, jspContentAssistProcessor);

		IContentAssistProcessor jspJavaContentAssistProcessor = new JSPJavaContentAssistProcessor();
		// JSP Java
		fPartitionToProcessorMap.put(StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA, jspJavaContentAssistProcessor);
	}

	/* 
	 * @see com.ibm.sse.editor.xml.contentassist.NoRegionContentAssistProcessor#initNameToProcessorMap()
	 */
	protected void initNameToProcessorMap() {
		super.initNameToProcessorMap();
		JSPPropertyContentAssistProcessor jspPropertyCAP = new JSPPropertyContentAssistProcessor();
		fNameToProcessorMap.put(JSP11Namespace.ElementName.SETPROPERTY, jspPropertyCAP);
		fNameToProcessorMap.put(JSP11Namespace.ElementName.GETPROPERTY, jspPropertyCAP);
		fNameToProcessorMap.put(JSP11Namespace.ElementName.USEBEAN, new JSPUseBeanContentAssistProcessor());
	}

	private void initJSPContexts() {
		fJSPContexts = new String[]{DOMJSPRegionContexts.JSP_CLOSE, DOMJSPRegionContexts.JSP_CONTENT, DOMJSPRegionContexts.JSP_DECLARATION_OPEN, DOMJSPRegionContexts.JSP_EXPRESSION_OPEN, DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN};
	}

	/**
	 * Quick check to see if context (String) should map to JSPContentAssistProcessor.
	 * @param context
	 * @return if it's a JSP Region (for which we need JSP Content assist)
	 */
	private boolean isJSPRegion(IStructuredDocumentRegion sdRegion) {
		String context = sdRegion.getType();
		for (int i = 0; i < fJSPContexts.length; i++) {
			if (context == fJSPContexts[i])
				return true;
		}
		return false;
	}

	/*
	 * @see com.ibm.sse.editor.xml.contentassist.NoRegionContentAssistProcessor#guessContentAssistProcessor(org.eclipse.jface.text.ITextViewer, int)
	 */
	protected IContentAssistProcessor guessContentAssistProcessor(ITextViewer viewer, int documentOffset) {
		IContentAssistProcessor p = super.guessContentAssistProcessor(viewer, documentOffset);
		if (p == null) {
			IStructuredDocumentRegion sdRegion = ((IStructuredDocument) viewer.getDocument()).getRegionAtCharacterOffset(documentOffset);
			if (isJSPRegion(sdRegion))
				p = (IContentAssistProcessor) fPartitionToProcessorMap.get(StructuredTextPartitionerForJSP.ST_JSP_CONTENT_JAVA);
		}
		return p;
	}
}