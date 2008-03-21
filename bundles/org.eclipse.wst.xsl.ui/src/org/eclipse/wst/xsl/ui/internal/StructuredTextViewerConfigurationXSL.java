/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.internal;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;
import org.eclipse.wst.xsl.ui.internal.contentassist.XSLContentAssistProcessor;
import org.eclipse.wst.xsl.ui.internal.editor.XSLHyperlinkDetector;

/**
 * StructuredTextViewerConfigurationXSL implements content assistance for
 * attributes and other XPath related functionality.
 * 
 * @author dcarver
 * 
 */
public class StructuredTextViewerConfigurationXSL extends StructuredTextViewerConfigurationXML
{

	/**
	 * Configuration for XSL Content Types
	 */
	public StructuredTextViewerConfigurationXSL()
	{
		super();
	}

	/**
	 * Return the processors for the current content type.
	 */
	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType)
	{
		IContentAssistProcessor[] processors = null;

		if ((partitionType == IStructuredPartitions.DEFAULT_PARTITION) || (partitionType == IXMLPartitions.XML_DEFAULT))
		{
			processors = new IContentAssistProcessor[] { new XSLContentAssistProcessor() };
		}
		else if (partitionType == IStructuredPartitions.UNKNOWN_PARTITION)
		{
			processors = new IContentAssistProcessor[] { new NoRegionContentAssistProcessor() };
		}
		return processors;
	}

	/** (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextSourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer)
	{
		if (sourceViewer == null || !fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;
		IHyperlinkDetector[] superDetectors = super.getHyperlinkDetectors(sourceViewer);
		IHyperlinkDetector[] allDetectors = new IHyperlinkDetector[superDetectors.length + 1];
		allDetectors[0] = new XSLHyperlinkDetector();
		System.arraycopy(superDetectors, 0, allDetectors, 1, superDetectors.length);
		return allDetectors;
	}
}
