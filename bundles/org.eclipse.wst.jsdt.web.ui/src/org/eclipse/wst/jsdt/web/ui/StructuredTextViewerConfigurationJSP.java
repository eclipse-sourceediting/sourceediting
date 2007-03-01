/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jsdt.ui.JavaUI;
import org.eclipse.jsdt.ui.PreferenceConstants;
import org.eclipse.jsdt.ui.text.IJavaPartitions;
import org.eclipse.jsdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.wst.jsdt.web.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.wst.jsdt.web.core.text.IJSPPartitions;
import org.eclipse.wst.jsdt.web.ui.internal.autoedit.AutoEditStrategyForTabs;
import org.eclipse.wst.jsdt.web.ui.internal.autoedit.StructuredAutoEditStrategyJSP;
import org.eclipse.wst.jsdt.web.ui.internal.contentassist.JSPContentAssistProcessor;
import org.eclipse.wst.jsdt.web.ui.internal.contentassist.JSPJavaContentAssistProcessor;
import org.eclipse.wst.jsdt.web.ui.internal.contentassist.NoRegionContentAssistProcessorForJSP;
import org.eclipse.wst.jsdt.web.ui.internal.format.FormattingStrategyJSPJava;
import org.eclipse.wst.jsdt.web.ui.internal.hyperlink.JSPJavaHyperlinkDetector;
import org.eclipse.wst.jsdt.web.ui.internal.hyperlink.XMLHyperlinkDetector;
import org.eclipse.wst.jsdt.web.ui.internal.style.java.LineStyleProviderForJava;
import org.eclipse.wst.jsdt.web.ui.internal.taginfo.JSPJavaJavadocHoverProcessor;
import org.eclipse.wst.jsdt.web.ui.internal.taginfo.JSPJavaJavadocInformationProvider;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.css.core.text.ICSSPartitions;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

/**
 * Configuration for a source viewer which shows JSP content.
 * <p>
 * Clients can subclass and override just those methods which must be specific
 * to their needs.
 * </p>
 * 
 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration
 * @since 1.0
 */
