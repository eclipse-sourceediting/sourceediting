/*******************************************************************************
 * Copyright (c) 2001, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.ltk.parser;

import java.util.List;

public interface JSPCapableParser extends RegionParser, BlockTagParser {
	void addNestablePrefix(TagMarker marker);

	/**
	 * returns the TagMarkers for prefixes that are allowed to be nestable
	 * 
	 * @return
	 */
	List<TagMarker> getNestablePrefixes();

	void removeNestablePrefix(String tagName);
}
