/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui.internal.provisional;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.format.FormatProcessorCSS;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.text.ICSSPartitionTypes;
import org.eclipse.wst.css.ui.internal.autoedit.StructuredAutoEditStrategyCSS;
import org.eclipse.wst.css.ui.internal.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.css.ui.internal.style.LineStyleProviderForCSS;
import org.eclipse.wst.css.ui.internal.taginfo.CSSBestMatchHoverProcessor;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.provisional.style.IHighlighter;
import org.eclipse.wst.sse.ui.internal.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;

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
	
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		Vector vector = new Vector();

		// prefix[0] is either '\t' or ' ' x tabWidth, depending on preference
		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		int indentationWidth = preferences.getInt(CSSCorePreferenceNames.INDENTATION_SIZE);
		String indentCharPref = preferences.getString(CSSCorePreferenceNames.INDENTATION_CHAR);
		boolean useSpaces = CSSCorePreferenceNames.SPACE.equals(indentCharPref);

		for (int i = 0; i <= indentationWidth; i++) {
			StringBuffer prefix = new StringBuffer();
			boolean appendTab = false;

			if (useSpaces) {
				for (int j = 0; j + i < indentationWidth; j++)
					prefix.append(' ');

				if (i != 0)
					appendTab = true;
			} else {
				for (int j = 0; j < i; j++)
					prefix.append(' ');

				if (i != indentationWidth)
					appendTab = true;
			}

			if (appendTab) {
				prefix.append('\t');
				vector.add(prefix.toString());
				// remove the tab so that indentation - tab is also an indent
				// prefix
				prefix.deleteCharAt(prefix.length() - 1);
			}
			vector.add(prefix.toString());
		}

		vector.add(""); //$NON-NLS-1$

		return (String[]) vector.toArray(new String[vector.size()]);
	}
}