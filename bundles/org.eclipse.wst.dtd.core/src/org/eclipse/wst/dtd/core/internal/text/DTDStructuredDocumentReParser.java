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

import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.text.StructuredDocumentReParser;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;

public class DTDStructuredDocumentReParser extends StructuredDocumentReParser {
	protected StructuredDocumentEvent reparse(IStructuredDocumentRegion dirtyStart, IStructuredDocumentRegion dirtyEnd) {
		return super.reparse(dirtyStart, dirtyEnd);
	}

	public StructuredDocumentEvent checkForCrossStructuredDocumentRegionBoundryCases() {
		IStructuredDocumentRegion startNode = fStructuredDocument.getRegionAtCharacterOffset(fStart);
		IStructuredDocumentRegion endNode = fStructuredDocument.getRegionAtCharacterOffset(fStart + fLengthToReplace - 1);
		return reparse(startNode.getStart(), endNode.getEnd());
	}

}
