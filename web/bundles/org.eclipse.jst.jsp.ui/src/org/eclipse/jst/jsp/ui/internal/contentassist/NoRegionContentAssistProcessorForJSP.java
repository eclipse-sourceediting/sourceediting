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
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.internal.contentassist.NoRegionContentAssistProcessorForHTML;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.xml.core.text.IXMLPartitions;

/**
 * @plannedfor 1.0
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
		addPartitionProcessor(IStructuredPartitions.DEFAULT_PARTITION, jspContentAssistProcessor);
		addPartitionProcessor(IXMLPartitions.XML_DEFAULT, jspContentAssistProcessor);
		addPartitionProcessor(IHTMLPartitions.HTML_DEFAULT, jspContentAssistProcessor);
		addPartitionProcessor(IHTMLPartitions.HTML_COMMENT, jspContentAssistProcessor);
		addPartitionProcessor(IJSPPartitions.JSP_DEFAULT, jspContentAssistProcessor);
		addPartitionProcessor(IJSPPartitions.JSP_DIRECTIVE, jspContentAssistProcessor);
		addPartitionProcessor(IJSPPartitions.JSP_CONTENT_DELIMITER, jspContentAssistProcessor);
		addPartitionProcessor(IJSPPartitions.JSP_CONTENT_JAVASCRIPT, jspContentAssistProcessor);

		IContentAssistProcessor jspJavaContentAssistProcessor = new JSPJavaContentAssistProcessor();
		addPartitionProcessor(IJSPPartitions.JSP_CONTENT_JAVA, jspJavaContentAssistProcessor);

	}

	protected void initNameToProcessorMap() {
		super.initNameToProcessorMap();
		JSPPropertyContentAssistProcessor jspPropertyCAP = new JSPPropertyContentAssistProcessor();
		addNameProcessor(JSP11Namespace.ElementName.SETPROPERTY, jspPropertyCAP);
		addNameProcessor(JSP11Namespace.ElementName.GETPROPERTY, jspPropertyCAP);
		addNameProcessor(JSP11Namespace.ElementName.USEBEAN, new JSPUseBeanContentAssistProcessor());
	}

	private void initJSPContexts() {
		fJSPContexts = new String[]{DOMJSPRegionContexts.JSP_CLOSE, DOMJSPRegionContexts.JSP_CONTENT, DOMJSPRegionContexts.JSP_DECLARATION_OPEN, DOMJSPRegionContexts.JSP_EXPRESSION_OPEN, DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN};
	}

	/**
	 * Quick check to see if context (String) should map to
	 * JSPContentAssistProcessor.
	 * 
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

	protected IContentAssistProcessor guessContentAssistProcessor(ITextViewer viewer, int documentOffset) {
		IContentAssistProcessor p = super.guessContentAssistProcessor(viewer, documentOffset);
		if (p == null) {
			IStructuredDocumentRegion sdRegion = ((IStructuredDocument) viewer.getDocument()).getRegionAtCharacterOffset(documentOffset);
			if (isJSPRegion(sdRegion))
				p = getPartitionProcessor(IJSPPartitions.JSP_CONTENT_JAVA);
		}
		return p;
	}
}
