/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.events;



import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;

/**
 * This event is used when a IStructuredDocumentRegion is deleted, or replaced
 * with more than one IStructuredDocumentRegion, or when simply more than one
 * IStructuredDocumentRegion changes.
 */

public class StructuredDocumentRegionsReplacedEvent extends StructuredDocumentEvent {

	private IStructuredDocumentRegionList fNewStructuredDocumentRegions;
	private IStructuredDocumentRegionList fOldStructuredDocumentRegions;

	/**
	 * Creates an instance of StructuredDocumentRegionsReplacedEvent
	 * 
	 * @param source
	 * @param originalSource
	 * @param oldStructuredDocumentRegions
	 * @param newStructuredDocumentRegions
	 * @param changes
	 * @param offset
	 * @param lengthToReplace
	 */
	public StructuredDocumentRegionsReplacedEvent(IStructuredDocument document, Object originalRequester, IStructuredDocumentRegionList oldStructuredDocumentRegions, IStructuredDocumentRegionList newStructuredDocumentRegions, String changes, int offset, int lengthToReplace) {
		super(document, originalRequester, changes, offset, lengthToReplace);
		fOldStructuredDocumentRegions = oldStructuredDocumentRegions;
		fNewStructuredDocumentRegions = newStructuredDocumentRegions;
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
}
