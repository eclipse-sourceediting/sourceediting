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
package org.eclipse.wst.sse.core.text;

import java.util.Iterator;


public interface ITextRegionList {
	public boolean add(ITextRegion region);

	public boolean addAll(int insertPos, ITextRegionList newRegions);

	public void clear();

	public ITextRegion get(int index);

	public int indexOf(ITextRegion region);

	public boolean isEmpty();

	public Iterator iterator();

	public ITextRegion remove(int index);

	public void remove(ITextRegion region);

	public void removeAll(ITextRegionList regionList);

	public int size();

	public ITextRegion[] toArray();

}
