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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.contentassist.IResourceDependentProcessor;
import org.eclipse.wst.sse.ui.extension.IExtendedConfiguration;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.editor.HTMLTextPresenter;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessorExtension;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorBuilder;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorMetaData;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorStrategy;
import org.eclipse.wst.sse.ui.style.Highlighter;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;


/**
 * Configuration for a TextViewer that shows Structured Text (XML)
 */
public class StructuredTextViewerConfiguration extends SourceViewerConfiguration implements IExtendedConfiguration {

	public static final String ID = "textviewerconfiguration"; //$NON-NLS-1$
	protected String[] configuredContentTypes;
	protected IEditorPart editorPart;
	private IAnnotationHover fAnnotationHover = null;
	protected IContentAssistant fContentAssistant;
	private List fContentAssistProcessors = null;
	protected IContentAssistant fCorrectionAssistant;
	private String fDeclaringID;
	protected IHighlighter fHighlighter;
	//protected StructuredRegionProcessor fReconciler;
    protected StructuredRegionProcessorExtension fReconciler;
	protected IResource fResource = null;
	protected final String SSE_EDITOR_ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$
	protected final String SSE_MODEL_ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 */
	public StructuredTextViewerConfiguration() {
		super();
		fContentAssistProcessors = new ArrayList();
	}

	/**
	 * use this constructor to have reconciler
	 */
	public StructuredTextViewerConfiguration(IEditorPart textEditor) {
		this();
		editorPart = textEditor;
	}

	/**
	 * Content assist processors should be added to the ContentAssistant via
	 * this method so that they are initialized/released properly.
	 * 
	 * @param ca
	 * @param processor
	 * @param partitionType
	 */
	public void addContentAssistProcessor(ContentAssistant ca, IContentAssistProcessor processor, String partitionType) {
		// save for reinit and release
		fContentAssistProcessors.add(processor);
		if (processor instanceof IResourceDependentProcessor)
			((IResourceDependentProcessor) processor).initialize(fResource);
		ca.setContentAssistProcessor(processor, partitionType);
	}

	public void configureOn(IResource resource) {
		fResource = resource;
		updateForResource();
	}

	protected ValidatorStrategy createValidatorStrategy(String contentTypeId) {
		ValidatorStrategy validatorStrategy = new ValidatorStrategy((ITextEditor) editorPart, contentTypeId);
		ValidatorBuilder vBuilder = new ValidatorBuilder();
		ValidatorMetaData[] vmds = vBuilder.getValidatorMetaData("org.eclipse.wst.sse.ui"); //$NON-NLS-1$
		for (int i = 0; i < vmds.length; i++) {
			if (vmds[i].canHandleContentType(contentTypeId))
				validatorStrategy.addValidatorMetaData(vmds[i]);
		}
		return validatorStrategy;
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
		if (fAnnotationHover == null) {
			fAnnotationHover = new StructuredTextAnnotationHover();
		}
		return fAnnotationHover;
	}

	/**
	 * @param sourceViewer
	 * @return a Map of partition content types to java.util.List of
	 *         IAutoEditStrategy objects
	 */
	public Map getAutoEditStrategies(ISourceViewer sourceViewer) {
		return new HashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return IStructuredDocument.DEFAULT_STRUCTURED_PARTITIONING;
	}

