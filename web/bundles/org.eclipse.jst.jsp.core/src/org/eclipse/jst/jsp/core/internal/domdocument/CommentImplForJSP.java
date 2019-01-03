/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.internal.domdocument;

import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.xml.core.internal.document.CommentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * CommentImplForJSP
 */
public class CommentImplForJSP extends CommentImpl {
	protected CommentImplForJSP() {
		super();
	}

	protected CommentImplForJSP(CommentImpl that) {
		super(that);
	}

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
	
	public Node cloneNode(boolean deep) {
		CommentImpl cloned = new CommentImplForJSP(this);
		return cloned;
	}
}
