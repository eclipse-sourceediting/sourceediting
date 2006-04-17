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

import org.eclipse.wst.css.core.internal.document.CSSImportRuleImpl;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.CSSUtil;

public class JSPCSSImportRuleImpl extends CSSImportRuleImpl implements IJSPCSSImportRule {

	JSPCSSImportRuleImpl() {
		super();
	}
	
	JSPCSSImportRuleImpl(JSPCSSImportRuleImpl that) {
		super(that);
	}

	
	public ICSSNode cloneNode(boolean deep) {
		JSPCSSImportRuleImpl cloned = new JSPCSSImportRuleImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}
	public String getHref() {
		return getAttribute(HREF);
	}

	public void setAttribute(String name, String value) {
		if (HREF.equals(name)){
			value = CSSUtil.extractUriContents(value);
		}
		super.setAttribute(name, value);
	}
}
