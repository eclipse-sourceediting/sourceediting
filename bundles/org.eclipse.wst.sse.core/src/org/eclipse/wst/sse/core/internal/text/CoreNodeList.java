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
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.text;



import java.util.Enumeration;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;


public class CoreNodeList implements IStructuredDocumentRegionList {
	int countedLength;
	int currentIndex = -1;

	IStructuredDocumentRegion[] flatNodes;
	IStructuredDocumentRegion head;

	/**
	 * CoreNodeList constructor comment.
	 */
	public CoreNodeList() {
		super();
		// create an array, even if zero length
		flatNodes = new IStructuredDocumentRegion[0];
	}

	public CoreNodeList(IStructuredDocumentRegion newHead) {
		super();
		// save head
		head = newHead;
		int count = 0;
		IStructuredDocumentRegion countNode = newHead;
		// we have to go through the list once, to get its
		// length in order to create the array
		while (countNode != null) {
			count++;
			countNode = countNode.getNext();
		}
		// create an array, even if zero length
		flatNodes = new IStructuredDocumentRegion[count];
		// start countNode over again, so to speak.
		countNode = newHead;
		count = 0;
		while (countNode != null) {
			flatNodes[count++] = countNode;
			countNode = countNode.getNext();
		}
		if (count > 0) {
			currentIndex = 0;
			// else it stays at -1 initialized at object creation
			//
			// save length
			countedLength = count;
		}
	}

	public CoreNodeList(IStructuredDocumentRegion start, IStructuredDocumentRegion end) {
		super();
		// save head
		head = start;
		int count = 0;
		IStructuredDocumentRegion countNode = start;
		if ((start == null) || (end == null)) {
			// error condition
			//throw new IllegalArgumentException("Must provide start and end
			// nodes to construct CoreNodeList");
		} else {
			count = 1;
			while ((countNode != null) && (countNode != end)) {
				count++;
				countNode = countNode.getNext();
			}
		}
		// if we ended because the last one was null,
		// backup one.
		if (countNode == null)
			count--;
		if (count < 0) {
			count = 0;
		}
		flatNodes = new IStructuredDocumentRegion[count];
		if (count > 0) {
			flatNodes[0] = countNode = start;
			for (int i = 1; i < count; i++) {
				flatNodes[i] = flatNodes[i - 1].getNext();
			}

		}
		currentIndex = 0;
		countedLength = count;
	}

	public Enumeration elements() {
		StructuredDocumentRegionEnumeration result = null;
		if ((flatNodes != null) && (flatNodes.length > 0))
			result = new StructuredDocumentRegionEnumeration(flatNodes[0], flatNodes[flatNodes.length - 1]);
		else
			result = new StructuredDocumentRegionEnumeration(null);
		return result;
	}

	public int getLength() {
		return flatNodes.length;
	}

	public boolean includes(Object o) {
		if (flatNodes == null)
			return false;
		for (int i = 0; i < flatNodes.length; i++)
			if (flatNodes[i] == o)
				return true;
		return false;
	}

	public IStructuredDocumentRegion item(int i) {
		return flatNodes[i];
	}
}
