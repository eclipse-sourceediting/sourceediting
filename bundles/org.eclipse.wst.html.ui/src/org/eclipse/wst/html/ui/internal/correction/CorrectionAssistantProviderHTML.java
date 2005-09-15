/*
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 *   Jens Lukowski/Innoopract - initial renaming/restructuring
 * 
 */
package org.eclipse.wst.html.ui.internal.correction;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.html.core.internal.provisional.text.IHTMLPartitionTypes;
import org.eclipse.wst.sse.ui.internal.correction.CorrectionAssistantProvider;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.internal.correction.CorrectionProcessorXML;

/**
 * Correction assistant for HTML
 */
public class CorrectionAssistantProviderHTML extends CorrectionAssistantProvider {

	public IContentAssistant getCorrectionAssistant(ISourceViewer sourceViewer) {
		IContentAssistant ca = null;

		if (sourceViewer != null) {
			ContentAssistant assistant = new ContentAssistant();

			if (sourceViewer != null) {
				IContentAssistProcessor correctionProcessor = new CorrectionProcessorXML(sourceViewer);
				assistant.setContentAssistProcessor(correctionProcessor, IHTMLPartitionTypes.HTML_DEFAULT);
				assistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_CDATA);
				assistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_COMMENT);
				assistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_DECLARATION);
				assistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_PI);
				assistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.DTD_SUBSET);
			}
			ca = assistant;
		}

		return ca;
	}

}
