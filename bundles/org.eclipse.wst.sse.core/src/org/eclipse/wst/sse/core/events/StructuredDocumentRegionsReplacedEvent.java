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
package org.eclipse.wst.sse.core.events;



import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;

/**
 * This event is used when a IStructuredDocumentRegion is deleted, or replaced with more than one IStructuredDocumentRegion,
 * or when simply more than one IStructuredDocumentRegion changes.
 */

public class StructuredDocumentRegionsReplacedEvent extends StructuredDocumentEvent {

	private IStructuredDocumentRegionList fNewStructuredDocumentRegions;
	private IStructuredDocumentRegionList fOldStructuredDocumentRegions;

	public StructuredDocumentRegionsReplacedEvent(IStructuredDocument source, Object originalSource, IStructuredDocumentRegionList oldStructuredDocumentRegions, IStructuredDocumentRegionList newStructuredDocumentRegions, String changes, int offset, int lengthToReplace) {
		super(source, originalSource, changes, offset, lengthToReplace);
		fOldStructuredDocumentRegions = oldStructuredDocumentRegions;
		fNewStructuredDocumentRegions = newStructuredDocumentRegions;
	}

	public IStructuredDocumentRegionList getNewStructuredDocumentRegions() {
		return fNewStructuredDocumentRegions;
	}

	public IStructuredDocumentRegionList getOldStructuredDocumentRegions() {
		return fOldStructuredDocumentRegions;
	}
}
