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
package org.eclipse.wst.sse.ui.extensions.openon;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

/**
 * Interface for open on navigation
 * @author amywu
 */
public interface IOpenOn {
	/**
	 * @deprecated use getOpenRegion(IDocument, int) instead TODO remove in C5
	 */
	public IRegion getOpenOnRegion(ITextViewer viewer, int offset);

	/**
	 * @deprecated use openOn(IDocument, IRegion) instead TODO remove in C5
	 */
	public void openOn(ITextViewer viewer, IRegion region);
	
	/**
	 * Returns the entire region relevant to the current offset where an openable
	 * source region is found.  null if offset does not contain an openable source.
	 * @param document IDocument
	 * @param offset int
	 * @return IRegion entire region of openable source
	 */
	public IRegion getOpenOnRegion(IDocument document, int offset);

	/**
	 * Opens the file/source relevant to region if possible.
	 * @param viewer ITextViewer
	 * @param region Region to examine
	 */
	public void openOn(IDocument document, IRegion region);
}
