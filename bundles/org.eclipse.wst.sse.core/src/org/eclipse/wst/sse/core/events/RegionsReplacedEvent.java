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
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;

/**
 * This event is used when a node's regions change, but the node itself
 * doesn't. This says nothing about the semantics of the node, that may still
 * have changed. Also, its assumed/required that ALL the regions are replaced
 * (even those that may not have changed).
 */
public class RegionsReplacedEvent extends StructuredDocumentEvent {
	private IStructuredDocumentRegion fStructuredDocumentRegion;
	private ITextRegionList fOldRegions;
	private ITextRegionList fNewRegions;

	public RegionsReplacedEvent(IStructuredDocument source, Object originalSource, IStructuredDocumentRegion flatNode, ITextRegionList oldRegions, ITextRegionList newRegions, String changes, int offset, int lengthToReplace) {
		super(source, originalSource, changes, offset, lengthToReplace);
		fStructuredDocumentRegion = flatNode;
		fOldRegions = oldRegions;
		fNewRegions = newRegions;
	}

	public IStructuredDocumentRegion getStructuredDocumentRegion() {
		return fStructuredDocumentRegion;
	}

	public ITextRegionList getNewRegions() {
		return fNewRegions;
	}

	public ITextRegionList getOldRegions() {
		return fOldRegions;
	}
}
