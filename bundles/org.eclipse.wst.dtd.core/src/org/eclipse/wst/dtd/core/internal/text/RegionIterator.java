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
package org.eclipse.wst.dtd.core.internal.text;

import java.util.NoSuchElementException;

import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;

public class RegionIterator {
	public RegionIterator(IStructuredDocumentRegion node) {
		this(node, node.getStart(), node.getEnd());
	}

	public RegionIterator(ITextRegionList regions) {
		this.regions = regions;
		startRegion = regions.get(0);
		endRegion = regions.get(regions.size() - 1);
		currentIndex = 0;
		//    this(node, node.getStart(), node.getEnd());
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

	private IStructuredDocumentRegion flatNode;
	private ITextRegionList regions;
	private ITextRegion startRegion, endRegion;
	private int startOffset, endOffset;

	private int currentIndex;

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
}
