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
import org.eclipse.wst.html.core.internal.document.ElementStyleImpl;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ElementImplForJSP extends ElementStyleImpl {
	/**
	 * 
	 */
	public ElementImplForJSP() {
		super();
	}

	/**
	 * @param that
	 */
	public ElementImplForJSP(ElementImpl that) {
		super(that);
	}

	@Override
	protected boolean isNestedEndTag(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_ROOT_TAG_NAME
				|| regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME;
		return result;
	}

	@Override
	protected boolean isNestedClosed(String regionType) {
		boolean result = (regionType == DOMJSPRegionContexts.JSP_CLOSE || regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE);
		return result;
	}

	@Override
	protected boolean isNestedClosedComment(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_COMMENT_CLOSE;
		return result;
	}

	@Override
	protected boolean isClosedNestedDirective(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE;
		return result;
	}

	@Override
	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	@Override
	protected void setTagName(String tagName) {
		super.setTagName(tagName);
	}

	@Override
	public Node cloneNode(boolean deep) {
		ElementImpl cloned = new ElementImplForJSP(this);
		if (deep) {
			cloneChildNodes(cloned, deep);
		}
		return cloned;
	}
}
