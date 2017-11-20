/*******************************************************************************
 * Copyright (c) 2009, 2011 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *     Jesper Steen Moller - bug 313992 - XPath evaluation does not show atomics
 *     Jesper Steen Moller - bug 343804 - Updated API information
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.core.util;

import org.eclipse.wst.xml.xpath2.api.Item;
import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NodeType;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

/**
 * @since 1.1
 */
public class NodeListImpl implements NodeList {

	ResultSequence rs;
	
	/**
	 * @since 1.2
	 */
	public NodeListImpl(ResultSequence result) {
		rs = result;
	}
	
	public int getLength() {
		return rs.size();
	}

	public Node item(int arg0) {
		final Item type = rs.item(arg0);
		if (type instanceof NodeType) {
			NodeType nodeType = (NodeType) type;
			return nodeType.node_value();
		}
		return createTextNode(type.getStringValue());
	}

	private static Node createTextNode(final String value) {
		return new Text() {
			public void appendData(String arg0) throws DOMException {
			}

			public void deleteData(int arg0, int arg1) throws DOMException {
			}

			public String getData() throws DOMException {
				return value;
			}

			public int getLength() {
				return getData().length();
			}

			public void insertData(int arg0, String arg1) throws DOMException {
			}

			public void replaceData(int arg0, int arg1, String arg2)
					throws DOMException {
			}

			public void setData(String arg0) throws DOMException {
			}

			public String substringData(int beginIndex, int endIndex) throws DOMException {
				return getData().substring(beginIndex, endIndex);
			}

			public Node appendChild(Node arg0) throws DOMException {
				return null;
			}

			public Node cloneNode(boolean arg0) {
				return null;
			}

			public short compareDocumentPosition(Node arg0) throws DOMException {
				return 0;
			}

			public NamedNodeMap getAttributes() {
				return null;
			}

			public String getBaseURI() {
				return null;
			}

			public NodeList getChildNodes() {
				return null;
			}

			public Object getFeature(String arg0, String arg1) {
				return null;
			}

			public Node getFirstChild() {
				return null;
			}

			public Node getLastChild() {
				return null;
			}

			public String getLocalName() {
				return "#text";
			}

			public String getNamespaceURI() {
				return null;
			}

			public Node getNextSibling() {
				return null;
			}

			public String getNodeName() {
				return null;
			}

			public short getNodeType() {
				return TEXT_NODE;
			}

			public String getNodeValue() throws DOMException {
				return getData();
			}

			public Document getOwnerDocument() {
				return null;
			}

			public Node getParentNode() {
				return null;
			}

			public String getPrefix() {
				return null;
			}

			public Node getPreviousSibling() {
				return null;
			}

			public String getTextContent() throws DOMException {
				return getData();
			}

			public Object getUserData(String arg0) {
				return null;
			}

			public boolean hasAttributes() {
				return false;
			}

			public boolean hasChildNodes() {
				return false;
			}

			public Node insertBefore(Node arg0, Node arg1) throws DOMException {
				return null;
			}

			public boolean isDefaultNamespace(String arg0) {
				return false;
			}

			public boolean isEqualNode(Node arg0) {
				return false;
			}

			public boolean isSameNode(Node arg0) {
				return false;
			}

			public boolean isSupported(String arg0, String arg1) {
				return false;
			}

			public String lookupNamespaceURI(String arg0) {
				return null;
			}

			public String lookupPrefix(String arg0) {
				return null;
			}

			public void normalize() {
			}

			public Node removeChild(Node arg0) throws DOMException {
				return null;
			}

			public Node replaceChild(Node arg0, Node arg1) throws DOMException {
				return null;
			}

			public void setNodeValue(String arg0) throws DOMException {
			}

			public void setPrefix(String arg0) throws DOMException {
			}

			public void setTextContent(String arg0) throws DOMException {
			}

			public Object setUserData(String arg0, Object arg1,
					UserDataHandler arg2) {
				return null;
			}

			public String getWholeText() {
				return getData();
			}

			public boolean isElementContentWhitespace() {
				return false;
			}

			public Text replaceWholeText(String arg0) throws DOMException {
				return null;
			}

			public Text splitText(int arg0) throws DOMException {
				return null;
			}
		};
	}
}
