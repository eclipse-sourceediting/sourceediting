/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.css.core.internal.text.rules.StructuredTextPartitionerForCSS;
import org.eclipse.wst.css.ui.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.css.ui.style.LineStyleProviderForEmbeddedCSS;
import org.eclipse.wst.html.core.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.internal.text.rules.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLContentAssistProcessor;
import org.eclipse.wst.html.ui.internal.contentassist.NoRegionContentAssistProcessorForHTML;
import org.eclipse.wst.html.ui.internal.hyperlink.URIHyperlinkDetector;
import org.eclipse.wst.html.ui.style.LineStyleProviderForHTML;
import org.eclipse.wst.html.ui.taginfo.HTMLBestMatchHoverProcessor;
import org.eclipse.wst.html.ui.taginfo.HTMLInformationProvider;
import org.eclipse.wst.html.ui.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.javascript.common.ui.internal.contentassist.JavaScriptContentAssistProcessor;
import org.eclipse.wst.javascript.common.ui.internal.style.LineStyleProviderForJavaScript;
import org.eclipse.wst.javascript.common.ui.internal.taginfo.JavaScriptBestMatchHoverProcessor;
import org.eclipse.wst.javascript.common.ui.internal.taginfo.JavaScriptInformationProvider;
import org.eclipse.wst.javascript.common.ui.internal.taginfo.JavaScriptTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.doubleclick.XMLDoubleClickStrategy;
import org.eclipse.wst.xml.ui.internal.autoedit.StructuredAutoEditStrategyXML;
import org.eclipse.wst.xml.ui.internal.correction.CorrectionProcessorXML;
import org.eclipse.wst.xml.ui.reconcile.StructuredTextReconcilingStrategyForMarkup;

public class StructuredTextViewerConfigurationHTML extends StructuredTextViewerConfiguration {

	InformationPresenter fInformationPresenter = null;

	private JavaSourceViewerConfiguration fJavaSourceViewerConfiguration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.StructuredTextViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public Map getAutoEditStrategies(ISourceViewer sourceViewer) {
		Map result = super.getAutoEditStrategies(sourceViewer);

		if (result.get(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML) == null)
			result.put(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, new ArrayList(1));
		if (result.get(StructuredTextPartitionerForHTML.ST_HTML_DECLARATION) == null)
			result.put(StructuredTextPartitionerForHTML.ST_HTML_DECLARATION, new ArrayList(1));

		IAutoEditStrategy autoEditStrategy = new StructuredAutoEditStrategyXML();
		List strategies = (List) result.get(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML);
		strategies.add(autoEditStrategy);
		strategies = (List) result.get(StructuredTextPartitionerForHTML.ST_HTML_DECLARATION);
		strategies.add(autoEditStrategy);

		return result;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			String[] htmlTypes = StructuredTextPartitionerForHTML.getConfiguredContentTypes();
			configuredContentTypes = new String[2 + xmlTypes.length + htmlTypes.length];

			configuredContentTypes[0] = StructuredTextPartitioner.ST_DEFAULT_PARTITION;
			configuredContentTypes[1] = StructuredTextPartitioner.ST_UNKNOWN_PARTITION;

			int index = 0;
			System.arraycopy(xmlTypes, 0, configuredContentTypes, index += 2, xmlTypes.length);
			System.arraycopy(htmlTypes, 0, configuredContentTypes, index += xmlTypes.length, htmlTypes.length);
		}

		return configuredContentTypes;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		IContentAssistant ca = super.getContentAssistant(sourceViewer);

		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant contentAssistant = (ContentAssistant) ca;

			IContentAssistProcessor htmlContentAssistProcessor = new HTMLContentAssistProcessor();
			IContentAssistProcessor jsContentAssistProcessor = new JavaScriptContentAssistProcessor();
			IContentAssistProcessor cssContentAssistProcessor = new CSSContentAssistProcessor();
			IContentAssistProcessor noRegionProcessorForHTML = new NoRegionContentAssistProcessorForHTML();

			// HTML
			setContentAssistProcessor(contentAssistant, htmlContentAssistProcessor, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML);
			setContentAssistProcessor(contentAssistant, htmlContentAssistProcessor, StructuredTextPartitionerForHTML.ST_HTML_COMMENT);

			// JavaScript
			setContentAssistProcessor(contentAssistant, jsContentAssistProcessor, StructuredTextPartitionerForHTML.ST_SCRIPT);

			// CSS
			setContentAssistProcessor(contentAssistant, cssContentAssistProcessor, StructuredTextPartitionerForCSS.ST_STYLE);

