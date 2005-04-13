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
package org.eclipse.jst.jsp.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IJavaPartitions;
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
import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.jst.jsp.core.text.IJSPPartitionTypes;
import org.eclipse.jst.jsp.ui.internal.autoedit.StructuredAutoEditStrategyJSP;
import org.eclipse.jst.jsp.ui.internal.contentassist.JSPContentAssistProcessor;
import org.eclipse.jst.jsp.ui.internal.contentassist.JSPJavaContentAssistProcessor;
import org.eclipse.jst.jsp.ui.internal.contentassist.NoRegionContentAssistProcessorForJSP;
import org.eclipse.jst.jsp.ui.internal.correction.CorrectionProcessorJSP;
import org.eclipse.jst.jsp.ui.internal.format.FormattingStrategyJSPJava;
import org.eclipse.jst.jsp.ui.internal.hyperlink.JSPJavaHyperlinkDetector;
import org.eclipse.jst.jsp.ui.internal.hyperlink.TaglibHyperlinkDetector;
import org.eclipse.jst.jsp.ui.internal.hyperlink.URIHyperlinkDetector;
import org.eclipse.jst.jsp.ui.internal.reconcile.StructuredTextReconcilingStrategyForJSP;
import org.eclipse.jst.jsp.ui.internal.style.LineStyleProviderForJSP;
import org.eclipse.jst.jsp.ui.internal.style.java.LineStyleProviderForJava;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPBestMatchHoverProcessor;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPJavaBestMatchHoverProcessor;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPJavaJavadocHoverProcessor;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPTagInfoHoverProcessor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.css.core.text.ICSSPartitionTypes;
import org.eclipse.wst.css.ui.internal.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.css.ui.internal.style.LineStyleProviderForEmbeddedCSS;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.core.text.IHTMLPartitionTypes;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLContentAssistProcessor;
import org.eclipse.wst.html.ui.internal.style.LineStyleProviderForHTML;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLBestMatchHoverProcessor;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLInformationProvider;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.javascript.ui.internal.common.contentassist.JavaScriptContentAssistProcessor;
import org.eclipse.wst.javascript.ui.internal.common.style.LineStyleProviderForJavaScript;
import org.eclipse.wst.javascript.ui.internal.common.taginfo.JavaScriptBestMatchHoverProcessor;
import org.eclipse.wst.javascript.ui.internal.common.taginfo.JavaScriptInformationProvider;
import org.eclipse.wst.javascript.ui.internal.common.taginfo.JavaScriptTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.internal.correction.CorrectionProcessorXML;
import org.eclipse.wst.xml.ui.internal.doubleclick.XMLDoubleClickStrategy;
import org.eclipse.wst.xml.ui.internal.style.LineStyleProviderForXML;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLBestMatchHoverProcessor;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLInformationProvider;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLTagInfoHoverProcessor;
import org.eclipse.wst.xml.ui.reconcile.StructuredTextReconcilingStrategyForMarkup;

public class StructuredTextViewerConfigurationJSP extends StructuredTextViewerConfiguration {

	InformationPresenter fInformationPresenter = null;

	private JavaSourceViewerConfiguration fJavaSourceViewerConfiguration;

	public StructuredTextViewerConfigurationJSP() {
		super();
	}

	public StructuredTextViewerConfigurationJSP(IPreferenceStore store) {
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

		if (contentType == IJSPPartitionTypes.JSP_CONTENT_JAVA) {
			allStrategies.add(getJavaSourceViewerConfiguration().getAutoEditStrategies(sourceViewer, IJavaPartitions.JAVA_PARTITIONING)[0]);
		}

		if (contentType == IHTMLPartitionTypes.HTML_DEFAULT || contentType == IHTMLPartitionTypes.HTML_DECLARATION) {
			allStrategies.add(new StructuredAutoEditStrategyJSP());
		}

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[0]);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			String[] htmlTypes = StructuredTextPartitionerForHTML.getConfiguredContentTypes();
			String[] jspTypes = StructuredTextPartitionerForJSP.getConfiguredContentTypes();
			configuredContentTypes = new String[2 + xmlTypes.length + htmlTypes.length + jspTypes.length];

