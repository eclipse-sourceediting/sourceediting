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
package org.eclipse.wst.sse.core.parser;

import java.util.List;

public interface JSPCapableParser extends RegionParser, BlockTagParser {
	void addNestablePrefix(TagMarker marker);

	/**
	 * returns the TagMarkers for prefixes that are allowed to be nestable
	 * @return
	 */
	List getNestablePrefixes();

	void removeNestablePrefix(String tagName);
}
