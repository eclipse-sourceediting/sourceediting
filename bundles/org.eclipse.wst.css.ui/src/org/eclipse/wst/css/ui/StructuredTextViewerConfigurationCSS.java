/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.css.core.format.FormatProcessorCSS;
import org.eclipse.wst.css.core.text.ICSSPartitionTypes;
import org.eclipse.wst.css.ui.autoedit.StructuredAutoEditStrategyCSS;
import org.eclipse.wst.css.ui.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.css.ui.style.LineStyleProviderForCSS;
import org.eclipse.wst.css.ui.taginfo.CSSBestMatchHoverProcessor;
import org.eclipse.wst.sse.core.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;

public class StructuredTextViewerConfigurationCSS extends StructuredTextViewerConfiguration {
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);
		
		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}
		
		if (contentType == ICSSPartitionTypes.STYLE) {
			allStrategies.add(new StructuredAutoEditStrategyCSS());
		}

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[0]);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			configuredContentTypes = new String[]{ICSSPartitionTypes.STYLE, IStructuredPartitionTypes.DEFAULT_PARTITION, IStructuredPartitionTypes.UNKNOWN_PARTITION};
		}
		return configuredContentTypes;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		IContentAssistant contentAssistant = super.getContentAssistant(sourceViewer);

		if (contentAssistant != null && contentAssistant instanceof ContentAssistant) {
			//((ContentAssistant)
			// contentAssistant).setContentAssistProcessor(new
			// CSSContentAssistProcessor(),
			// ICSSPartitions.STYLE);
			IContentAssistProcessor cssProcessor = new CSSContentAssistProcessor();
			setContentAssistProcessor((ContentAssistant) contentAssistant, cssProcessor, ICSSPartitionTypes.STYLE);
			setContentAssistProcessor((ContentAssistant) contentAssistant, cssProcessor, IStructuredPartitionTypes.UNKNOWN_PARTITION);
		}

		return contentAssistant;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), ICSSPartitionTypes.STYLE);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new FormatProcessorCSS()));

		return formatter;
	}

	public IHighlighter getHighlighter(ISourceViewer sourceViewer) {
		IHighlighter highlighter = super.getHighlighter(sourceViewer);

		if (highlighter != null) {
			highlighter.addProvider(ICSSPartitionTypes.STYLE, new LineStyleProviderForCSS());
		}

		return highlighter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String, int)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		// content type does not really matter since only combo, problem,
		// annotation hover is available
		TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
		int i = 0;
		while (i < hoverDescs.length) {
			if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
				String hoverType = hoverDescs[i].getId();
				if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
					return new CSSBestMatchHoverProcessor();
				else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
					return new ProblemAnnotationHoverProcessor();
				else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
					return new AnnotationHoverProcessor();
			}
			i++;
		}
		return super.getTextHover(sourceViewer, contentType, stateMask);
	}
}