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

package org.eclipse.wst.jsdt.web.core.internal.domdocument;

import org.eclipse.wst.jsdt.web.core.internal.regions.DOMJSPRegionContexts;
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

	@Override
	protected boolean isNestedCommentClose(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_COMMENT_CLOSE;
		return result;
	}

	@Override
	protected boolean isNestedCommentOpenClose(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_COMMENT_OPEN
				|| regionType == DOMJSPRegionContexts.JSP_COMMENT_CLOSE;
		return result;
	}

	@Override
	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	@Override
	public Node cloneNode(boolean deep) {
		CommentImpl cloned = new CommentImplForJSP(this);
		return cloned;
	}
}
