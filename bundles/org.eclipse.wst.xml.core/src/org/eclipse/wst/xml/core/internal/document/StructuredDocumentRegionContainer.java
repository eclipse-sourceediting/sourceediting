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

	/**
	 */
	IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		if (this.flatNodes.isEmpty())
			return null;
		return (IStructuredDocumentRegion) this.flatNodes.elementAt(0);
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

	/** 
	 */
	public int getNumberOfRegions() {
		// not supported
		return 0;
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

	/**
	 */
	public boolean sameAs(ITextRegion region, int shift) {
		// not support
		return false;
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
	public void setLength(int newLength) {
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
