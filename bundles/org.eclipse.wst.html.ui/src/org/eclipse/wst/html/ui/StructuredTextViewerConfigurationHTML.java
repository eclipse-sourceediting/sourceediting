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
import java.util.Vector;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.css.core.internal.provisional.text.ICSSPartitionTypes;
import org.eclipse.wst.css.ui.internal.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.css.ui.internal.style.LineStyleProviderForEmbeddedCSS;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.format.HTMLFormatProcessorImpl;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.core.internal.provisional.text.IHTMLPartitionTypes;
import org.eclipse.wst.html.core.internal.text.StructuredTextPartitionerForHTML;
import org.eclipse.wst.html.ui.internal.autoedit.AutoEditStrategyForTabs;
import org.eclipse.wst.html.ui.internal.autoedit.StructuredAutoEditStrategyHTML;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLContentAssistProcessor;
import org.eclipse.wst.html.ui.internal.contentassist.NoRegionContentAssistProcessorForHTML;
import org.eclipse.wst.html.ui.internal.hyperlink.XMLHyperlinkDetector;
import org.eclipse.wst.html.ui.internal.style.LineStyleProviderForHTML;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLInformationProvider;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.javascript.ui.internal.common.contentassist.JavaScriptContentAssistProcessor;
import org.eclipse.wst.javascript.ui.internal.common.style.LineStyleProviderForJavaScript;
import org.eclipse.wst.javascript.ui.internal.common.taginfo.JavaScriptInformationProvider;
import org.eclipse.wst.javascript.ui.internal.common.taginfo.JavaScriptTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

