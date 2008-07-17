/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
				if (isXMLTag(node))
					handler = new ElementNodeCleanupHandler();
				else
					handler = new JSPElementNodeCleanupHandler();
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
	
	private boolean isXMLTag(Node node) {
		if(node instanceof IDOMNode) {
			IStructuredDocumentRegion region = ((IDOMNode) node).getFirstStructuredDocumentRegion();
			return (DOMRegionContext.XML_TAG_NAME == region.getType());
		}
		return false;
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
