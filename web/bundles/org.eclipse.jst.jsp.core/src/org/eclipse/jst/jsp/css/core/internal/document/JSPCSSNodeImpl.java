/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.css.core.internal.document;


import org.eclipse.wst.css.core.internal.document.CSSStructuredDocumentRegionContainer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;


class JSPCSSNodeImpl extends CSSStructuredDocumentRegionContainer implements IJSPCSSNode {


	private String fText;

	JSPCSSNodeImpl(JSPCSSNodeImpl that) {
		super(that);

	}

	JSPCSSNodeImpl(String text) {
		super();
		fText = text;
	}
	
	public ICSSNode cloneNode(boolean deep) {
		JSPCSSNodeImpl cloned = new JSPCSSNodeImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	public short getNodeType() {
		return JSP_NODE;
	}

	public String getCssText() {
		return fText;
	}

}
