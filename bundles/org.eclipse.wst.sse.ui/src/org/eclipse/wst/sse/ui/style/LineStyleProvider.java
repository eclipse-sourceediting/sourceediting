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
package org.eclipse.wst.sse.ui.style;



import java.util.Collection;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;

public interface LineStyleProvider {

	void init(IStructuredDocument document, Highlighter highlighter);

	/**
	 * This method must add StyleRanges to the holdResults collection, for the text
	 * starting with startStructuredDocumentRegion and ending with endStructuredDocumentRegion.
	 */
	boolean prepareRegions(ITypedRegion currentRegion, int start, int length, Collection holdResults);

	/**
	 * This method must add StyleRanges to the holdResults collection, for the text
	 * starting with startStructuredDocumentRegion and ending with endStructuredDocumentRegion.
	 * @deprecated -- use single node version
	 * I'd like to get rid of this method (if no objections) and
	 * just do the "looping" from start to end node in highlighter.
	 */
	//void prepareStyleRanges(ITextRegionContainer startStructuredDocumentRegion, ITextRegionContainer endStructuredDocumentRegion, int start, int length, Collection holdResults);
	/**
	 * This method allows the implementer to free up any "resources" 
	 * they might be holding on to (such as listening for preference
	 * changes)
	 */
	void release();
}
