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
package org.eclipse.wst.dtd.core.builder.delegates;

import org.eclipse.wst.dtd.core.parser.DTDRegionTypes;
import org.eclipse.wst.sse.core.participants.TaskTagSeeker;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;


public class DTDTaskTagSeeker extends TaskTagSeeker {
	protected boolean isCommentRegion(IStructuredDocumentRegion region, ITextRegion textRegion) {
		return textRegion.getType().equals(DTDRegionTypes.COMMENT_CONTENT);
	}
}
