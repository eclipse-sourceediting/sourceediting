/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.css.core.internal.parser;

import org.eclipse.jst.jsp.css.core.internal.parserz.JSPedCSSRegionContexts;
import org.eclipse.wst.css.core.internal.parser.CSSRegionUtil;
import org.eclipse.wst.css.core.internal.parser.CSSSourceParser;
import org.eclipse.wst.css.core.internal.parser.ICSSTokenizer;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;


public class JSPedCSSSourceParser extends CSSSourceParser {

	private JSPedCSSTokenizer fTokenizer;
	protected boolean mustBeStart(String type, String docRegionType) {
		return ((type == JSPedCSSRegionContexts.CSS_JSP_COMMENT || type == JSPedCSSRegionContexts.CSS_JSP_DIRECTIVE || type == JSPedCSSRegionContexts.CSS_JSP_END || type == CSSRegionContexts.CSS_DELIMITER || type == CSSRegionContexts.CSS_LBRACE || type == CSSRegionContexts.CSS_RBRACE || type == CSSRegionContexts.CSS_IMPORT || type == CSSRegionContexts.CSS_PAGE || type == CSSRegionContexts.CSS_MEDIA || type == CSSRegionContexts.CSS_FONT_FACE || type == CSSRegionContexts.CSS_CHARSET || type == CSSRegionContexts.CSS_ATKEYWORD || type == CSSRegionContexts.CSS_DECLARATION_PROPERTY || type == CSSRegionContexts.CSS_DECLARATION_DELIMITER) || (docRegionType == CSSRegionContexts.CSS_DECLARATION_PROPERTY && type == CSSRegionContexts.CSS_S) || (!CSSRegionUtil.isSelectorBegginingType(docRegionType) && (type == CSSRegionContexts.CSS_SELECTOR_ELEMENT_NAME || type == CSSRegionContexts.CSS_SELECTOR_UNIVERSAL || type == CSSRegionContexts.CSS_SELECTOR_PSEUDO || type == CSSRegionContexts.CSS_SELECTOR_CLASS || type == CSSRegionContexts.CSS_SELECTOR_ID || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_START)));
	}

	protected boolean mustBeEnd(String type) {
		return (type == JSPedCSSRegionContexts.CSS_JSP_COMMENT || type == JSPedCSSRegionContexts.CSS_JSP_DIRECTIVE || type == JSPedCSSRegionContexts.CSS_JSP_END || type == CSSRegionContexts.CSS_DELIMITER || type == CSSRegionContexts.CSS_LBRACE || type == CSSRegionContexts.CSS_RBRACE || type == CSSRegionContexts.CSS_DECLARATION_DELIMITER);
	}
	
	public ICSSTokenizer getTokenizer() {
		if (fTokenizer == null) {
			fTokenizer = new JSPedCSSTokenizer();
		}
		return fTokenizer;
	}
	
	public RegionParser newInstance() {
		return new JSPedCSSSourceParser();
	}
	
}
