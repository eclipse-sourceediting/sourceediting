/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.text;

import org.eclipse.wst.sse.ui.internal.text.DocumentRegionEdgeMatcher;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JsDocumentRegionEdgeMatcher extends DocumentRegionEdgeMatcher {
	protected final static char[] BRACKETS = { '{', '}', '(', ')', '[', ']' };
	
	/**
	 * @param validContexts
	 * @param nextMatcher
	 */
	public JsDocumentRegionEdgeMatcher() {
		super(new String[] { DOMRegionContext.XML_TAG_NAME, DOMRegionContext.XML_COMMENT_TEXT, DOMRegionContext.XML_CDATA_TEXT, DOMRegionContext.XML_PI_OPEN,
				DOMRegionContext.XML_PI_CONTENT }, new JsPairMatcher(JsDocumentRegionEdgeMatcher.BRACKETS));
	}
}
