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
package org.eclipse.wst.html.core.internal.cleanup;



import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.internal.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Node;

// nakamori_TODO: check and remove CSS formatting

class HTMLCleanupHandlerFactory {



	private static HTMLCleanupHandlerFactory fInstance = null;

	static synchronized HTMLCleanupHandlerFactory getInstance() {
		if (fInstance == null) {
			fInstance = new HTMLCleanupHandlerFactory();
		}
		return fInstance;
	}

	private HTMLCleanupHandlerFactory() {
		super();
	}

	IStructuredCleanupHandler createHandler(Node node, IStructuredCleanupPreferences cleanupPreferences) {
		short nodeType = node.getNodeType();
		IStructuredCleanupHandler handler = null;
		switch (nodeType) {
			case Node.ELEMENT_NODE : {
				if (isJSPTag(node))
					handler = new JSPElementNodeCleanupHandler();
				else
					handler = new ElementNodeCleanupHandler();
				break;
			}
			case Node.TEXT_NODE : {
				if (isParentStyleTag(node))
					handler = new CSSTextNodeCleanupHandler();
				else
					handler = new NodeCleanupHandler();
				break;
			}
			default : {
				handler = new NodeCleanupHandler();
			}
		}

		handler.setCleanupPreferences(cleanupPreferences);

		return handler;
	}

	/**
	 * ISSUE: this is a bit of hidden JSP knowledge that was implemented this
	 * way for expedency. Should be evolved in future to depend on "nestedContext".
	 */

	private boolean isJSPTag(Node node) {

		final String JSP_CLOSE = "JSP_CLOSE"; //$NON-NLS-1$
		// final String JSP_COMMENT_CLOSE = "JSP_COMMENT_CLOSE"; //$NON-NLS-1$

		// final String JSP_COMMENT_OPEN = "JSP_COMMENT_OPEN"; //$NON-NLS-1$
		// final String JSP_COMMENT_TEXT = "JSP_COMMENT_TEXT"; //$NON-NLS-1$

		final String JSP_CONTENT = "JSP_CONTENT"; //$NON-NLS-1$
		final String JSP_DECLARATION_OPEN = "JSP_DECLARATION_OPEN"; //$NON-NLS-1$
		final String JSP_DIRECTIVE_CLOSE = "JSP_DIRECTIVE_CLOSE"; //$NON-NLS-1$
		final String JSP_DIRECTIVE_NAME = "JSP_DIRECTIVE_NAME"; //$NON-NLS-1$

		final String JSP_DIRECTIVE_OPEN = "JSP_DIRECTIVE_OPEN"; //$NON-NLS-1$
		final String JSP_EXPRESSION_OPEN = "JSP_EXPRESSION_OPEN"; //$NON-NLS-1$

		// final String JSP_ROOT_TAG_NAME = "JSP_ROOT_TAG_NAME"; //$NON-NLS-1$

		final String JSP_SCRIPTLET_OPEN = "JSP_SCRIPTLET_OPEN"; //$NON-NLS-1$

		boolean result = false;

		if (node instanceof IDOMNode) {
			IStructuredDocumentRegion flatNode = ((IDOMNode) node).getFirstStructuredDocumentRegion();
			// in some cases, the nodes exists, but hasn't been associated
			// with
			// a flatnode yet (the screen updates can be initiated on a
			// different thread,
			// so the request for a flatnode can come in before the node is
			// fully formed.
			// if the flatnode is null, we'll just allow the defaults to
			// apply.
			if (flatNode != null) {
				String flatNodeType = flatNode.getType();
				// should not be null, but just to be sure
				if (flatNodeType != null) {
					if ((flatNodeType.equals(JSP_CONTENT)) || (flatNodeType.equals(JSP_EXPRESSION_OPEN)) || (flatNodeType.equals(JSP_SCRIPTLET_OPEN)) || (flatNodeType.equals(JSP_DECLARATION_OPEN)) || (flatNodeType.equals(JSP_DIRECTIVE_CLOSE)) || (flatNodeType.equals(JSP_DIRECTIVE_NAME)) || (flatNodeType.equals(JSP_DIRECTIVE_OPEN)) || (flatNodeType.equals(JSP_CLOSE))) {
						result = true;
					}
				}
			}
		}

		return result;
	}

	private boolean isParentStyleTag(Node node) {
		if (node == null)
			return false;
		if (node.getNodeType() != Node.TEXT_NODE)
			return false;
		if (!(node instanceof IDOMNode))
			return false;
		IStructuredDocumentRegion flatNode = ((IDOMNode) node).getFirstStructuredDocumentRegion();
		if (flatNode == null)
			return false;
		if (flatNode.getType() != DOMRegionContext.BLOCK_TEXT)
			return false;

		// check if the parent is STYLE element
		Node parent = node.getParentNode();
		if (parent == null)
			return false;
		if (parent.getNodeType() != Node.ELEMENT_NODE)
			return false;
		String name = parent.getNodeName();
		if (name == null)
			return false;
		if (!name.equalsIgnoreCase("STYLE"))//$NON-NLS-1$
			return false;
		return true;
	}

}
