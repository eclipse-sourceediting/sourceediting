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

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;



/**
 * A simple description of a bit of text (technically, a bit of a text buffer) than has
 * a "type" associated with it. For example, for the XML text "&LT;IMG&GT;", the '<' 
 * might form a region of type "open bracket" where as the text "IMG" might form a region
 * of type "tag name".
 *
 * Note that there are three positions associated with a region, the start, the end, and 
 * the end of the text. The end of the region should always be greater than or equal to
 * the end of the text, because the end of the text simply includes any white space that
 * might follow the non-whitespace portion of the region. This whitespace is assumed to
 * be ignorable except for reasons of maintaining it in the original document for formatting,
 * appearance, etc.
 * 
 */
public interface ITextRegion {

	int getEnd();

	int getLength();

	int getStart();

	/**
	 *  In some implementations, the "end" of the region (accessible
	 *  via getEnd()) also contains any and all white space that may
	 *  or may not be present after the "token" (read: relevant) part
	 *  of the region. This method, getTextEnd(), is specific for the
	 *  "token" part of the region, without the whitespace.
	 */
	int getTextEnd();

	/**
	 * The text length is equal to length if there is no white space at the end of a region.
	 * Otherwise it is smaller than length.
	 */
	int getTextLength();

	String getType();

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

	/**
	 * For use by parsers and reparsers only.
	 */
	//StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion parent, String changes, int startOffset, int lengthToReplace);
	/**
	 * @deprecated - may be removed after C3.
	 * it seems no one implements this in a meaningful way. 
	 */
	StructuredDocumentEvent updateModel(Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace);

}
