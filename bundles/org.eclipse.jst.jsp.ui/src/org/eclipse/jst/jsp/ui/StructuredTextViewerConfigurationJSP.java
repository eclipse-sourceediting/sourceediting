/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthias Fuessel, mat.fuessel@gmx.net - [177387] use base hyperlinking extension points     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.internal.autoedit.AutoEditStrategyForTabs;
import org.eclipse.jst.jsp.ui.internal.autoedit.StructuredAutoEditStrategyJSP;
import org.eclipse.jst.jsp.ui.internal.autoedit.StructuredAutoEditStrategyJSPJava;
import org.eclipse.jst.jsp.ui.internal.contentassist.JSPStructuredContentAssistProcessor;
import org.eclipse.jst.jsp.ui.internal.format.FormattingStrategyJSPJava;
import org.eclipse.jst.jsp.ui.internal.style.LineStyleProviderForJSP;
import org.eclipse.jst.jsp.ui.internal.style.java.LineStyleProviderForJava;
import org.eclipse.jst.jsp.ui.internal.style.jspel.LineStyleProviderForJSPEL;
import org.eclipse.wst.css.core.text.ICSSPartitions;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeLabelProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

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
public class StructuredTextViewerConfigurationJSP extends StructuredTextViewerConfiguration {
	/*
	 * One instance per configuration because not sourceviewer-specific and
	 * it's a String array
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
	private LineStyleProvider fLineStyleProviderForJSPEL;
	private StructuredTextViewerConfiguration fHTMLSourceViewerConfiguration;
	private JavaSourceViewerConfiguration fJavaSourceViewerConfiguration;
	private StructuredTextViewerConfiguration fXMLSourceViewerConfiguration;
	private ILabelProvider fStatusLineLabelProvider;

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
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy[] strategies = null;

		if (contentType == IXMLPartitions.XML_DEFAULT) {
			// xml autoedit strategies
			strategies = getXMLSourceViewerConfiguration().getAutoEditStrategies(sourceViewer, contentType);
		}
		else if (contentType == IJSPPartitions.JSP_CONTENT_JAVA) {
			// jsp java autoedit strategies
			List allStrategies = new ArrayList(0);

			// add the scritplet autoedit strategy first
			allStrategies.add(new StructuredAutoEditStrategyJSPJava());
			
			IAutoEditStrategy[] javaStrategies = getJavaSourceViewerConfiguration().getAutoEditStrategies(sourceViewer, IJavaPartitions.JAVA_PARTITIONING);
			for (int i = 0; i < javaStrategies.length; i++) {
				allStrategies.add(javaStrategies[i]);
			}
			// be sure this is added last, after others, so it can modify
			// results from earlier steps.
			// add auto edit strategy that handles when tab key is pressed
			allStrategies.add(new AutoEditStrategyForTabs());

			strategies = (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
		}
		else if (contentType == IHTMLPartitions.HTML_DEFAULT || contentType == IHTMLPartitions.HTML_DECLARATION || contentType == IJSPPartitions.JSP_DIRECTIVE) {
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
			// default autoedit strategies
			List allStrategies = new ArrayList(0);

			IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
			for (int i = 0; i < superStrategies.length; i++) {
				allStrategies.add(superStrategies[i]);
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
	
	/**
	 * @see org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration#getContentAssistProcessors(
	 * 	org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	protected IContentAssistProcessor[] getContentAssistProcessors(
			ISourceViewer sourceViewer, String partitionType) {
		
		IContentAssistProcessor processor = new JSPStructuredContentAssistProcessor(
				this.getContentAssistant(), partitionType, sourceViewer);
		return new IContentAssistProcessor[]{processor};
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		IContentFormatter formatter = super.getContentFormatter(sourceViewer);
		// super was unable to create a formatter, probably because
		// sourceViewer does not have document set yet, so just create a
		// generic one
		if (!(formatter instanceof MultiPassContentFormatter))
			formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IXMLPartitions.XML_DEFAULT);

		MultiPassContentFormatter multiFormatter = (MultiPassContentFormatter) formatter;
		multiFormatter.setMasterStrategy(new StructuredFormattingStrategy(new HTMLFormatProcessorImpl()));
		multiFormatter.setSlaveStrategy(new FormattingStrategyJSPJava(), IJSPPartitions.JSP_CONTENT_JAVA);

		return formatter;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		ITextDoubleClickStrategy strategy = null;

		// html or javascript
		if (contentType == IHTMLPartitions.HTML_DEFAULT || contentType == IHTMLPartitions.SCRIPT)
			strategy = getHTMLSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, contentType);
		else if (contentType == IJSPPartitions.JSP_CONTENT_JAVA || contentType == IJSPPartitions.JSP_CONTENT_JAVASCRIPT)
			// JSP Java or JSP JavaScript
			strategy = getJavaSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, contentType);
		else if (contentType == IJSPPartitions.JSP_DEFAULT)
			// JSP (just treat like html)
			strategy = getHTMLSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, IHTMLPartitions.HTML_DEFAULT);
		else
			strategy = super.getDoubleClickStrategy(sourceViewer, contentType);

		return strategy;
	}

	private StructuredTextViewerConfiguration getHTMLSourceViewerConfiguration() {
		if (fHTMLSourceViewerConfiguration == null) {
			fHTMLSourceViewerConfiguration = new StructuredTextViewerConfigurationHTML();
		}
		return fHTMLSourceViewerConfiguration;
	}

	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		String[] indentations = null;

		if (contentType == IXMLPartitions.XML_DEFAULT)
			indentations = getXMLSourceViewerConfiguration().getIndentPrefixes(sourceViewer, contentType);
		else
			indentations = getHTMLSourceViewerConfiguration().getIndentPrefixes(sourceViewer, contentType);

		return indentations;
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
			fJavaSourceViewerConfiguration = new JavaSourceViewerConfiguration(JavaUI.getColorManager(), store, null, IJavaPartitions.JAVA_PARTITIONING);
		}
		return fJavaSourceViewerConfiguration;
	}

	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == IHTMLPartitions.HTML_DEFAULT || partitionType == IHTMLPartitions.HTML_COMMENT || partitionType == IHTMLPartitions.HTML_DECLARATION) {
			providers = getHTMLSourceViewerConfiguration().getLineStyleProviders(sourceViewer, IHTMLPartitions.HTML_DEFAULT);
		}
		else if (partitionType == IHTMLPartitions.SCRIPT || partitionType == IJSPPartitions.JSP_CONTENT_JAVASCRIPT) {
			providers = getHTMLSourceViewerConfiguration().getLineStyleProviders(sourceViewer, IHTMLPartitions.SCRIPT);
		}
		else if (partitionType == ICSSPartitions.STYLE || partitionType == ICSSPartitions.COMMENT) {
			providers = getHTMLSourceViewerConfiguration().getLineStyleProviders(sourceViewer, ICSSPartitions.STYLE);
		}
		else if (partitionType == IXMLPartitions.XML_DEFAULT || partitionType == IXMLPartitions.XML_CDATA || partitionType == IXMLPartitions.XML_COMMENT || partitionType == IXMLPartitions.XML_DECLARATION || partitionType == IXMLPartitions.XML_PI) {
			providers = getXMLSourceViewerConfiguration().getLineStyleProviders(sourceViewer, IXMLPartitions.XML_DEFAULT);
		}
		else if (partitionType == IJSPPartitions.JSP_CONTENT_JAVA) {
			providers = new LineStyleProvider[]{getLineStyleProviderForJava()};
		}
		else if (partitionType == IJSPPartitions.JSP_DEFAULT_EL) {
			providers = new LineStyleProvider[]{getLineStyleProviderForJSPEL()};
		}
		else if (partitionType == IJSPPartitions.JSP_COMMENT || partitionType == IJSPPartitions.JSP_CONTENT_DELIMITER || partitionType == IJSPPartitions.JSP_DEFAULT || partitionType == IJSPPartitions.JSP_DIRECTIVE) {
			providers = new LineStyleProvider[]{getLineStyleProviderForJSP()};
		}

		return providers;
	}

	private LineStyleProvider getLineStyleProviderForJava() {
		if (fLineStyleProviderForJava == null) {
			fLineStyleProviderForJava = new LineStyleProviderForJava();
		}
		return fLineStyleProviderForJava;
	}

	private LineStyleProvider getLineStyleProviderForJSP() {
		if (fLineStyleProviderForJSP == null) {
			fLineStyleProviderForJSP = new LineStyleProviderForJSP();
		}
		return fLineStyleProviderForJSP;
	}

	private LineStyleProvider getLineStyleProviderForJSPEL() {
		if (fLineStyleProviderForJSPEL == null) {
			fLineStyleProviderForJSPEL = new LineStyleProviderForJSPEL();
		}
		return fLineStyleProviderForJSPEL;
	}

	public ILabelProvider getStatusLineLabelProvider(ISourceViewer sourceViewer) {
		if (fStatusLineLabelProvider == null) {
			fStatusLineLabelProvider = new JFaceNodeLabelProvider() {
				public String getText(Object element) {
					if (element == null)
						return null;

					StringBuffer s = new StringBuffer();
					Node node = (Node) element;
					while (node != null) {
						if (node.getNodeType() != Node.DOCUMENT_NODE) {
							s.insert(0, super.getText(node));
						}

						if (node.getNodeType() == Node.ATTRIBUTE_NODE)
							node = ((Attr) node).getOwnerElement();
						else
							node = node.getParentNode();
					
						if (node != null && node.getNodeType() != Node.DOCUMENT_NODE) {
							s.insert(0, IPath.SEPARATOR);
						}
					}
					return s.toString();
				}

			};
		}
		return fStatusLineLabelProvider;
	}

	private StructuredTextViewerConfiguration getXMLSourceViewerConfiguration() {
		if (fXMLSourceViewerConfiguration == null) {
			fXMLSourceViewerConfiguration = new StructuredTextViewerConfigurationXML();
		}
		return fXMLSourceViewerConfiguration;
	}

	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map targets = super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put(ContentTypeIdForJSP.ContentTypeID_JSP, null);

		// also add html & xml since there could be html/xml content in jsp
		// (just hope the hyperlink detectors will do additional checking)
		targets.put(ContentTypeIdForHTML.ContentTypeID_HTML, null);
		targets.put(ContentTypeIdForXML.ContentTypeID_XML, null);
		return targets;
	}
}