public class StructuredTextViewerConfigurationJSP extends
		StructuredTextViewerConfiguration {
	/*
	 * One instance per configuration because not sourceviewer-specific and it's
	 * a String array
	 */
	private String[] fConfiguredContentTypes;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForJava;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForJSP;
	/*
	 * One instance per configuration
	 */

	private StructuredTextViewerConfiguration fHTMLSourceViewerConfiguration;
	private JavaSourceViewerConfiguration fJavaSourceViewerConfiguration;
	private StructuredTextViewerConfiguration fXMLSourceViewerConfiguration;

	/**
	 * Create new instance of StructuredTextViewerConfigurationJSP
	 */
	public StructuredTextViewerConfigurationJSP() {
		// Must have empty constructor to createExecutableExtension
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy[] strategies = null;
		
		if (contentType==IJSPPartitions.JSP_CONTENT_JAVA) {
			// jsp java autoedit strategies
			List allStrategies = new ArrayList(0);

			IAutoEditStrategy[] javaStrategies = getJavaSourceViewerConfiguration()
					.getAutoEditStrategies(sourceViewer,
							IJavaPartitions.JAVA_PARTITIONING);
			for (int i = 0; i < javaStrategies.length; i++) {
				allStrategies.add(javaStrategies[i]);
			}
			// be sure this is added last, after others, so it can modify
			// results from earlier steps.
			// add auto edit strategy that handles when tab key is pressed
			allStrategies.add(new AutoEditStrategyForTabs());

			strategies = (IAutoEditStrategy[]) allStrategies
					.toArray(new IAutoEditStrategy[allStrategies.size()]);
		}
		return strategies;
//		if (contentType == IXMLPartitions.XML_DEFAULT) {
//			// xml autoedit strategies
//			strategies = getXMLSourceViewerConfiguration()
//					.getAutoEditStrategies(sourceViewer, contentType);
//		} else if (contentType == IJSPPartitions.JSP_CONTENT_JAVA) {
//			// jsp java autoedit strategies
//			List allStrategies = new ArrayList(0);
//
//			IAutoEditStrategy[] javaStrategies = getJavaSourceViewerConfiguration()
//					.getAutoEditStrategies(sourceViewer,
//							IJavaPartitions.JAVA_PARTITIONING);
//			for (int i = 0; i < javaStrategies.length; i++) {
//				allStrategies.add(javaStrategies[i]);
//			}
//			// be sure this is added last, after others, so it can modify
//			// results from earlier steps.
//			// add auto edit strategy that handles when tab key is pressed
//			allStrategies.add(new AutoEditStrategyForTabs());
//
//			strategies = (IAutoEditStrategy[]) allStrategies
//					.toArray(new IAutoEditStrategy[allStrategies.size()]);
//		} else if (contentType == IHTMLPartitions.HTML_DEFAULT
//				|| contentType == IHTMLPartitions.HTML_DECLARATION) {
//			// html and jsp autoedit strategies
//			List allStrategies = new ArrayList(0);
//
//			// add the jsp autoedit strategy first then add all html's
//			allStrategies.add(new StructuredAutoEditStrategyJSP());
//
//			IAutoEditStrategy[] htmlStrategies = getHTMLSourceViewerConfiguration()
//					.getAutoEditStrategies(sourceViewer, contentType);
//			for (int i = 0; i < htmlStrategies.length; i++) {
//				allStrategies.add(htmlStrategies[i]);
//			}
//
//			strategies = (IAutoEditStrategy[]) allStrategies
//					.toArray(new IAutoEditStrategy[allStrategies.size()]);
//		} else {
//			// default autoedit strategies
//			List allStrategies = new ArrayList(0);
//
//			IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(
//					sourceViewer, contentType);
//			for (int i = 0; i < superStrategies.length; i++) {
//				allStrategies.add(superStrategies[i]);
//			}
//
//			// be sure this is added last, after others, so it can modify
//			// results from earlier steps.
//			// add auto edit strategy that handles when tab key is pressed
//			allStrategies.add(new AutoEditStrategyForTabs());
//
//			strategies = (IAutoEditStrategy[]) allStrategies
//					.toArray(new IAutoEditStrategy[allStrategies.size()]);
//		}
//
//		return strategies;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			/*
			 * A little bit of cheating because assuming html's configured
			 * content types will add default, unknown, and all xml configured
			 * content types
			 */
//			String[] htmlTypes = getHTMLSourceViewerConfiguration()
//					.getConfiguredContentTypes(sourceViewer);
//			String[] jspTypes = StructuredTextPartitionerForJSP
//					.getConfiguredContentTypes();
//			fConfiguredContentTypes = new String[htmlTypes.length
//					+ jspTypes.length];
//
//			int index = 0;
//			System.arraycopy(htmlTypes, 0, fConfiguredContentTypes, index,
//					htmlTypes.length);
//			System.arraycopy(jspTypes, 0, fConfiguredContentTypes,
//					index += htmlTypes.length, jspTypes.length);
		}
		return new String[] {IJSPPartitions.JSP_CONTENT_JAVA};
		//return fConfiguredContentTypes;
	}

	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
																   IContentAssistProcessor[] processors = null;

			if ( partitionType == IJSPPartitions.JSP_CONTENT_JAVA ){
				processors = new IContentAssistProcessor[] { new JSPJavaContentAssistProcessor() };
			}
			
//		if ((partitionType == IXMLPartitions.XML_CDATA)
//				|| (partitionType == IJSPPartitions.JSP_CONTENT_JAVA)) {
//		
//			
//			processors = new IContentAssistProcessor[] { new JSPJavaContentAssistProcessor() };
//
//		} else if (partitionType == ICSSPartitions.STYLE) {
//			// HTML CSS
//			IContentAssistant htmlContentAssistant = getHTMLSourceViewerConfiguration()
//					.getContentAssistant(sourceViewer);
//			IContentAssistProcessor processor = htmlContentAssistant
//					.getContentAssistProcessor(ICSSPartitions.STYLE);
//			processors = new IContentAssistProcessor[] { processor };
//		} else if ((partitionType == IXMLPartitions.XML_DEFAULT)
//				|| (partitionType == IHTMLPartitions.HTML_DEFAULT)
//				|| (partitionType == IHTMLPartitions.HTML_COMMENT)) {
//			// jsp
//			processors = new IContentAssistProcessor[] { new JSPContentAssistProcessor() };
//		}
//
//		else if (partitionType == IStructuredPartitions.UNKNOWN_PARTITION) {
//			// unknown
//			processors = new IContentAssistProcessor[] { new NoRegionContentAssistProcessorForJSP() };
//		}
//
		return processors;
	}

	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		MultiPassContentFormatter formatter = new MultiPassContentFormatter(
				getConfiguredDocumentPartitioning(sourceViewer),
				IXMLPartitions.XML_DEFAULT);
