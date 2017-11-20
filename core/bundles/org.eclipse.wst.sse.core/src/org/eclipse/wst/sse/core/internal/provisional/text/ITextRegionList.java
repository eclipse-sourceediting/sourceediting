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
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.provisional.text;

import java.util.Iterator;

/**
 * ITextRegionList is to provide a list of regions. It can be used so clients
 * do not need to be aware of underlying implementation.
 */
public interface ITextRegionList {

	/**
	 * Adds region to the list.
	 * 
	 * For use by parsers and reparsers only, while list is being created.
	 * 
	 * @param region
	 * @return
	 */
	public boolean add(ITextRegion region);

	/**
	 * Adds new regions to the list.
	 * 
	 * For use by parsers and reparsers only, while list is being created.
	 * 
	 * @param insertPos
	 * @param newRegions
	 * @return whether the contents of this list were modified
	 */
	public boolean addAll(int insertPos, ITextRegionList newRegions);

	/**
	 * Removes all regions from the list.
	 * 
	 * For use by parsers and reparsers only, while list is being created.
	 * 
	 */
	public void clear();


	/**
	 * Returns the region at <code>index</code>, where 0 is first one in
	 * the list. Throws an <code>ArrayIndexOutOfBoundsException</code> if
	 * list is empty, or if index is out of range.
	 * 
	 * @param index
	 * @return
	 */
	public ITextRegion get(int index);

	/**
	 * Returns the index of <code>region</code> or -1 if <code>region</code>
	 * is not in the list.
	 * 
	 * @param region
	 * @return
	 */
	public int indexOf(ITextRegion region);

	/**
	 * Returns true if list has no regions.
	 * 
	 * @return true if list has no regions.
	 */
	public boolean isEmpty();


	/**
	 * Returns an iterator for this list.
	 * 
	 * @return an iterator for this list.
	 */
	public Iterator iterator();

	/**
	 * Removes the region at index.
	 * 
	 * For use by parsers and reparsers only, while list is being created.
	 * 
	 */
	public ITextRegion remove(int index);

	/**
	 * Removes the region.
	 * 
	 * For use by parsers and reparsers only, while list is being created.
	 * 
	 */
	public void remove(ITextRegion region);


	/**
	 * Removes all regionList from this list.
	 * 
	 * For use by parsers and reparsers only, while list is being created.
	 * 
	 */
	public void removeAll(ITextRegionList regionList);

	/**
	 * Returns the size of the list.
	 * 
	 * @return the size of the list.
	 */
	public int size();


	/**
	 * Creates and returns the regions in an array. No assumptions should be
	 * made if the regions in the array are clones are same instance of
	 * original region.
	 * 
	 * ISSUE: do we need to specify if cloned copies or not?
	 * 
	 * @return an array of regions.
	 */
	public ITextRegion[] toArray();

}
