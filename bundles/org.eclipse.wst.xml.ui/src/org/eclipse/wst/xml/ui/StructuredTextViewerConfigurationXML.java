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
package org.eclipse.wst.xml.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.dtd.ui.style.LineStyleProviderForDTDSubSet;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.StructuredTextReconciler;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;
import org.eclipse.wst.xml.core.format.FormatProcessorXML;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.contentassist.NoRegionContentAssistProcessor;
import org.eclipse.wst.xml.ui.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.doubleclick.XMLDoubleClickStrategy;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.autoedit.StructuredAutoEditStrategyXML;
import org.eclipse.wst.xml.ui.internal.correction.CorrectionProcessorXML;
import org.eclipse.wst.xml.ui.reconcile.StructuredTextReconcilingStrategyForContentModel;
import org.eclipse.wst.xml.ui.reconcile.StructuredTextReconcilingStrategyForMarkup;
import org.eclipse.wst.xml.ui.style.LineStyleProviderForXML;
import org.eclipse.wst.xml.ui.taginfo.XMLBestMatchHoverProcessor;
import org.eclipse.wst.xml.ui.taginfo.XMLInformationProvider;
import org.eclipse.wst.xml.ui.taginfo.XMLTagInfoHoverProcessor;

public class StructuredTextViewerConfigurationXML extends StructuredTextViewerConfiguration {
	InformationPresenter fInformationPresenter = null;
	private boolean reconcilerStrategiesAreSet;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public Map getAutoEditStrategies(ISourceViewer sourceViewer) {
		Map result = super.getAutoEditStrategies(sourceViewer);

		if (result.get(StructuredTextPartitionerForXML.ST_DEFAULT_XML) == null)
			result.put(StructuredTextPartitionerForXML.ST_DEFAULT_XML, new ArrayList(1));
		List strategies = (List) result.get(StructuredTextPartitionerForXML.ST_DEFAULT_XML);
		strategies.add(new StructuredAutoEditStrategyXML());
		return result;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		if (configuredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			configuredContentTypes = new String[xmlTypes.length + 2];
			configuredContentTypes[0] = StructuredTextPartitioner.ST_DEFAULT_PARTITION;
			configuredContentTypes[1] = StructuredTextPartitioner.ST_UNKNOWN_PARTITION;
			int index = 0;
			System.arraycopy(xmlTypes, 0, configuredContentTypes, index += 2, xmlTypes.length);
		}
		return configuredContentTypes;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		IContentAssistant ca = super.getContentAssistant(sourceViewer);
		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant contentAssistant = (ContentAssistant) ca;
			IContentAssistProcessor xmlContentAssistProcessor = new XMLContentAssistProcessor();
			IContentAssistProcessor noRegionProcessor = new NoRegionContentAssistProcessor();
			addContentAssistProcessor(contentAssistant, xmlContentAssistProcessor, StructuredTextPartitioner.ST_DEFAULT_PARTITION);
			addContentAssistProcessor(contentAssistant, xmlContentAssistProcessor, StructuredTextPartitionerForXML.ST_DEFAULT_XML);
			addContentAssistProcessor(contentAssistant, noRegionProcessor, StructuredTextPartitioner.ST_UNKNOWN_PARTITION);
			// contentAssistant.setContentAssistProcessor(xmlContentAssistProcessor,
			// StructuredTextPartitioner.ST_DEFAULT_PARTITION);
			// contentAssistant.setContentAssistProcessor(xmlContentAssistProcessor,
			// StructuredTextPartitionerForXML.ST_DEFAULT_XML);
			// contentAssistant.setContentAssistProcessor(noRegionProcessor,
			// StructuredTextPartitioner.ST_UNKNOWN_PARTITION);
		}
		return ca;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), StructuredTextPartitionerForXML.ST_DEFAULT_XML);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new FormatProcessorXML()));

		return formatter;
	}

	public IContentAssistant getCorrectionAssistant(ISourceViewer sourceViewer) {
		IContentAssistant ca = super.getCorrectionAssistant(sourceViewer);

		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant correctionAssistant = (ContentAssistant) ca;
			ITextEditor editor = getTextEditor();
			if (editor != null) {
				IContentAssistProcessor correctionProcessor = new CorrectionProcessorXML(editor);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, StructuredTextPartitionerForXML.ST_DEFAULT_XML);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, StructuredTextPartitionerForXML.ST_XML_CDATA);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, StructuredTextPartitionerForXML.ST_XML_COMMENT);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, StructuredTextPartitionerForXML.ST_XML_DECLARATION);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, StructuredTextPartitionerForXML.ST_XML_PI);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, StructuredTextPartitionerForXML.ST_DTD_SUBSET);
			}
		}
		return ca;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {

		ITextDoubleClickStrategy doubleClickStrategy = null;
		if (contentType.compareTo(StructuredTextPartitionerForXML.ST_DEFAULT_XML) == 0)
			doubleClickStrategy = new XMLDoubleClickStrategy();
		else
			doubleClickStrategy = super.getDoubleClickStrategy(sourceViewer, contentType);
		return doubleClickStrategy;
	}

	public IHighlighter getHighlighter(ISourceViewer sourceViewer) {

		IHighlighter highlighter = super.getHighlighter(sourceViewer);
		if (highlighter != null) {
			LineStyleProvider xmlProvider = new LineStyleProviderForXML();
			highlighter.addProvider(StructuredTextPartitionerForXML.ST_DEFAULT_XML, xmlProvider);
			highlighter.addProvider(StructuredTextPartitionerForXML.ST_XML_CDATA, xmlProvider);
			highlighter.addProvider(StructuredTextPartitionerForXML.ST_XML_COMMENT, xmlProvider);
			highlighter.addProvider(StructuredTextPartitionerForXML.ST_XML_DECLARATION, xmlProvider);
			highlighter.addProvider(StructuredTextPartitionerForXML.ST_XML_PI, xmlProvider);
			highlighter.addProvider(StructuredTextPartitionerForXML.ST_DTD_SUBSET, new LineStyleProviderForDTDSubSet(getConfiguredDocumentPartitioning(sourceViewer)));
		}
		return highlighter;
	}

	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {

		if (fInformationPresenter == null) {
			fInformationPresenter = new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));
			IInformationProvider xmlInformationProvider = new XMLInformationProvider();
			fInformationPresenter.setInformationProvider(xmlInformationProvider, StructuredTextPartitioner.ST_DEFAULT_PARTITION);
			fInformationPresenter.setInformationProvider(xmlInformationProvider, StructuredTextPartitionerForXML.ST_DEFAULT_XML);
			fInformationPresenter.setSizeConstraints(60, 10, true, true);
		}
		return fInformationPresenter;
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {

		if (fReconciler != null) {
			// a reconciler should always be installed or disposed of
			if (!fReconciler.isInstalled()) {
				fReconciler = null;
				reconcilerStrategiesAreSet = false;
			}
		}

		// the first time running through, there's no model (so no pref store)
		// but the reconciler still needs to be created so that its document
		// gets set
		if (fReconciler == null) {
			// create one
			fReconciler = new StructuredTextReconciler();
			// a null editorPart is valid
			// fReconciler.setEditor(editorPart);
		}

		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		boolean reconcilingEnabled = store.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);

		// the second time through, the strategies are set
		if (fReconciler != null && !reconcilerStrategiesAreSet && reconcilingEnabled) {
//			StructuredTextViewer viewer = null;
//			if (sourceViewer instanceof StructuredTextViewer) {
//				viewer = ((StructuredTextViewer) sourceViewer);
//			}
			IDocument doc = ((StructuredTextEditor)editorPart).getDocumentProvider().getDocument(editorPart.getEditorInput());
			IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
			//IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
			try {

				if (sModel != null) {
					String validationMethodPref = XMLUIPlugin.getDefault().getPreferenceStore().getString(CommonEditorPreferenceNames.EDITOR_VALIDATION_METHOD);
					IReconcilingStrategy defaultStrategy = null;

					// pref set to no validation, so return
					if (validationMethodPref.equals(CommonEditorPreferenceNames.EDITOR_VALIDATION_NONE))
						return fReconciler;

					// content model is the default for xml..
					// "Content Model" strategies (requires propagating
					// adapter
					// from AdapterFactoryProviderFor*)

					IReconcilingStrategy markupStrategy = new StructuredTextReconcilingStrategyForMarkup((ITextEditor) editorPart);
					IReconcilingStrategy xmlStrategy = new StructuredTextReconcilingStrategyForContentModel((ITextEditor) editorPart);
					defaultStrategy = xmlStrategy;
					fReconciler.setReconcilingStrategy(markupStrategy, StructuredTextPartitioner.ST_DEFAULT_PARTITION);
					fReconciler.setReconcilingStrategy(xmlStrategy, StructuredTextPartitionerForXML.ST_DEFAULT_XML);
					fReconciler.setDefaultStrategy(defaultStrategy);

					reconcilerStrategiesAreSet = true;
				}
			} finally {
				if (sModel != null)
					sModel.releaseFromRead();
			}
		}
		return fReconciler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String, int)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		// look for appropriate text hover processor to return based on
		// content type and state mask
		if ((contentType == StructuredTextPartitioner.ST_DEFAULT_PARTITION) || (contentType == StructuredTextPartitionerForXML.ST_DEFAULT_XML)) {
			// check which of xml's text hover is handling stateMask
			TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
			int i = 0;
			while (i < hoverDescs.length) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
						return new XMLBestMatchHoverProcessor();
					else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						return new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						return new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
						return new XMLTagInfoHoverProcessor();
				}
				i++;
			}
		}
		return super.getTextHover(sourceViewer, contentType, stateMask);
	}

	public void unConfigure(ISourceViewer viewer) {

		super.unConfigure(viewer);

		// InformationPresenters
		if (fInformationPresenter != null) {
			fInformationPresenter.uninstall();
		}
	}
}
