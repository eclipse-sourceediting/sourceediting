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
package org.eclipse.wst.xml.core.internal.parser.regions;

import org.eclipse.wst.sse.core.text.ITextRegion;


public class RegionToStringUtil {
	static public String toString(ITextRegion region) {
		String className = region.getClass().getName();
		String shortClassName = className.substring(className.lastIndexOf(".") + 1); //$NON-NLS-1$
		// ==> // String resultText = null;
		String result = shortClassName + "--> " + region.getType() + ": " + region.getStart() + "-" + region.getTextEnd() + (region.getTextEnd() != region.getEnd() ? ("/" + region.getEnd()) : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		// NOTE: if the document held by any region has been updated and the region offsets have not
		// yet been updated, the output from this method invalid.
		return result;
	}

}
