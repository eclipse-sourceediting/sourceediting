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
import org.eclipse.wst.sse.core.text.ITextRegion;

/**
 * This event is used when a node changes in a non-structural way.
 * Non-structural, that is, as far as the IStructuredDocument is concerned.
 * The whole node, along with the new text is sent, just in case a listener
 * (e.g. a tree model) might make its own determination of what to do, and
 * need the whole node to act appropriately.
 * 
 * Note: users should not make assumptions about whether the node is literally
 * the same instance or not -- it is currently a different instance that is
 * identical to the old except for the changed region, but this implementation
 * may change.
 */
public class RegionChangedEvent extends StructuredDocumentEvent {
	private ITextRegion fChangedRegion;
	private IStructuredDocumentRegion fStructuredDocumentRegion;

	public RegionChangedEvent(IStructuredDocument source, Object originalSource, IStructuredDocumentRegion flatNode, ITextRegion changedRegion, String changes, int offset, int lengthToReplace) {
		super(source, originalSource, changes, offset, lengthToReplace);
		fStructuredDocumentRegion = flatNode;
		fChangedRegion = changedRegion;
	}

	public ITextRegion getRegion() {
		return fChangedRegion;
	}

	public IStructuredDocumentRegion getStructuredDocumentRegion() {
		return fStructuredDocumentRegion;
	}
}
