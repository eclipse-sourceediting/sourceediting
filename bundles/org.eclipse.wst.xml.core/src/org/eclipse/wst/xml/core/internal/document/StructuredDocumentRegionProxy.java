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
package org.eclipse.wst.xml.core.internal.document;



import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;


class StructuredDocumentRegionProxy implements IStructuredDocumentRegion {
	private IStructuredDocumentRegion flatNode = null;
	private int length = 0;

	private int offset = 0;

	/**
	 */
	StructuredDocumentRegionProxy() {
		super();
	}

	/**
	 */
	StructuredDocumentRegionProxy(int offset, int length) {
		super();

		this.offset = offset;
		this.length = length;
	}

	/**
	 */
	StructuredDocumentRegionProxy(int offset, int length, IStructuredDocumentRegion flatNode) {
		super();

		this.offset = offset;
		this.length = length;
		this.flatNode = flatNode;
		if (this.flatNode != null)
			this.offset -= this.flatNode.getStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#addRegion(com.ibm.sed.structured.text.ITextRegion)
	 */
	public void addRegion(ITextRegion aRegion) {
		// XXX Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjust(int)
	 */
	public void adjust(int i) {
		// XXX Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustLengthWith(int)
	 */
	public void adjustLengthWith(int i) {
		// XXX Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustStart(int)
	 */
	public void adjustStart(int i) {
		// XXX Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustTextLength(int)
	 */
	public void adjustTextLength(int i) {
		// XXX Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#containsOffset(int)
	 */
	public boolean containsOffset(int i) {
		// XXX Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#containsOffset(com.ibm.sed.structured.text.ITextRegion,
	 *      int)
	 */
	public boolean containsOffset(ITextRegion region, int i) {
		// XXX Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#equatePositions(com.ibm.sed.structured.text.ITextRegion)
	 */
	public void equatePositions(ITextRegion region) {
		// XXX Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getDeepestRegionAtCharacterOffset(int)
	 */
	public ITextRegion getDeepestRegionAtCharacterOffset(int offset) {
		// XXX Auto-generated method stub
		return null;
	}

	/**
	 */
	public int getEnd() {
		int flatNodeOffset = 0;
		if (this.flatNode != null)
			flatNodeOffset = this.flatNode.getStart();
		return flatNodeOffset + this.offset + this.length;
	}

	/**
	 */
	public int getEndOffset() {
		return getEnd();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getEndOffset(com.ibm.sed.structured.text.ITextRegion)
	 */
	public int getEndOffset(ITextRegion containedRegion) {
		// XXX Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getFirstRegion()
	 */
	public ITextRegion getFirstRegion() {
		// XXX Auto-generated method stub
		return null;
	}

	/**
	 */
	public String getFullText() {
		return getText();
	}

	/**
	 */
	public String getFullText(ITextRegion aRegion) {
		// not supported
		return null;
	}

	/**
	 */
	public String getFullText(String context) {
		// not supported
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getLastRegion()
	 */
	public ITextRegion getLastRegion() {
		// XXX Auto-generated method stub
		return null;
	}

	/** 
	 */
	public int getLength() {
		return this.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#getNext()
	 */
	public IStructuredDocumentRegion getNext() {
		// XXX Auto-generated method stub
		return null;
	}

	/** 
	 */
	public int getNumberOfRegions() {
		// not supported
		return 0;
	}

	/** 
	 */
	int getOffset() {
		int flatNodeOffset = 0;
		if (this.flatNode != null)
			flatNodeOffset = this.flatNode.getStart();
		return flatNodeOffset + this.offset;
	}

	/**
	 */
	public ITextRegionContainer getParent() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#getParentDocument()
	 */
	public IStructuredDocument getParentDocument() {
		// XXX Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#getPrevious()
	 */
	public IStructuredDocumentRegion getPrevious() {
		// XXX Auto-generated method stub
		return null;
	}

	/**
	 */
	public ITextRegion getRegionAtCharacterOffset(int offset) {
		// not supported
		return null;
	}

	/**
	 */
	public ITextRegionList getRegions() {
		// not supported
		return null;
	}

	/**
	 */
	public int getStart() {
		int flatNodeOffset = 0;
		if (this.flatNode != null)
			flatNodeOffset = this.flatNode.getStart();
		return flatNodeOffset + this.offset;
	}

	/**
	 */
	public int getStartOffset() {
		return getStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getStartOffset(com.ibm.sed.structured.text.ITextRegion)
	 */
	public int getStartOffset(ITextRegion containedRegion) {
		// XXX Auto-generated method stub
		return 0;
	}

	/**
	 */
	public IStructuredDocument getStructuredDocument() {
		// not supported
		return null;
	}

	/** 
	 */
	IStructuredDocumentRegion getStructuredDocumentRegion() {
		return this.flatNode;
	}

	/**
	 */
	public String getText() {
		if (this.flatNode == null)
			return new String();
		String text = this.flatNode.getText();
		if (text == null)
			return new String();
		int end = this.offset + this.length;
		return text.substring(this.offset, end);
	}

	/**
	 */
	public String getText(ITextRegion aRegion) {
		// not supported
		return null;
	}

	/**
	 */
	public String getText(String context) {
		// not supported
		return null;
	}

	/**
	 */
	public int getTextEnd() {
		return getEnd();
	}

	/**
	 */
	public int getTextEndOffset() {
		return getTextEnd();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getTextEndOffset(com.ibm.sed.structured.text.ITextRegion)
	 */
	public int getTextEndOffset(ITextRegion containedRegion) {
		// XXX Auto-generated method stub
		return 0;
	}

	/**
	 * The text length is equal to length if there is no white space at the
	 * end of a region. Otherwise it is smaller than length.
	 */
	public int getTextLength() {
		return getLength();
	}

	/**
	 */
	public String getType() {
		return "StructuredDocumentRegionProxy";//$NON-NLS-1$
	}

	public boolean isDeleted() {
		// I'll assume never really needed here
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#isEnded()
	 */
	public boolean isEnded() {
		// XXX Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#sameAs(com.ibm.sed.structured.text.IStructuredDocumentRegion,
	 *      int)
	 */
	public boolean sameAs(IStructuredDocumentRegion region, int shift) {
		// XXX Auto-generated method stub
		return false;
	}

	/**
	 */
	public boolean sameAs(ITextRegion region, int shift) {
		// not supported
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#sameAs(com.ibm.sed.structured.text.ITextRegion,
	 *      com.ibm.sed.structured.text.IStructuredDocumentRegion,
	 *      com.ibm.sed.structured.text.ITextRegion, int)
	 */
	public boolean sameAs(ITextRegion oldRegion, IStructuredDocumentRegion documentRegion, ITextRegion newRegion, int shift) {
		// XXX Auto-generated method stub
		return false;
	}

	public void setDeleted(boolean deleted) {
		// I'll assume never really needed here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setEnded(boolean)
	 */
	public void setEnded(boolean hasEnd) {
		// XXX Auto-generated method stub

	}

	/**
	 * had to make public, due to API transition.
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setNext(com.ibm.sed.structured.text.IStructuredDocumentRegion)
	 */
	public void setNext(IStructuredDocumentRegion newNext) {
		// XXX Auto-generated method stub

	}

	/**
	 */
	void setOffset(int offset) {
		this.offset = offset;
		if (this.flatNode != null)
			this.offset -= this.flatNode.getStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setParentDocument(com.ibm.sed.structured.text.IStructuredDocument)
	 */
	public void setParentDocument(IStructuredDocument document) {
		// XXX Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setPrevious(com.ibm.sed.structured.text.IStructuredDocumentRegion)
	 */
	public void setPrevious(IStructuredDocumentRegion newPrevious) {
		// XXX Auto-generated method stub

	}

	/**
	 */
	public void setRegions(ITextRegionList embeddedRegions) {
		// not supported
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setStart(int)
	 */
	public void setStart(int newStart) {
		// XXX Auto-generated method stub

	}

	/**
	 */
	void setStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		if (this.flatNode != null)
			this.offset += this.flatNode.getStart();
		this.flatNode = flatNode;
		if (this.flatNode != null)
			this.offset -= flatNode.getStart();
	}

	/**
	 * toString method
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[');
		buffer.append(getStart());
		buffer.append(',');
		buffer.append(getEnd());
		buffer.append(']');
		buffer.append('(');
		if (this.flatNode != null)
			buffer.append(this.flatNode.toString());
		else
			buffer.append("null");//$NON-NLS-1$
		buffer.append(')');
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#updateModel(java.lang.Object,
	 *      com.ibm.sed.structured.text.IStructuredDocumentRegion,
	 *      java.lang.String, int, int)
	 */
	public StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion flatnode, String changes, int start, int end) {
		// XXX Auto-generated method stub
		return null;
	}
}