			configuredContentTypes[0] = IStructuredPartitionTypes.DEFAULT_PARTITION;
			configuredContentTypes[1] = IStructuredPartitionTypes.UNKNOWN_PARTITION;

			int index = 0;
			System.arraycopy(xmlTypes, 0, configuredContentTypes, index += 2, xmlTypes.length);
			System.arraycopy(htmlTypes, 0, configuredContentTypes, index += xmlTypes.length, htmlTypes.length);
			System.arraycopy(jspTypes, 0, configuredContentTypes, index += htmlTypes.length, jspTypes.length);
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
			IContentAssistProcessor jspContentAssistProcessor = new JSPContentAssistProcessor();
			IContentAssistProcessor jspJavaContentAssistProcessor = new JSPJavaContentAssistProcessor();
			IContentAssistProcessor noRegionProcessorJsp = new NoRegionContentAssistProcessorForJSP();

			// HTML
			setContentAssistProcessor(contentAssistant, htmlContentAssistProcessor, IHTMLPartitionTypes.HTML_DEFAULT);
			setContentAssistProcessor(contentAssistant, htmlContentAssistProcessor, IHTMLPartitionTypes.HTML_COMMENT);

			// HTML JavaScript
			setContentAssistProcessor(contentAssistant, jsContentAssistProcessor, IHTMLPartitionTypes.SCRIPT);

			// CSS
			setContentAssistProcessor(contentAssistant, cssContentAssistProcessor, ICSSPartitionTypes.STYLE);
			setContentAssistProcessor(contentAssistant, cssContentAssistProcessor, ICSSPartitionTypes.STYLE);

