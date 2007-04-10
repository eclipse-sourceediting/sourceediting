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
package org.eclipse.wst.dtd.core.internal.text;

import java.util.NoSuchElementException;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;


public class RegionIterator {

	private int currentIndex;

	// private IStructuredDocumentRegion flatNode;
	private ITextRegionList regions;
	// private int startOffset, endOffset;
	private ITextRegion startRegion, endRegion;

	public RegionIterator(IStructuredDocumentRegion node) {
		this(node, node.getStart(), node.getEnd());
	}

	public RegionIterator(IStructuredDocumentRegion node, int startOffset, int endOffset) {
		regions = node.getRegions();
		startRegion = node.getRegionAtCharacterOffset(startOffset);
		endRegion = node.getRegionAtCharacterOffset(endOffset - 1);

		for (int i = 0; i < regions.size(); i++) {
			ITextRegion region = regions.get(i);
			if (startRegion == region) {
				currentIndex = i;
				break;
			}
		}
	}

	public RegionIterator(ITextRegionList regions) {
		this.regions = regions;
		startRegion = regions.get(0);
		endRegion = regions.get(regions.size() - 1);
		currentIndex = 0;
		// this(node, node.getStart(), node.getEnd());
	}

	public boolean hasNext() {
		if (currentIndex < regions.size()) {
			return currentIndex <= regions.indexOf(endRegion);
		}
		return false;
	}

	public boolean hasPrevious() {
		if (currentIndex >= 0) {
			return currentIndex >= regions.indexOf(startRegion);
		}
		return false;
	}

	public ITextRegion next() {
		if (hasNext()) {
			return regions.get(currentIndex++);
		}
		throw new NoSuchElementException();
	}

	public ITextRegion previous() {
		if (hasPrevious()) {
			return regions.get(--currentIndex);
		}
		throw new NoSuchElementException();
	}
}
