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
 *     David Carver (Intalio) - bug 300434 - Make inner classes static where possible
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.text;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;


public class TextRegionListImpl implements ITextRegionList {

	static private class NullIterator implements Iterator {
		public NullIterator() {
		}

		public boolean hasNext() {
			return false;
		}

		public Object next() {
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException("can not remove regions via iterator"); //$NON-NLS-1$

		}

	}

	private static class RegionIterator implements Iterator {
		private ITextRegion[] fIteratorRegions;
		private int index = -1;
		private int maxindex = -1;

		public RegionIterator(ITextRegion[] regions) {
			fIteratorRegions = regions;
			maxindex = fIteratorRegions.length - 1;
		}

		public boolean hasNext() {
			return index < maxindex;
		}

		public Object next() {
			if (!(index < maxindex))
				throw new NoSuchElementException();
			return fIteratorRegions[++index];
		}

		public void remove() {
			if (index < 0) {
				// next() has never been called
				throw new IllegalStateException("can not remove regions without prior invocation of next()"); //$NON-NLS-1$
			}
			throw new UnsupportedOperationException("can not remove regions via iterator"); //$NON-NLS-1$
		}

	}

	private final static int growthConstant = 2;

	private ITextRegion[] fRegions;
	private int fRegionsCount = 0;

	public TextRegionListImpl() {
		super();
	}

	public TextRegionListImpl(ITextRegionList regionList) {
		this();
		fRegions = (ITextRegion[]) regionList.toArray().clone();
		fRegionsCount = fRegions.length;
	}

	public boolean add(ITextRegion region) {

		if (region == null)
			return false;
		ensureCapacity(fRegionsCount + 1);
		fRegions[fRegionsCount++] = region;
		return true;
	}

	public boolean addAll(int insertPos, ITextRegionList newRegions) {
		// beginning of list is 0 to insertPos-1
		// remainder of list is insertPos to fRegionsCount
		// resulting total will be be fRegionsCount + newRegions.size()
		if (insertPos < 0 || insertPos > fRegionsCount) {
			throw new ArrayIndexOutOfBoundsException(insertPos);
		}

		int newRegionsSize = newRegions.size();

		ensureCapacity(fRegionsCount + newRegionsSize);

		int numMoved = fRegionsCount - insertPos;
		if (numMoved > 0)
			System.arraycopy(fRegions, insertPos, fRegions, insertPos + newRegionsSize, numMoved);

		for (int i = 0; i < newRegionsSize; i++)
			fRegions[insertPos++] = newRegions.get(i);

		fRegionsCount += newRegionsSize;
		return newRegionsSize != 0;

	}

	public void clear() {
		// note: size of array is not reduced!
		fRegionsCount = 0;
	}

	private void ensureCapacity(int needed) {
		if (fRegions == null) {
			// first time
			fRegions = new ITextRegion[needed];
			return;
		}
		int oldLength = fRegions.length;
		if (oldLength < needed) {
			ITextRegion[] oldAdapters = fRegions;
			ITextRegion[] newAdapters = new ITextRegion[needed + growthConstant];
			System.arraycopy(oldAdapters, 0, newAdapters, 0, fRegionsCount);
			fRegions = newAdapters;
		}
	}

	public ITextRegion get(int index) {
		if (index < 0 || index > fRegionsCount) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return fRegions[index];
	}

	public int indexOf(ITextRegion region) {

		int result = -1;
		if (region != null) {
			if (fRegions != null) {
				for (int i = 0; i < fRegions.length; i++) {
					if (region.equals(fRegions[i])) {
						result = i;
						break;
					}
				}
			}
		}
		return result;
	}

	public boolean isEmpty() {
		return fRegionsCount == 0;
	}

	public Iterator iterator() {
		if (size() == 0) {
			return new NullIterator();
		} else {
			return new RegionIterator(toArray());
		}
	}

	public ITextRegion remove(int index) {
		// much more efficient ways to implement this, but
		// I doubt if called often
		ITextRegion oneToRemove = get(index);
		remove(oneToRemove);
		return oneToRemove;
	}

	public void remove(ITextRegion a) {
		if (fRegions == null || a == null)
			return;
		int newIndex = 0;
		ITextRegion[] newRegions = new ITextRegion[fRegionsCount];
		int oldRegionCount = fRegionsCount;
		boolean found = false;
		for (int oldIndex = 0; oldIndex < oldRegionCount; oldIndex++) {
			ITextRegion candidate = fRegions[oldIndex];
			if (a == candidate) {
				fRegionsCount--;
				found = true;
			} else
				newRegions[newIndex++] = fRegions[oldIndex];
		}
		if (found)
			fRegions = newRegions;
	}

	public void removeAll(ITextRegionList regionList) {
		// much more efficient ways to implement this, but
		// I doubt if called often
		if (regionList != null) {
			for (int i = 0; i < regionList.size(); i++) {
				this.remove(regionList.get(i));
			}
		}

	}

	public int size() {
		return fRegionsCount;
	}

	public ITextRegion[] toArray() {
		// return "clone" of internal array
		ITextRegion[] newArray = new ITextRegion[fRegionsCount];
		System.arraycopy(fRegions, 0, newArray, 0, fRegionsCount);
		return newArray;
	}
	
	public void trimToSize() {
		if (fRegions.length > fRegionsCount) {
			ITextRegion[] newRegions = new ITextRegion[fRegionsCount];
			System.arraycopy(fRegions, 0, newRegions, 0, fRegionsCount);
			fRegions = newRegions;
		}
	}

}
