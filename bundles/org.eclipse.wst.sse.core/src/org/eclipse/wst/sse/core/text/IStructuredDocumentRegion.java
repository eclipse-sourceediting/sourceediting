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
package org.eclipse.wst.sse.core.text;

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;



/**
 * A ITextRegionCollection is a collection of ITextRegions. It is a 
 * structural unit, but a minimal one. For example, in might 
 * consist of a "start tag" but not a whole XML node.
 */
public interface IStructuredDocumentRegion extends ITextRegionCollection {



	IStructuredDocumentRegion getNext();

	IStructuredDocumentRegion getPrevious();

	boolean isEnded();

	IStructuredDocument getParentDocument();

	/**
	 * For use by parsers and reparsers only.
	 */
	void addRegion(ITextRegion aRegion);

	/**
	 * For use by parsers and reparsers only.
	 */
	void adjustLengthWith(int i);

	/**
	 * For use by parsers and reparsers only.
	 */
	void adjustStart(int i);

	/**
	 * For use by parsers and reparsers only.
	 */
	void setEnded(boolean hasEnd);

	/**
	 * For use by parsers and reparsers only.
	 */
	void setLength(int newLength);

	/**
	 * For use by parsers and reparsers only.
	 */
	void setNext(IStructuredDocumentRegion newNext);

	/**
	 * For use by parsers and reparsers only.
	 */
	void setPrevious(IStructuredDocumentRegion newPrevious);

	/**
	 * For use by parsers and reparsers only.
	 */
	void setStart(int newStart);

	/**
	 * For use by parsers and reparsers only.
	 */
	StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion flatnode, String changes, int start, int end);

	/**
	 * For use by parsers and reparsers only.
	 */
	void setParentDocument(IStructuredDocument document);

	boolean sameAs(IStructuredDocumentRegion region, int shift);

	boolean sameAs(ITextRegion oldRegion, IStructuredDocumentRegion documentRegion, ITextRegion newRegion, int shift);
}
