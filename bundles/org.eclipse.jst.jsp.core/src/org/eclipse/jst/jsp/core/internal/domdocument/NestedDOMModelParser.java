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

package org.eclipse.jst.jsp.core.internal.domdocument;

import org.eclipse.jst.jsp.core.model.parser.XMLJSPRegionContexts;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.document.JSPTag;
import org.eclipse.wst.xml.core.internal.document.XMLModelImpl;
import org.eclipse.wst.xml.core.internal.document.XMLModelParser;

public class NestedDOMModelParser extends XMLModelParser {

	/**
	 * @param model
	 */
	public NestedDOMModelParser(XMLModelImpl model) {
		super(model);
	}

	protected boolean isNestedCommentOpen(String regionType) {
		boolean result = regionType == XMLJSPRegionContexts.JSP_COMMENT_OPEN;
		return result;
	}

	protected boolean isNestedCommentText(String regionType) {
		boolean result = regionType == XMLJSPRegionContexts.JSP_COMMENT_TEXT;
		return result;
	}

	protected boolean isNestedContent(String regionType) {
		boolean result = regionType == XMLJSPRegionContexts.JSP_CONTENT;
		return result;
	}

	protected boolean isNestedTag(String regionType) {
		boolean result = regionType == XMLJSPRegionContexts.JSP_SCRIPTLET_OPEN || regionType == XMLJSPRegionContexts.JSP_EXPRESSION_OPEN || regionType == XMLJSPRegionContexts.JSP_DECLARATION_OPEN || regionType == XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN || regionType == XMLJSPRegionContexts.JSP_CLOSE;
		return result;
	}

	protected boolean isNestedTagName(String regionType) {
		boolean result = regionType == XMLJSPRegionContexts.JSP_ROOT_TAG_NAME || regionType == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME;
		return result;
	}

	protected String computeNestedTag(String regionType, String tagName, IStructuredDocumentRegion structuredDocumentRegion, ITextRegion region) {
		String resultTagName = tagName;
		if (regionType == XMLJSPRegionContexts.JSP_SCRIPTLET_OPEN) {
			resultTagName = JSPTag.JSP_SCRIPTLET;
		}
		else if (regionType == XMLJSPRegionContexts.JSP_EXPRESSION_OPEN) {
			resultTagName = JSPTag.JSP_EXPRESSION;
		}
		else if (regionType == XMLJSPRegionContexts.JSP_DECLARATION_OPEN) {
			resultTagName = JSPTag.JSP_DECLARATION;
		}
		else if (regionType == XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN) {
			resultTagName = JSPTag.JSP_DIRECTIVE;
		}
		else if (regionType == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME) {
			resultTagName += '.';
			resultTagName += structuredDocumentRegion.getText(region);
		}
		return resultTagName;
	}

	protected boolean isNestedTagClose(String regionType) {
		boolean result = regionType == XMLJSPRegionContexts.JSP_CLOSE;
		return result;
	}

}
