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
package org.eclipse.wst.xml.core.internal.text;

import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;


public class XMLStructuredDocumentRegion extends BasicStructuredDocumentRegion implements IStructuredDocumentRegion {

	public XMLStructuredDocumentRegion() {
		super();
	}

	public String getType() {
		String result = super.getType();
		// normally, we want the second region as the flatnode type ... since the
		// first one is usually just "open tag".
		if ((result != XMLRegionContext.XML_PI_OPEN) && (getRegions().size() > 1)) {
			result = getRegions().get(1).getType();
		}
		return result;
	}
}
