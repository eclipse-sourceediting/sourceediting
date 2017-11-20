/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.provisional.text;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.text.rules.IStructuredTypedRegion;


/**
 * A partitioner interface required for handling the embedded content type 
 * properly. 
 * 
 * ISSUE: need to investigate necessity of these methods
 * 
 * @plannedfor 1.0
 */

public interface IStructuredTextPartitioner extends IDocumentPartitioner {

	/**
	 * Used by JSP partitioner to ensure that the partitioner of the
	 * embedded content type gets to create the partition in case the specific
	 * classes are important.
	 * 
	 * ISSUE: investigate if we really need this...
	 */
	IStructuredTypedRegion createPartition(int offset, int length, String partitionType);

	/**
	 * Returns the Default partition type for this partitioner.
	 * <p>
	 * eg:
	 * <br><code>org.eclipse.wst.xml.core.text.IXMLPartitions.XML_DEFAULT</code>
	 * <br><code>org.eclipse.wst.html.core.text.IHTMLPartitions.HTML_DEFAULT</code>
	 * <br><code>org.eclipse.wst.jsp.core.text.IJSPPartitions.JSP_DEFAULT</code>
	 * </p>
	 * @see org.eclipse.wst.sse.core.text.IStructuredPartitions
	 * @see org.eclipse.wst.xml.core.text.IXMLPartitions
	 * @see org.eclipse.wst.html.core.text.IHTMLPartitions
	 * @see org.eclipse.wst.jsp.core.text.IJSPPartitions
	 * 
	 * @return the Default partition type for this partitioner.
	 */
	String getDefaultPartitionType();

	/**
	 * Returns the particular partition type for the given region/offset.
	 * <p>
	 * eg:
	 * <br><code>org.eclipse.wst.xml.core.text.IXMLPartitions.XML_DEFAULT</code>
	 * <br><code>org.eclipse.wst.html.core.text.IHTMLPartitions.HTML_DEFAULT</code>
	 * <br><code>org.eclipse.wst.jsp.core.text.IJSPPartitions.JSP_DEFAULT</code>
	 * </p>
	 * 
	 * @param region of the IStructuredDocument
	 * @param offset in the IStructuredDoucment
	 * @return the particular partition type for the given region/offset.
	 */
	String getPartitionType(ITextRegion region, int offset);

	/**
	 * Returns the partition type String of the IStructuredDocumentRegion 
	 * between the 2 region parameters.
	 * Useful for finding the partition type of a 0 length region.
	 * eg.
	 * <pre>
	 * 	<script type="text/javascript">|</script>
	 * </pre>
	 * @param previousNode
	 * @param nextNode
	 * @return the partition type of the node between previousNode and nextNode
	 * @deprecated move to IDocumentPartitionerExtension2 ->
	 *  String getContentType(int offset, boolean preferOpenPartitions);
	 */
	String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, IStructuredDocumentRegion nextNode);
}
