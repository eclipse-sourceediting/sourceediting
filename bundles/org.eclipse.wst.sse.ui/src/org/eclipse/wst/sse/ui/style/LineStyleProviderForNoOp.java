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
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;


/**
 * This class is used by default, if no attribute provider
 * is found for a certain node type. Its probably an error
 * in a factory somewhere if an adapter is not found, 
 * but this class allows the logic to proceed basically
 * simply providing default colored syntax highlighting. 
 */
public class LineStyleProviderForNoOp extends AbstractLineStyleProvider implements LineStyleProvider {


	/**
	 */
	protected PreferenceManager getColorManager() {
		return null;
	}

	/**
	 */
	public boolean prepareRegions(ITypedRegion currentRegion, int start, int length, Collection holdResults) {
		// add nothing
		return true;
	}

}
