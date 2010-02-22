/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (Intalio) - bug 300430 - String concatenation
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.text;



import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.internal.util.Utilities;


public class BasicStructuredDocumentRegion implements IStructuredDocumentRegion {
	private static final String TEXT_STORE_NOT_ASSIGNED = "text store not assigned yet"; //$NON-NLS-1$
	private static final String UNDEFINED = "org.eclipse.wst.sse.core.structuredDocument.UNDEFINED"; //$NON-NLS-1$

	private ITextRegionList _regions;
	/**
	 * has this region been removed from its document
	 */
	private static final byte MASK_IS_DELETED = 1;
	/**
	 * was this region terminated normally
	 */
	private static final byte MASK_IS_ENDED = 1 << 1;

	private byte fIsDeletedOrEnded = 0;

	/**
	 * allow a pointer back to this nodes model
	 */
	private IStructuredDocument fParentDocument;
	
	protected int fLength;
	private IStructuredDocumentRegion next = null;
	private IStructuredDocumentRegion previous = null;
	protected int start;

	public BasicStructuredDocumentRegion() {
		super();
		_regions = new TextRegionListImpl();

	}

	/**
	 * Even inside-this class uses of 'regions' should use this method, as
	 * this is where (soft) memory management/reparsing, etc., will be
	 * centralized.
	 */
	private ITextRegionList _getRegions() {

		return _regions;
	}

	public void addRegion(ITextRegion aRegion) {
		_getRegions().add(aRegion);
	}

	public void adjust(int i) {
		start += i;
	}

	public void adjustLength(int i) {
		fLength += i;
	}

	public void adjustStart(int i) {
		start += i;
	}

	public void adjustTextLength(int i) {
		// not supported

	}

	public boolean containsOffset(int i) {

		return getStartOffset() <= i && i < getEndOffset();
	}

	public boolean containsOffset(ITextRegion containedRegion, int offset) {
		return getStartOffset(containedRegion) <= offset && offset < getEndOffset(containedRegion);
	}

	public void equatePositions(ITextRegion region) {
		start = region.getStart();
		fLength = region.getLength();
	}

	/**
	 * getEnd and getEndOffset are the same only for
	 * IStructuredDocumentRegions
	 */
	public int getEnd() {
		return start + fLength;
	}

	/**
	 * getEnd and getEndOffset are the same only for
	 * IStructuredDocumentRegions
	 */
	public int getEndOffset() {
		return getEnd();
	}

	public int getEndOffset(ITextRegion containedRegion) {
		return getStartOffset(containedRegion) + containedRegion.getLength();
	}

	public ITextRegion getFirstRegion() {
		if (_getRegions() == null)
			return null;
		return _getRegions().get(0);
	}

