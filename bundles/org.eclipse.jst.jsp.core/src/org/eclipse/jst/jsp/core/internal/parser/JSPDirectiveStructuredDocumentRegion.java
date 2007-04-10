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
package org.eclipse.jst.jsp.core.internal.parser;

import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.text.XMLStructuredDocumentRegion;

public class JSPDirectiveStructuredDocumentRegion extends XMLStructuredDocumentRegion {
	/**
	 * JSPDirectiveStructuredDocumentRegion constructor comment.
	 */
	public JSPDirectiveStructuredDocumentRegion() {
		super();
	}

	protected StructuredDocumentEvent reparse(Object requester, String changes, int requestStart, int lengthToReplace) {
		return null;
	}

	// This is a language specific method (e.g. HTML, Java, Prolog, etc.) so
	// could/should be made configurable.
	public StructuredDocumentEvent updateModel(Object requester, String changes, int requestStart, int lengthToReplace, IStructuredDocumentRegion flatnode) {
		return null;
	}
}
