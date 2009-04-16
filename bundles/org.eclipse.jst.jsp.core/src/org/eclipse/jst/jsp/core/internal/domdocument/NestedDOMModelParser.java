/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.document.JSPTag;
import org.eclipse.wst.xml.core.internal.document.XMLModelParser;

public class NestedDOMModelParser extends XMLModelParser {

	/**
	 * @param model
	 */
	public NestedDOMModelParser(DOMModelImpl model) {
		super(model);
	}

	protected boolean isNestedCommentOpen(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_COMMENT_OPEN;
		return result;
	}

	protected boolean isNestedCommentText(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_COMMENT_TEXT;
		return result;
	}

	protected boolean isNestedContent(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_CONTENT;
		return result;
	}

	protected boolean isNestedTag(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN || regionType == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN || regionType == DOMJSPRegionContexts.JSP_DECLARATION_OPEN || regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN || regionType == DOMJSPRegionContexts.JSP_CLOSE;
		return result;
	}

	protected boolean isNestedTagName(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_ROOT_TAG_NAME || regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME;
		return result;
	}
	protected boolean isNestedTagOpen(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN || regionType == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN || regionType == DOMJSPRegionContexts.JSP_DECLARATION_OPEN || regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN;
		return result;
	}
	protected String computeNestedTag(String regionType, String tagName, IStructuredDocumentRegion structuredDocumentRegion, ITextRegion region) {
		String resultTagName = tagName;
		if (regionType == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN) {
			resultTagName = JSPTag.JSP_SCRIPTLET;
		}
		else if (regionType == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN) {
			resultTagName = JSPTag.JSP_EXPRESSION;
		}
		else if (regionType == DOMJSPRegionContexts.JSP_DECLARATION_OPEN) {
			resultTagName = JSPTag.JSP_DECLARATION;
		}
		else if (regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN) {
			resultTagName = JSPTag.JSP_DIRECTIVE;
		}
		else if (regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
			resultTagName += '.';
			resultTagName += structuredDocumentRegion.getText(region);
		}
		return resultTagName;
	}

	protected boolean isNestedTagClose(String regionType) {
		boolean result = regionType == DOMJSPRegionContexts.JSP_CLOSE || regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE;
		return result;
	}

}
