/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.editor;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.dtd.core.internal.rules.StructuredTextPartitionerForDTD;
import org.eclipse.wst.dtd.ui.internal.style.LineStyleProviderForDTD;
import org.eclipse.wst.dtd.ui.internal.taginfo.DTDBestMatchHoverProcessor;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.style.LineStyleProviderForNoOp;
import org.eclipse.wst.sse.ui.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;


public class StructuredTextViewerConfigurationDTD extends StructuredTextViewerConfiguration {
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			configuredContentTypes = new String[]{StructuredTextPartitionerForDTD.ST_DTD_DEFAULT, StructuredTextPartitioner.ST_DEFAULT_PARTITION, StructuredTextPartitioner.ST_UNKNOWN_PARTITION};
		}
		return configuredContentTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration#getHighlighter(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IHighlighter getHighlighter(ISourceViewer sourceViewer) {
		IHighlighter highlighter = super.getHighlighter(sourceViewer);

		// We need to add the providers each time this method is called.
		// See StructuredTextViewer.configure() method (defect#246727)
		LineStyleProvider dtdProvider = new LineStyleProviderForDTD();
		LineStyleProvider noopProvider = new LineStyleProviderForNoOp();

		highlighter.addProvider(StructuredTextPartitionerForDTD.ST_DTD_DEFAULT, dtdProvider);
		highlighter.addProvider(StructuredTextPartitioner.ST_DEFAULT_PARTITION, dtdProvider);
		highlighter.addProvider(StructuredTextPartitioner.ST_UNKNOWN_PARTITION, noopProvider);

		highlighter.setDocument((IStructuredDocument) sourceViewer.getDocument());

		return highlighter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String, int)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		/*
		 * content type does not really matter since only combo, problem,
		 * annotation hover are available
		 */
		TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
		for (int i = 0; i < hoverDescs.length; i++) {
			if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
				String hoverType = hoverDescs[i].getId();
				if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
					return new DTDBestMatchHoverProcessor();
				else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
					return new ProblemAnnotationHoverProcessor();
				else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
					return new AnnotationHoverProcessor();
			}
		}
		return super.getTextHover(sourceViewer, contentType, stateMask);
	}
}
