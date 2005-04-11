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
package org.eclipse.wst.xml.core.internal.builder.delegates;

import org.eclipse.wst.sse.core.participants.TaskTagSeeker;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


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
	 * @see org.eclipse.wst.sse.core.participants.TaskTagSeeker#isCommentRegion(org.eclipse.wst.sse.core.text.IStructuredDocumentRegion,
	 *      org.eclipse.wst.sse.core.text.ITextRegion)
	 */
	protected boolean isCommentRegion(IStructuredDocumentRegion region, ITextRegion textRegion) {
		return textRegion.getType().equals(DOMRegionContext.XML_COMMENT_TEXT);

	}

}
