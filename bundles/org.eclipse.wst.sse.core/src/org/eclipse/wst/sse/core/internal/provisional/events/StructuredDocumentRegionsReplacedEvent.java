/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
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
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;

/**
 * This event is used when a IStructuredDocumentRegion is deleted, or replaced
 * with more than one IStructuredDocumentRegion, or when simply more than one
 * IStructuredDocumentRegion changes.
 * 
 * @plannedfor 1.0
 */
public class StructuredDocumentRegionsReplacedEvent extends StructuredDocumentEvent {

	private IStructuredDocumentRegionList fNewStructuredDocumentRegions;
	private IStructuredDocumentRegionList fOldStructuredDocumentRegions;

	private boolean fIsEntireDocumentReplaced;

	/**
	 * Creates an instance of StructuredDocumentRegionsReplacedEvent
	 * 
	 * @param document -
	 *            the document being changed.
	 * @param originalRequester -
	 *            the requester of the change.
	 * @param oldStructuredDocumentRegions -
	 *            the old document regions removed.
	 * @param newStructuredDocumentRegions -
	 *            the new document regions added.
	 * @param changes -
	 *            a string representing the text change.
	 * @param offset -
	 *            the offset of the change.
	 * @param lengthToReplace -
	 *            the length of text requested to be replaced.
	 */
	public StructuredDocumentRegionsReplacedEvent(IStructuredDocument document, Object originalRequester, IStructuredDocumentRegionList oldStructuredDocumentRegions, IStructuredDocumentRegionList newStructuredDocumentRegions, String changes, int offset, int lengthToReplace) {
		super(document, originalRequester, changes, offset, lengthToReplace);
		fOldStructuredDocumentRegions = oldStructuredDocumentRegions;
		fNewStructuredDocumentRegions = newStructuredDocumentRegions;
	}

	public StructuredDocumentRegionsReplacedEvent(IStructuredDocument document, Object originalRequester, IStructuredDocumentRegionList oldStructuredDocumentRegions, IStructuredDocumentRegionList newStructuredDocumentRegions, String changes, int offset, int lengthToReplace, boolean entireDocumentReplaced) {
		this(document, originalRequester, oldStructuredDocumentRegions, newStructuredDocumentRegions, changes, offset, lengthToReplace);
		fIsEntireDocumentReplaced = entireDocumentReplaced;
	}

	/**
	 * Returns the new structured document regions.
	 * 
	 * @return the new structured document regions.
	 */
	public IStructuredDocumentRegionList getNewStructuredDocumentRegions() {
		return fNewStructuredDocumentRegions;
	}

	/**
	 * Returns the old structured document regions.
	 * 
	 * @return the old structured document regions.
	 */
	public IStructuredDocumentRegionList getOldStructuredDocumentRegions() {
		return fOldStructuredDocumentRegions;
	}

	public boolean isEntireDocumentReplaced() {
		return fIsEntireDocumentReplaced;
	}
}