	public String getFullText() {
		String result = ""; //$NON-NLS-1$
		try {
			result = getParentDocument().get(start, fLength);
		}
		catch (BadLocationException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
		return result;
	}

	public String getFullText(ITextRegion aRegion) {
		String result = ""; //$NON-NLS-1$
		try {
			int regionStart = aRegion.getStart();
			int regionLength = aRegion.getLength();
			result = fParentDocument.get(start + regionStart, regionLength);
		}
		catch (BadLocationException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
		return result;
	}

	public String getFullText(String context) {
		// DMW: looping is faster than enumeration,
		// so switched around 2/12/03
		// Enumeration e = getRegions().elements();
		ITextRegion region = null;
		String result = ""; //$NON-NLS-1$
		int length = getRegions().size();
		StringBuffer sb = new StringBuffer(result);
		for (int i = 0; i < length; i++) {
			region = getRegions().get(i);
			if (region.getType() == context)
				sb.append(getFullText(region));
		}
		result = sb.toString();
		return result;
	}

	public ITextRegion getLastRegion() {
		if (_getRegions() == null)
			return null;
		return _getRegions().get(_getRegions().size() - 1);
	}

	public int getLength() {
		return fLength;
	}

	public IStructuredDocumentRegion getNext() {
		return next;
	}

	public int getNumberOfRegions() {
		return _getRegions().size();
	}

	public IStructuredDocument getParentDocument() {

		return fParentDocument;
	}

	public IStructuredDocumentRegion getPrevious() {
		return previous;
	}

	/**
	 * The parameter offset refers to the overall offset in the document.
	 */
	public ITextRegion getRegionAtCharacterOffset(int offset) {
		if (_getRegions() != null) {
			int thisStartOffset = getStartOffset();
			if (offset < thisStartOffset)
				return null;
			int thisEndOffset = getStartOffset() + getLength();
			if (offset > thisEndOffset)
				return null;
			// transform the requested offset to the "scale" that
			// regions are stored in, which are all relative to the
			// start point.
			// int transformedOffset = offset - getStartOffset();
			//
			ITextRegionList regions = getRegions();
			int length = regions.size();
			int low = 0;
			int high = length;
			int mid = 0;
			// Binary search for the region
			while (low < high) {
				mid = low + ((high - low) >> 1);
				ITextRegion region = regions.get(mid);
				if (Debug.debugStructuredDocument) {
					System.out.println("region(s) in IStructuredDocumentRegion::getRegionAtCharacterOffset: " + region); //$NON-NLS-1$
					System.out.println("       requested offset: " + offset); //$NON-NLS-1$
					// System.out.println(" transformedOffset: " +
					// transformedOffset); //$NON-NLS-1$
					System.out.println("       region start: " + region.getStart()); //$NON-NLS-1$
					System.out.println("       region end: " + region.getEnd()); //$NON-NLS-1$
					System.out.println("       region type: " + region.getType()); //$NON-NLS-1$
					System.out.println("       region class: " + region.getClass()); //$NON-NLS-1$

				}
				// Region is before this one
				if (offset < region.getStart() + thisStartOffset)
					high = mid;
				else if (offset > (region.getEnd() + thisStartOffset - 1))
					low = mid + 1;
				else
					return region;
			}
		}
		return null;
	}

	public ITextRegionList getRegions() {
		return _getRegions();
	}

	/**
	 * getStart and getStartOffset are the same only for
	 * IStrucutredDocumentRegions
	 */
	public int getStart() {
		return start;
	}

	/**
	 * getStart and getStartOffset are the same only for
	 * IStrucutredDocumentRegions
	 */
	public int getStartOffset() {
		return getStart();
	}

	public int getStartOffset(ITextRegion containedRegion) {
		// assert: containedRegion can not be null
		// (might be performance hit if literally put in assert call,
		// but containedRegion can not be null). Needs to be checked
		// by calling code.
		return getStartOffset() + containedRegion.getStart();
	}

	public String getText() {
		String result = null;
		try {
			if (fParentDocument == null) {
				// likely to happen during inspecting
				result = TEXT_STORE_NOT_ASSIGNED;
			}
			else {
				result = fParentDocument.get(start, fLength);
			}
		}
		catch (BadLocationException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
		return result;
	}

	public String getText(ITextRegion aRegion) {
		// assert: aRegion can not be null
		// (might be performance hit if literally put in assert call,
		// but aRegion can not be null). Needs to be checked
		// by calling code.
		try {
			return fParentDocument.get(this.getStartOffset(aRegion), aRegion.getTextLength());
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Returns the text of the first region with the matching context type
	 */
	public String getText(String context) {
		// DMW: looping is faster than enumeration,
		// so switched around 2/12/03
		// Enumeration e = getRegions().elements();
		ITextRegion region = null;
		String result = ""; //$NON-NLS-1$
		int length = getRegions().size();
		for (int i = 0; i < length; i++) {
			region = getRegions().get(i);
			if (region.getType() == context) {
				result = getText(region);
				break;
			}
		}
		return result;
	}

	public int getTextEnd() {
		return start + fLength;
	}

	/**
	 * @return int
	 */
	public int getTextEndOffset() {
		ITextRegion region = _getRegions().get(_getRegions().size() - 1);
		return getStartOffset() + region.getTextEnd();
	}

	public int getTextEndOffset(ITextRegion containedRegion) {
		return getStartOffset(containedRegion) + containedRegion.getTextLength();
	}

	public int getTextLength() {
		return fLength;
	}

	/**
	 * Provides the type of IStructuredDocumentRegion ... not to be confused
	 * with type of XML node! This is subclassed, if something other than type
	 * of first region is desired.
	 * 
	 */
	public String getType() {
		String result = UNDEFINED;
		ITextRegionList subregions = getRegions();
		if (subregions != null && subregions.size() > 0) {
			ITextRegion firstRegion = subregions.get(0);
			if (firstRegion != null) {
				result = firstRegion.getType();
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocumentRegion#isDeleted()
	 */
	public boolean isDeleted() {
		return (fIsDeletedOrEnded & MASK_IS_DELETED) != 0 || (fParentDocument == null);
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isEnded() {
		return (fIsDeletedOrEnded & MASK_IS_ENDED) != 0;
	}

	public boolean sameAs(IStructuredDocumentRegion region, int shift) {
		boolean result = false;
		// if region == null, we return false;
		if (region != null) {
			// if the regions are the same instance, they are equal
			if (this == region) {
				result = true;
			}
			else {
				// this is the non-trivial part
				// note: we change for type first, then start offset and end
				// offset,
				// since that would decide many cases right away and avoid the
				// text comparison
				if (getType() == region.getType()) {
					if (sameOffsetsAs(region, shift) && sameTextAs(region, shift)) {
						result = true;
					}
				}

			}
		}
		return result;
	}

	public boolean sameAs(ITextRegion oldRegion, IStructuredDocumentRegion newDocumentRegion, ITextRegion newRegion, int shift) {
		boolean result = false;
		// if any region is null, we return false (even if both are!)
		if ((oldRegion != null) && (newRegion != null)) {
			// if the regions are the same instance, they are equal
			if (oldRegion == newRegion) {
				result = true;
			}
			else {
				// this is the non-trivial part
				// note: we change for type first, then start offset and end
				// offset,
				// since that would decide many cases right away and avoid the
				// text comparison
				if (oldRegion.getType() == newRegion.getType()) {
					if (sameOffsetsAs(oldRegion, newDocumentRegion, newRegion, shift)) {
						if (sameTextAs(oldRegion, newDocumentRegion, newRegion, shift)) {
							result = true;
						}
					}
				}
			}

		}

		return result;
	}

	private boolean sameOffsetsAs(IStructuredDocumentRegion region, int shift) {
		if (getStartOffset() == region.getStartOffset() - shift) {
			if (getEndOffset() == region.getEndOffset() - shift) {
				return true;
			}
		}
		return false;
	}

	private boolean sameOffsetsAs(ITextRegion oldRegion, IStructuredDocumentRegion documentRegion, ITextRegion newRegion, int shift) {
		if (getStartOffset(oldRegion) == documentRegion.getStartOffset(newRegion) - shift) {
			if (getEndOffset(oldRegion) == documentRegion.getEndOffset(newRegion) - shift) {
				return true;
			}
		}
		return false;
	}

	private boolean sameTextAs(IStructuredDocumentRegion region, int shift) {
		boolean result = false;
		try {
			if (getText().equals(region.getText())) {
				result = true;
			}
		}
		// ISSUE: we should not need this
		catch (StringIndexOutOfBoundsException e) {
			result = false;
		}

		return result;
	}

	private boolean sameTextAs(ITextRegion oldRegion, IStructuredDocumentRegion documentRegion, ITextRegion newRegion, int shift) {
		boolean result = false;

		if (getText(oldRegion).equals(documentRegion.getText(newRegion))) {
			result = true;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocumentRegion#setDelete(boolean)
	 */
	public void setDeleted(boolean isDeleted) {
		if (isDeleted)
			fIsDeletedOrEnded |= MASK_IS_DELETED;
		else
			fIsDeletedOrEnded &= ~MASK_IS_DELETED;
	}

	/**
	 * 
	 * @param newHasEnd
	 *            boolean
	 */
	public void setEnded(boolean newHasEnd) {
		if (newHasEnd)
			fIsDeletedOrEnded |= MASK_IS_ENDED;
		else
			fIsDeletedOrEnded &= ~MASK_IS_ENDED;
	}

	public void setLength(int newLength) {
		// textLength = newLength;
		fLength = newLength;
	}

	public void setNext(IStructuredDocumentRegion newNext) {
		next = newNext;
	}

	public void setParentDocument(IStructuredDocument document) {
		fParentDocument = document;

	}

	public void setPrevious(IStructuredDocumentRegion newPrevious) {
		previous = newPrevious;
	}

	public void setRegions(ITextRegionList containedRegions) {
		_regions = containedRegions;
	}

	public void setStart(int newStart) {
		start = newStart;
	}

	public String toString() {
		// NOTE: if the document held by any region has been updated and the
		// region offsets have not
		// yet been updated, the output from this method invalid.
		// Also note, this method can not be changed, without "breaking"
		// unit tests, since some of them compare current results to previous
		// results.
		String result = null;
		result = "[" + getStart() + ", " + getEnd() + "] (" + getText() + ")"; //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		return result;
	}

	private void updateDownStreamRegions(ITextRegion changedRegion, int lengthDifference) {
		int listLength = _getRegions().size();
		int startIndex = 0;
		// first, loop through to find index of where to start
		for (int i = 0; i < listLength; i++) {
			ITextRegion region = _getRegions().get(i);
			if (region == changedRegion) {
				startIndex = i;
				break;
			}
		}
		// now, beginning one past the one that was changed, loop
		// through to end of list, adjusting the start postions.
		startIndex++;
		for (int j = startIndex; j < listLength; j++) {
			ITextRegion region = _getRegions().get(j);
			region.adjustStart(lengthDifference);
		}
	}

	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion structuredDocumentRegion, String changes, int requestStart, int lengthToReplace) {
		StructuredDocumentEvent result = null;
		int lengthDifference = Utilities.calculateLengthDifference(changes, lengthToReplace);
		// Get the region pointed to by the requestStart postion, and give
		// that region a chance to effect
		// the update.
		ITextRegion region = getRegionAtCharacterOffset(requestStart);
		// if there is no region, then the requested changes must come right
		// after the
		// node (and right after the last region). This happens, for example,
		// when someone
		// types something at the end of the document, or more commonly, when
		// they are right
		// at the beginning of one node, and the dirty start is therefore
		// calculated to be the
		// previous node.
		// So, in this case, we'll give the last region a chance to see if it
		// wants to
		// swallow the requested changes -- but only for inserts -- deletes
		// and "replaces"
		// should be reparsed if they are in these border regions, and only if
		// the
		if ((region == null) && (lengthToReplace == 0)) {
			region = _getRegions().get(_getRegions().size() - 1);
			// make sure the region is contiguous
			if (getEndOffset(region) == requestStart) {
				result = region.updateRegion(requester, this, changes, requestStart, lengthToReplace);
			}
		}
		else {
			if (region != null) {
				//
				// If the requested change spans more than one region, then
				// we don't give the region a chance to update.
				if ((containsOffset(region, requestStart)) && (containsOffset(region, requestStart + lengthToReplace))) {
					result = region.updateRegion(requester, this, changes, requestStart, lengthToReplace);
				}
			}
		}
		// if result is not null, then we need to update the start and end
		// postions of the regions that follow this one
		// if result is null, then apply the flatnode specific checks on what
		// it can change
		// (i.e. more than one region, but no change to the node itself)
		if (result != null) {
			// That is, a region decided it could handle the change and
			// created
			// a region changed event.
			Assert.isTrue(result instanceof RegionChangedEvent, "Program Error"); //$NON-NLS-1$
			updateDownStreamRegions(((RegionChangedEvent) result).getRegion(), lengthDifference);
			// PLUS, we need to update our own node end point (length)
			setLength(getLength() + lengthDifference);
		}

		return result;
	}

}
