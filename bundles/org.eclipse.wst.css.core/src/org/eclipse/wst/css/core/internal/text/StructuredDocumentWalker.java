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
package org.eclipse.wst.css.core.internal.text;



import java.util.Enumeration;

import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.util.Utilities;

public class StructuredDocumentWalker {

	IStructuredDocumentRegionList fOldStructuredDocumentRegionList = null;
	IStructuredDocument fStructuredDocument = null;
	String fOriginalChanges = null;
	int fOriginalOffset = 0;
	int fOriginalLengthToReplace = 0;
	int fLengthDifference = 0;

	/**
	 * 
	 */
	public StructuredDocumentWalker() {
	}

	/**
	 * 
	 */
	public StructuredDocumentWalker(StructuredDocumentEvent event) {
		initialize(event);
	}

	/**
	 * 
	 */
	public IStructuredDocument getStructuredDocument() {
		return fStructuredDocument;
	}

	/**
	 * Old Nodes: --[O1]--[O2]--[O3]--- : / \ Current Nodes:
	 * ---[C1]-----[C2]-----[C3]-----[C4]---
	 * 
	 * Input: O1 -> Output: O2 Input: O2 -> Output: O3 Input: O3 -> Output: C4
	 * Input: Cn -> Output: Cn+1
	 */
	public IStructuredDocumentRegion getNextNode(IStructuredDocumentRegion node) {
		IStructuredDocumentRegion nextNode = null;
		if (node != null) {
			nextNode = node.getNext();
			if (nextNode == null) {
				if (isOldNode(node)) {
					// this may be the end of old flatnodes
					int newStart = node.getEnd() + fLengthDifference;
					nextNode = fStructuredDocument.getRegionAtCharacterOffset(newStart);
				}
			}
		}
		return nextNode;
	}

	/**
	 * Old Nodes: --[O1]--[O2]--[O3]--- : / \ Current Nodes:
	 * ---[C1]-----[C2]-----[C3]-----[C4]---
	 * 
	 * Input: O* -> Output: C4 Input: Cn -> Output: Cn+1
	 */
	public IStructuredDocumentRegion getNextNodeInCurrent(IStructuredDocumentRegion node) {
		IStructuredDocumentRegion nextNode = null;
		if (isOldNode(node)) {
			IStructuredDocumentRegion oldEndNode = fOldStructuredDocumentRegionList.item(fOldStructuredDocumentRegionList.getLength() - 1);
			int newStart = oldEndNode.getEnd() + fLengthDifference;
			nextNode = fStructuredDocument.getRegionAtCharacterOffset(newStart);
		}
		else if (node != null) {
			nextNode = node.getNext();
		}
		return nextNode;
	}

	/**
	 * Old Nodes: --[O1]--[O2]--[O3]--- : / \ Current Nodes:
	 * ---[C1]-----[C2]-----[C3]-----[C4]---
	 * 
	 * Input: O1 -> Output: C1 Input: O2 -> Output: O1 Input: O3 -> Output: O2
	 * Input: Cn -> Output: Cn-1
	 */
	public IStructuredDocumentRegion getPrevNode(IStructuredDocumentRegion node) {
		IStructuredDocumentRegion prevNode = null;
		if (node != null) {
			prevNode = node.getPrevious();
			if (prevNode == null) {
				if (isOldNode(node)) {
					// this may be the start of old flatnodes
					int newEnd = node.getStart() - 1;
					prevNode = fStructuredDocument.getRegionAtCharacterOffset(newEnd);
				}
			}
		}
		return prevNode;
	}

	/**
	 * Old Nodes: --[O1]--[O2]--[O3]--- : / \ Current Nodes:
	 * ---[C1]-----[C2]-----[C3]-----[C4]---
	 * 
	 * Input: O* -> Output: C1 Input: Cn -> Output: Cn-1
	 */
	public IStructuredDocumentRegion getPrevNodeInCurrent(IStructuredDocumentRegion node) {
		IStructuredDocumentRegion prevNode = null;
		if (isOldNode(node)) {
			IStructuredDocumentRegion oldStartNode = fOldStructuredDocumentRegionList.item(0);
			int newEnd = oldStartNode.getStart() - 1;
			prevNode = fStructuredDocument.getRegionAtCharacterOffset(newEnd);
		}
		else if (node != null) {
			prevNode = node.getPrevious();
		}
		return prevNode;
	}

	/**
	 * 
	 */
	public void initialize(StructuredDocumentEvent event) {
		fStructuredDocument = event.getStructuredDocument();
		fOriginalChanges = event.getText();
		fOriginalOffset = event.getOffset();
		fOriginalLengthToReplace = event.getLength();
		fLengthDifference = Utilities.calculateLengthDifference(fOriginalChanges, fOriginalLengthToReplace);

		if (event instanceof StructuredDocumentRegionsReplacedEvent) {
			fOldStructuredDocumentRegionList = ((StructuredDocumentRegionsReplacedEvent) event).getOldStructuredDocumentRegions();
		}
		else {
			fOldStructuredDocumentRegionList = null;
		}
	}

	/**
	 * 
	 */
	public boolean isOldNode(IStructuredDocumentRegion node) {
		boolean bOld = false;
		if (fOldStructuredDocumentRegionList != null) {
			Enumeration e = fOldStructuredDocumentRegionList.elements();
			while (e.hasMoreElements()) {
				if (e.nextElement() == node) {
					bOld = true;
					break;
				}
			}
		}
		return bOld;
	}
}
