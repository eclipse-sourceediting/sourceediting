/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui;

import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.eclipse.wst.xml.ui.XMLSpellCheckTarget;


public class JSPSpellCheckTarget extends XMLSpellCheckTarget {

	/**
	 * @param editor
	 */
	public JSPSpellCheckTarget() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.SpellCheckTargetImpl#isValidType(java.lang.String)
	 */
	protected boolean isValidType(String type) {
		boolean valid = false;
		if (//XMLRegionContext.UNDEFINED.equals(type) ||
		//XMLRegionContext.BLOCK_TEXT.equals(type) ||
		//XMLRegionContext.XML_CDATA_TEXT.equals(type) ||
		//XMLRegionContext.XML_PI_CONTENT.equals(type) ||
		//XMLRegionContext.XML_ELEMENT_DECL_CONTENT.equals(type) ||
		//XMLRegionContext.XML_ATTLIST_DECL_CONTENT.equals(type) ||
		//XMLJSPRegionContexts.JSP_CONTENT.equals(type) ||
		//XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE.equals(type) ||
		XMLJSPRegionContexts.JSP_COMMENT_TEXT.equals(type)) {
			valid = true;
		}
		return valid || super.isValidType(type);
	}

}