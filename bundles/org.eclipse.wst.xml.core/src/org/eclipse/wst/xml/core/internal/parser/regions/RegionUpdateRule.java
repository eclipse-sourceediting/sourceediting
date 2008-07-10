/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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


/**
 * 
 * This is a utility class to centralize 'region' update. Note: care must be
 * taken that is is not used for StructuredDocumentRegions, or container
 * regions, its only for "token regions"
 *  
 */
public class RegionUpdateRule {

	static public boolean allLetterOrDigit(String changes) {
		boolean result = true;
		for (int i = 0; i < changes.length(); i++) {
			// TO_DO_FUTURE: check that a Java Letter or Digit is
			// the same thing as an XML letter or digit
			if (!(Character.isLetterOrDigit(changes.charAt(i)))) {
				result = false;
				break;
			}
		}
		return result;
	}

	static public boolean allWhiteSpace(String changes) {
		boolean result = true;
		for (int i = 0; i < changes.length(); i++) {
			if (!Character.isWhitespace(changes.charAt(i))) {
				result = false;
				break;
			}
		}
		return result;
	}

	static public boolean canHandleAsLetterOrDigit(ITextRegion region, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		if (parent == null)
			return canHandleAsLetterOrDigit(region, changes, requestStart, lengthToReplace);
		boolean result = false;
		// Make sure we are in a non-white space area
		if ((requestStart <= (parent.getTextEndOffset(region))) && (allLetterOrDigit(changes))) {
			result = true;
		}
		return result;
	}

	static public boolean canHandleAsLetterOrDigit(ITextRegion region, String changes, int requestStart, int lengthToReplace) {
		boolean result = false;
		// Make sure we are in a non-white space area
		if ((requestStart <= (region.getTextEnd())) && (allLetterOrDigit(changes))) {
			result = true;
		}
		return result;
	}

	static public boolean canHandleAsWhiteSpace(ITextRegion region, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		// we don't explect a null parent, but just in case!
		// (in which case, we must be dealing with regions that are
		// structuredDocumentRegions).
		if (parent == null)
			return canHandleAsWhiteSpace(region, changes, requestStart, lengthToReplace);
		boolean result = false;
		// if we are in the "white space" area of a region, then
		// we don't want to handle, a reparse is needed.
		// the white space region is consider anywhere that would
		// leave whitespace between this character and the text part.
		// and of course, we can insert whitespace in whitespace region
		//
		// if there is no whitespace in this region, no need to look further
		if (region.getEnd() > region.getTextEnd()) {
			// no need to add one to end of text, as we used to, since we
			// change definition of length to equate to offset plus one.
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=105866
			// watch out for whitespace at end of text
			if (requestStart >= parent.getTextEndOffset(region)) {
				// ok, we are in the whitespace region, so we can't handle,
				// unless
				// we are just inserting whitespace.
				if (allWhiteSpace(changes)) {
					result = true;
				} else {
					result = false;
				}
			}
		}
		return result;
	}

	static public boolean canHandleAsWhiteSpace(ITextRegion region, String changes, int requestStart, int lengthToReplace) {
		boolean result = false;
		// if we are in the "white space" area of a region, then
		// we don't want to handle, a reparse is needed.
		// the white space region is consider anywhere that would
		// leave whitespace between this character and the text part.
		// and of course, we can insert whitespace in whitespace region
		//
		// if there is no whitespace in this region, no need to look further
		if (region.getEnd() > region.getTextEnd()) {
			// no need to add one to end of text, as we used to, since we
			// change definition of length to equate to offset plus one.
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=105866
			// watch out for whitespace at end of text
			if (requestStart >= region.getTextEnd()) {
				// ok, we are in the whitespace region, so we can't handle,
				// unless
				// we are just inserting whitespace.
				if (allWhiteSpace(changes)) {
					result = true;
				} else {
					result = false;
				}
			}
		}
		return result;
	}

	// need an adjust text length API before this can be used
	static public StructuredDocumentEvent updateModel(ITextRegion region, Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		RegionChangedEvent result = null;
		// if the region is an easy type (e.g. attribute value),
		// and the requested changes are all
		// alphanumeric, then make the change here locally.
		// (This can obviously be made more sophisticated as the need arises,
		// but should
		// always follow this pattern.)
		if (Debug.debugStructuredDocument) {
			System.out.println("\t\tContextRegion::updateModel"); //$NON-NLS-1$
			System.out.println("\t\t\tregion type is " + region.getType()); //$NON-NLS-1$
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
			if ((region.getStart() >= region.getTextEnd()) || (Math.abs(lengthToReplace) >= region.getTextEnd() - region.getStart())) {
				canHandle = false;
			} else {
				canHandle = true;
			}
		} else {
			if ((RegionUpdateRule.canHandleAsWhiteSpace(region, parent, changes, requestStart, lengthToReplace)) || RegionUpdateRule.canHandleAsLetterOrDigit(region, parent, changes, requestStart, lengthToReplace)) {
				canHandle = true;
			} else {
				canHandle = false;
			}
		}
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
			region.adjustLength(lengthDifference);
			// TO_DO_FUTURE: cache value of canHandleAsWhiteSpace from above
			// If we are handling as whitespace, there is no need to increase
			// the text length, only
			// the total length is changing.
			if (!RegionUpdateRule.canHandleAsWhiteSpace(region, parent, changes, region.getStart(), lengthToReplace)) {
				//				region.adjustTextLength(lengthDifference);
			}
			result = new RegionChangedEvent(parent.getParentDocument(), requester, parent, region, changes, requestStart, lengthToReplace);
		}
		return result;
	}
}
