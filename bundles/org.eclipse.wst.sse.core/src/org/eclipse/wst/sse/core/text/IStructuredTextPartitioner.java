/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;


/**
 * A partitioner interface required by the StructuredTextPartitionerForJSP
 * for handling the embedded content type properly.  This has not yet been
 * finalized.
 */

public interface IStructuredTextPartitioner extends IDocumentPartitioner {
	void connect(IDocument document);

	/**
	 * @deprecated - not sure why we needed to make 
	 * this part of interface ... but probably shouldn't be.
	 */
	StructuredTypedRegion createPartition(int offset, int length, String type);

	void disconnect();

	String getDefault();

	String getPartitionType(ITextRegion region, int offset);

	String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, ITextRegion previousStartTagNameRegion, IStructuredDocumentRegion nextNode, ITextRegion nextEndTagNameRegion);

	String[] getLegalContentTypes();
}
