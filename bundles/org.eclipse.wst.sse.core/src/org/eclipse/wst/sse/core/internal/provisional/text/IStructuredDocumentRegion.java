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
package org.eclipse.wst.sse.core.internal.provisional.text;

/**
 * A ITextRegionCollection is a collection of ITextRegions. It is a structural
 * unit, but a minimal one. For example, in might consist of a "start tag" but
 * not a whole XML element.
 */
public interface IStructuredDocumentRegion extends ITextRegionCollection {

	/**
	 * Adds a text region to the end of the collection of regions contained by
	 * this region. It is the parsers responsibility to make sure its a
	 * correct region (that is, its start offset is one greater than previous
	 * regions end offset)
	 * 
	 * For use by parsers and reparsers only.
	 */
	void addRegion(ITextRegion aRegion);

	/**
	 * Returns the structured document region that follows this one or null if
	 * at end of document.
	 * 
	 * @return the structured document region that follows this one.
	 * 
	 * ISSUE: for thread safety, this should be more restrictive.
	 */
	IStructuredDocumentRegion getNext();

	/**
	 * Returns this regions parent document.
	 * 
	 * @return this regions parent document.
	 */
	IStructuredDocument getParentDocument();

	/**
	 * Returns the structured document region that preceeds this one or null
	 * if at beginning of document.
	 * 
	 * @return the structured document region that follows this one.
	 * 
	 * ISSUE: for thread safety, this should be more restrictive.
	 */
	IStructuredDocumentRegion getPrevious();

	/**
	 * Returns true if this document has been deleted, and is no longer part
	 * of the actual structured document. This field can be used in
	 * multi-threaded operations, which may retrieve a long list of regions
	 * and be iterating through them at the same time the document is
	 * modified, and regions deleted.
	 * 
	 * @return true if this region is no longer part of document.
	 */
	boolean isDeleted();

	/**
	 * Returns true if this structured document region was ended "naturally"
	 * by syntactic rules, or if simply assumed to end so another could be
	 * started.
	 * 
	 * @return true if region has syntactic end.
	 */
	boolean isEnded();

	/**
	 * Tests is region is equal to this one, ignoring position offsets of
	 * shift.
	 * 
	 * For use by parsers and reparsers only.
	 * 
	 * @param region
	 * @param shift
	 * @return
	 */
	boolean sameAs(IStructuredDocumentRegion region, int shift);

	/**
	 * Tests if <code>oldRegion</code> is same as <code>newRegion</code>,
	 * ignoring position offsets of <code>shift</code>.
	 * 
	 * ISSUE: which document region are old and new in?
	 * 
	 * For use by parsers and reparsers only.
	 * 
	 * @param oldRegion
	 * @param documentRegion
	 * @param newRegion
	 * @param shift
	 * @return
	 */
	boolean sameAs(ITextRegion oldRegion, IStructuredDocumentRegion documentRegion, ITextRegion newRegion, int shift);

	/**
	 * Set to true if/when this region is removed from a document, during the
	 * process of re-parsing.
	 * 
	 * For use by parsers and reparsers only.
	 * 
	 * @param deleted
	 */
	void setDeleted(boolean deleted);

	/**
	 * Set to true by parser/reparser if region deemed to end syntactically.
	 * 
	 * For use by parsers and reparsers only.
	 * 
	 * @param hasEnd
	 */
	void setEnded(boolean hasEnd);

	/**
	 * Sets length of region.
	 * 
	 * For use by parsers and reparsers only.
	 */
	void setLength(int newLength);

	/**
	 * Assigns pointer to next region, or null if last region.
	 * 
	 * For use by parsers and reparsers only.
	 */
	void setNext(IStructuredDocumentRegion newNext);

	/**
	 * Assigns parent documnet.
	 * 
	 * For use by parsers and reparsers only.
	 */
	void setParentDocument(IStructuredDocument document);

	/**
	 * Assigns pointer to previous region, or null if first region.
	 * 
	 * For use by parsers and reparsers only.
	 */
	void setPrevious(IStructuredDocumentRegion newPrevious);

	/**
	 * Sets start offset of region, relative to beginning of document.
	 * 
	 * For use by parsers and reparsers only.
	 */
	void setStart(int newStart);



}
