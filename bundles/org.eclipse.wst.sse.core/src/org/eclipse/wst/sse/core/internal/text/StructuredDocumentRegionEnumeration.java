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
import java.util.NoSuchElementException;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.util.Debug;


public class StructuredDocumentRegionEnumeration implements Enumeration {

	private int count;
	private IStructuredDocumentRegion head;
	private IStructuredDocumentRegion oldHead;

	/**
	 * StructuredDocumentRegionEnumeration constructor comment.
	 */
	public StructuredDocumentRegionEnumeration(IStructuredDocumentRegion newHead) {
		super();
		IStructuredDocumentRegion countNode = head = newHead;
		while (countNode != null) {
			count++;
			countNode = countNode.getNext();
		}
		if (Debug.DEBUG > 5) {
			System.out.println("N Nodes in StructuredDocumentRegionEnumeration Contructor: " + count); //$NON-NLS-1$
		}
	}

	/**
	 * StructuredDocumentRegionEnumeration constructor comment.
	 */
	public StructuredDocumentRegionEnumeration(IStructuredDocumentRegion start, IStructuredDocumentRegion end) {
		super();
		IStructuredDocumentRegion countNode = head = start;
		if ((start == null) || (end == null)) {
			// error condition
			count = 0;
			return;
		}
		//If both nodes are non-null, we assume there is always at least one
		// item
		count = 1;
		while (countNode != end) {
			count++;
			countNode = countNode.getNext();
		}
		if (org.eclipse.wst.sse.core.internal.util.Debug.DEBUG > 5) {
			System.out.println("N Nodes in StructuredDocumentRegionEnumeration Contructor: " + count); //$NON-NLS-1$
		}
	}

	/**
	 * hasMoreElements method comment.
	 */
	public synchronized boolean hasMoreElements() {
		return count > 0;
	}

	/**
	 * nextElement method comment.
	 */
	public synchronized Object nextElement() {
		if (count > 0) {
			count--;
			oldHead = head;
			head = head.getNext();
			return oldHead;
		}
		throw new NoSuchElementException("StructuredDocumentRegionEnumeration"); //$NON-NLS-1$
	}
}
