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
package org.eclipse.wst.dtd.core.internal.text;

import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;

public class DTDStructuredDocumentRegionFactory {
	public static final int DTD_GENERIC = 5;

	public static IStructuredDocumentRegion createStructuredDocumentRegion(int type) {
		IStructuredDocumentRegion instance = null;
		switch (type) {
			case DTD_GENERIC :
				instance = new BasicStructuredDocumentRegion();
				break;
			default :
				break;
		}
		return instance;
	}
}
