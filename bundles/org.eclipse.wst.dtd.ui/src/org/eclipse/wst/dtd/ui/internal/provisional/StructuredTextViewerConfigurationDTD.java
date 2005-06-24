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
package org.eclipse.wst.dtd.ui.internal.provisional;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.dtd.core.internal.provisional.text.IDTDPartitionTypes;
import org.eclipse.wst.dtd.ui.internal.style.LineStyleProviderForDTD;
import org.eclipse.wst.dtd.ui.internal.taginfo.DTDBestMatchHoverProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.provisional.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.provisional.style.IHighlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProviderForNoOp;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;


/**
 * A source viewer configuration for DTDs.
 * 
 * @plannedfor 1.0
 */
public class StructuredTextViewerConfigurationDTD extends StructuredTextViewerConfiguration {

	/**
	 * 
	 */
	public StructuredTextViewerConfigurationDTD() {
		super();
	}

	/**
	 * @param store
	 */
	public StructuredTextViewerConfigurationDTD(IPreferenceStore store) {
		super(store);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			configuredContentTypes = new String[]{IDTDPartitionTypes.DTD_DEFAULT, IStructuredPartitionTypes.DEFAULT_PARTITION, IStructuredPartitionTypes.UNKNOWN_PARTITION};
		}
		return configuredContentTypes;
	}

	// WORKAROUND for bug 98408
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = (ContentAssistant) super.getContentAssistant(sourceViewer);
		assistant.setContentAssistProcessor(null, IDTDPartitionTypes.DTD_DEFAULT);
		return assistant;
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

		highlighter.addProvider(IDTDPartitionTypes.DTD_DEFAULT, dtdProvider);
		highlighter.addProvider(IStructuredPartitionTypes.DEFAULT_PARTITION, dtdProvider);
		highlighter.addProvider(IStructuredPartitionTypes.UNKNOWN_PARTITION, noopProvider);

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

	public IReconciler getReconciler(ISourceViewer sourceViewer) {

		if (fReconciler != null) {
			// a reconciler should always be installed or disposed of
			if (!fReconciler.isInstalled()) {
				fReconciler = null;
			}
		}

		if (fReconciler == null) {
			fReconciler = new StructuredRegionProcessor();
			fReconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		}

		boolean reconcilingEnabled = fPreferenceStore.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);

		if (!reconcilingEnabled)
			return fReconciler;

		// the second time through, the strategies are set
		if (fReconciler != null) {

			IDocument doc = ((StructuredTextEditor) editorPart).getDocumentProvider().getDocument(editorPart.getEditorInput());
			IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);

			try {

				if (sModel != null) {

					// IReconcilingStrategy markupStrategy = new
					// StructuredTextReconcilingStrategyForMarkup((ITextEditor)
					// editorPart);
					// fReconciler.setReconcilingStrategy(markupStrategy,
					// IXMLPartitions.XML_DEFAULT);
					// fReconciler.setDefaultStrategy(markupStrategy);

					String contentTypeId = sModel.getContentTypeIdentifier();
					if (contentTypeId != null)
						fReconciler.setValidatorStrategy(createValidatorStrategy(contentTypeId));
				}
			}
			finally {
				if (sModel != null)
					sModel.releaseFromRead();
			}
		}
		return fReconciler;
	}
}
