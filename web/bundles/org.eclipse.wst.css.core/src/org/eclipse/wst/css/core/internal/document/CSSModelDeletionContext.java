/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;


/**
 * 
 */
class CSSModelDeletionContext {

	private int fOldStart = -1;
	private int fOldLength = 0;
	private int fNewStart = -1;
	private int fNewLength = 0;
	private int fLengthDifference = 0;
	// private ICSSNode fRootNode = null;
	private int fRemovedRangeBegin = -1;
	private int fRemovedRangeEnd = -1;
	private CSSNodeListImpl fNodesToBeRemoved = new CSSNodeListImpl();
	private Set fOldRegionsList;

	/**
	 * CSSModelDeletionContext constructor comment.
	 */
	CSSModelDeletionContext(ICSSNode rootNode) {
		super();

		// fRootNode = rootNode;
	}

	/**
	 * 
	 */
	boolean addNodeToBeRemoved(ICSSNode node) {
		int nNodes = fNodesToBeRemoved.getLength();
		for (int i = 0; i < nNodes; i++) {
			ICSSNode parent = fNodesToBeRemoved.item(i);
			if (CSSModelUtil.isParentOf(parent, node)) {
				return false;
			}
		}
		fNodesToBeRemoved.appendNode(node);
		return true;
	}

	/**
	 * 
	 */
	void expandRemovedRangeBegin(IStructuredDocumentRegion flatNode) {
		if (flatNode == null) {
			return;
		}
		int newBegin = flatNode.getStart(); // fOldStart == fNewStart, right??
		if (fRemovedRangeBegin < 0 || newBegin < fRemovedRangeBegin) {
			fRemovedRangeBegin = newBegin;
		}
	}

	/**
	 * 
	 */
	void expandRemovedRangeEnd(IStructuredDocumentRegion flatNode) {
		if (flatNode == null) {
			return;
		}
		int newEnd = flatNode.getEnd() + ((isOldNode(flatNode)) ? fLengthDifference : 0);
		if (fRemovedRangeEnd < 0 || fRemovedRangeEnd < newEnd) {
			fRemovedRangeEnd = newEnd;
		}
	}

	/**
	 * 
	 */
	private CSSStructuredDocumentRegionContainer findContainer(CSSNodeImpl parent, IStructuredDocumentRegion flatNode) {
		if (parent instanceof CSSStructuredDocumentRegionContainer) {
			// Am i a container of flatNode?
			IStructuredDocumentRegion firstNode = ((CSSStructuredDocumentRegionContainer) parent).getFirstStructuredDocumentRegion();
			IStructuredDocumentRegion lastNode = ((CSSStructuredDocumentRegionContainer) parent).getLastStructuredDocumentRegion();
			int firstStart = getOriginalOffset(firstNode);
			int lastStart = getOriginalOffset(lastNode);
			int start = flatNode.getStart();

			if (firstStart <= start && start <= lastStart) {
				// I am a container, is my child a container ?
				for (ICSSNode node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
					if (node instanceof CSSNodeImpl) {
						CSSStructuredDocumentRegionContainer container = findContainer((CSSNodeImpl) node, flatNode);
						if (container != null) {
							return container;
						}
					}
				}
				return (CSSStructuredDocumentRegionContainer) parent;
			}
		}

		return null;
	}

