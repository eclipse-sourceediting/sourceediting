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
package org.eclipse.wst.sse.core.internal.provisional.events;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

/**
 * This event is used when a node's regions change, but the document region
 * itself doesn't. This says nothing about the semantics of the document
 * region, that may still have changed. Also, its assumed/required that all
 * the regions are replaced (even those that may not have changed).
 * 
 * @plannedfor 1.0
 */
public class RegionsReplacedEvent extends StructuredDocumentEvent {
	private ITextRegionList fNewRegions;
	private ITextRegionList fOldRegions;
	private IStructuredDocumentRegion fStructuredDocumentRegion;

	/**
	 * Creates an instance of the RegionsReplacedEvent.
	 * 
	 * @param document -
	 *            document being changed.
	 * @param originalRequester -
	 *            requester of the change.
	 * @param structuredDocumentRegion -
	 *            the region containing the change.
	 * @param oldRegions -
	 *            the old Regions being replaced.
	 * @param newRegions -
	 *            the new regions being added.
	 * @param changes -
	 *            the String representing the change.
	 * @param offset -
	 *            the offset of the change.
	 * @param lengthToReplace -
	 *            the length of text to replace.
	 */
	public RegionsReplacedEvent(IStructuredDocument document, Object originalRequester, IStructuredDocumentRegion structuredDocumentRegion, ITextRegionList oldRegions, ITextRegionList newRegions, String changes, int offset, int lengthToReplace) {
		super(document, originalRequester, changes, offset, lengthToReplace);
		fStructuredDocumentRegion = structuredDocumentRegion;
		fOldRegions = oldRegions;
		fNewRegions = newRegions;
	}

	/**
	 * Returns the new text regions.
	 * 
	 * @return the new text regions.
	 */
	public ITextRegionList getNewRegions() {
		return fNewRegions;
	}

	/**
	 * Returns the old text regions.
	 * 
	 * @return the old text regions.
	 */
	public ITextRegionList getOldRegions() {
		return fOldRegions;
	}

	/**
	 * Returns the structured document region.
	 * 
	 * @return the structured document region.
	 */
	public IStructuredDocumentRegion getStructuredDocumentRegion() {
		return fStructuredDocumentRegion;
	}
}
