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
import org.eclipse.wst.css.core.internal.text.rules.StructuredTextPartitionerForCSS;
import org.eclipse.wst.css.ui.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.html.core.internal.text.rules.StructuredTextPartitionerForHTML;
import org.eclipse.wst.javascript.common.ui.contentassist.JavaScriptContentAssistProcessor;
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
		fPartitionToProcessorMap.put(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, htmlProcessor);
		fPartitionToProcessorMap.put(StructuredTextPartitionerForHTML.ST_HTML_COMMENT, htmlProcessor);

		IContentAssistProcessor jsContentAssistProcessor = new JavaScriptContentAssistProcessor();
		fPartitionToProcessorMap.put(StructuredTextPartitionerForHTML.ST_SCRIPT, jsContentAssistProcessor);

		IContentAssistProcessor cssContentAssistProcessor = new CSSContentAssistProcessor();
		fPartitionToProcessorMap.put(StructuredTextPartitionerForCSS.ST_STYLE, cssContentAssistProcessor);
	}
}