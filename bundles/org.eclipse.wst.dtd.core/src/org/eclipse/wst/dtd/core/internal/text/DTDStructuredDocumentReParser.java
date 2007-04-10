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
package org.eclipse.wst.dtd.core.internal.text;

import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.text.StructuredDocumentReParser;


public class DTDStructuredDocumentReParser extends StructuredDocumentReParser {

	public StructuredDocumentEvent checkForCrossStructuredDocumentRegionBoundryCases() {
		IStructuredDocumentRegion startNode = fStructuredDocument.getRegionAtCharacterOffset(fStart);
		IStructuredDocumentRegion endNode = fStructuredDocument.getRegionAtCharacterOffset(fStart + fLengthToReplace - 1);
		return reparse(startNode.getStart(), endNode.getEnd());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.StructuredDocumentReParser#newInstance()
	 */
	public IStructuredTextReParser newInstance() {
		return new DTDStructuredDocumentReParser();
	}

	protected StructuredDocumentEvent reparse(IStructuredDocumentRegion dirtyStart, IStructuredDocumentRegion dirtyEnd) {
		return super.reparse(dirtyStart, dirtyEnd);
	}
}
