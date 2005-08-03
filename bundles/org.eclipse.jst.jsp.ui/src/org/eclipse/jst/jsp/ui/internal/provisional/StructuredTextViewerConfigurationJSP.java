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
package org.eclipse.jst.jsp.ui.internal.provisional;

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
import org.eclipse.jst.jsp.core.internal.provisional.text.IJSPPartitionTypes;
import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.jst.jsp.ui.internal.autoedit.StructuredAutoEditStrategyJSP;
import org.eclipse.jst.jsp.ui.internal.contentassist.JSPContentAssistProcessor;
import org.eclipse.jst.jsp.ui.internal.contentassist.JSPELContentAssistProcessor;
import org.eclipse.jst.jsp.ui.internal.contentassist.JSPJavaContentAssistProcessor;
import org.eclipse.jst.jsp.ui.internal.contentassist.NoRegionContentAssistProcessorForJSP;
import org.eclipse.jst.jsp.ui.internal.format.FormattingStrategyJSPJava;
import org.eclipse.jst.jsp.ui.internal.hyperlink.JSPJavaHyperlinkDetector;
import org.eclipse.jst.jsp.ui.internal.hyperlink.TaglibHyperlinkDetector;
import org.eclipse.jst.jsp.ui.internal.hyperlink.XMLHyperlinkDetector;
import org.eclipse.jst.jsp.ui.internal.reconcile.StructuredTextReconcilingStrategyForJSP;
import org.eclipse.jst.jsp.ui.internal.style.LineStyleProviderForJSP;
import org.eclipse.jst.jsp.ui.internal.style.java.LineStyleProviderForJava;
import org.eclipse.jst.jsp.ui.internal.style.jspel.LineStyleProviderForJSPEL;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPBestMatchHoverProcessor;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPJavaBestMatchHoverProcessor;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPJavaJavadocHoverProcessor;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPTagInfoHoverProcessor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.css.core.internal.provisional.text.ICSSPartitionTypes;
import org.eclipse.wst.css.ui.internal.style.LineStyleProviderForEmbeddedCSS;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.internal.provisional.text.IHTMLPartitionTypes;
import org.eclipse.wst.html.ui.internal.autoedit.AutoEditStrategyForTabs;
import org.eclipse.wst.html.ui.internal.provisional.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.html.ui.internal.style.LineStyleProviderForHTML;
import org.eclipse.wst.javascript.ui.internal.common.style.LineStyleProviderForJavaScript;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
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
import org.eclipse.wst.xml.ui.internal.provisional.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.style.LineStyleProviderForXML;
import org.eclipse.wst.xml.ui.internal.validation.StructuredTextReconcilingStrategyForMarkup;

public class StructuredTextViewerConfigurationJSP extends StructuredTextViewerConfiguration {

	InformationPresenter fInformationPresenter = null;

	private JavaSourceViewerConfiguration fJavaSourceViewerConfiguration;
	private StructuredTextViewerConfiguration fXMLSourceViewerConfiguration;
	private StructuredTextViewerConfiguration fHTMLSourceViewerConfiguration;

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
		IAutoEditStrategy[] strategies = null;
		
