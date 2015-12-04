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
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.document.XMLModelContext
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.document;

import org.eclipse.wst.json.core.document.IJSONArray;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONStructure;
import org.eclipse.wst.json.core.internal.document.JSONObjectImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * JSONModelContext class
 */
class JSONModelContext {
	private IJSONNode nextNode = null;
	private IJSONNode parentNode = null;

	// private JSONModelImpl model = null;
	private IJSONNode rootNode = null;

	/**
	 * JSONModelContext constructor
	 * 
	 * @param rootNode
	 *            org.w3c.dom.Node
	 */
	JSONModelContext(IJSONNode rootNode) {
		super();

		this.rootNode = rootNode;
	}

	/**
	 * findEndTag method
	 * 
	 * @return org.w3c.dom.Element
	 * @param tagName
	 *            java.lang.String
	 */
	// Element findEndTag(String tagName) {
	// if (tagName == null)
	// return null;
	// if (this.parentNode == null)
	// return null;
	//
	// for (IJSONNode parent = this.parentNode.getParentNode(); parent != null;
	// parent = parent
	// .getParentNode()) {
	// if (parent.getNodeType() != IJSONNode.ELEMENT_NODE)
	// break;
	// ElementImpl element = (ElementImpl) parent;
	// if (element.hasEndTag()) {
	// if (element.matchTagName(tagName))
	// return element;
	// // if ancestor element has end tag stop search
	// break;
	// }
	// if (element.getNextSibling() != null)
	// break;
	// }
	//
	// return null;
	// }

	/**
	 */
	// Text findNextText() {
	// Node node = this.nextNode;
	// while (node != null) {
	// if (node != this.nextNode && node.getNodeType() == Node.TEXT_NODE) {
	// TextImpl text = (TextImpl) node;
	// // skip empty text
	// if (text.getStructuredDocumentRegion() != null)
	// return text;
	// }
	// Node child = node.getFirstChild();
	// if (child != null) {
	// node = child;
	// continue;
	// }
	// while (node != null) {
	// Node next = node.getNextSibling();
	// if (next != null) {
	// node = next;
	// break;
	// }
	// node = node.getParentNode();
	// }
	// }
	// return null;
	// }

	/**
	 * findPreviousText method
	 * 
	 * @return org.w3c.dom.Text
	 */
	// Text findPreviousText() {
	// if (this.parentNode == null)
	// return null;
	// Node node = null;
	// if (this.nextNode != null)
	// node = this.nextNode.getPreviousSibling();
	// else
	// node = this.parentNode.getLastChild();
	// if (node == null || node.getNodeType() != Node.TEXT_NODE)
	// return null;
	// return (Text) node;
	// }

	/**
	 * findStartTag method
	 * 
	 * @return org.w3c.dom.Element
	 * @param tagName
	 *            java.lang.String
	 */
	// IJSONObject findStartTag(String tagName, String rootName) {
	// if (tagName == null)
	// return null;
	//
	// // check previous for empty content element
	// IJSONNode prev = null;
	// if (this.nextNode != null)
	// prev = this.nextNode.getPreviousSibling();
	// else if (this.parentNode != null)
	// prev = this.parentNode.getLastChild();
	// if (prev != null && prev.getNodeType() == IJSONNode.OBJECT_NODE) {
	// ElementImpl element = (ElementImpl) prev;
	// if (!element.hasEndTag() && !element.isEmptyTag()
	// && element.matchTagName(tagName))
	// return element;
	// }
	//
	// for (Node parent = this.parentNode; parent != null; parent = parent
	// .getParentNode()) {
	// if (parent.getNodeType() != Node.ELEMENT_NODE)
	// break;
	// ElementImpl element = (ElementImpl) parent;
	// if (element.matchTagName(tagName))
	// return element;
	// if (rootName != null && element.matchTagName(rootName))
	// break;
	// }
	//
	// return null;
	// }

	IJSONObject findPreviousObject() {
		if (parentNode != null
				&& parentNode.getNodeType() == IJSONNode.OBJECT_NODE) {
			return (IJSONObject) parentNode;
		}
		return null;
	}

	IJSONArray findPreviousArray() {
		if (parentNode != null
				&& parentNode.getNodeType() == IJSONNode.ARRAY_NODE) {
			return (IJSONArray) parentNode;
		}
		return null;
	}

	IJSONStructure findPreviousStructure() {
		if (parentNode != null
				&& (parentNode.getNodeType() == IJSONNode.OBJECT_NODE || parentNode
						.getNodeType() == IJSONNode.ARRAY_NODE)) {
			return (IJSONStructure) parentNode;
		}
		return null;
	}

	/**
	 * getNextNode method
	 * 
	 * @return org.w3c.dom.Node
	 */
	IJSONNode getNextNode() {
		return this.nextNode;
	}

	/**
	 * getParentNode method
	 * 
	 * @return org.w3c.dom.Node
	 */
	IJSONNode getParentNode() {
		return this.parentNode;
	}

	/**
	 * getRootNode method
	 * 
	 * @return org.w3c.dom.Node
	 */
	IJSONNode getRootNode() {
		return this.rootNode;
	}

	/**
	 * setLast method
	 */
	void setLast() {
		if (this.parentNode == null)
			return;
		if (this.nextNode != null) {
			IJSONNode prev = this.nextNode.getPreviousSibling();
			if (prev == null || prev.getNodeType() != IJSONNode.OBJECT_NODE)
				return;
			JSONObjectImpl element = (JSONObjectImpl) prev;
			if (element.hasEndTag() || !element.isContainer()
					|| element.isEmptyTag())
				return;
			setParentNode(prev);
		}

		// find last open parent
		IJSONNode parent = this.parentNode;
		IJSONNode last = parent.getLastChild();
		while (last != null) {
			if (last.getNodeType() != IJSONNode.OBJECT_NODE)
				break;
			JSONObjectImpl element = (JSONObjectImpl) last;
			if (element.hasEndTag() || !element.isContainer()
					|| element.isEmptyTag())
				break;
			parent = element;
			last = parent.getLastChild();
		}
		if (parent != this.parentNode)
			setParentNode(parent);
	}

	/**
	 * setNextNode method
	 * 
	 * @param nextNode
	 *            org.w3c.dom.Node
	 */
	void setNextNode(IJSONNode nextNode) {
		this.nextNode = nextNode;
		if (nextNode == null)
			return;
		this.parentNode = nextNode.getParentNode();
	}

	/**
	 * setParentNode method
	 * 
	 * @param parentNode
	 *            org.w3c.dom.Node
	 */
	void setParentNode(IJSONNode parentNode) {
		this.parentNode = parentNode;
		this.nextNode = null;
	}

}