			// JSP
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IStructuredPartitionTypes.DEFAULT_PARTITION);
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IXMLPartitions.XML_DEFAULT);
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IHTMLPartitionTypes.HTML_DEFAULT);
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IHTMLPartitionTypes.HTML_COMMENT);
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IJSPPartitionTypes.JSP_DEFAULT);

			// JSP directives
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IJSPPartitionTypes.JSP_DIRECTIVE);
			// JSP delimiters
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IJSPPartitionTypes.JSP_CONTENT_DELIMITER);
			// JSP JavaScript
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IJSPPartitionTypes.JSP_CONTENT_JAVASCRIPT);
			// JSP Java
			setContentAssistProcessor(contentAssistant, jspJavaContentAssistProcessor, IJSPPartitionTypes.JSP_CONTENT_JAVA);
			// unknown
			setContentAssistProcessor(contentAssistant, noRegionProcessorJsp, IStructuredPartitionTypes.UNKNOWN_PARTITION);
			// CMVC 269718
			// JSP COMMENT
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IJSPPartitionTypes.JSP_COMMENT);
		}

		return ca;
	}

	public IContentAssistant getCorrectionAssistant(ISourceViewer sourceViewer) {
		IContentAssistant ca = super.getCorrectionAssistant(sourceViewer);

		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant correctionAssistant = (ContentAssistant) ca;
			ITextEditor editor = getTextEditor();
			if (editor != null) {
				IContentAssistProcessor correctionProcessor = new CorrectionProcessorXML(editor);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IHTMLPartitionTypes.HTML_DEFAULT);

				correctionProcessor = new CorrectionProcessorJSP(editor);
				correctionAssistant.setContentAssistProcessor(correctionProcessor, IJSPPartitionTypes.JSP_CONTENT_JAVA);
			}
		}

		return ca;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IXMLPartitions.XML_DEFAULT);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new HTMLFormatProcessorImpl()));
		formatter.setSlaveStrategy(new FormattingStrategyJSPJava(), IJSPPartitionTypes.JSP_CONTENT_JAVA);

		return formatter;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (contentType.compareTo(IHTMLPartitionTypes.HTML_DEFAULT) == 0)
			// HTML
			return new XMLDoubleClickStrategy();
		else if (contentType.compareTo(IHTMLPartitionTypes.SCRIPT) == 0 || contentType.compareTo(IJSPPartitionTypes.JSP_CONTENT_JAVA) == 0 || contentType.compareTo(IJSPPartitionTypes.JSP_CONTENT_JAVASCRIPT) == 0)
			// HTML JavaScript
			// JSP Java or JSP JavaScript
			return getJavaSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, contentType);
		else if (contentType.compareTo(IJSPPartitionTypes.JSP_DEFAULT) == 0)
			// JSP
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

			// HTML JavaScript
			LineStyleProvider jsLineStyleProvider = new LineStyleProviderForJavaScript();
			highlighter.addProvider(IHTMLPartitionTypes.SCRIPT, jsLineStyleProvider);

			// CSS
			LineStyleProvider cssLineStyleProvider = new LineStyleProviderForEmbeddedCSS();
			highlighter.addProvider(ICSSPartitionTypes.STYLE, cssLineStyleProvider);

			// JSP
			LineStyleProvider jspLineStyleProvider = new LineStyleProviderForJSP();
			highlighter.addProvider(IJSPPartitionTypes.JSP_DEFAULT, jspLineStyleProvider);
			highlighter.addProvider(IJSPPartitionTypes.JSP_COMMENT, jspLineStyleProvider);
			highlighter.addProvider(IJSPPartitionTypes.JSP_DIRECTIVE, jspLineStyleProvider);
			highlighter.addProvider(IJSPPartitionTypes.JSP_CONTENT_DELIMITER, jspLineStyleProvider);

			// XML
			LineStyleProviderForXML xmlLineStyleProvider = new LineStyleProviderForXML();
			highlighter.addProvider(IXMLPartitions.XML_DEFAULT, xmlLineStyleProvider);
			highlighter.addProvider(IXMLPartitions.XML_COMMENT, xmlLineStyleProvider);
			highlighter.addProvider(IXMLPartitions.XML_CDATA, xmlLineStyleProvider);
			highlighter.addProvider(IXMLPartitions.XML_DECLARATION, xmlLineStyleProvider);
			highlighter.addProvider(IXMLPartitions.XML_PI, xmlLineStyleProvider);


			// JSP Java or JSP JavaScript
			highlighter.addProvider(IJSPPartitionTypes.JSP_CONTENT_JAVA, new LineStyleProviderForJava());
			highlighter.addProvider(IJSPPartitionTypes.JSP_CONTENT_JAVASCRIPT, new LineStyleProviderForJavaScript());
		}

		return highlighter;
	}

	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		if (fInformationPresenter == null) {
			fInformationPresenter = new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));

			// HTML
			IInformationProvider htmlInformationProvider = new HTMLInformationProvider();
			fInformationPresenter.setInformationProvider(htmlInformationProvider, IHTMLPartitionTypes.HTML_DEFAULT);

			// HTML JavaScript
			IInformationProvider javascriptInformationProvider = new JavaScriptInformationProvider();
			fInformationPresenter.setInformationProvider(javascriptInformationProvider, IHTMLPartitionTypes.SCRIPT);

			// XML
			IInformationProvider xmlInformationProvider = new XMLInformationProvider();
			fInformationPresenter.setInformationProvider(xmlInformationProvider, IXMLPartitions.XML_DEFAULT);

			fInformationPresenter.setSizeConstraints(60, 10, true, true);
		}
		return fInformationPresenter;
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		// html
		if (contentType == IHTMLPartitionTypes.HTML_DEFAULT) {
			TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
			int i = 0;
			while (i < hoverDescs.length) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
						return new HTMLBestMatchHoverProcessor();
					else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						return new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						return new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
						return new HTMLTagInfoHoverProcessor();
				}
				i++;
			}
		} else if (contentType == IHTMLPartitionTypes.SCRIPT) {
			// HTML JavaScript
			TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
			int i = 0;
			while (i < hoverDescs.length) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
						return new JavaScriptBestMatchHoverProcessor();
					else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						return new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						return new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
						return new JavaScriptTagInfoHoverProcessor();
				}
				i++;
			}
		} else if ((contentType == IJSPPartitionTypes.JSP_DEFAULT) || (contentType == IJSPPartitionTypes.JSP_DIRECTIVE)) {
			// JSP
			TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
			int i = 0;
			while (i < hoverDescs.length) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
						return new JSPBestMatchHoverProcessor();
					else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						return new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						return new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
						return new JSPTagInfoHoverProcessor();
				}
				i++;
			}
		} else if (contentType == IJSPPartitionTypes.JSP_CONTENT_JAVA) {
			// JSP Java
			TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
			int i = 0;
			while (i < hoverDescs.length) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType)) {
						JSPJavaBestMatchHoverProcessor hover = new JSPJavaBestMatchHoverProcessor();
						return hover;
					} else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						return new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						return new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType)) {
						JSPJavaJavadocHoverProcessor hover = new JSPJavaJavadocHoverProcessor();
						return hover;
					}
				}
				i++;
			}
		} else if (contentType == IXMLPartitions.XML_DEFAULT) {
			// XML
			TextHoverManager.TextHoverDescriptor[] hoverDescs = SSEUIPlugin.getDefault().getTextHoverManager().getTextHovers();
			int i = 0;
			while (i < hoverDescs.length) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType)) {
						XMLBestMatchHoverProcessor hover = new XMLBestMatchHoverProcessor();
						// hover.setEditor(getEditorPart());
						return hover;
					} else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						return new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						return new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType)) {
						XMLTagInfoHoverProcessor hover = new XMLTagInfoHoverProcessor();
						// hover.setEditor(getEditorPart());
						return hover;
					}
				}
				i++;
			}
		}
		return super.getTextHover(sourceViewer, contentType, stateMask);
	}

	public void unConfigure(ISourceViewer viewer) {
		super.unConfigure(viewer);

		// InformationPresenters
		if (fInformationPresenter != null)
			fInformationPresenter.uninstall();
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {

		if (fPreferenceStore == null)
			return null;

		if (fReconciler != null) {
			// a reconciler should always be installed or disposed of
			if (!fReconciler.isInstalled()) {
				fReconciler = null;
			}
		}

		// the first time running through, there's no model (so no pref store)
		// but the reconciler still needs to be created so that its document
		// gets set
		if (fReconciler == null) {
			// create one
			fReconciler = new StructuredRegionProcessor();
			fReconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		}

		boolean reconcilingEnabled = fPreferenceStore.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);

		if (!reconcilingEnabled)
			return fReconciler;

		if (fReconciler != null) {
			IDocument doc = ((ITextEditor) editorPart).getDocumentProvider().getDocument(editorPart.getEditorInput());
			IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
			try {
				if (sModel != null) {

					IReconcilingStrategy markupStrategy = new StructuredTextReconcilingStrategyForMarkup((ITextEditor) editorPart);
					IReconcilingStrategy jspStrategy = new StructuredTextReconcilingStrategyForJSP((ITextEditor) editorPart);

					fReconciler.setReconcilingStrategy(markupStrategy, IStructuredPartitionTypes.DEFAULT_PARTITION);
					fReconciler.setReconcilingStrategy(markupStrategy, IXMLPartitions.XML_DEFAULT);

					fReconciler.setReconcilingStrategy(jspStrategy, IJSPPartitionTypes.JSP_DEFAULT);
					fReconciler.setReconcilingStrategy(jspStrategy, IJSPPartitionTypes.JSP_CONTENT_JAVA);
					fReconciler.setReconcilingStrategy(jspStrategy, IJSPPartitionTypes.JSP_CONTENT_DELIMITER);
					fReconciler.setReconcilingStrategy(jspStrategy, IJSPPartitionTypes.JSP_DIRECTIVE);

					fReconciler.setDefaultStrategy(markupStrategy);

					String contentTypeId = sModel.getContentTypeIdentifier();
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

	private JavaSourceViewerConfiguration getJavaSourceViewerConfiguration() {
		if (fJavaSourceViewerConfiguration == null) {
			IPreferenceStore store = PreferenceConstants.getPreferenceStore();
			JavaTextTools javaTextTools = new JavaTextTools(store);
			fJavaSourceViewerConfiguration = new JavaSourceViewerConfiguration(javaTextTools.getColorManager(), store, getTextEditor(), IJavaPartitions.JAVA_PARTITIONING);
			//fJavaSourceViewerConfiguration = new JavaSourceViewerConfiguration(javaTextTools, getTextEditor());
		}
		return fJavaSourceViewerConfiguration;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 * @since 3.1
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (fPreferenceStore == null)
			return null;
		if (sourceViewer == null || !fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;

		List allDetectors = new ArrayList(0);
		allDetectors.add(new JSPJavaHyperlinkDetector());
		allDetectors.add(new TaglibHyperlinkDetector());
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
}
