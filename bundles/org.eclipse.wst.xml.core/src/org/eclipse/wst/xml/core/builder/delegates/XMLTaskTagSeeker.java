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
package org.eclipse.wst.xml.core.builder.delegates;

import org.eclipse.wst.sse.core.builder.participants.TaskTagSeeker;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;


public class XMLTaskTagSeeker extends TaskTagSeeker {

	/**
	 *  
	 */
	public XMLTaskTagSeeker() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	protected boolean isCommentRegion(IStructuredDocumentRegion region, ITextRegion textRegion) {
		return textRegion.getType().equals(XMLRegionContext.XML_COMMENT_TEXT);

	}

}
