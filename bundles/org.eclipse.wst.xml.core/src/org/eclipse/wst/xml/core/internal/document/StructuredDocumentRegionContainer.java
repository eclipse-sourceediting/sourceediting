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



import java.util.Vector;

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;


class StructuredDocumentRegionContainer implements IStructuredDocumentRegion {

	private Vector flatNodes = new Vector(2);

	/**
	 */
	StructuredDocumentRegionContainer() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#addRegion(com.ibm.sed.structured.text.ITextRegion)
	 */
	public void addRegion(ITextRegion aRegion) {
		throw new Error("intentionally not implemented since should never be called");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjust(int)
	 */
	public void adjust(int i) {
		throw new Error("intentionally not implemented since should never be called");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustLengthWith(int)
	 */
	public void adjustLength(int i) {
		throw new Error("intentionally not implemented since should never be called");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustStart(int)
	 */
	public void adjustStart(int i) {
		throw new Error("intentionally not implemented since should never be called");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustTextLength(int)
	 */
	public void adjustTextLength(int i) {
		throw new Error("intentionally not implemented since should never be called");

	}

	/**
	 */
	void appendStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return;
		if (flatNode instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
			if (container.getStructuredDocumentRegionCount() > 0) {
				this.flatNodes.addAll(container.flatNodes);
			}
		}
		else {
			this.flatNodes.addElement(flatNode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#containsOffset(int)
	 */
	public boolean containsOffset(int i) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#containsOffset(com.ibm.sed.structured.text.ITextRegion,
	 *      int)
	 */
	public boolean containsOffset(ITextRegion region, int i) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#equatePositions(com.ibm.sed.structured.text.ITextRegion)
	 */
	public void equatePositions(ITextRegion region) {
		throw new Error("intentionally not implemented since should never be called");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getDeepestRegionAtCharacterOffset(int)
	 */
	public ITextRegion getDeepestRegionAtCharacterOffset(int offset) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public int getEnd() {
		IStructuredDocumentRegion last = getLastStructuredDocumentRegion();
		if (last == null)
			return 0;
		return last.getEnd();
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
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getFirstRegion()
	 */
	public ITextRegion getFirstRegion() {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		if (this.flatNodes.isEmpty())
			return null;
		return (IStructuredDocumentRegion) this.flatNodes.elementAt(0);
	}

	/**
	 */
	public String getFullText() {
		return getText();
	}

	/**
	 */
	public String getFullText(ITextRegion aRegion) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public String getFullText(String context) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegionCollection#getLastRegion()
	 */
	public ITextRegion getLastRegion() {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		int size = this.flatNodes.size();
		if (size == 0)
			return null;
		return (IStructuredDocumentRegion) this.flatNodes.elementAt(size - 1);
	}

	/** 
	 */
	public int getLength() {
		return (getEnd() - getStart());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#getNext()
	 */
	public IStructuredDocumentRegion getNext() {
		throw new Error("intentionally not implemented since should never be called");
	}

	/** 
	 */
	public int getNumberOfRegions() {
		throw new Error("intentionally not implemented since should never be called");
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
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#getPrevious()
	 */
	public IStructuredDocumentRegion getPrevious() {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public ITextRegion getRegionAtCharacterOffset(int offset) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public ITextRegionList getRegions() {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public int getStart() {
		IStructuredDocumentRegion first = getFirstStructuredDocumentRegion();
		if (first == null)
			return 0;
		return first.getStart();
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
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public IStructuredDocument getStructuredDocument() {
		IStructuredDocumentRegion first = getFirstStructuredDocumentRegion();
		if (first == null)
			return null;
		return first.getParentDocument();
	}

	/**
	 */
	IStructuredDocumentRegion getStructuredDocumentRegion(int index) {
		if (index < 0 || index >= this.flatNodes.size())
			return null;
		return (IStructuredDocumentRegion) this.flatNodes.elementAt(index);
	}

	/**
	 */
	int getStructuredDocumentRegionCount() {
		return this.flatNodes.size();
	}

	/**
	 */
	public String getText() {
		int size = this.flatNodes.size();
		if (size == 0)
			return new String();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size; i++) {
			IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) this.flatNodes.elementAt(i);
			if (flatNode == null)
				continue;
			buffer.append(flatNode.getText());
		}
		return buffer.toString();
	}

	/**
	 */
	public String getText(ITextRegion aRegion) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public String getText(String context) {
		throw new Error("intentionally not implemented since should never be called");
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
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 * The text length is equal to length if there is no white space at the
	 * end of a region. Otherwise it is smaller than length.
	 */
	public int getTextLength() {
		return (getTextEnd() - getStart());
	}

	/**
	 */
	public String getType() {
		return "StructuredDocumentRegionContainer";//$NON-NLS-1$
	}

	/**
	 */
	void insertStructuredDocumentRegion(IStructuredDocumentRegion flatNode, int index) {
		if (flatNode == null)
			return;
		if (index < 0)
			return;
		int size = this.flatNodes.size();
		if (index > size)
			return;
		if (index == size) {
			appendStructuredDocumentRegion(flatNode);
			return;
		}
		this.flatNodes.insertElementAt(flatNode, index);
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
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	IStructuredDocumentRegion removeStructuredDocumentRegion(int index) {
		if (index < 0 || index >= this.flatNodes.size())
			return null;
		IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) this.flatNodes.elementAt(index);
		this.flatNodes.removeElementAt(index);
		return flatNode;
	}

	/**
	 */
	IStructuredDocumentRegion removeStructuredDocumentRegion(IStructuredDocumentRegion oldStructuredDocumentRegion) {
		if (oldStructuredDocumentRegion == null)
			return null;
		int size = this.flatNodes.size();
		for (int i = 0; i < size; i++) {
			IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) this.flatNodes.elementAt(i);
			if (flatNode == oldStructuredDocumentRegion) {
				this.flatNodes.removeElementAt(i);
				return flatNode;
			}
		}
		return null; // not found
	}

	/**
	 */
	IStructuredDocumentRegion replaceStructuredDocumentRegion(IStructuredDocumentRegion flatNode, int index) {
		if (flatNode == null)
			return removeStructuredDocumentRegion(index);
		if (index < 0 || index >= this.flatNodes.size())
			return null;
		IStructuredDocumentRegion oldStructuredDocumentRegion = (IStructuredDocumentRegion) this.flatNodes.elementAt(index);
		this.flatNodes.setElementAt(flatNode, index);
		return oldStructuredDocumentRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#sameAs(com.ibm.sed.structured.text.IStructuredDocumentRegion,
	 *      int)
	 */
	public boolean sameAs(IStructuredDocumentRegion region, int shift) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public boolean sameAs(ITextRegion region, int shift) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#sameAs(com.ibm.sed.structured.text.ITextRegion,
	 *      com.ibm.sed.structured.text.IStructuredDocumentRegion,
	 *      com.ibm.sed.structured.text.ITextRegion, int)
	 */
	public boolean sameAs(ITextRegion oldRegion, IStructuredDocumentRegion documentRegion, ITextRegion newRegion, int shift) {
		throw new Error("intentionally not implemented since should never be called");
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
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setLength(int)
	 */
	public void setLength(int newLength) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setNext(com.ibm.sed.structured.text.IStructuredDocumentRegion)
	 */
	public void setNext(IStructuredDocumentRegion newNext) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setParentDocument(com.ibm.sed.structured.text.IStructuredDocument)
	 */
	public void setParentDocument(IStructuredDocument document) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setPrevious(com.ibm.sed.structured.text.IStructuredDocumentRegion)
	 */
	public void setPrevious(IStructuredDocumentRegion newPrevious) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 */
	public void setRegions(ITextRegionList embeddedRegions) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.IStructuredDocumentRegion#setStart(int)
	 */
	public void setStart(int newStart) {
		throw new Error("intentionally not implemented since should never be called");
	}

	/**
	 * toString method
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('{');
		int count = getStructuredDocumentRegionCount();
		for (int i = 0; i < count; i++) {
			if (i != 0)
				buffer.append(',');
			IStructuredDocumentRegion flatNode = getStructuredDocumentRegion(i);
			if (flatNode == null)
				buffer.append("null");//$NON-NLS-1$
			else
				buffer.append(flatNode.toString());
		}
		buffer.append('}');
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#updateModel(java.lang.Object,
	 *      com.ibm.sed.structured.text.IStructuredDocumentRegion,
	 *      java.lang.String, int, int)
	 */
	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion flatnode, String changes, int start, int end) {
		throw new Error("intentionally not implemented since should never be called");
	}
}
