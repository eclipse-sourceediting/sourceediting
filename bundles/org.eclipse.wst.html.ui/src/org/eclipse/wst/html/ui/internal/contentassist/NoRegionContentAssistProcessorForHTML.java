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
package org.eclipse.wst.html.ui.internal.contentassist;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.wst.css.core.text.ICSSPartitionTypes;
import org.eclipse.wst.css.ui.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.html.core.text.IHTMLPartitionTypes;
import org.eclipse.wst.javascript.common.ui.internal.contentassist.JavaScriptContentAssistProcessor;
import org.eclipse.wst.xml.ui.contentassist.NoRegionContentAssistProcessor;

/**
 * 
 * @author pavery
 */
public class NoRegionContentAssistProcessorForHTML extends NoRegionContentAssistProcessor {
	/*
	 * @see com.ibm.sse.editor.xml.contentassist.NoRegionContentAssistProcessor#initPartitionToProcessorMap()
	 */
	protected void initPartitionToProcessorMap() {
		super.initPartitionToProcessorMap();
		IContentAssistProcessor htmlProcessor = new HTMLContentAssistProcessor();
		fPartitionToProcessorMap.put(IHTMLPartitionTypes.HTML_DEFAULT, htmlProcessor);
		fPartitionToProcessorMap.put(IHTMLPartitionTypes.HTML_COMMENT, htmlProcessor);

		IContentAssistProcessor jsContentAssistProcessor = new JavaScriptContentAssistProcessor();
		fPartitionToProcessorMap.put(IHTMLPartitionTypes.SCRIPT, jsContentAssistProcessor);

		IContentAssistProcessor cssContentAssistProcessor = new CSSContentAssistProcessor();
		fPartitionToProcessorMap.put(ICSSPartitionTypes.STYLE, cssContentAssistProcessor);
	}
}