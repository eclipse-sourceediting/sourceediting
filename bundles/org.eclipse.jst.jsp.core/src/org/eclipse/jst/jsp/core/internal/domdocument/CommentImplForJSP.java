/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.internal.domdocument;

import org.eclipse.jst.jsp.core.model.parser.DOMJSPRegionContexts;
import org.eclipse.wst.xml.core.internal.document.CommentImpl;
import org.w3c.dom.Document;

public class CommentImplForJSP extends CommentImpl {
	protected boolean isNestedCommentClose(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_COMMENT_CLOSE;
		return result;
	}

	protected boolean isNestedCommentOpenClose(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_COMMENT_OPEN || regionType == DOMJSPRegionContexts.JSP_COMMENT_CLOSE;
		return result;
	}

	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}
}