		if (contentType == IXMLPartitions.XML_DEFAULT) {
			strategies = getXMLSourceViewerConfiguration().getAutoEditStrategies(sourceViewer, contentType);
		} else if (contentType == IJSPPartitionTypes.JSP_CONTENT_JAVA) {
			List allStrategies = new ArrayList(0);
			
			IAutoEditStrategy[] javaStrategies = getJavaSourceViewerConfiguration().getAutoEditStrategies(sourceViewer, IJavaPartitions.JAVA_PARTITIONING);
			for (int i = 0; i < javaStrategies.length; i++) {
				allStrategies.add(javaStrategies[i]);
			}
			// be sure this is added last, after others, so it can modify 
			// results from earlier steps.
			// add auto edit strategy that handles when tab key is pressed
			allStrategies.add(new AutoEditStrategyForTabs());
			
			strategies = (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
		} else {
			List allStrategies = new ArrayList(0);
	
			IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
			for (int i = 0; i < superStrategies.length; i++) {
				allStrategies.add(superStrategies[i]);
			}
			
	
			if (contentType == IHTMLPartitionTypes.HTML_DEFAULT || contentType == IHTMLPartitionTypes.HTML_DECLARATION) {
				allStrategies.add(new StructuredAutoEditStrategyJSP());
			}
			// be sure this is added last, after others, so it can modify 
			// results from earlier steps.
			// add auto edit strategy that handles when tab key is pressed
			allStrategies.add(new AutoEditStrategyForTabs());
			
			strategies = (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
		}

		return strategies;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			/*
			 * A little bit of cheating because assuming html's configured
			 * content types will add default, unknown, and all xml configured
			 * content types
			 */
			String[] htmlTypes = getHTMLSourceViewerConfiguration().getConfiguredContentTypes(sourceViewer);
			String[] jspTypes = StructuredTextPartitionerForJSP.getConfiguredContentTypes();
			configuredContentTypes = new String[htmlTypes.length + jspTypes.length];

			int index = 0;
			System.arraycopy(htmlTypes, 0, configuredContentTypes, index, htmlTypes.length);
			System.arraycopy(jspTypes, 0, configuredContentTypes, index += htmlTypes.length, jspTypes.length);
		}

		return configuredContentTypes;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		IContentAssistant ca = super.getContentAssistant(sourceViewer);

		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant contentAssistant = (ContentAssistant) ca;

			IContentAssistant htmlContentAssistant = getHTMLSourceViewerConfiguration().getContentAssistant(sourceViewer);

			IContentAssistProcessor jspContentAssistProcessor = new JSPContentAssistProcessor();
			IContentAssistProcessor jspJavaContentAssistProcessor = new JSPJavaContentAssistProcessor();
			IContentAssistProcessor noRegionProcessorJsp = new NoRegionContentAssistProcessorForJSP();
			IContentAssistProcessor jspELContentAssistProcessor = new JSPELContentAssistProcessor();

			// HTML
			setContentAssistProcessor(contentAssistant, htmlContentAssistant.getContentAssistProcessor(IHTMLPartitionTypes.HTML_DEFAULT), IHTMLPartitionTypes.HTML_DEFAULT);
			setContentAssistProcessor(contentAssistant, htmlContentAssistant.getContentAssistProcessor(IHTMLPartitionTypes.HTML_COMMENT), IHTMLPartitionTypes.HTML_COMMENT);

			// HTML JavaScript
			setContentAssistProcessor(contentAssistant, htmlContentAssistant.getContentAssistProcessor(IHTMLPartitionTypes.SCRIPT), IHTMLPartitionTypes.SCRIPT);

			// CSS
			setContentAssistProcessor(contentAssistant, htmlContentAssistant.getContentAssistProcessor(ICSSPartitionTypes.STYLE), ICSSPartitionTypes.STYLE);

			// JSP
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IStructuredPartitionTypes.DEFAULT_PARTITION);
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IXMLPartitions.XML_DEFAULT);
			// chances are it's jsp-java, if not you'll just get no proposals
			setContentAssistProcessor(contentAssistant, jspJavaContentAssistProcessor, IXMLPartitions.XML_CDATA);
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
			// JSP EL
			setContentAssistProcessor(contentAssistant, jspELContentAssistProcessor, IJSPPartitionTypes.JSP_DEFAULT_EL);
			// unknown
			setContentAssistProcessor(contentAssistant, noRegionProcessorJsp, IStructuredPartitionTypes.UNKNOWN_PARTITION);
			// CMVC 269718
			// JSP COMMENT
			setContentAssistProcessor(contentAssistant, jspContentAssistProcessor, IJSPPartitionTypes.JSP_COMMENT);
		}

		return ca;
	}

	public IContentAssistant getCorrectionAssistant(ISourceViewer sourceViewer) {
		return null;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IXMLPartitions.XML_DEFAULT);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new HTMLFormatProcessorImpl()));
		formatter.setSlaveStrategy(new FormattingStrategyJSPJava(), IJSPPartitionTypes.JSP_CONTENT_JAVA);

		return formatter;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		ITextDoubleClickStrategy strategy = null;

		// html or javascript
		if (contentType == IHTMLPartitionTypes.HTML_DEFAULT || contentType == IHTMLPartitionTypes.SCRIPT)
			strategy = getHTMLSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, contentType);
		else if (contentType == IJSPPartitionTypes.JSP_CONTENT_JAVA || contentType == IJSPPartitionTypes.JSP_CONTENT_JAVASCRIPT)
			// JSP Java or JSP JavaScript
			strategy = getJavaSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, contentType);
		else if (contentType == IJSPPartitionTypes.JSP_DEFAULT)
			// JSP (just treat like html)
			strategy = getHTMLSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, IHTMLPartitionTypes.HTML_DEFAULT);
		else
			strategy = super.getDoubleClickStrategy(sourceViewer, contentType);

		return strategy;
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

			// JSPEL
			highlighter.addProvider(IJSPPartitionTypes.JSP_DEFAULT_EL, new LineStyleProviderForJSPEL());
		}

		return highlighter;
	}

	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		if (fInformationPresenter == null) {
			fInformationPresenter = new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));

			// HTML
			IInformationPresenter htmlPresenter = getHTMLSourceViewerConfiguration().getInformationPresenter(sourceViewer);
			IInformationProvider htmlInformationProvider = htmlPresenter.getInformationProvider(IHTMLPartitionTypes.HTML_DEFAULT);
			fInformationPresenter.setInformationProvider(htmlInformationProvider, IHTMLPartitionTypes.HTML_DEFAULT);

			// HTML JavaScript
			IInformationProvider javascriptInformationProvider = htmlPresenter.getInformationProvider(IHTMLPartitionTypes.SCRIPT);
			fInformationPresenter.setInformationProvider(javascriptInformationProvider, IHTMLPartitionTypes.SCRIPT);

			// XML
			IInformationPresenter xmlPresenter = getXMLSourceViewerConfiguration().getInformationPresenter(sourceViewer);
			IInformationProvider xmlInformationProvider = xmlPresenter.getInformationProvider(IXMLPartitions.XML_DEFAULT);
			fInformationPresenter.setInformationProvider(xmlInformationProvider, IXMLPartitions.XML_DEFAULT);

			fInformationPresenter.setSizeConstraints(60, 10, true, true);
			fInformationPresenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		}
		return fInformationPresenter;
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover hover = null;

		// html + javascript
		if (contentType == IHTMLPartitionTypes.HTML_DEFAULT || contentType == IHTMLPartitionTypes.SCRIPT) {
			hover = getHTMLSourceViewerConfiguration().getTextHover(sourceViewer, contentType, stateMask);
		} else if ((contentType == IJSPPartitionTypes.JSP_DEFAULT) || (contentType == IJSPPartitionTypes.JSP_DIRECTIVE)) {
			// JSP
			TextHoverManager.TextHoverDescriptor[] hoverDescs = getTextHovers();
			int i = 0;
			while (i < hoverDescs.length) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
						hover = new JSPBestMatchHoverProcessor();
					else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						hover = new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						hover = new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
						hover = new JSPTagInfoHoverProcessor();
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
						hover = new JSPJavaBestMatchHoverProcessor();
					} else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
						hover = new ProblemAnnotationHoverProcessor();
					else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
						hover = new AnnotationHoverProcessor();
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType)) {
						hover = new JSPJavaJavadocHoverProcessor();
					}
				}
				i++;
			}
		} else if (contentType == IXMLPartitions.XML_DEFAULT) {
			hover = getXMLSourceViewerConfiguration().getTextHover(sourceViewer, contentType, stateMask);
		} else {
			hover = super.getTextHover(sourceViewer, contentType, stateMask);
		}
		return hover;
	}

	public void unConfigure(ISourceViewer viewer) {
		super.unConfigure(viewer);

		// InformationPresenters
		if (fInformationPresenter != null)
			fInformationPresenter.uninstall();

		if (fXMLSourceViewerConfiguration != null)
			fXMLSourceViewerConfiguration.unConfigure(viewer);
		if (fHTMLSourceViewerConfiguration != null)
			fHTMLSourceViewerConfiguration.unConfigure(viewer);
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
					fReconciler.setReconcilingStrategy(jspStrategy, IJSPPartitionTypes.JSP_DEFAULT_EL);

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
			// fJavaSourceViewerConfiguration = new
			// JavaSourceViewerConfiguration(javaTextTools, getTextEditor());
		}
		return fJavaSourceViewerConfiguration;
	}

	private StructuredTextViewerConfiguration getXMLSourceViewerConfiguration() {
		if (fXMLSourceViewerConfiguration == null) {
			fXMLSourceViewerConfiguration = new StructuredTextViewerConfigurationXML(fPreferenceStore);
			fXMLSourceViewerConfiguration.setEditorPart(getEditorPart());
			fXMLSourceViewerConfiguration.configureOn(fResource);
		}
		return fXMLSourceViewerConfiguration;
	}

	private StructuredTextViewerConfiguration getHTMLSourceViewerConfiguration() {
		if (fHTMLSourceViewerConfiguration == null) {
			fHTMLSourceViewerConfiguration = new StructuredTextViewerConfigurationHTML(fPreferenceStore);
			fHTMLSourceViewerConfiguration.setEditorPart(getEditorPart());
			fHTMLSourceViewerConfiguration.configureOn(fResource);
		}
		return fHTMLSourceViewerConfiguration;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 * @see Eclipse 3.1
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (fPreferenceStore == null)
			return null;
		if (sourceViewer == null || !fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;

		List allDetectors = new ArrayList(0);
		allDetectors.add(new JSPJavaHyperlinkDetector());
		allDetectors.add(new TaglibHyperlinkDetector());
		allDetectors.add(new XMLHyperlinkDetector());

		IHyperlinkDetector[] superDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!allDetectors.contains(detector)) {
				allDetectors.add(detector);
			}
		}
		return (IHyperlinkDetector[]) allDetectors.toArray(new IHyperlinkDetector[0]);
	}

	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		String[] indentations = null;

		if (contentType == IXMLPartitions.XML_DEFAULT)
			indentations = getXMLSourceViewerConfiguration().getIndentPrefixes(sourceViewer, contentType);
		else
			indentations = getHTMLSourceViewerConfiguration().getIndentPrefixes(sourceViewer, contentType);

		return indentations;
	}
}
