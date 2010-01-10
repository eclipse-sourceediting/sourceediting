/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *     David Carver - Intalio - general clean up of unused imports.
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.internal;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider; // import
																			// org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;
import org.eclipse.wst.xsl.core.internal.text.IXSLPartitions;
import org.eclipse.wst.xsl.core.internal.text.rules.StructuredTextPartitionerForXSL;
import org.eclipse.wst.xsl.ui.internal.contentassist.ContentAssistProcessorFactory;
import org.eclipse.wst.xsl.ui.internal.editor.XSLHyperlinkDetector;
import org.eclipse.wst.xsl.ui.internal.style.LineStyleProviderForXSL;

/**
 * StructuredTextViewerConfigurationXSL implements content assistance for
 * attributes and other XPath related functionality.
 * 
 * @author dcarver
 * 
 */
public class StructuredTextViewerConfigurationXSL extends
		StructuredTextViewerConfigurationXML {

	private String[] fConfiguredContentTypes;
	private LineStyleProvider fLineStyleProviderForXSL;
//	private LineStyleProvider fLineStyleProviderForXML;

	/**
	 * Configuration for XSL Content Types
	 */
	public StructuredTextViewerConfigurationXSL() {
		super();
	}

	/**
	 * Return the processors for the current content type.
	 */
	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors(
			ISourceViewer sourceViewer, String partitionType) {
		IContentAssistProcessor[] processors = null;

		if ((partitionType.equals(IStructuredPartitions.DEFAULT_PARTITION))
				|| (partitionType.equals(IXMLPartitions.XML_DEFAULT))
				|| (partitionType.equals(IXSLPartitions.XSL_XPATH))) {
				processors = ContentAssistProcessorFactory.createProcessors();		
		} else if (partitionType.equals(IStructuredPartitions.UNKNOWN_PARTITION)) {
			processors = new IContentAssistProcessor[] { new NoRegionContentAssistProcessor() };
		}
		return processors;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null
				|| !fPreferenceStore
						.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;
		IHyperlinkDetector[] superDetectors = super
				.getHyperlinkDetectors(sourceViewer);
		IHyperlinkDetector[] allDetectors = new IHyperlinkDetector[superDetectors.length + 1];
		allDetectors[0] = new XSLHyperlinkDetector();
		System.arraycopy(superDetectors, 0, allDetectors, 1,
				superDetectors.length);
		return allDetectors;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			String[] xslTypes = StructuredTextPartitionerForXSL
					.getConfiguredContentTypes();
			fConfiguredContentTypes = new String[xslTypes.length + 2];
			fConfiguredContentTypes[0] = IStructuredPartitions.DEFAULT_PARTITION;
			fConfiguredContentTypes[1] = IStructuredPartitions.UNKNOWN_PARTITION;
			int index = 0;
			System.arraycopy(xslTypes, 0, fConfiguredContentTypes, index += 2,
					xslTypes.length);
		}
		return fConfiguredContentTypes;
	}

	@Override
	public LineStyleProvider[] getLineStyleProviders(
			ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;
        if (isXMLPartition(partitionType)) {
    		providers = new LineStyleProvider[] {getLineStyleProviderForXSL()};
		}
		return providers;
	}

	private boolean isXMLPartition(String partitionType) {
		return partitionType.equals(IXMLPartitions.XML_DEFAULT)
				|| partitionType.equals(IXMLPartitions.XML_CDATA)
				|| partitionType.equals(IXMLPartitions.XML_COMMENT)
				|| partitionType.equals(IXMLPartitions.XML_DECLARATION)
				|| partitionType.equals(IXMLPartitions.XML_PI)
				|| partitionType.equals(IXSLPartitions.XSL_XPATH);
	}
	
	protected LineStyleProvider getLineStyleProviderForXSL() {
		if (fLineStyleProviderForXSL == null) {
			fLineStyleProviderForXSL = new LineStyleProviderForXSL();
		}
		return fLineStyleProviderForXSL;
	}
	
}
