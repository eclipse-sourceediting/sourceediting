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

/**
 * This class can be used by default, if no attribute provider is found for a
 * certain region type. Its probably an error in a factory somewhere if an
 * adapter is not found, but this class allows the logic to proceed basically
 * simply providing default colored syntax highlighting.
 * 
 * Not to be subclassed.
 */
final public class LineStyleProviderForNoOp extends AbstractLineStyleProvider implements LineStyleProvider {

	/**
	 * @see org.eclipse.wst.sse.ui.style.AbstractLineStyleProvider#prepareRegions(org.eclipse.jface.text.ITypedRegion,
	 *      int, int, java.util.Collection)
	 */
	public boolean prepareRegions(ITypedRegion currentRegion, int start, int length, Collection holdResults) {
		// add nothing, but say handled.
		return true;
	}

}
