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
package org.eclipse.wst.sse.core.internal.document;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;

/**
 * To be used when no known partitioner is available. Always returns the
 * unknown type.
 */
public class NullStructuredDocumentPartitioner implements IDocumentPartitioner {

	public class NullStructuredTypedRegion implements ITypedRegion {

		private int fLength;

		private int fOffset;

		private String fType;

		public int getLength() {
			return fLength;
		}

		public int getOffset() {
			return fOffset;
		}

		public String getType() {
			return fType;
		}

		public void setLength(int length) {
			fLength = length;

		}

		public void setOffset(int offset) {
			fOffset = offset;
		}

		public void setType(String type) {
			fType = type;
		}

	}

	public final static String ST_UNKNOWN_PARTITION = "org.eclipse.wst.sse.core.UNKNOWN_PARTITION_TYPE"; //$NON-NLS-1$
	private final String[] legalTypes = new String[]{ST_UNKNOWN_PARTITION};

	public NullStructuredDocumentPartitioner() {
		super();
	}

	public ITypedRegion[] computePartitioning(int offset, int length) {
		ITypedRegion[] alwaysOne = new ITypedRegion[]{createPartition(offset, length, ST_UNKNOWN_PARTITION)};
		return alwaysOne;
	}

	public void connect(IDocument document) {
		// nothing to do
	}

	public ITypedRegion createPartition(int offset, int length, String type) {
		ITypedRegion result = new TypedRegion(offset, length, type);
		return result;
	}

	public void disconnect() {
		// nothing to do
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	public boolean documentChanged(DocumentEvent event) {
		return false;
	}

	public String getContentType(int offset) {
		return getDefault();
	}

	public String getDefault() {
		return ST_UNKNOWN_PARTITION;
	}

	public String[] getLegalContentTypes() {
		return legalTypes;
	}

	public ITypedRegion getPartition(int offset) {
		return createPartition(offset, 1, getDefault());
	}

	public String getPartitionType(IRegion region, int offset) {
		return ST_UNKNOWN_PARTITION;
	}

}
