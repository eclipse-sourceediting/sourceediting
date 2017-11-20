/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.util.RegionIterator
 *                                           modified in order to process JSON Objects.                                
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.util;



import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * 
 */
public class RegionIterator {

	private IStructuredDocumentRegion documentRegion = null;
	private IStructuredDocumentRegion curDocumentRegion = null;
	private int current = -1;

	/**
	 * 
	 */
	public RegionIterator(IStructuredDocument structuredDocument, int index) {
		super();

		reset(structuredDocument, index);
	}

	/**
	 * 
	 */
	public RegionIterator(IStructuredDocumentRegion flatNode, ITextRegion region) {
		super();
		reset(flatNode, region);
	}

	/**
	 * 
	 */
	public IStructuredDocumentRegion getStructuredDocumentRegion() {
		return curDocumentRegion;
	}

	/**
	 * 
	 */
	public boolean hasNext() {
		if (documentRegion == null)
			return false;
		if (current < 0)
			return false;
		if (current < documentRegion.getRegions().size())
			return true;
		return false;
	}

	/**
	 * 
	 */
	public boolean hasPrev() {
		// the same as hasNext()
		return hasNext();
	}

	/**
	 * 
	 */
	public ITextRegion next() {
		if (documentRegion == null)
			return null;
		if (current < 0 || documentRegion.getRegions() == null || documentRegion.getRegions().size() <= current)
			return null;

		ITextRegion region = documentRegion.getRegions().get(current);
		curDocumentRegion = documentRegion;

		if (current >= documentRegion.getRegions().size() - 1) {
			documentRegion = documentRegion.getNext();
			current = -1;
		}
		current++;

		return region;
	}

	/**
	 * 
	 */
	public ITextRegion prev() {
		if (documentRegion == null)
			return null;
		if (current < 0 || documentRegion.getRegions() == null || documentRegion.getRegions().size() <= current)
			return null;

		ITextRegion region = documentRegion.getRegions().get(current);
		curDocumentRegion = documentRegion;

		if (current == 0) {
			documentRegion = documentRegion.getPrevious();
			if (documentRegion != null)
				current = documentRegion.getRegions().size();
			else
				current = 0;
		}
		current--;

		return region;
	}

	/**
	 * 
	 */
	public void reset(IStructuredDocument structuredDocument, int index) {
		documentRegion = structuredDocument.getRegionAtCharacterOffset(index);
		curDocumentRegion = documentRegion;
		if (documentRegion != null) {
			ITextRegion region = documentRegion.getRegionAtCharacterOffset(index);
			current = documentRegion.getRegions().indexOf(region);
		}
	}

	/**
	 * 
	 */
	public void reset(IStructuredDocumentRegion flatNode, ITextRegion region) {
		if (region != null && flatNode != null) {
			this.documentRegion = flatNode;
			curDocumentRegion = flatNode;
			current = flatNode.getRegions().indexOf(region);
		}
	}
}
