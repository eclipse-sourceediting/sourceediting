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
package org.eclipse.wst.html.ui.internal.provisional;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Preferences;
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
import org.eclipse.wst.css.core.internal.provisional.text.ICSSPartitionTypes;
import org.eclipse.wst.css.ui.internal.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.css.ui.internal.style.LineStyleProviderForEmbeddedCSS;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.core.internal.provisional.text.IHTMLPartitionTypes;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.ui.internal.autoedit.AutoEditStrategyForTabs;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLContentAssistProcessor;
import org.eclipse.wst.html.ui.internal.contentassist.NoRegionContentAssistProcessorForHTML;
import org.eclipse.wst.html.ui.internal.hyperlink.URIHyperlinkDetector;
import org.eclipse.wst.html.ui.internal.style.LineStyleProviderForHTML;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLBestMatchHoverProcessor;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLInformationProvider;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.javascript.ui.internal.common.contentassist.JavaScriptContentAssistProcessor;
import org.eclipse.wst.javascript.ui.internal.common.style.LineStyleProviderForJavaScript;
import org.eclipse.wst.javascript.ui.internal.common.taginfo.JavaScriptBestMatchHoverProcessor;
import org.eclipse.wst.javascript.ui.internal.common.taginfo.JavaScriptInformationProvider;
import org.eclipse.wst.javascript.ui.internal.common.taginfo.JavaScriptTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.provisional.style.IHighlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.internal.autoedit.StructuredAutoEditStrategyXML;
import org.eclipse.wst.xml.ui.internal.correction.CorrectionProcessorXML;
import org.eclipse.wst.xml.ui.internal.doubleclick.XMLDoubleClickStrategy;
import org.eclipse.wst.xml.ui.internal.validation.StructuredTextReconcilingStrategyForMarkup;

public class StructuredTextViewerConfigurationHTML extends StructuredTextViewerConfiguration {

	InformationPresenter fInformationPresenter = null;

	public StructuredTextViewerConfigurationHTML() {
		super();
	}

	public StructuredTextViewerConfigurationHTML(IPreferenceStore store) {
		super(store);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);

		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}
		
		if (contentType == IHTMLPartitionTypes.HTML_DEFAULT || contentType == IHTMLPartitionTypes.HTML_DECLARATION) {
			allStrategies.add(new StructuredAutoEditStrategyXML());
		}
		
		// be sure this is added last in list, so it has a change to modify 
		// previous results.
		// add auto edit strategy that handles when tab key is pressed
		allStrategies.add(new AutoEditStrategyForTabs());

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			String[] htmlTypes = StructuredTextPartitionerForHTML.getConfiguredContentTypes();
			configuredContentTypes = new String[2 + xmlTypes.length + htmlTypes.length];

			configuredContentTypes[0] = IStructuredPartitionTypes.DEFAULT_PARTITION;
			configuredContentTypes[1] = IStructuredPartitionTypes.UNKNOWN_PARTITION;

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
			setContentAssistProcessor(contentAssistant, htmlContentAssistProcessor, IHTMLPartitionTypes.HTML_DEFAULT);
			setContentAssistProcessor(contentAssistant, htmlContentAssistProcessor, IHTMLPartitionTypes.HTML_COMMENT);

			// JavaScript
			setContentAssistProcessor(contentAssistant, jsContentAssistProcessor, IHTMLPartitionTypes.SCRIPT);

			// CSS
			setContentAssistProcessor(contentAssistant, cssContentAssistProcessor, ICSSPartitionTypes.STYLE);

			// unknown
			setContentAssistProcessor(contentAssistant, noRegionProcessorForHTML, IStructuredPartitionTypes.UNKNOWN_PARTITION);
		}

		return ca;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IHTMLPartitionTypes.HTML_DEFAULT);

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
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IHTMLPartitionTypes.HTML_DEFAULT);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_CDATA);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_COMMENT);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_DECLARATION);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.XML_PI);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IXMLPartitions.DTD_SUBSET);
			}
		}

		return ca;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (contentType.compareTo(IHTMLPartitionTypes.HTML_DEFAULT) == 0)
			// HTML
			return new XMLDoubleClickStrategy();
		else
			return super.getDoubleClickStrategy(sourceViewer, contentType);
	}

	public IHighlighter getHighlighter(ISourceViewer sourceViewer) {
		IHighlighter highlighter = super.getHighlighter(sourceViewer);

		if (highlighter != null) {
			// HTML
			LineStyleProvider htmlLineStyleProvider = new LineStyleProviderForHTML();
			highlighter.addProvider(IHTMLPartitionTypes.HTML_DEFAULT, htmlLineStyleProvider);
			highlighter.addProvider(IHTMLPartitionTypes.HTML_COMMENT, htmlLineStyleProvider);
			highlighter.addProvider(IHTMLPartitionTypes.HTML_DECLARATION, htmlLineStyleProvider);

			// JavaScript
			LineStyleProvider jsLineStyleProvider = new LineStyleProviderForJavaScript();
			highlighter.addProvider(IHTMLPartitionTypes.SCRIPT, jsLineStyleProvider);

			// CSS
			LineStyleProvider cssLineStyleProvider = new LineStyleProviderForEmbeddedCSS();
			highlighter.addProvider(ICSSPartitionTypes.STYLE, cssLineStyleProvider);
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
			fInformationPresenter.setInformationProvider(htmlInformationProvider, IHTMLPartitionTypes.HTML_DEFAULT);

			// JavaScript
			IInformationProvider javascriptInformationProvider = new JavaScriptInformationProvider();
			fInformationPresenter.setInformationProvider(javascriptInformationProvider, IHTMLPartitionTypes.SCRIPT);

			fInformationPresenter.setSizeConstraints(60, 10, true, true);
			fInformationPresenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		}

		return fInformationPresenter;
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

					fReconciler.setReconcilingStrategy(markupStrategy, IStructuredPartitionTypes.DEFAULT_PARTITION);
					fReconciler.setReconcilingStrategy(markupStrategy, IXMLPartitions.XML_DEFAULT);

					fReconciler.setDefaultStrategy(markupStrategy);

					if (contentTypeId != null)
						fReconciler.setValidatorStrategy(createValidatorStrategy(contentTypeId));
				}
			} finally {
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
					// check if script or html is needed
					if (contentType == IHTMLPartitionTypes.SCRIPT) {
						hover = new JavaScriptBestMatchHoverProcessor();
					} else if (contentType == IHTMLPartitionTypes.HTML_DEFAULT) {
						hover = new HTMLBestMatchHoverProcessor();
					}
				} else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType)) {
					hover = new ProblemAnnotationHoverProcessor();
				} else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType)) {
					hover = new AnnotationHoverProcessor();
				} else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType)) {
					// check if script or html is needed
					if (contentType == IHTMLPartitionTypes.SCRIPT) {
						hover = new JavaScriptTagInfoHoverProcessor();
					} else if (contentType == IHTMLPartitionTypes.HTML_DEFAULT) {
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

	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		Vector vector = new Vector();

		// prefix[0] is either '\t' or ' ' x tabWidth, depending on preference
		Preferences preferences = HTMLCorePlugin.getDefault().getPluginPreferences();
		int indentationWidth = preferences.getInt(HTMLCorePreferenceNames.INDENTATION_SIZE);
		String indentCharPref = preferences.getString(HTMLCorePreferenceNames.INDENTATION_CHAR);
		boolean useSpaces = HTMLCorePreferenceNames.SPACE.equals(indentCharPref);

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