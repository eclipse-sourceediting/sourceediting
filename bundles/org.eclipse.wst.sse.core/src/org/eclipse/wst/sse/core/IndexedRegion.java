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
package org.eclipse.wst.sse.core;



/**
 * This type is used to indicate positions and lengths in source.
 * 
 * @since 1.0
 */
public interface IndexedRegion {

	/**
	 * Can be used to test if the indexed regions contains the test position.
	 * 
	 * @param testPosition
	 * @return true if test position is greater than or equal to start offset
	 *         and less than start offset plus length.
	 */
	boolean contains(int testPosition);

	/**
	 * ISSUE: should be replaced with getLength, to be consistent
	 * 
	 * @deprecated -
	 * @return
	 */
	int getEndOffset();

	/**
	 * Can be used to get source postion of beginning of indexed region.
	 * 
	 * @return int position of start of index region.
	 */
	int getStartOffset();
}
