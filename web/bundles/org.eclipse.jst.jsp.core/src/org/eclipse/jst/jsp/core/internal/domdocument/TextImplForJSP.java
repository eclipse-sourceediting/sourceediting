/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
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

import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.xml.core.internal.document.TextImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

public class TextImplForJSP extends TextImpl {

	protected TextImplForJSP() {
	}

	protected TextImplForJSP(TextImplForJSP that) {
		super(that);
	}

	protected boolean isNotNestedContent(String regionType) {
		boolean result = regionType != DOMJSPRegionContexts.JSP_CONTENT;
		return result;
	}
	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	public Node cloneNode(boolean deep) {
		Node cloned = new TextImplForJSP(this);
		notifyUserDataHandlers(UserDataHandler.NODE_CLONED, cloned);
		return cloned;
	}

}
