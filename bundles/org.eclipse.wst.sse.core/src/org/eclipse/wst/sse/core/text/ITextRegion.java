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

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;



/**
 * A simple description of a bit of text (technically, a bit of a text buffer)
 * than has a "type" associated with it. For example, for the XML text
 * "&LT;IMG&GT;", the ' <' might form a region of type "open bracket" where as
 * the text "IMG" might form a region of type "tag name".
 * 
 * Note that there are three positions associated with a region, the start,
 * the end, and the end of the text. The end of the region should always be
 * greater than or equal to the end of the text, because the end of the text
 * simply includes any white space that might follow the non-whitespace
 * portion of the region. This whitespace is assumed to be ignorable except
 * for reasons of maintaining it in the original document for formatting,
 * appearance, etc.
 *  
 */
public interface ITextRegion {

	/**
	 * For use by parsers and reparsers only.
	 */
	void adjustLengthWith(int i);

	/**
	 * For use by parsers and reparsers only.
	 */
	void adjustStart(int i);

	/**
	 * 
	 * For use by parsers and reparsers only.
	 */
	void adjustTextLength(int i);

	void equatePositions(ITextRegion region);

	int getEnd();

	int getLength();

	int getStart();

	/**
	 * In some implementations, the "end" of the region (accessible via
	 * getEnd()) also contains any and all white space that may or may not be
	 * present after the "token" (read: relevant) part of the region. This
	 * method, getTextEnd(), is specific for the "token" part of the region,
	 * without the whitespace.
	 */
	int getTextEnd();

	/**
	 * The text length is equal to length if there is no white space at the
	 * end of a region. Otherwise it is smaller than length.
	 */
	int getTextLength();

	String getType();

	/**
	 * @deprecated - few implement this in a meaningful way.
	 */
	StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace);

}
