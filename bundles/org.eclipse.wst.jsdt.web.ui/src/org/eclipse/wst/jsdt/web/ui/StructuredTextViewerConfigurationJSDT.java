/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.jsdt.web.core.text.IJsPartitions;
import org.eclipse.wst.jsdt.web.ui.contentassist.JSDTStructuredContentAssistProcessor;
import org.eclipse.wst.jsdt.web.ui.internal.autoedit.AutoEditStrategyForJs;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
* 
 * Configuration for a source viewer which shows Html and supports JSDT.
 * <p>
 * Clients can subclass and override just those methods which must be specific
 * to their needs.
 * </p>
 * 
 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration
 * @since 1.0
 */
public class StructuredTextViewerConfigurationJSDT extends StructuredTextViewerConfigurationHTML {
	/**
	 * Create new instance of StructuredTextViewerConfigurationHTML
	 */
	public StructuredTextViewerConfigurationJSDT() {
		// Must have empty constructor to createExecutableExtension
		super();
	}
	
	/**
	 * @see org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		if(contentType.equals(IHTMLPartitions.SCRIPT) || contentType.equals(IHTMLPartitions.SCRIPT_EVENTHANDLER)) {
			IAutoEditStrategy[] strategies = new IAutoEditStrategy[1];
			strategies[0] = new AutoEditStrategyForJs();
			return strategies;
		} else {
			return super.getAutoEditStrategies(sourceViewer, contentType);
		}
	}
	
	/**
	 * @see org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML#getContentAssistProcessors(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	protected IContentAssistProcessor[] getContentAssistProcessors(
			ISourceViewer sourceViewer, String partitionType) {
		
		IContentAssistProcessor[] processors;
		
		if(isJavascriptPartitionType(partitionType)) {
			IContentAssistProcessor processor = new JSDTStructuredContentAssistProcessor(
					this.getContentAssistant(), partitionType, sourceViewer);
			processors = new IContentAssistProcessor[]{processor};
		} else {
			processors = super.getContentAssistProcessors(sourceViewer, partitionType);
		} 
		
		return processors;
	}
	
	/**
	 * @param partitionTypeID check to see if this partition type ID is for a Javascript partition type
	 * @return <code>true</code> if the given partiton type is a Javascript partition type,
	 * <code>false</code> otherwise
	 */
	private static boolean isJavascriptPartitionType(String partitionTypeID) {
		return IJsPartitions.HtmlJsPartition.equals(partitionTypeID);
	}
}
