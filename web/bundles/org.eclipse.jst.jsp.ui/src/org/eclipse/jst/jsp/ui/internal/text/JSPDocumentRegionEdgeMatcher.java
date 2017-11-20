/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.text;

import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.ui.internal.text.DocumentRegionEdgeMatcher;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class JSPDocumentRegionEdgeMatcher extends DocumentRegionEdgeMatcher {

	protected final static char[] BRACKETS = {'{', '}', '(', ')', '[', ']','"','"','\'','\'','<','>'};

	/**
	 * @param validContexts
	 * @param nextMatcher
	 */
	public JSPDocumentRegionEdgeMatcher() {
		super(new String[]{DOMRegionContext.XML_TAG_NAME, DOMRegionContext.XML_COMMENT_TEXT, DOMJSPRegionContexts.JSP_COMMENT_TEXT, DOMJSPRegionContexts.JSP_DIRECTIVE_NAME, DOMJSPRegionContexts.JSP_ROOT_TAG_NAME, DOMRegionContext.XML_CDATA_TEXT, DOMRegionContext.XML_PI_OPEN, DOMRegionContext.XML_PI_CONTENT}, new JavaPairMatcher(BRACKETS));
	}
}