	/*
	 * @see SourceViewerConfiguration#getConfiguredTextHoverStateMasks(ISourceViewer,
	 *      String)
	 * @since 2.1
	 */
	public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer, String contentType) {
		// content type does not matter when getting hover state mask
		TextHoverManager.TextHoverDescriptor[] hoverDescs = SSEUIPlugin.getDefault().getTextHoverManager().getTextHovers();
		int stateMasks[] = new int[hoverDescs.length];
		int stateMasksLength = 0;
		for (int i = 0; i < hoverDescs.length; i++) {
			if (hoverDescs[i].isEnabled()) {
				int j = 0;
				int stateMask = EditorUtility.computeStateMask(hoverDescs[i].getModifierString());
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
	 * @see ISourceViewerConfiguration#getContentAssistant
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (fContentAssistant == null) {
			// Ensure that only one assistant is ever returned. Creating a
			// second assistant
			// that is added to a viewer can cause odd key-eating by the wrong
			// one.
			ContentAssistant assistant = new ContentAssistant();
			// content assistant configurations
			assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
			assistant.enableAutoActivation(true);
			assistant.setAutoActivationDelay(500);
			assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
			assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
			fContentAssistant = assistant;
		}
		updateForResource();
		return fContentAssistant;
	}

	public IContentAssistant getCorrectionAssistant(ISourceViewer sourceViewer) {
		if (fCorrectionAssistant == null) {
			// Ensure that only one assistant is ever returned. Creating a
			// second assistant
			// that is added to a viewer can cause odd key-eating by the wrong
			// one.
			ContentAssistant assistant = new ContentAssistant();
			assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
			fCorrectionAssistant = assistant;
		}
		updateForResource();
		return fCorrectionAssistant;
	}

	/**
	 * @return Returns the declaringID.
	 */
	public String getDeclaringID() {
		return fDeclaringID;
	}

	/**
	 * @return Returns the editorPart.
	 */
	public IEditorPart getEditorPart() {
		return editorPart;
	}

	public IHighlighter getHighlighter(ISourceViewer viewer) {
		/*
		 * Assuming for now that only one highlighter is needed per
		 * configuration, and that its just configured for lots of different
		 * content types. In the future, this may change, if its tied closer
		 * to the actual content type (for example, made specifc for HTML vs.
		 * XML). I think it would be little impact to create a new instance
		 * each time.
		 */
		if (fHighlighter != null) {
			fHighlighter.uninstall();
		}
		else {
			Highlighter highlighter = new Highlighter();
			highlighter.setDocumentPartitioning(getConfiguredDocumentPartitioning(viewer));
			fHighlighter = highlighter;
		}
		// Allow viewer to be null for easier unit testing, but during normal
		// use, would not be null
		if (viewer != null) {
			IStructuredDocument document = (IStructuredDocument) viewer.getDocument();
			fHighlighter.setDocument(document);
		}
		return fHighlighter;
	}

	/*
	 * @see SourceViewerConfiguration#getHoverControlCreator(ISourceViewer)
	 */
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {

		return getInformationControlCreator(sourceViewer, false);
	}

	private IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer, final boolean cutDown) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				// int style= cutDown ? SWT.NONE : (SWT.V_SCROLL |
				// SWT.H_SCROLL);
				int style = SWT.NONE;
				return new DefaultInformationControl(parent, style, new HTMLTextPresenter(cutDown));
			}
		};
	}

	/**
	 * Returns the information presenter control creator. The creator is a
	 * factory creating the presenter controls for the given source viewer.
	 * This implementation always returns a creator for
	 * <code>DefaultInformationControl</code> instances. (Copied from
	 * JavaSourceViewerConfiguration)
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return an information control creator
	 */
	protected IInformationControlCreator getInformationPresenterControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE;
				int style = SWT.V_SCROLL | SWT.H_SCROLL;
				return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getOverviewRulerAnnotationHover(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IAnnotationHover getOverviewRulerAnnotationHover(ISourceViewer arg0) {
		return new StructuredTextAnnotationHover();
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTabWidth(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public int getTabWidth(ISourceViewer sourceViewer) {
		return SSEUIPlugin.getDefault().getPreferenceStore().getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
	}

	protected ITextEditor getTextEditor() {
		ITextEditor editor = null;
		if (editorPart instanceof ITextEditor)
			editor = (ITextEditor) editorPart;
		if (editor == null && editorPart != null)
			editor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
		return editor;
	}

	/**
	 * Returns the text hovers available in StructuredTextEditors
	 * 
	 * @return TextHoverManager.TextHoverDescriptor[]
	 */
	protected TextHoverManager.TextHoverDescriptor[] getTextHovers() {
		return SSEUIPlugin.getDefault().getTextHoverManager().getTextHovers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getUndoManager(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
		return new StructuredTextViewerUndoManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extension.IExtendedConfiguration#setDeclaringID(java.lang.String)
	 */
	public void setDeclaringID(String targetID) {
		fDeclaringID = targetID;
	}

	/**
	 * @param editorPart
	 *            The editorPart to set.
	 */
	public void setEditorPart(IEditorPart editorPart) {
		this.editorPart = editorPart;
	}

	/**
	 * This method is allow any cleanup to take place that is not otherwise
	 * done in the viewer's unConfigure method. In some cases, things may be
	 * done "twice" ... so uninstall, release, etc., should be prepared.
	 */
	public void unConfigure(ISourceViewer viewer) {
		editorPart = null;
		// If there're any processors we're hanging on to, be sure they have a
		// chance to clean themselves up.
		if (fHighlighter != null) {
			fHighlighter.uninstall();
		}
		if (fContentAssistant != null) {
			fContentAssistant.uninstall();
		}
		if (fReconciler != null) {
			fReconciler.uninstall();
		}
		if (fContentAssistant != null) {
			unconfigureContentAssistProcessors();
		}
		if (fAnnotationHover != null && fAnnotationHover instanceof IReleasable) {
			((IReleasable) fAnnotationHover).release();
		}
	}

	/**
	 * 
	 */
	private void unconfigureContentAssistProcessors() {
		if (!fContentAssistProcessors.isEmpty()) {
			Iterator it = fContentAssistProcessors.iterator();
			IContentAssistProcessor p = null;
			while (it.hasNext()) {
				p = (IContentAssistProcessor) it.next();
				if (p instanceof IReleasable)
					((IReleasable) p).release();
			}
		}
	}

	protected void updateForResource() {
		if (!fContentAssistProcessors.isEmpty()) {
			Iterator it = fContentAssistProcessors.iterator();
			IContentAssistProcessor p = null;
			while (it.hasNext()) {
				p = (IContentAssistProcessor) it.next();
				if (p instanceof IResourceDependentProcessor)
					((IResourceDependentProcessor) p).initialize(fResource);
			}
		}
	}
}
