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
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.w3c.dom.Document;

public class AttrImplForJSP extends AttrImpl {

	@Override
	protected boolean isNestedLanguageOpening(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN
				|| regionType == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN
				|| regionType == DOMJSPRegionContexts.JSP_DECLARATION_OPEN
				|| regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN;
		return result;
	}

	@Override
	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

	@Override
	protected void setName(String name) {
		super.setName(name);
	}

	@Override
	protected void setNamespaceURI(String namespaceURI) {
		super.setNamespaceURI(namespaceURI);
	}

}