//
//		formatter.setMasterStrategy(new StructuredFormattingStrategy(
//				new HTMLFormatProcessorImpl()));
		formatter.setSlaveStrategy(new FormattingStrategyJSPJava(),
				IJSPPartitions.JSP_CONTENT_JAVA);

		return formatter;
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		ITextDoubleClickStrategy strategy = null;

		if (contentType ==IJSPPartitions.JSP_CONTENT_JAVA) {
			// JSP Java or JSP JavaScript
			strategy = getJavaSourceViewerConfiguration()
					.getDoubleClickStrategy(sourceViewer, contentType);
		}	
		// html or javascript
//		if (contentType == IHTMLPartitions.HTML_DEFAULT) {
//			strategy = getHTMLSourceViewerConfiguration()
//					.getDoubleClickStrategy(sourceViewer, contentType);
//		} else if (contentType == IJSPPartitions.JSP_CONTENT_JAVA) {
//			// JSP Java or JSP JavaScript
//			strategy = getJavaSourceViewerConfiguration()
//					.getDoubleClickStrategy(sourceViewer, contentType);
//		} else {
//			strategy = super.getDoubleClickStrategy(sourceViewer, contentType);
//		}

		return strategy;
	}

//	private StructuredTextViewerConfiguration getHTMLSourceViewerConfiguration() {
//		if (fHTMLSourceViewerConfiguration == null) {
//			fHTMLSourceViewerConfiguration = new StructuredTextViewerConfigurationHTML();
//		}
//		return fHTMLSourceViewerConfiguration;
//	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
	
		if (fPreferenceStore == null) {
			return null;
		}
		if (sourceViewer == null
				|| !fPreferenceStore
						.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED)) {
			return null;
		}

		List allDetectors = new ArrayList(0);
		allDetectors.add(new JSPJavaHyperlinkDetector());
		// allDetectors.add(new TaglibHyperlinkDetector());
//				allDetectors.add(new XMLHyperlinkDetector());
//
//		IHyperlinkDetector[] superDetectors = super
//				.getHyperlinkDetectors(sourceViewer);
//		for (int m = 0; m < superDetectors.length; m++) {
//			IHyperlinkDetector detector = superDetectors[m];
//			if (!allDetectors.contains(detector)) {
//				allDetectors.add(detector);
//			}
//		}
		return (IHyperlinkDetector[]) allDetectors
				.toArray(new IHyperlinkDetector[0]);
	}

	@Override
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		String[] indentations = null;

//		if (contentType == IXMLPartitions.XML_DEFAULT) {
//			indentations = getXMLSourceViewerConfiguration().getIndentPrefixes(
//					sourceViewer, contentType);
//		} else {
//			indentations = getHTMLSourceViewerConfiguration()
//					.getIndentPrefixes(sourceViewer, contentType);
//		}

		return indentations;
	}

	@Override
	protected IInformationProvider getInformationProvider(ISourceViewer sourceViewer, String partitionType) {
		IInformationProvider provider = null;
//		if (partitionType == IHTMLPartitions.HTML_DEFAULT) {
//			// HTML
//			IInformationPresenter htmlPresenter = getHTMLSourceViewerConfiguration()
//					.getInformationPresenter(sourceViewer);
//			provider = htmlPresenter
//					.getInformationProvider(IHTMLPartitions.HTML_DEFAULT);
//		}
//
//		else if (partitionType == IXMLPartitions.XML_DEFAULT) {
//			// XML
//			IInformationPresenter xmlPresenter = getXMLSourceViewerConfiguration()
//					.getInformationPresenter(sourceViewer);
//			provider = xmlPresenter
//					.getInformationProvider(IXMLPartitions.XML_DEFAULT);
//		}
//
//		else 
		if (partitionType==IJSPPartitions.JSP_CONTENT_JAVA){
			// JSP java
			provider = new JSPJavaJavadocInformationProvider();
		}
		return provider;
	}

	private JavaSourceViewerConfiguration getJavaSourceViewerConfiguration() {
		if (fJavaSourceViewerConfiguration == null) {
			IPreferenceStore store = PreferenceConstants.getPreferenceStore();
			/*
			 * NOTE: null text editor is being passed to
			 * JavaSourceViewerConfiguration because
			 * StructuredTextViewerConfiguration does not know current editor.
			 * this is okay because editor is not needed in the cases we are
			 * using javasourceviewerconfiguration.
			 */
			fJavaSourceViewerConfiguration = new JavaSourceViewerConfiguration(
					JavaUI.getColorManager(), store, null,
					IJavaPartitions.JAVA_PARTITIONING);
		}
		return fJavaSourceViewerConfiguration;
	}

	@Override
	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		
		LineStyleProvider[] providers = null;

