/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.text;

import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.wst.sse.ui.internal.text.DocumentRegionEdgeMatcher;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


public class XMLDocumentRegionEdgeMatcher extends DocumentRegionEdgeMatcher {

	private static final char[] PAIRS = {'{', '}', '(', ')', '[', ']','"','"','\'','\''};

	public XMLDocumentRegionEdgeMatcher() {
		super(new String[]{DOMRegionContext.XML_TAG_NAME, DOMRegionContext.XML_COMMENT_TEXT, DOMRegionContext.XML_CDATA_TEXT, DOMRegionContext.XML_PI_OPEN, DOMRegionContext.XML_PI_CONTENT}, new DefaultCharacterPairMatcher(PAIRS));
	}
}
