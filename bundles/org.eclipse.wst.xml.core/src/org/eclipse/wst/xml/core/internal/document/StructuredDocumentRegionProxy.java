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
package org.eclipse.wst.xml.core.internal.document;



import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;

class StructuredDocumentRegionProxy implements IStructuredDocumentRegion {

	private int offset = 0;
	private int length = 0;
	private IStructuredDocumentRegion flatNode = null;

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

	/** 
	 */
	public int getLength() {
		return this.length;
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

	/**
	 * The text length is equal to length if there is no white space at the end of a region.
	 * Otherwise it is smaller than length.
	 */
	public int getTextLength() {
		return getLength();
	}

	/**
	 */
	public String getType() {
		return "StructuredDocumentRegionProxy";//$NON-NLS-1$
	}

	/**
	 */
	public boolean sameAs(ITextRegion region, int shift) {
		// not supported
		return false;
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
	 * had to make public, due to API transition.  
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 */
	void setOffset(int offset) {
		this.offset = offset;
		if (this.flatNode != null)
			this.offset -= this.flatNode.getStart();
	}

	/**
	 */
	public void setRegions(ITextRegionList embeddedRegions) {
		// not supported
	}

	/**
	 * toString method
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

	/* (non-Javadoc)
	 */
	public void addRegion(ITextRegion aRegion) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public void adjustLengthWith(int i) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public void adjustStart(int i) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public IStructuredDocumentRegion getNext() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 */
	public IStructuredDocument getParentDocument() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 */
	public IStructuredDocumentRegion getPrevious() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 */
	public boolean isEnded() {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 */
	public boolean sameAs(IStructuredDocumentRegion region, int shift) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 */
	public boolean sameAs(ITextRegion oldRegion, IStructuredDocumentRegion documentRegion, ITextRegion newRegion, int shift) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 */
	public void setEnded(boolean hasEnd) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public void setNext(IStructuredDocumentRegion newNext) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public void setParentDocument(IStructuredDocument document) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public void setPrevious(IStructuredDocumentRegion newPrevious) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public void setStart(int newStart) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion flatnode, String changes, int start, int end) {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 */
	public boolean containsOffset(int i) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 */
	public boolean containsOffset(ITextRegion region, int i) {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 */
	public int getEndOffset(ITextRegion containedRegion) {
		// XXX Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 */
	public ITextRegion getFirstRegion() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 */
	public ITextRegion getLastRegion() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 */
	public int getStartOffset(ITextRegion containedRegion) {
		// XXX Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 */
	public int getTextEndOffset(ITextRegion containedRegion) {
		// XXX Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 */
	public void adjust(int i) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public void equatePositions(ITextRegion region) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 */
	public ITextRegion getDeepestRegionAtCharacterOffset(int offset) {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 */
	public void adjustTextLength(int i) {
		// XXX Auto-generated method stub

	}

}
