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
package org.eclipse.wst.dtd.ui.ui.text;

import org.eclipse.wst.dtd.core.parser.DTDRegionTypes;
import org.eclipse.wst.sse.ui.ui.text.DocumentRegionEdgeMatcher;

public class DTDDocumentRegionEdgeMatcher extends DocumentRegionEdgeMatcher {

	/**
	 * @param validContexts
	 * @param nextMatcher
	 */
	public DTDDocumentRegionEdgeMatcher() {
		super(new String[]{DTDRegionTypes.START_TAG, DTDRegionTypes.COMMENT_START}, null);
	}
}