public class StructuredTextViewerConfigurationHTML extends StructuredTextViewerConfiguration {
	/*
	 * One instance per configuration because not sourceviewer-specific and
	 * it's a String array
	 */
	private String[] fConfiguredContentTypes;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForEmbeddedCSS;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForHTML;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForJavascript;
	/*
	 * One instance per configuration
	 */
	private StructuredTextViewerConfiguration fXMLSourceViewerConfiguration;

	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);

		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}

		if (contentType == IHTMLPartitionTypes.HTML_DEFAULT || contentType == IHTMLPartitionTypes.HTML_DECLARATION) {
			allStrategies.add(new StructuredAutoEditStrategyHTML());
		}

		// be sure this is added last in list, so it has a change to modify
		// previous results.
		// add auto edit strategy that handles when tab key is pressed
		allStrategies.add(new AutoEditStrategyForTabs());

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			String[] htmlTypes = StructuredTextPartitionerForHTML.getConfiguredContentTypes();
			fConfiguredContentTypes = new String[2 + xmlTypes.length + htmlTypes.length];

			fConfiguredContentTypes[0] = IStructuredPartitionTypes.DEFAULT_PARTITION;
			fConfiguredContentTypes[1] = IStructuredPartitionTypes.UNKNOWN_PARTITION;

			int index = 0;
			System.arraycopy(xmlTypes, 0, fConfiguredContentTypes, index += 2, xmlTypes.length);
			System.arraycopy(htmlTypes, 0, fConfiguredContentTypes, index += xmlTypes.length, htmlTypes.length);
		}

		return fConfiguredContentTypes;
	}

	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		IContentAssistProcessor[] processors = null;

		if ((partitionType == IHTMLPartitionTypes.HTML_DEFAULT) || (partitionType == IHTMLPartitionTypes.HTML_COMMENT)) {
			processors = new IContentAssistProcessor[]{new HTMLContentAssistProcessor()};
		}
		else if (partitionType == IHTMLPartitionTypes.SCRIPT) {
			processors = new IContentAssistProcessor[]{new JavaScriptContentAssistProcessor()};
		}
		else if (partitionType == ICSSPartitionTypes.STYLE) {
			processors = new IContentAssistProcessor[]{new CSSContentAssistProcessor()};
		}
		else if (partitionType == IStructuredPartitionTypes.UNKNOWN_PARTITION) {
			processors = new IContentAssistProcessor[]{new NoRegionContentAssistProcessorForHTML()};
		}

		return processors;
	}

	/**
	 * Returns the content formatter ready to be used with the given source
	 * viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return a content formatter or <code>null</code> if formatting should
	 *         not be supported
	 */
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IHTMLPartitionTypes.HTML_DEFAULT);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new HTMLFormatProcessorImpl()));

		return formatter;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (contentType == IHTMLPartitionTypes.HTML_DEFAULT) {
			// use xml's doubleclick strategy
			return getXMLSourceViewerConfiguration().getDoubleClickStrategy(sourceViewer, IXMLPartitions.XML_DEFAULT);
		}
		else
			return super.getDoubleClickStrategy(sourceViewer, contentType);
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
			}
			else {
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

	protected IInformationProvider getInformationProvider(ISourceViewer sourceViewer, String partitionType) {
		IInformationProvider provider = null;
		if (partitionType == IHTMLPartitionTypes.HTML_DEFAULT) {
			// HTML
			provider = new HTMLInformationProvider();
		}
		else if (partitionType == IHTMLPartitionTypes.SCRIPT) {
			// HTML JavaScript
			provider = new JavaScriptInformationProvider();
		}
		return provider;
	}

	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == IHTMLPartitionTypes.HTML_DEFAULT || partitionType == IHTMLPartitionTypes.HTML_COMMENT || partitionType == IHTMLPartitionTypes.HTML_DECLARATION) {
			providers = new LineStyleProvider[]{getLineStyleProviderForHTML()};
		}
		else if (partitionType == IHTMLPartitionTypes.SCRIPT) {
			providers = new LineStyleProvider[]{getLineStyleProviderForJavascript()};
		}
		else if (partitionType == ICSSPartitionTypes.STYLE) {
			providers = new LineStyleProvider[]{getLineStyleProviderForEmbeddedCSS()};
		}

		return providers;
	}

	private LineStyleProvider getLineStyleProviderForEmbeddedCSS() {
		if (fLineStyleProviderForEmbeddedCSS == null) {
			fLineStyleProviderForEmbeddedCSS = new LineStyleProviderForEmbeddedCSS();
		}
		return fLineStyleProviderForEmbeddedCSS;
	}

	private LineStyleProvider getLineStyleProviderForHTML() {
		if (fLineStyleProviderForHTML == null) {
			fLineStyleProviderForHTML = new LineStyleProviderForHTML();
		}
		return fLineStyleProviderForHTML;
	}

	private LineStyleProvider getLineStyleProviderForJavascript() {
		if (fLineStyleProviderForJavascript == null) {
			fLineStyleProviderForJavascript = new LineStyleProviderForJavaScript();
		}
		return fLineStyleProviderForJavascript;
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover textHover = null;

		// look for appropriate text hover processor to return based on
		// content type and state mask
		TextHoverManager manager = SSEUIPlugin.getDefault().getTextHoverManager();
		TextHoverManager.TextHoverDescriptor[] hoverDescs = manager.getTextHovers();
		int i = 0;
		while (i < hoverDescs.length && textHover == null) {
			if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
				String hoverType = hoverDescs[i].getId();
				if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType)) {
					// check if script or html is needed
					if (contentType == IHTMLPartitionTypes.SCRIPT) {
						textHover = manager.createBestMatchHover(new JavaScriptTagInfoHoverProcessor());
					}
					else if (contentType == IHTMLPartitionTypes.HTML_DEFAULT) {
						textHover = manager.createBestMatchHover(new HTMLTagInfoHoverProcessor());
					}
				}
				else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
					// check if script or html is needed
					if (contentType == IHTMLPartitionTypes.SCRIPT) {
						textHover = new JavaScriptTagInfoHoverProcessor();
					}
					else if (contentType == IHTMLPartitionTypes.HTML_DEFAULT) {
						textHover = new HTMLTagInfoHoverProcessor();
					}
			}
			i++;
		}

		// no appropriate text hovers found, try super
		if (textHover == null) {
			textHover = super.getTextHover(sourceViewer, contentType, stateMask);
		}
		return textHover;
	}

	private StructuredTextViewerConfiguration getXMLSourceViewerConfiguration() {
		if (fXMLSourceViewerConfiguration == null) {
			fXMLSourceViewerConfiguration = new StructuredTextViewerConfigurationXML();
		}
		return fXMLSourceViewerConfiguration;
	}
}