/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.util;

import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.TextRegionListImpl;


public class ZeroStructuredDocumentRegion implements IStructuredDocumentRegion {
	private int length = 0;

	private int offset = 0;
	private IStructuredDocument fParentDocument;

	/**
	 */
	public ZeroStructuredDocumentRegion(IStructuredDocument document, int start) {
		super();
		fParentDocument = document;
		offset = start;
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

	public boolean containsOffset(int i) {
		return offset <= i && i < getEndOffset();
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

	public int getEnd() {
		return offset + length;
	}

	public int getEndOffset() {
		return getEnd();
	}

	public int getEndOffset(ITextRegion containedRegion) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public ITextRegion getFirstRegion() {
		return null;
	}

	public String getFullText() {
		return getText();
	}

	public String getFullText(ITextRegion aRegion) {
		return getText();
	}

	/**
	 */
	public String getFullText(String context) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public ITextRegion getLastRegion() {
		return null;
	}

	public int getLength() {
		return this.length;
	}

	public IStructuredDocumentRegion getNext() {
		return getParentDocument().getFirstStructuredDocumentRegion();
	}

	public int getNumberOfRegions() {
		return 0;
	}

	int getOffset() {
		return offset;
	}

	/**
	 */
	public ITextRegionContainer getParent() {
		return null;
	}

	public IStructuredDocument getParentDocument() {
		return fParentDocument;
	}

	public IStructuredDocumentRegion getPrevious() {
		return null; //$NON-NLS-1$
	}

	/**
	 */
	public ITextRegion getRegionAtCharacterOffset(int offset) {
		return null; //$NON-NLS-1$
	}

	/**
	 */
	public ITextRegionList getRegions() {
		return new TextRegionListImpl(); //$NON-NLS-1$
	}

	/**
	 */
	public int getStart() {
		return this.offset;
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
		return getParentDocument();
	}

	/**
	 */
	public String getText() {
		return new String();
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
		return getLength();
	}

	/**
	 */
	public String getType() {
		return "ZeroStructuredDocumentRegion";//$NON-NLS-1$
	}

	public boolean isDeleted() {
		return false;
	}

	public boolean isEnded() {
		return true;
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

	public void setDeleted(boolean deleted) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setEnded(boolean hasEnd) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	/**
	 * had to make public, due to API transition.
	 */
	public void setLength(int length) {
		this.length = length;
	}

	public void setNext(IStructuredDocumentRegion newNext) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	/**
	 */
	void setOffset(int offset) {
		this.offset = offset;
	}

	public void setParentDocument(IStructuredDocument document) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setPrevious(IStructuredDocumentRegion newPrevious) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setRegions(ITextRegionList embeddedRegions) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public void setStart(int newStart) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}

	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion flatnode, String changes, int start, int end) {
		throw new Error("intentionally not implemented since should never be called"); //$NON-NLS-1$
	}
}
