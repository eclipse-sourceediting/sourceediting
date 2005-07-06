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
package org.eclipse.jst.jsp.ui.tests.other;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * Insert the type's description here. Creation date: (8/23/2000 9:24:02 PM)
 * 
 * @author: Kit Lo
 */
public class SimpleNode implements Node {
	String fNodeName;

	/**
	 * SimpleNode constructor comment.
	 */
	public SimpleNode() {
		super();
	}

	/**
	 * SimpleNode constructor comment.
	 */
	public SimpleNode(String nodeName) {
		fNodeName = nodeName;
	}

	/**
	 * appendChild method comment.
	 */
	public Node appendChild(Node newChild) throws DOMException {
		return null;
	}

	/**
	 * cloneNode method comment.
	 */
	public Node cloneNode(boolean deep) {
		return null;
	}

	/**
	 * getAttributes method comment.
	 */
	public NamedNodeMap getAttributes() {
		return null;
	}

	/**
	 * getChildNodes method comment.
	 */
	public NodeList getChildNodes() {
		return null;
	}

	/**
	 * getFirstChild method comment.
	 */
	public Node getFirstChild() {
		return null;
	}

	/**
	 * getLastChild method comment.
	 */
	public Node getLastChild() {
		return null;
	}

	/**
	 * Returns the local part of the qualified name of this node. <br>
	 * For nodes of any type other than <code>ELEMENT_NODE</code> and
	 * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
	 * method, such as <code>createElement</code> from the
	 * <code>Document</code> interface, this is always <code>null</code>.
	 * 
	 * @ link DOM Level 2
	 */
	public String getLocalName() {
		return null;
	}

	/**
	 * The namespace URI of this node, or <code>null</code> if it is
	 * unspecified. <br>
	 * This is not a computed value that is the result of a namespace lookup
	 * based on an examination of the namespace declarations in scope. It is
	 * merely the namespace URI given at creation time. <br>
	 * For nodes of any type other than <code>ELEMENT_NODE</code> and
	 * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
	 * method, such as <code>createElement</code> from the
	 * <code>Document</code> interface, this is always <code>null</code>.Per
	 * the Namespaces in XML Specification an attribute does not inherit its
	 * namespace from the element it is attached to. If an attribute is not
	 * explicitly given a namespace, it simply has no namespace.
	 * 
	 * @ link DOM Level 2
	 */
	public String getNamespaceURI() {
		return null;
	}

	/**
	 * getNextSibling method comment.
	 */
	public Node getNextSibling() {
		return null;
	}

	/**
	 * getNodeName method comment.
	 */
	public String getNodeName() {
		return fNodeName;
	}

	/**
	 * getNodeType method comment.
	 */
	public short getNodeType() {
		return (short) 0;
	}

	/**
	 * getNodeValue method comment.
	 */
	public String getNodeValue() throws DOMException {
		return "NodeValue";
	}

	/**
	 * getOwnerDocument method comment.
	 */
	public Document getOwnerDocument() {
		return null;
	}

	/**
	 * getParentNode method comment.
	 */
	public Node getParentNode() {
		return null;
	}

	/**
	 * The namespace prefix of this node, or <code>null</code> if it is
	 * unspecified. <br>
	 * Note that setting this attribute, when permitted, changes the
	 * <code>nodeName</code> attribute, which holds the qualified name, as
	 * well as the <code>tagName</code> and <code>name</code> attributes
	 * of the <code>Element</code> and <code>Attr</code> interfaces, when
	 * applicable. <br>
	 * Note also that changing the prefix of an attribute that is known to
	 * have a default value, does not make a new attribute with the default
	 * value and the original prefix appear, since the
	 * <code>namespaceURI</code> and <code>localName</code> do not change.
	 * <br>
	 * For nodes of any type other than <code>ELEMENT_NODE</code> and
	 * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
	 * method, such as <code>createElement</code> from the
	 * <code>Document</code> interface, this is always <code>null</code>.
	 * 
	 * @exception DOMException
	 *                INVALID_CHARACTER_ERR: Raised if the specified prefix
	 *                contains an illegal character. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
	 *                readonly. <br>
	 *                NAMESPACE_ERR: Raised if the specified
	 *                <code>prefix</code> is malformed, if the
	 *                <code>namespaceURI</code> of this node is
	 *                <code>null</code>, if the specified prefix is "xml"
	 *                and the <code>namespaceURI</code> of this node is
	 *                different from " http://www.w3.org/XML/1998/namespace",
	 *                if this node is an attribute and the specified prefix is
	 *                "xmlns" and the <code>namespaceURI</code> of this node
	 *                is different from " http://www.w3.org/2000/xmlns/", or
	 *                if this node is an attribute and the
	 *                <code>qualifiedName</code> of this node is "xmlns" .
	 * @ link DOM Level 2
	 */
	public String getPrefix() {
		return null;
	}