	/**
	 * 
	 */
	CSSStructuredDocumentRegionContainer findDeletionTarget(CSSNodeImpl parent, IStructuredDocumentRegion flatNode) {
		CSSStructuredDocumentRegionContainer target = findContainer(parent, flatNode);
		if (target == null) {
			return null;
		}

		// System.out.print(flatNode.toString() + ": ");

		// child a(=====)b(=====)c
		// parent (================) a,c can remove parent, but b cannot.

		ICSSNode child;

		for (child = target.getFirstChild(); child != null && !(child instanceof CSSStructuredDocumentRegionContainer); child = child.getNextSibling()) {
			// just advancing
		}

		if (child == null) {
			// System.out.println("target has no children."); // TESTBYCOZY
			return target; // has no child containers.
		}
		else {
			IStructuredDocumentRegion firstNode = ((CSSStructuredDocumentRegionContainer) child).getFirstStructuredDocumentRegion();
			if (flatNode.getStart() < getOriginalOffset(firstNode)) {
				// System.out.println("flatNode is in front of first child");
				// // TESTBYCOZY
				return target; // a
			}
		}

		for (child = target.getLastChild(); child != null && !(child instanceof CSSStructuredDocumentRegionContainer); child = child.getPreviousSibling()) {
			// just advancing
		}

		if (child == null) {
			// System.out.println("target has no children."); // TESTBYCOZY
			return target; // has no child containers.
		}
		else {
			IStructuredDocumentRegion firstNode = ((CSSStructuredDocumentRegionContainer) child).getFirstStructuredDocumentRegion();
			if (getOriginalOffset(firstNode) < flatNode.getStart()) {
				// System.out.println("flatNode is in after of last child");
				// // TESTBYCOZY
				return target; // c
			}
		}

		// System.out.println("flatNode inner of children"); // TESTBYCOZY
		return null; // b
	}

	/**
	 * 
	 */
	Iterator getNodesToBeRemoved() {
		Collection nodes = new ArrayList();
		int nNodes = fNodesToBeRemoved.getLength();
		for (int i = 0; i < nNodes; i++) {
			nodes.add(fNodesToBeRemoved.item(i));
		}
		return nodes.iterator();
	}

	/**
	 * 
	 */
	private int getOriginalOffset(IStructuredDocumentRegion flatNode) {
		int offset = 0;
		if (flatNode != null) {
			offset = flatNode.getStart();
			if (0 <= fLengthDifference) {
				if (fNewStart + fNewLength < offset) {
					offset -= fLengthDifference;
				}
			}
			else {
				if (fOldStart + fOldLength <= offset || (fNewStart < 0 && fOldStart <= offset && !isOldNode(flatNode)) || (0 <= fNewStart && fNewStart + fNewLength <= offset && !isOldNode(flatNode))) {
					offset -= fLengthDifference;
				}
			}
		}

		return offset;
	}

	/**
	 * 
	 */
	int getRemovedRangeBegin() {
		return fRemovedRangeBegin;
	}

	/**
	 * 
	 */
	int getRemovedRangeEnd() {
		return fRemovedRangeEnd;
	}

	/**
	 * 
	 */
	private boolean isOldNode(IStructuredDocumentRegion flatNode) {
		return fOldRegionsList.contains(flatNode);
	}

	/**
	 * 
	 */
	void setupContext(IStructuredDocumentRegionList newStructuredDocumentRegions, IStructuredDocumentRegionList oldStructuredDocumentRegions) {
		IStructuredDocumentRegion flatNode = null;
		fOldLength = CSSUtil.getTextLength(oldStructuredDocumentRegions);
		if (oldStructuredDocumentRegions != null && 0 < oldStructuredDocumentRegions.getLength() && (flatNode = oldStructuredDocumentRegions.item(0)) != null) {
			fOldStart = flatNode.getStart();
		}
		else {
			fOldStart = -1;
		}
		fNewLength = CSSUtil.getTextLength(newStructuredDocumentRegions);
		if (newStructuredDocumentRegions != null && 0 < newStructuredDocumentRegions.getLength() && (flatNode = newStructuredDocumentRegions.item(0)) != null) {
			fNewStart = flatNode.getStart();
			fRemovedRangeBegin = fNewStart;
			fRemovedRangeEnd = fNewStart + fNewLength;
		}
		else {
			fNewStart = -1;
			fRemovedRangeBegin = fRemovedRangeEnd = -1;
		}
		fLengthDifference = fNewLength - fOldLength;

		fOldRegionsList = new HashSet(); 
		final Enumeration elements = oldStructuredDocumentRegions.elements();
		while (elements.hasMoreElements()) {
			fOldRegionsList.add(elements.nextElement());
		}
		// cleanup nodes
		while (0 < fNodesToBeRemoved.getLength()) {
			fNodesToBeRemoved.removeNode(0);
		}
	}
}
