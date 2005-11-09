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
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xml.core.internal.provisional.text.IXMLPartitions;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.internal.autoedit.AutoEditStrategyForTabs;
import org.eclipse.wst.xml.ui.internal.autoedit.StructuredAutoEditStrategyXML;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.doubleclick.XMLDoubleClickStrategy;
import org.eclipse.wst.xml.ui.internal.hyperlink.XMLHyperlinkDetector;
import org.eclipse.wst.xml.ui.internal.style.LineStyleProviderForXML;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLInformationProvider;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLTagInfoHoverProcessor;

/**
 * This class provides a SourceViewerConfiguration for editing XML content
 * type. Not intended to be subclassed.
 * 
 * @plannedfor 1.0
 */
public class StructuredTextViewerConfigurationXML extends StructuredTextViewerConfiguration {
	/*
	 * One instance per configuration because not sourceviewer-specific and
	 * it's a String array
	 */
	private String[] fConfiguredContentTypes;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForXML;

	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);

		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}

		if (contentType == IXMLPartitions.XML_DEFAULT) {
			allStrategies.add(new StructuredAutoEditStrategyXML());
		}

		// be sure this is last, so it can modify any results form previous
		// commands that might on on same partiion type.
		// add auto edit strategy that handles when tab key is pressed
		allStrategies.add(new AutoEditStrategyForTabs());

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		if (fConfiguredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			fConfiguredContentTypes = new String[xmlTypes.length + 2];
			fConfiguredContentTypes[0] = IStructuredPartitionTypes.DEFAULT_PARTITION;
			fConfiguredContentTypes[1] = IStructuredPartitionTypes.UNKNOWN_PARTITION;
			int index = 0;
			System.arraycopy(xmlTypes, 0, fConfiguredContentTypes, index += 2, xmlTypes.length);
		}
		return fConfiguredContentTypes;
	}

	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		IContentAssistProcessor[] processors = null;

		if ((partitionType == IStructuredPartitionTypes.DEFAULT_PARTITION) || (partitionType == IXMLPartitions.XML_DEFAULT)) {
			processors = new IContentAssistProcessor[]{new XMLContentAssistProcessor()};
		}
		else if (partitionType == IStructuredPartitionTypes.UNKNOWN_PARTITION) {
			processors = new IContentAssistProcessor[]{new NoRegionContentAssistProcessor()};
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
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IXMLPartitions.XML_DEFAULT);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new FormatProcessorXML()));

		return formatter;
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {

		ITextDoubleClickStrategy doubleClickStrategy = null;
		if (contentType.compareTo(IXMLPartitions.XML_DEFAULT) == 0)
			doubleClickStrategy = new XMLDoubleClickStrategy();
		else
			doubleClickStrategy = super.getDoubleClickStrategy(sourceViewer, contentType);
		return doubleClickStrategy;
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
		Preferences preferences = XMLCorePlugin.getDefault().getPluginPreferences();
		int indentationWidth = preferences.getInt(XMLCorePreferenceNames.INDENTATION_SIZE);
		String indentCharPref = preferences.getString(XMLCorePreferenceNames.INDENTATION_CHAR);
		boolean useSpaces = XMLCorePreferenceNames.SPACE.equals(indentCharPref);

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
		if ((partitionType == IStructuredPartitionTypes.DEFAULT_PARTITION) || (partitionType == IXMLPartitions.XML_DEFAULT)) {
			provider = new XMLInformationProvider();
		}
		return provider;
	}

	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == IXMLPartitions.XML_DEFAULT || partitionType == IXMLPartitions.XML_CDATA || partitionType == IXMLPartitions.XML_COMMENT || partitionType == IXMLPartitions.XML_DECLARATION || partitionType == IXMLPartitions.XML_PI) {
			providers = new LineStyleProvider[]{getLineStyleProviderForXML()};
		}

		return providers;
	}

	private LineStyleProvider getLineStyleProviderForXML() {
		if (fLineStyleProviderForXML == null)
			fLineStyleProviderForXML = new LineStyleProviderForXML();
		return fLineStyleProviderForXML;
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover textHover = null;

		// look for appropriate text hover processor to return based on
		// content type and state mask
		if ((contentType == IStructuredPartitionTypes.DEFAULT_PARTITION) || (contentType == IXMLPartitions.XML_DEFAULT)) {
			// check which of xml's text hover is handling stateMask
			TextHoverManager manager = SSEUIPlugin.getDefault().getTextHoverManager();
			TextHoverManager.TextHoverDescriptor[] hoverDescs = manager.getTextHovers();
			int i = 0;
			while (i < hoverDescs.length && textHover == null) {
				if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
					String hoverType = hoverDescs[i].getId();
					if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
						textHover = manager.createBestMatchHover(new XMLTagInfoHoverProcessor());
					else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType))
						textHover = new XMLTagInfoHoverProcessor();
				}
				i++;
			}
		}

		// no appropriate text hovers found, try super
		if (textHover == null)
			textHover = super.getTextHover(sourceViewer, contentType, stateMask);

		return textHover;
	}
}