	/**
	 * getPreviousSibling method comment.
	 */
	public Node getPreviousSibling() {
		return null;
	}

	/**
	 * Returns whether this node (if it is an element) has any attributes.
	 * 
	 * @return <code>true</code> if this node has any attributes,
	 *         <code>false</code> otherwise.
	 * @ link DOM Level 2
	 */
	public boolean hasAttributes() {
		return false;
	}

	/**
	 * hasChildNodes method comment.
	 */
	public boolean hasChildNodes() {
		return false;
	}

	/**
	 * insertBefore method comment.
	 */
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return null;
	}

	/**
	 * Tests whether the DOM implementation implements a specific feature and
	 * that feature is supported by this node.
	 * 
	 * @param featureThe
	 *            name of the feature to test. This is the same name which can
	 *            be passed to the method <code>hasFeature</code> on
	 *            <code>DOMImplementation</code>.
	 * @param versionThis
	 *            is the version number of the feature to test. In Level 2,
	 *            version 1, this is the string "2.0". If the version is not
	 *            specified, supporting any version of the feature will cause
	 *            the method to return <code>true</code>.
	 * @return Returns <code>true</code> if the specified feature is
	 *         supported on this node, <code>false</code> otherwise.
	 * @ link DOM Level 2
	 */
	public boolean isSupported(String feature, String version) {
		return false;
	}

	/**
	 * Puts all <code>Text</code> nodes in the full depth of the sub-tree
	 * underneath this <code>Node</code>, including attribute nodes, into a
	 * "normal" form where only structure (e.g., elements, comments,
	 * processing instructions, CDATA sections, and entity references)
	 * separates <code>Text</code> nodes, i.e., there are neither adjacent
	 * <code>Text</code> nodes nor empty <code>Text</code> nodes. This can
	 * be used to ensure that the DOM view of a document is the same as if it
	 * were saved and re-loaded, and is useful when operations (such as
	 * XPointer lookups) that depend on a particular document tree structure
	 * are to be used.In cases where the document contains
	 * <code>CDATASections</code>, the normalize operation alone may not be
	 * sufficient, since XPointers do not differentiate between
	 * <code>Text</code> nodes and <code>CDATASection</code> nodes.
	 * 
	 * @version DOM Level 2
	 */
	public void normalize() {
	}

	/**
	 * removeChild method comment.
	 */
	public Node removeChild(Node oldChild) throws DOMException {
		return null;
	}

	/**
	 * replaceChild method comment.
	 */
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return null;
	}

	/**
	 * setNodeValue method comment.
	 */
	public void setNodeValue(String nodeValue) throws DOMException {
	}

	/**
	 * setPrefix method comment.
	 */
	public void setPrefix(String prefix) throws DOMException {
	}

	public String toString() {
		return getNodeName();
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public short compareDocumentPosition(Node other) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String getBaseURI() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public Object getFeature(String feature, String version) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String getTextContent() throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public Object getUserData(String key) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isDefaultNamespace(String namespaceURI) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isEqualNode(Node arg) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isSameNode(Node other) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String lookupNamespaceURI(String prefix) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String lookupPrefix(String namespaceURI) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public void setTextContent(String textContent) throws DOMException {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public TypeInfo getSchemaTypeInfo() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isId() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Not implemented in this version.");
	}


}