//		if (partitionType == IHTMLPartitions.HTML_DEFAULT
//				|| partitionType == IHTMLPartitions.HTML_COMMENT
//				|| partitionType == IHTMLPartitions.HTML_DECLARATION) {
//			providers = getHTMLSourceViewerConfiguration()
//					.getLineStyleProviders(sourceViewer,
//							IHTMLPartitions.HTML_DEFAULT);
//		}
//
//		else if (partitionType == ICSSPartitions.STYLE) {
//			providers = getHTMLSourceViewerConfiguration()
//					.getLineStyleProviders(sourceViewer, ICSSPartitions.STYLE);
//		} else if (partitionType == IXMLPartitions.XML_DEFAULT
//				|| partitionType == IXMLPartitions.XML_CDATA
//				|| partitionType == IXMLPartitions.XML_COMMENT
//				|| partitionType == IXMLPartitions.XML_DECLARATION
//				|| partitionType == IXMLPartitions.XML_PI) {
//			providers = getXMLSourceViewerConfiguration()
//					.getLineStyleProviders(sourceViewer,
//							IXMLPartitions.XML_DEFAULT);
//		} else 
		if (partitionType == IJSPPartitions.JSP_CONTENT_JAVA) {
			providers = new LineStyleProvider[] { getLineStyleProviderForJava() };
		}

		return providers;
	}

	private LineStyleProvider getLineStyleProviderForJava() {
		if (fLineStyleProviderForJava == null) {
			fLineStyleProviderForJava = new LineStyleProviderForJava();
		}
		return fLineStyleProviderForJava;
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType, int stateMask) {
		ITextHover hover = null;

//		if (contentType == IHTMLPartitions.HTML_DEFAULT) {
//			// html and javascript regions
//			hover = getHTMLSourceViewerConfiguration().getTextHover(
//					sourceViewer, contentType, stateMask);
//		} else if (contentType == IXMLPartitions.XML_DEFAULT) {
//			// xml regions
//			hover = getXMLSourceViewerConfiguration().getTextHover(
//					sourceViewer, contentType, stateMask);
//		} else 
		if (contentType==IJSPPartitions.JSP_CONTENT_JAVA) {
			TextHoverManager manager = SSEUIPlugin.getDefault()
					.getTextHoverManager();
			TextHoverManager.TextHoverDescriptor[] hoverDescs = manager
					.getTextHovers();
			int i = 0;
			while (i < hoverDescs.length && hover == null) {
				if (hoverDescs[i].isEnabled()
						&& EditorUtility.computeStateMask(hoverDescs[i]
								.getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER
							.equalsIgnoreCase(hoverType)) {

						hover = manager
								.createBestMatchHover(new JSPJavaJavadocHoverProcessor());

					} else if (TextHoverManager.DOCUMENTATION_HOVER
							.equalsIgnoreCase(hoverType)) {

						hover = new JSPJavaJavadocHoverProcessor();

					}
				}
				i++;
			}
		}

		// no appropriate text hovers found, try super
		if (hover == null) {
			hover = super.getTextHover(sourceViewer, contentType, stateMask);
		}
		return hover;

	}


	private StructuredTextViewerConfiguration getXMLSourceViewerConfiguration() {
		if (fXMLSourceViewerConfiguration == null) {
			fXMLSourceViewerConfiguration = new StructuredTextViewerConfigurationXML();
		}
		return fXMLSourceViewerConfiguration;
	}
}
