/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import java.util.Vector;

import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;


class StructuredDocumentRegionContainer implements IStructuredDocumentRegion {

	private Vector flatNodes = new Vector(2);

	/**
	 */
	StructuredDocumentRegionContainer() {
		super();
	}


	public void addRegion(ITextRegion aRegion) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$

	}

	public void adjust(int i) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$

	}

	public void adjustLength(int i) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$

	}

	public void adjustStart(int i) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$

	}

	public void adjustTextLength(int i) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$

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

	public boolean containsOffset(int i) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public boolean containsOffset(ITextRegion region, int i) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void equatePositions(ITextRegion region) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$

	}

	public ITextRegion getDeepestRegionAtCharacterOffset(int offset) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
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

	public int getEndOffset(ITextRegion containedRegion) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public ITextRegion getFirstRegion() {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
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
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	/**
	 */
	public String getFullText(String context) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public ITextRegion getLastRegion() {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

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

	public IStructuredDocumentRegion getNext() {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public int getNumberOfRegions() {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public ITextRegionContainer getParent() {
		return null;
	}

	public IStructuredDocument getParentDocument() {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegion getPrevious() {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	/**
	 */
	public ITextRegion getRegionAtCharacterOffset(int offset) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	/**
	 */
	public ITextRegionList getRegions() {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
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

	public int getStartOffset(ITextRegion containedRegion) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
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
			return NodeImpl.EMPTY_STRING;
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
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	/**
	 */
	public String getText(String context) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
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

	public int getTextEndOffset(ITextRegion containedRegion) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
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

	public boolean isEnded() {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
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

	public boolean sameAs(IStructuredDocumentRegion region, int shift) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	/**
	 */
	public boolean sameAs(ITextRegion region, int shift) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public boolean sameAs(ITextRegion oldRegion, IStructuredDocumentRegion documentRegion, ITextRegion newRegion, int shift) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setEnded(boolean hasEnd) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setLength(int newLength) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setNext(IStructuredDocumentRegion newNext) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setParentDocument(IStructuredDocument document) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setPrevious(IStructuredDocumentRegion newPrevious) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	/**
	 */
	public void setRegions(ITextRegionList embeddedRegions) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setStart(int newStart) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
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

	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion flatnode, String changes, int start, int end) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}


	public boolean isDeleted() {
		// if someone "gets" these temp regions by
		// accident, we'll always return "deleted".
		return true;
	}


	public void setDeleted(boolean deleted) {
		// do nothing

	}
}
