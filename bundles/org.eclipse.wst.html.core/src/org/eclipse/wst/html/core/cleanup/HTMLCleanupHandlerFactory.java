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
package org.eclipse.wst.html.core.cleanup;



import org.eclipse.wst.sse.core.cleanup.IStructuredCleanupHandler;
import org.eclipse.wst.sse.core.cleanup.IStructuredCleanupPreferences;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
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
			case Node.ELEMENT_NODE :
				{
					if (isJSPTag(node))
						handler = new JSPElementNodeCleanupHandler();
					else
						handler = new ElementNodeCleanupHandler();
					break;
				}
			case Node.TEXT_NODE :
				{
					if (isParentStyleTag(node))
						handler = new CSSTextNodeCleanupHandler();
					else
						handler = new NodeCleanupHandler();
					break;
				}
			default :
				{
					handler = new NodeCleanupHandler();
				}
		}

		handler.setCleanupPreferences(cleanupPreferences);

		return handler;
	}

	private boolean isJSPTag(Node node) {
		boolean result = false;

		if (node instanceof XMLNode) {
			IStructuredDocumentRegion flatNode = ((XMLNode) node).getFirstStructuredDocumentRegion();
			// in some cases, the nodes exists, but hasn't been associated with
			// a flatnode yet (the screen updates can be initiated on a different thread,
			// so the request for a flatnode can come in before the node is fully formed.
			// if the flatnode is null, we'll just allow the defaults to apply.
			// (html adapter in this case).
			if (flatNode != null) {
				String flatNodeType = flatNode.getType();
				if ((flatNodeType == XMLJSPRegionContexts.JSP_CONTENT) || (flatNodeType == XMLJSPRegionContexts.JSP_EXPRESSION_OPEN) || (flatNodeType == XMLJSPRegionContexts.JSP_SCRIPTLET_OPEN) || (flatNodeType == XMLJSPRegionContexts.JSP_DECLARATION_OPEN) || (flatNodeType == XMLJSPRegionContexts.JSP_DIRECTIVE_CLOSE) || (flatNodeType == XMLJSPRegionContexts.JSP_DIRECTIVE_NAME) || (flatNodeType == XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN) || (flatNodeType == XMLJSPRegionContexts.JSP_CLOSE)) {
					result = true;
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
		if (!(node instanceof XMLNode))
			return false;
		IStructuredDocumentRegion flatNode = ((XMLNode) node).getFirstStructuredDocumentRegion();
		if (flatNode == null)
			return false;
		if (flatNode.getType() != XMLRegionContext.BLOCK_TEXT)
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