/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Sep 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.html.core.internal.document;

import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleDeclarationAdapter;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ElementCSSInlineStyle;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.stylesheets.StyleSheet;

public class ElementStyleImpl extends ElementImpl implements IDOMElement, ElementCSSInlineStyle, LinkStyle {
	public ElementStyleImpl() {
		super();
	}

	public ElementStyleImpl(ElementImpl that) {
		super(that);
	}

	public StyleSheet getSheet() {
		INodeAdapter adapter = getAdapterFor(IStyleSheetAdapter.class);
		if (adapter == null)
			return null;
		if (!(adapter instanceof IStyleSheetAdapter))
			return null;
		return ((IStyleSheetAdapter) adapter).getSheet();
	}

	public CSSStyleDeclaration getStyle() {
		INodeAdapter adapter = getAdapterFor(IStyleDeclarationAdapter.class);
		if (adapter == null)
			return null;
		if (!(adapter instanceof IStyleDeclarationAdapter))
			return null;
		return ((IStyleDeclarationAdapter) adapter).getStyle();
	}

	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	protected void setTagName(String tagName) {
		super.setTagName(tagName);
	}

	public Node cloneNode(boolean deep) {
		ElementImpl cloned = new ElementStyleImpl(this);
		if (deep)
			cloneChildNodes(cloned, deep);
		return cloned;
	}

}
