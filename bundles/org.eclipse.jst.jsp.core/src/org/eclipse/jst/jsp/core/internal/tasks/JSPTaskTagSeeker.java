/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.tasks;

import org.eclipse.jst.jsp.core.model.parser.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.builder.delegates.XMLTaskTagSeeker;

public class JSPTaskTagSeeker extends XMLTaskTagSeeker {
	protected boolean isCommentRegion(IStructuredDocumentRegion region, ITextRegion textRegion) {
		return super.isCommentRegion(region, textRegion) || textRegion.getType().equals(DOMJSPRegionContexts.JSP_COMMENT_TEXT);
	}
}
