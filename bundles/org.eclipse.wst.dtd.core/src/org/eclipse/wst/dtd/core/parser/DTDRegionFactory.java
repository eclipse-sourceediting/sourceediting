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
package org.eclipse.wst.dtd.core.parser;

import org.eclipse.wst.sse.core.text.ITextRegion;

public class DTDRegionFactory implements DTDRegionTypes {
	public static ITextRegion createRegion(String tokenKind, int start, int length) {
		ITextRegion region = null;
		if (tokenKind != null) {
			region = new DTDRegion(tokenKind, start, length);
		}
		return region;
	}

}
