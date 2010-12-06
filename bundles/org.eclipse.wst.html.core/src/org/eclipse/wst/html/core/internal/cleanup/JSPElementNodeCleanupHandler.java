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
package org.eclipse.wst.html.core.internal.cleanup;



import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

public class JSPElementNodeCleanupHandler extends ElementNodeCleanupHandler {
	private static final String JSP_ROOT_TAG_NAME = "JSP_ROOT_TAG_NAME"; //$NON-NLS-1$
	private static final String JSP_DIRECTIVE_NAME = "JSP_DIRECTIVE_NAME"; //$NON-NLS-1$

	public Node cleanup(Node node) {
		/* <jsp:root> should cleanup its descendant nodes */
		if(node instanceof IDOMNode) {
			IStructuredDocumentRegion region = ((IDOMNode) node).getFirstStructuredDocumentRegion();
			String regionType = region.getType();
			if(JSP_ROOT_TAG_NAME.equals(regionType))
				return super.cleanup(node);
			else if (JSP_DIRECTIVE_NAME.equals(regionType)){
				IDOMNode renamedNode = (IDOMNode) cleanupChildren(node);
				return quoteAttrValue(renamedNode);
			}
		}
		return node;
	}
}
