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
package org.eclipse.jst.jsp.core.internal.encoding;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.contenttype.JSPResourceEncodingDetector;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;

/**
 * This class parses beginning portion of JSP file to get attributes in page
 * directiive
 *  
 */
public class JSPDocumentHeadContentDetector extends JSPResourceEncodingDetector implements IJSPHeadContentDetector {

	public JSPDocumentHeadContentDetector() {
		super();
	}

	public void set(IDocument document) {
		set(new DocumentReader(document, 0));

	}

}
