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
package org.eclipse.wst.sse.core.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;


/**
 * A partitioner interface required by the StructuredTextPartitionerForJSP for
 * handling the embedded content type properly. This has not yet been
 * finalized.
 */

public interface IStructuredTextPartitioner extends IDocumentPartitioner {
	void connect(IDocument document);

	/**
	 * @deprecated - (dmw) not sure why we needed to make this part of
	 *             interface ... but probably shouldn't be.
	 * 
	 * (nsd) Used by JSP partitioner to ensure that the partitioner of the
	 * embedded content type gets to create the partition in case the specific
	 * classes are important.
	 */
	StructuredTypedRegion createPartition(int offset, int length, String type);

	void disconnect();

	String getDefault();

	String[] getLegalContentTypes();

	String getPartitionType(ITextRegion region, int offset);

	String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, ITextRegion previousStartTagNameRegion, IStructuredDocumentRegion nextNode, ITextRegion nextEndTagNameRegion);
}
