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
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * This event is used when a document region changes in a non-structural way.
 * Non-structural, that is, as far as the IStructuredDocument is concerned.
 * The whole region, along with the new text is sent, just in case a listener
 * (e.g. a tree model) might make its own determination of what to do, and
 * needs the whole region to act appropriately.
 * 
 * Note: users should not make assumptions about whether the region is
 * literally the same instance or not -- it is currently a different instance
 * that is identical to the old except for the changed region, but this
 * implementation may change.
 * 
 * @plannedfor 1.0
 */
public class RegionChangedEvent extends StructuredDocumentEvent {
	private ITextRegion fChangedRegion;
	private IStructuredDocumentRegion fStructuredDocumentRegion;

	/**
	 * Creates instance of a RegionChangedEvent.
	 * 
	 * @param document
	 *            the document being changed.
	 * @param originalRequester
	 *            the object making the request for the change.
	 * @param structuredDocumentRegion
	 *            the containing region
	 * @param changedRegion
	 *            the region that has changed.
	 * @param changes
	 *            the string representing the change.
	 * @param offset
	 *            the offset of the change.
	 * @param lengthToReplace
	 *            the length specified to be replaced.
	 */
	public RegionChangedEvent(IStructuredDocument document, Object originalRequester, IStructuredDocumentRegion structuredDocumentRegion, ITextRegion changedRegion, String changes, int offset, int lengthToReplace) {
		super(document, originalRequester, changes, offset, lengthToReplace);
		fStructuredDocumentRegion = structuredDocumentRegion;
		fChangedRegion = changedRegion;
	}


	/**
	 * Returns the text region changed.
	 * 
	 * @return the text region changed
	 */
	public ITextRegion getRegion() {
		return fChangedRegion;
	}


	/**
	 * Returns the document region changed.
	 * 
	 * @return the document region changed
	 */
	public IStructuredDocumentRegion getStructuredDocumentRegion() {
		return fStructuredDocumentRegion;
	}
}
