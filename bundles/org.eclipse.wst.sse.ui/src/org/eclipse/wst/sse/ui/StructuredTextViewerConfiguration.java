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
package org.eclipse.wst.sse.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.StructuredTextAnnotationHover;
import org.eclipse.wst.sse.ui.internal.contentassist.StructuredContentAssistant;
import org.eclipse.wst.sse.ui.internal.hyperlink.HighlighterHyperlinkPresenter;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.BestMatchHover;
import org.eclipse.wst.sse.ui.internal.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;


/**
 * Configuration for the source viewer used by StructuredTextEditor.<br />
 * Note: While ISourceViewer is passed in for each get configuration, clients
 * should create a new viewer configuration instance for each instance of
 * source viewer as some methods return the same instance of an object,
 * regardless of the sourceviewer.
 * <p>
 * Clients should subclass and override just those methods which must be
 * specific to their needs.
 * </p>
 * 
 * @see org.eclipse.wst.sse.ui.StructuredTextEditor
 * @see org.eclipse.wst.sse.ui.internal.StructuredTextViewer
 */
public class StructuredTextViewerConfiguration extends TextSourceViewerConfiguration implements IExecutableExtension {
	/*
	 * One instance per configuration because creating a second assistant that
	 * is added to a viewer can cause odd key-eating by the wrong one.
	 */
	private StructuredContentAssistant fContentAssistant = null;
	private IReconciler fReconciler;

	public StructuredTextViewerConfiguration() {
		super();
		// initialize fPreferenceStore with same preference store used in
		// StructuredTextEditor
		fPreferenceStore = createCombinedPreferenceStore();
	}

	/**
	 * Create a preference store that combines the source editor preferences
	 * with the base editor's preferences.
	 * 
	 * @return IPreferenceStore
	 */
	private IPreferenceStore createCombinedPreferenceStore() {
		IPreferenceStore sseEditorPrefs = SSEUIPlugin.getDefault().getPreferenceStore();
		IPreferenceStore baseEditorPrefs = EditorsUI.getPreferenceStore();
		return new ChainedPreferenceStore(new IPreferenceStore[]{sseEditorPrefs, baseEditorPrefs});
	}

