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
package org.eclipse.wst.xml.core.internal.parser.regions;



import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.internal.util.Utilities;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


public class WhiteSpaceOnlyRegion implements ITextRegion {
	static private final byte fTextLength = 0;

	static private final String fType = DOMRegionContext.WHITE_SPACE;
	protected int fLength;
	protected int fStart;

	public WhiteSpaceOnlyRegion(int start, int length) {
		super();
		fStart = start;
		fLength = length;
	}

	public void adjust(int i) {
		fStart += i;
	}

	public void adjustLength(int i) {
		fLength += i;
	}

	public void adjustStart(int i) {
		fStart += i;
	}


	public void adjustTextLength(int i) {
		// not supported

	}

	public boolean contains(int position) {

		return fStart <= position && position < fStart + fLength;
	}

	public void equatePositions(ITextRegion region) {
		fStart = region.getStart();
		fLength = region.getLength();
	}

	public int getEnd() {
		return fStart + fLength;
	}

	public int getLength() {
		return fLength;
	}

	public int getStart() {
		return fStart;
	}

	public int getTextEnd() {
		return fStart + fTextLength;
	}

	public int getTextLength() {
		return fTextLength;
	}

	public String getType() {
		return fType;
	}

	public void setLength(int i) {
		fLength = i;
	}

	public void setStart(int i) {
		fStart = i;
	}

	public void setTextLength(short i) {
		throw new RuntimeException("invalid call"); //$NON-NLS-1$
	}

	public void setType(String string) {
		throw new RuntimeException("invalid call"); //$NON-NLS-1$
	}

	public String toString() {
		return RegionToStringUtil.toString(this);
	}

	/**
	 * For this ITextRegion type, the start must in terms of what the region
	 * expects ... that is, its not document offset, but start relative to
	 * what ever contains it.
	 */
	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		// if the region is an easy type (e.g. attribute value),
		// and the requested changes are all
		// alphanumeric, then make the change here locally.
		// (This can obviously be made more sophisticated as the need arises,
		// but should
		// always follow this pattern.)
		if (Debug.debugStructuredDocument) {
			System.out.println("\t\tContextRegion::updateModel"); //$NON-NLS-1$
			System.out.println("\t\t\tregion type is " + fType); //$NON-NLS-1$
		}
		boolean canHandle = false;
		// note: we'll always handle deletes from these
		// regions ... if its already that region,
		// deleting something from it won't change its
		// type. (remember, the calling program needs
		// to insure we are not called, if not all contained
		// on one region.
		if ((changes == null) || (changes.length() == 0)) {
			// delete case
			// We can not do the quick delete, if
			// if all the text in a region is to be deleted.
			// Or, if the delete starts in the white space region.
			// In these cases, a reparse is needed.
			// Minor note, we use textEnd-start since it always
			// less than or equal to end-start. This might
			// cause us to miss a few cases we could have handled,
			// but will prevent us from trying to handle funning cases
			// involving whitespace.
			if ((fStart >= getTextEnd()) || (Math.abs(lengthToReplace) >= getTextEnd() - getStart())) {
				canHandle = false;
			}
			else {
				canHandle = true;
			}
		}
		else {
			if (RegionUpdateRule.canHandleAsWhiteSpace(this, parent, changes, requestStart, lengthToReplace)) {
				canHandle = true;
			}
			else {
				canHandle = false;
			}

		}
		RegionChangedEvent result = null;

		if (canHandle) {
			// at this point, we still have the old region. We won't create a
			// new instance, we'll just update the one we have, by changing
			// its end postion,
			// The parent flatnode, upon return, has responsibility
			// for updating sibling regions.
			// and in turn, the structuredDocument itself has responsibility
			// for
			// updating the text store and down stream flatnodes.
			if (Debug.debugStructuredDocument) {
				System.out.println("change handled by region"); //$NON-NLS-1$
			}
			int lengthDifference = Utilities.calculateLengthDifference(changes, lengthToReplace);
			// Note: we adjust both end and text end, because for any change
			// that is in only the trailing whitespace region, we should not
			// do a quick change,
			// so 'canHandle' should have been false for those case.
			fLength += lengthDifference;

			result = new RegionChangedEvent(parent.getParentDocument(), requester, parent, this, changes, requestStart, lengthToReplace);
		}

		return result;
	}

}
