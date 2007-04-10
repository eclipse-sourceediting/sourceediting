/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.encoding;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.html.core.internal.contenttype.HTMLResourceEncodingDetector;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;

/**
 * This class parses beginning portion of HTML file to get the encoding value
 * in a META tag. Example:
 * <META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
 * 
 * Note: even though, technically, a META tag must come in a <HEAD>tag, we
 * don't check for that, under the assumption that whatever, or whereever, the
 * the first <META>tag is, it is the one intended to be used by the user, and
 * they may just be trying to repair the error. The HTML validator will tell
 * them if its in the wrong location.
 *  
 */
public class HTMLDocumentCharsetDetector extends HTMLResourceEncodingDetector implements IDocumentCharsetDetector {


	public HTMLDocumentCharsetDetector() {
		super();
	}

	public void set(IDocument document) {
		set(new DocumentReader(document, 0));


	}
}
