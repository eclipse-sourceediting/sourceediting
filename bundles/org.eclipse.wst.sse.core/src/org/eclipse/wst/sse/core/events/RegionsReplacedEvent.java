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
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;

/**
 * This event is used when a node's regions change, but the node itself
 * doesn't. This says nothing about the semantics of the node, that may still
 * have changed. Also, its assumed/required that ALL the regions are replaced
 * (even those that may not have changed).
 */
public class RegionsReplacedEvent extends StructuredDocumentEvent {
	private ITextRegionList fNewRegions;
	private ITextRegionList fOldRegions;
	private IStructuredDocumentRegion fStructuredDocumentRegion;

	public RegionsReplacedEvent(IStructuredDocument source, Object originalSource, IStructuredDocumentRegion flatNode, ITextRegionList oldRegions, ITextRegionList newRegions, String changes, int offset, int lengthToReplace) {
		super(source, originalSource, changes, offset, lengthToReplace);
		fStructuredDocumentRegion = flatNode;
		fOldRegions = oldRegions;
		fNewRegions = newRegions;
	}

	public ITextRegionList getNewRegions() {
		return fNewRegions;
	}

	public ITextRegionList getOldRegions() {
		return fOldRegions;
	}

	public IStructuredDocumentRegion getStructuredDocumentRegion() {
		return fStructuredDocumentRegion;
	}
}
