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
package org.eclipse.wst.sse.core.text;

import java.util.Iterator;


public interface ITextRegionList {
	public boolean add(ITextRegion region);

	public boolean addAll(int insertPos, ITextRegionList newRegions);

	public int size();

	public ITextRegion get(int index);

	public int indexOf(ITextRegion region);

	public boolean isEmpty();

	public Iterator iterator();

	public ITextRegion remove(int index);

	public void remove(ITextRegion region);

	public ITextRegion[] toArray();

	public void clear();

	public void removeAll(ITextRegionList regionList);

}
