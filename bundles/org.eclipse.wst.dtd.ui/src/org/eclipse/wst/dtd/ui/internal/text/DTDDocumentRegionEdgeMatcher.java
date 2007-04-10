/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.internal.text;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.sse.ui.internal.text.DocumentRegionEdgeMatcher;


public class DTDDocumentRegionEdgeMatcher extends DocumentRegionEdgeMatcher {

	/**
	 * @param validContexts
	 * @param nextMatcher
	 */
	public DTDDocumentRegionEdgeMatcher() {
		super(new String[]{DTDRegionTypes.START_TAG, DTDRegionTypes.COMMENT_START}, null);
	}
}
