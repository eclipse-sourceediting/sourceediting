/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.text;

import org.eclipse.wst.sse.ui.text.DocumentRegionEdgeMatcher;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;


public class XMLDocumentRegionEdgeMatcher extends DocumentRegionEdgeMatcher {

	/**
	 * @param validContexts
	 * @param nextMatcher
	 */
	public XMLDocumentRegionEdgeMatcher() {
		super(new String[]{XMLRegionContext.XML_TAG_NAME, XMLRegionContext.XML_COMMENT_TEXT, XMLRegionContext.XML_CDATA_TEXT, XMLRegionContext.XML_PI_OPEN, XMLRegionContext.XML_PI_CONTENT}, null);
	}
}