			// unknown
			setContentAssistProcessor(contentAssistant, noRegionProcessorForHTML, StructuredTextPartitioner.ST_UNKNOWN_PARTITION);
		}

		return ca;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), StructuredTextPartitionerForHTML.ST_DEFAULT_HTML);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new HTMLFormatProcessorImpl()));

		return formatter;
	}

	public IContentAssistant getCorrectionAssistant(ISourceViewer sourceViewer) {
		IContentAssistant ca = super.getCorrectionAssistant(sourceViewer);

		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant correctionAssistant = (ContentAssistant) ca;
			ITextEditor editor = getTextEditor();
			if (editor != null) {
				IContentAssistProcessor correctionProcessor = new CorrectionProcessorXML(editor);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML);
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
		if (contentType.compareTo(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML) == 0)
			// HTML
			return new XMLDoubleClickStrategy();
		else if (contentType.compareTo(StructuredTextPartitionerForHTML.ST_SCRIPT) == 0)
			// JavaScript
			return getJavaSourceViewerConfiguration(sourceViewer).getDoubleClickStrategy(sourceViewer, contentType);
		else
			return super.getDoubleClickStrategy(sourceViewer, contentType);
	}

	public IHighlighter getHighlighter(ISourceViewer sourceViewer) {
		IHighlighter highlighter = super.getHighlighter(sourceViewer);

		if (highlighter != null) {
			// HTML
			LineStyleProvider htmlLineStyleProvider = new LineStyleProviderForHTML();
			highlighter.addProvider(StructuredTextPartitionerForHTML.ST_DEFAULT_HTML, htmlLineStyleProvider);
			highlighter.addProvider(StructuredTextPartitionerForHTML.ST_HTML_COMMENT, htmlLineStyleProvider);
			highlighter.addProvider(StructuredTextPartitionerForHTML.ST_HTML_DECLARATION, htmlLineStyleProvider);

			// JavaScript
			LineStyleProvider jsLineStyleProvider = new LineStyleProviderForJavaScript();
			highlighter.addProvider(StructuredTextPartitionerForHTML.ST_SCRIPT, jsLineStyleProvider);

			// CSS
			LineStyleProvider cssLineStyleProvider = new LineStyleProviderForEmbeddedCSS();
			highlighter.addProvider(StructuredTextPartitionerForCSS.ST_STYLE, cssLineStyleProvider);
		}

		return highlighter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null || !fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;

		List allDetectors = new ArrayList(0);
		allDetectors.add(new URIHyperlinkDetector());

		IHyperlinkDetector[] superDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!allDetectors.contains(detector)) {
				allDetectors.add(detector);
			}
		}
		return (IHyperlinkDetector[]) allDetectors.toArray(new IHyperlinkDetector[0]);
	}

	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		if (fInformationPresenter == null) {
			fInformationPresenter = new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));

			// HTML
			IInformationProvider htmlInformationProvider = new HTMLInformationProvider();
			fInformationPresenter.setInformationProvider(htmlInformationProvider, StructuredTextPartitionerForHTML.ST_DEFAULT_HTML);

			// JavaScript
			IInformationProvider javascriptInformationProvider = new JavaScriptInformationProvider();
			fInformationPresenter.setInformationProvider(javascriptInformationProvider, StructuredTextPartitionerForHTML.ST_SCRIPT);

			fInformationPresenter.setSizeConstraints(60, 10, true, true);
		}

		return fInformationPresenter;
	}

	private JavaSourceViewerConfiguration getJavaSourceViewerConfiguration(ISourceViewer viewer) {
		if (fJavaSourceViewerConfiguration == null) {
			IPreferenceStore store = PreferenceConstants.getPreferenceStore();
			JavaTextTools javaTextTools = new JavaTextTools(store);
			fJavaSourceViewerConfiguration = new JavaSourceViewerConfiguration(javaTextTools.getColorManager(), store, getTextEditor(), getConfiguredDocumentPartitioning(viewer));
		}
		return fJavaSourceViewerConfiguration;
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconcilerg(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (fReconciler != null) {
			// a reconciler should always either be installed or disposed of
			if (!fReconciler.isInstalled()) {
				fReconciler = null;
			}
		}

		if (fReconciler == null) {
			// create one
			fReconciler = new StructuredRegionProcessor();
			fReconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		}

		boolean reconcilingEnabled = fPreferenceStore.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);

		if (!reconcilingEnabled)
			return fReconciler;

		if (fReconciler != null) {
			IDocument doc = ((StructuredTextEditor) editorPart).getDocumentProvider().getDocument(editorPart.getEditorInput());
			IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
			try {
				if (sModel != null) {

					String contentTypeId = sModel.getContentTypeIdentifier();

					IReconcilingStrategy markupStrategy = new StructuredTextReconcilingStrategyForMarkup((ITextEditor) editorPart);

					fReconciler.setReconcilingStrategy(markupStrategy, StructuredTextPartitioner.ST_DEFAULT_PARTITION);
					fReconciler.setReconcilingStrategy(markupStrategy, StructuredTextPartitionerForXML.ST_DEFAULT_XML);

					fReconciler.setDefaultStrategy(markupStrategy);

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

	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover hover = null;
		TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
		int i = 0;
		while (i < hoverDescs.length && hover == null) {
			if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
				String hoverType = hoverDescs[i].getId();
				if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType)) {
					// treat specially if it's JavaScript, HTML otherwise
					if (contentType.equals(StructuredTextPartitionerForHTML.ST_SCRIPT)) {
						hover = new JavaScriptBestMatchHoverProcessor();
					}
					else {
						hover = new HTMLBestMatchHoverProcessor();
					}
				}
				else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType)) {
					hover = new ProblemAnnotationHoverProcessor();
				}
				else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType)) {
					hover = new AnnotationHoverProcessor();
				}
				else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType)) {
					// treat specially if it's JavaScript, HTML otherwise
					if (contentType.equals(StructuredTextPartitionerForHTML.ST_SCRIPT)) {
						hover = new JavaScriptTagInfoHoverProcessor();
					}
					else {
						hover = new HTMLTagInfoHoverProcessor();
					}
				}
			}
			i++;
		}
		if (hover == null) {
			hover = super.getTextHover(sourceViewer, contentType, stateMask);
		}
		return hover;
	}

	public void unConfigure(ISourceViewer viewer) {
		super.unConfigure(viewer);

		// InformationPresenters
		if (fInformationPresenter != null)
			fInformationPresenter.uninstall();
	}
}