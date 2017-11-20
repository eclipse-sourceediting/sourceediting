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
package org.eclipse.wst.sse.core.internal.provisional.text;

import java.util.Enumeration;

/**
 * This is a class used to provide a list of StructuredDocumentRegions, so the
 * implementation of how the list is formed can be hidden (encapsulated by
 * this class).
 * 
 * ISSUE: should extend ITextRegionList instead?
 * 
 * @plannedfor 1.0
 * 
 */
public interface IStructuredDocumentRegionList {

	/**
	 * Returns enumeration.
	 * 
	 * @return enumeration.
	 */
	Enumeration elements();

	/**
	 * Returns size of list.
	 * 
	 * @return size of list.
	 */
	int getLength();

	/**
	 * Returns the structured document region at index i.
	 * 
	 * @param i
	 *            index of region to return
	 * @return the region at specified offset.
	 */
	IStructuredDocumentRegion item(int i);

}
