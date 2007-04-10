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
package org.eclipse.wst.dtd.core.internal.parser;

import org.eclipse.wst.sse.core.internal.parser.ContextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

public class DTDRegionFactory {
	public static ITextRegion createRegion(String tokenKind, int start, int length) {
		ITextRegion region = null;
		if (tokenKind != null) {
			// ISSUE: DTD regions don't distinguish text from white space
			region = new ContextRegion(tokenKind, start, length, length);
		}
		return region;
	}

}
