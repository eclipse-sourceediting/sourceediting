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
package org.eclipse.wst.dtd.ui;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.dtd.core.rules.StructuredTextPartitionerForDTD;
import org.eclipse.wst.dtd.ui.style.LineStyleProviderForDTD;
import org.eclipse.wst.dtd.ui.taginfo.DTDBestMatchHoverProcessor;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.style.Highlighter;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.style.LineStyleProviderForNoOp;
import org.eclipse.wst.sse.ui.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;


/**
 * Provides the best dtd hover help documentation (by using other hover help
 * processors) Priority of hover help processors is: ProblemHoverProcessor,
 * AnnotationHoverProcessor
 */
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
	public IHighlighter getHighlighter(ISourceViewer viewer) {
		if (fHighlighter == null) {
			fHighlighter = new Highlighter();
		}

		// We need to add the providers each time this method is called.
		// See StructuredTextViewer.configure() method (defect#246727)
		LineStyleProvider dtdProvider = new LineStyleProviderForDTD();
		LineStyleProvider noopProvider = new LineStyleProviderForNoOp();

		// com.ibm.sed.editor.ST_DEFAULT
		fHighlighter.addProvider(StructuredTextPartitionerForDTD.ST_DTD_DEFAULT, dtdProvider);
		fHighlighter.addProvider(StructuredTextPartitioner.ST_DEFAULT_PARTITION, dtdProvider);
		fHighlighter.addProvider(StructuredTextPartitioner.ST_UNKNOWN_PARTITION, noopProvider);

		//fHighlighter.setModel(((StructuredTextViewer) viewer).getModel());
		fHighlighter.setDocument((IStructuredDocument) viewer.getDocument());

		return fHighlighter;
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
					return new DTDBestMatchHoverProcessor();
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
