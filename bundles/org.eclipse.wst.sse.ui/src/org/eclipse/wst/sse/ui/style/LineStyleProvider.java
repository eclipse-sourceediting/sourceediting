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
package org.eclipse.wst.sse.ui.style;



import java.util.Collection;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;


public interface LineStyleProvider {

	void init(IStructuredDocument document, Highlighter highlighter);

	/**
	 * This method must add StyleRanges to the holdResults collection, for the
	 * text starting with startStructuredDocumentRegion and ending with
	 * endStructuredDocumentRegion.
	 */
	boolean prepareRegions(ITypedRegion currentRegion, int start, int length, Collection holdResults);

	/**
	 * This method allows the implementer to free up any "resources" they
	 * might be holding on to (such as listening for preference changes)
	 */
	void release();
}