	/**
	 * Returns the annotation hover which will provide the information to be
	 * shown in a hover popup window when requested for the given source
	 * viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return an annotation hover or <code>null</code> if no hover support
	 *         should be installed
	 */
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		/*
		 * This implmentation returns an annotation hover that works with
		 * StructuredTextViewer and breakpoints. Important! must remember to
		 * release it when done with it (during viewer.unconfigure)
		 */
		return new StructuredTextAnnotationHover();
	}

	/**
	 * Returns the configured partitioning for the given source viewer. The
	 * partitioning is used when the querying content types from the source
	 * viewer's input document.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return the configured partitioning
	 * @see #getConfiguredContentTypes(ISourceViewer)
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		/*
		 * This implementation returns default structured text partitioning
		 */
		return IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING;
	}

	public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer, String contentType) {
		/*
		 * This implementation returns configured text hover state masks for
		 * StructuredTextViewers
		 */
		TextHoverManager.TextHoverDescriptor[] hoverDescs = SSEUIPlugin.getDefault().getTextHoverManager().getTextHovers();
		int stateMasks[] = new int[hoverDescs.length];
		int stateMasksLength = 0;
		for (int i = 0; i < hoverDescs.length; i++) {
			if (hoverDescs[i].isEnabled()) {
				int j = 0;
				int stateMask = computeStateMask(hoverDescs[i].getModifierString());
				while (j < stateMasksLength) {
					if (stateMasks[j] == stateMask)
						break;
					j++;
				}
				if (j == stateMasksLength)
					stateMasks[stateMasksLength++] = stateMask;
			}
		}
		if (stateMasksLength == hoverDescs.length)
			return stateMasks;

		int[] shortenedStateMasks = new int[stateMasksLength];
		System.arraycopy(stateMasks, 0, shortenedStateMasks, 0, stateMasksLength);
		return shortenedStateMasks;
	}

	/**
	 * Returns the content assistant ready to be used with the given source
	 * viewer.<br />
	 * Note: The same instance of IContentAssistant is returned regardless of
	 * the source viewer passed in.
	 * <p>
	 * Clients overriding this method to add their own processors should call
	 * <code>super.getContentAssist()</code> to get the right content
	 * assistant and then call
	 * <code>((ContentAssistant)assistant).setContentAssistProcessor()</code>.
	 * </p>
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return a content assistant or <code>null</code> if content assist
	 *         should not be supported
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		/*
		 * Note: need to install content assistant so that content assist
		 * processors contributed via extension point will be picked up
		 */
		if (fContentAssistant == null) {
			fContentAssistant = new StructuredContentAssistant();

			// content assistant configurations
			fContentAssistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
			fContentAssistant.enableAutoActivation(true);
			fContentAssistant.setAutoActivationDelay(500);
			fContentAssistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
			fContentAssistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
			fContentAssistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		}
		return fContentAssistant;
	}

	/**
	 * Returns the hyperlink presenter for the given source viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return the hyperlink presenter or <code>null</code> if no hyperlink
	 *         support should be installed
	 * @since 3.1
	 */
	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
		/*
		 * This implementation returns a hyperlink presenter that uses
		 * Highlither instead of PresentationReconciler
		 */
		if (fPreferenceStore == null) {
			return super.getHyperlinkPresenter(sourceViewer);
		}
		return new HighlighterHyperlinkPresenter(fPreferenceStore);
	}

	/**
	 * Returns the line style providers that will be used for syntax
	 * highlighting in the given source viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @param partitionType
	 *            the partition type for which the lineStyleProviders are
	 *            applicable
	 * @return LineStyleProvders or null if should not be supported
	 */
	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		return null;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		/*
		 * This implementation returns null because StructuredTextViewer does
		 * not use presentation reconciler
		 */
		return null;
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover textHover = null;

		/*
		 * Returns a default problem, annotation, and best match hover
		 * depending on stateMask
		 */
		TextHoverManager.TextHoverDescriptor[] hoverDescs = SSEUIPlugin.getDefault().getTextHoverManager().getTextHovers();
		int i = 0;
		while (i < hoverDescs.length && textHover == null) {
			if (hoverDescs[i].isEnabled() && computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
				String hoverType = hoverDescs[i].getId();
				if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
					textHover = new ProblemAnnotationHoverProcessor();
				else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
					textHover = new AnnotationHoverProcessor();
				else if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
					textHover = new BestMatchHover(null);
			}
			i++;
		}
		return textHover;
	}

	/**
	 * Returns the undo manager for the given source viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return an undo manager or <code>null</code> if no undo/redo should
	 *         not be supported
	 */
	final public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
		/*
		 * This implementation returns an UndoManager that is used exclusively
		 * in StructuredTextViewer
		 */
		return new StructuredTextViewerUndoManager();
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		/*
		 * Currently no need for initialization data but is good practice to
		 * implement IExecutableExtension since is a class that can be created
		 * by executable extension
		 */
	}

	/**
	 * Returns the reconciler ready to be used with the given source viewer.<br />
	 * Note: The same instance of IReconciler is returned regardless of the
	 * source viewer passed in.
	 * <p>
	 * Clients cannot override this method. Instead, clients wanting to add
	 * their own reconciling strategy should use the
	 * <code>org.eclipse.wst.sse.ui.extensions.sourcevalidation</code>
	 * extension point.
	 * </p>
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return a reconciler
	 */
	final public IReconciler getReconciler(ISourceViewer sourceViewer) {
		boolean reconcilingEnabled = fPreferenceStore.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);
		if (sourceViewer == null || !reconcilingEnabled)
			return null;

		/*
		 * Only create reconciler if sourceviewer is present
		 */
		if (fReconciler == null && sourceViewer != null) {
			StructuredRegionProcessor reconciler = new StructuredRegionProcessor();

			// reconciler configurations

			reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

			fReconciler = reconciler;
		}
		return fReconciler;
	}
}
