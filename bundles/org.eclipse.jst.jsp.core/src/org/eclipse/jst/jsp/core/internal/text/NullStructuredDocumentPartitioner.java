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
package org.eclipse.jst.jsp.core.internal.text;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.IStructuredTypedRegion;

/**
 * To be used when no known partitioner is available.
 * Always returns the unknown type.
 */
public class NullStructuredDocumentPartitioner implements IStructuredTextPartitioner {

	public class NullStructuredTypedRegion implements IStructuredTypedRegion {

		private int fOffset;

		private int fLength;

		private String fType;

		public void setType(String type) {
			fType = type;
		}

		public void setLength(int length) {
			fLength = length;

		}

		public void setOffset(int offset) {
			fOffset = offset;
		}

		public String getType() {
			return fType;
		}

		public int getLength() {
			return fLength;
		}

		public int getOffset() {
			return fOffset;
		}

	}

	public final static String ST_UNKNOWN_PARTITION = "com.ibm.sse.UNKNOWN_PARTITION_TYPE"; //$NON-NLS-1$
	private final String[] legalTypes = new String[]{ST_UNKNOWN_PARTITION};

	public NullStructuredDocumentPartitioner() {
		super();
	}

	public void connect(IDocument document) {
		// nothing to do
	}

	public IStructuredTypedRegion createPartition(int offset, int length, String type) {
		IStructuredTypedRegion result = new NullStructuredTypedRegion();
		result.setOffset(offset);
		result.setLength(length);
		result.setType(type);
		return result;
	}

	public void disconnect() {
		// nothing to do
	}

	public String getDefaultPartitionType() {
		return ST_UNKNOWN_PARTITION;
	}

	public String[] getLegalContentTypes() {
		return legalTypes;
	}

	public String getPartitionType(ITextRegion region, int offset) {
		return ST_UNKNOWN_PARTITION;
	}

	public String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, IStructuredDocumentRegion nextNode) {
		return ST_UNKNOWN_PARTITION;
	}

	public ITypedRegion[] computePartitioning(int offset, int length) {
		ITypedRegion[] alwaysOne = new ITypedRegion[]{createPartition(offset, length, ST_UNKNOWN_PARTITION)};
		return alwaysOne;
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	public boolean documentChanged(DocumentEvent event) {
		return false;
	}

	public String getContentType(int offset) {
		return getDefaultPartitionType();
	}

	public ITypedRegion getPartition(int offset) {
		return createPartition(offset, 1, getDefaultPartitionType());
	}

}