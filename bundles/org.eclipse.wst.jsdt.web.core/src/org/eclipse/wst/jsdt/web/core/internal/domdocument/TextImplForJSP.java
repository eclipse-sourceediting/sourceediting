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
import org.eclipse.wst.xml.core.internal.document.TextImpl;
import org.w3c.dom.Document;

public class TextImplForJSP extends TextImpl {
	@Override
	protected boolean isNotNestedContent(String regionType) {
		boolean result = regionType != DOMJSPRegionContexts.JSP_CONTENT;
		return result;
	}

	@Override
	protected void setOwnerDocument(Document ownerDocument) {
		super.setOwnerDocument(ownerDocument);
	}

}
