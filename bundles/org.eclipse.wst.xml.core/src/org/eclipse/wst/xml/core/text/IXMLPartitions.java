package org.eclipse.wst.xml.core.text;



/**
 * This interface is not intended to be implemented.
 * It defines the partition types for XML.
 * Clients should reference the partition type Strings defined here directly.
 * 
 * @since 1.0
 */
public interface IXMLPartitions {
	
	String XML_DEFAULT = "org.eclipse.wst.xml.XML_DEFAULT"; //$NON-NLS-1$
	String XML_CDATA = "org.eclipse.wst.xml.XML_CDATA"; //$NON-NLS-1$
	String XML_PI = "org.eclipse.wst.xml.XML_PI"; //$NON-NLS-1$
	String XML_DECLARATION = "org.eclipse.wst.xml.XML_DECL"; //$NON-NLS-1$
	String XML_COMMENT = "org.eclipse.wst.xml.XML_COMMENT"; //$NON-NLS-1$
	
	/**
	 * Should match
	 * org.eclipse.wst.sse.core.dtd.partitioning.StructuredTextPartitionerForDTD.ST_DTD_SUBSET
	 */
	String DTD_SUBSET = "org.eclipse.wst.xml.dtd.internal_subset"; //$NON-NLS-1$
}
