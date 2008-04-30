/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.web.support.jsp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.StructuredTextViewerConfigurationJSP;
import org.eclipse.jst.jsp.ui.internal.autoedit.StructuredAutoEditStrategyJSP;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.css.core.text.ICSSPartitions;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.jsdt.web.ui.StructuredTextViewerConfigurationJSDT;
import org.eclipse.wst.jsdt.web.ui.StructuredTextViewerConfigurationJSDT.externalTypeExtension;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
/**
*
* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JSDTStructuredTextViewerConfigurationJSP extends StructuredTextViewerConfigurationJSP{

	private String[] fConfiguredContentTypes;
	
	private StructuredTextViewerConfiguration fHTMLSourceViewerConfiguration;

	private StructuredTextViewerConfiguration getHTMLSourceViewerConfiguration() {
		if (fHTMLSourceViewerConfiguration == null) {
			fHTMLSourceViewerConfiguration = new StructuredTextViewerConfigurationJSDT();
		}
		return fHTMLSourceViewerConfiguration;
	}
	
	/*
	 * From here down, had to copy code from the JSP because the class decided to make the two members above private
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy[] strategies = null;

		if (contentType == IHTMLPartitions.HTML_DEFAULT || contentType == IHTMLPartitions.HTML_DECLARATION) {
			// html and jsp autoedit strategies
			List allStrategies = new ArrayList(0);

			// add the jsp autoedit strategy first then add all html's
			allStrategies.add(new StructuredAutoEditStrategyJSP());

			IAutoEditStrategy[] htmlStrategies = getHTMLSourceViewerConfiguration().getAutoEditStrategies(sourceViewer, contentType);
			for (int i = 0; i < htmlStrategies.length; i++) {
				allStrategies.add(htmlStrategies[i]);
			}

			strategies = (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
		}
		else {
			strategies = super.getAutoEditStrategies(sourceViewer, contentType);
		}

		return strategies;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			/*
			 * A little bit of cheating because assuming html's configured
			 * content types will add default, unknown, and all xml configured
			 * content types
			 */
			String[] htmlTypes = getHTMLSourceViewerConfiguration().getConfiguredContentTypes(sourceViewer);
			String[] jspTypes = StructuredTextPartitionerForJSP.getConfiguredContentTypes();
			fConfiguredContentTypes = new String[htmlTypes.length + jspTypes.length];

			int index = 0;
			System.arraycopy(htmlTypes, 0, fConfiguredContentTypes, index, htmlTypes.length);
			System.arraycopy(jspTypes, 0, fConfiguredContentTypes, index += htmlTypes.length, jspTypes.length);
		}

		return fConfiguredContentTypes;
	}
	
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		IContentAssistProcessor[] processors = null;

		if (partitionType == IHTMLPartitions.SCRIPT) {
			// HTML JavaScript
			IContentAssistant htmlContentAssistant = getHTMLSourceViewerConfiguration().getContentAssistant(sourceViewer);
			IContentAssistProcessor processor = htmlContentAssistant.getContentAssistProcessor(IHTMLPartitions.SCRIPT);
			processors = new IContentAssistProcessor[]{processor};
		}
		else if (partitionType == ICSSPartitions.STYLE) {
			// HTML CSS
			IContentAssistant htmlContentAssistant = getHTMLSourceViewerConfiguration().getContentAssistant(sourceViewer);
			IContentAssistProcessor processor = htmlContentAssistant.getContentAssistProcessor(ICSSPartitions.STYLE);
			processors = new IContentAssistProcessor[]{processor};
		}
		else{
			processors = super.getContentAssistProcessors(sourceViewer, partitionType);
		}

		return processors;
	}
	
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final IContentFormatter formatter = super.getContentFormatter(sourceViewer);
		/*
		 * Check for any externally supported auto edit strategies from EP.
		 * [Bradley Childs - childsb@us.ibm.com]
		 */
		String[] contentTypes = getConfiguredContentTypes(sourceViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			IFormattingStrategy cf = (IFormattingStrategy) ExtendedConfigurationBuilder.getInstance().getConfiguration(externalTypeExtension.CONTENT_FORMATER, contentTypes[i]);
			if (cf != null && formatter instanceof MultiPassContentFormatter) {
				((MultiPassContentFormatter) formatter).setSlaveStrategy(cf, contentTypes[i]);
			}
		}
		return formatter;
	}
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		ITextDoubleClickStrategy strategy = null;

		// html or javascript
		if (contentType == IHTMLPartitions.HTML_DEFAULT || contentType == IHTMLPartitions.SCRIPT)
			strategy = getHTMLSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, contentType);
		else if (contentType == IJSPPartitions.JSP_DEFAULT)
			// JSP (just treat like html)
			strategy = getHTMLSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, IHTMLPartitions.HTML_DEFAULT);
		else
			strategy = super.getDoubleClickStrategy(sourceViewer, contentType);

		return strategy;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 */
	
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null || !fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED)) {
			return null;
		}
		List allDetectors = new ArrayList(0);
		IHyperlinkDetector[] superDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!allDetectors.contains(detector)) {
				allDetectors.add(detector);
			}
		}
		/* Check for external HyperLink Detectors */
		String[] contentTypes = getConfiguredContentTypes(sourceViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			IHyperlinkDetector hl = (IHyperlinkDetector) ExtendedConfigurationBuilder.getInstance().getConfiguration(externalTypeExtension.HYPERLINK_DETECTOR, contentTypes[i]);
			if (hl != null) {
				allDetectors.add(hl);
			}
		}
		return (IHyperlinkDetector[]) allDetectors.toArray(new IHyperlinkDetector[0]);
	}
	
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		String[] indentations = null;

		if (contentType == IXMLPartitions.XML_DEFAULT)
			indentations = super.getIndentPrefixes(sourceViewer, contentType);
		else
			indentations = getHTMLSourceViewerConfiguration().getIndentPrefixes(sourceViewer, contentType);

		return indentations;
	}
	
	protected IInformationProvider getInformationProvider(ISourceViewer sourceViewer, String partitionType) {
		IInformationProvider provider = null;
		if (partitionType == IHTMLPartitions.HTML_DEFAULT) {
			// HTML
			IInformationPresenter htmlPresenter = getHTMLSourceViewerConfiguration().getInformationPresenter(sourceViewer);
			provider = htmlPresenter.getInformationProvider(IHTMLPartitions.HTML_DEFAULT);
		}
		else if (partitionType == IHTMLPartitions.SCRIPT) {
			// HTML JavaScript
			IInformationPresenter htmlPresenter = getHTMLSourceViewerConfiguration().getInformationPresenter(sourceViewer);
			provider = htmlPresenter.getInformationProvider(IHTMLPartitions.SCRIPT);
		}
		else{
			provider = super.getInformationProvider(sourceViewer, partitionType);
		}
		return provider;
	}
	
	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == IHTMLPartitions.HTML_DEFAULT || partitionType == IHTMLPartitions.HTML_COMMENT || partitionType == IHTMLPartitions.HTML_DECLARATION) {
			providers = getHTMLSourceViewerConfiguration().getLineStyleProviders(sourceViewer, IHTMLPartitions.HTML_DEFAULT);
		}
		else if (partitionType == IHTMLPartitions.SCRIPT || partitionType == IJSPPartitions.JSP_CONTENT_JAVASCRIPT) {
			providers = getHTMLSourceViewerConfiguration().getLineStyleProviders(sourceViewer, IHTMLPartitions.SCRIPT);
		}
		else if (partitionType == ICSSPartitions.STYLE) {
			providers = getHTMLSourceViewerConfiguration().getLineStyleProviders(sourceViewer, ICSSPartitions.STYLE);
		}
		else{
			providers = super.getLineStyleProviders(sourceViewer, partitionType);
		}

		return providers;
	}
	
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover hover = null;

		if (contentType == IHTMLPartitions.HTML_DEFAULT || contentType == IHTMLPartitions.SCRIPT) {
			// html and javascript regions
			hover = getHTMLSourceViewerConfiguration().getTextHover(sourceViewer, contentType, stateMask);
		}
		else {
			hover = super.getTextHover(sourceViewer, contentType);
		}
	
		return hover;
	}
}
