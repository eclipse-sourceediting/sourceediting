/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.provisional;



/**
 * This type is used to indicate positions and lengths in source. Notice that
 * while getEndOffset and getLength are redundant, given that
 * 
 * <pre>
 * <code>
 *          getEndOffset() == getStartOffset() + getLength(); 
 * </code>
 * </pre>
 * 
 * we provide (require) both since in some cases implementors may be able to
 * provide one or the other more efficiently.
 * 
 * Note: it is not part of the API contract that implementors of IndexedRegion --
 * as a whole collection for a particular source -- must completely cover the
 * original source. They currently often do, so thought I'd mention explicitly
 * this may not always be true.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
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
	 * Can be used to get end offset of source text, relative to beginning of
	 * documnt. Implementers should return -1 if, or some reason, the region
	 * is not valid.
	 * 
	 * @return endoffset
	 */
	int getEndOffset();

	/**
	 * Can be used to get source postion of beginning of indexed region.
	 * Implementers should return -1 if, or some reason, the region is not
	 * valid.
	 * 
	 * @return int position of start of index region.
	 */
	int getStartOffset();

	/**
	 * Can be used to get the length of the source text. Implementers should
	 * return -1 if, or some reason, the region is not valid.
	 * 
	 * @return int position of length of index region.
	 */
	int getLength();

}
