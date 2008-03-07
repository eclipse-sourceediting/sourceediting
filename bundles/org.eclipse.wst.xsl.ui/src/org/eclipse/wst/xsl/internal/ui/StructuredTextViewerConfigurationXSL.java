package org.eclipse.wst.xsl.internal.ui;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.contentassist.NoRegionContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.eclipse.wst.xsl.internal.ui.contentassist.XSLContentAssistProcessor;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.Compiler;

/**
 * StructuredTextViewerConfigurationXSL implements content assistance
 * for attributes and other XPath related functionality.
 * 
 * @author dcarver
 *
 */
public class StructuredTextViewerConfigurationXSL extends
		StructuredTextViewerConfigurationXML {

	/**
	 * Configuration for XSL Content Types
	 */
	public StructuredTextViewerConfigurationXSL() {
		super();
	}

	/**
	 *  Return the processors for the current content type.
	 */
	@SuppressWarnings("restriction")
	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors(
			ISourceViewer sourceViewer, String partitionType) {
		IContentAssistProcessor[] processors = null;

		if ((partitionType == IStructuredPartitions.DEFAULT_PARTITION) || (partitionType == IXMLPartitions.XML_DEFAULT)) {
			processors = new IContentAssistProcessor[]{new XSLContentAssistProcessor()};
		}
		else if (partitionType == IStructuredPartitions.UNKNOWN_PARTITION) {
			processors = new IContentAssistProcessor[]{new NoRegionContentAssistProcessor()};
		}
		return processors;
	}
	
}
