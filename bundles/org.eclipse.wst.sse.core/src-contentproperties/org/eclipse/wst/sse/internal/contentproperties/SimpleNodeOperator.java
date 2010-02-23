/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (Intalio) - bug 300434 - Make inner classes static where possible
 *******************************************************************************/
package org.eclipse.wst.sse.internal.contentproperties;



import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.wst.sse.core.internal.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

/**
* @deprecated This is package protected so no one cares anyways.
*/
class SimpleNodeOperator {

	static class CreateContentSettingsFailureException extends Exception {
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		public CreateContentSettingsFailureException(String reason) {
			super(reason);
		}
	}


	static class ReadContentSettingsFailureException extends Exception {
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		public ReadContentSettingsFailureException(String reason) {
			super(reason);
		}
	}

	static class WriteContentSettingsFailureException extends Exception {
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		public WriteContentSettingsFailureException(String reason) {
			super(reason);
		}
	}

	// writer class for .contentSettings.
	static class XMLDocumentWriter {
		OutputStream fOut;

		protected XMLDocumentWriter(OutputStream out) {
			this.fOut = out;
		}

		protected final void close() {
			try {
				fOut.close();
			} catch (IOException e) {
				// do nothing, shouldn't matter
			}
		}

		protected void serialize(Document sourceDocument) throws WriteContentSettingsFailureException {
			// JAXP transformation
			Source domSource = new DOMSource(sourceDocument);
			try {
				Transformer serializer = TransformerFactory.newInstance().newTransformer();
				try {
					serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
					serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (IllegalArgumentException e) {
					// unsupported properties
				}
				serializer.transform(domSource, new StreamResult(fOut));
			} catch (TransformerConfigurationException e) {
				throw new WriteContentSettingsFailureException(e.getMessage());
			} catch (TransformerFactoryConfigurationError e) {
				throw new WriteContentSettingsFailureException(e.getMessage());
			} catch (TransformerException e) {
				throw new WriteContentSettingsFailureException(e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		SimpleNodeOperator a = null;
		try {
			a = new SimpleNodeOperator("workspace/org.eclipse.examples.contentsettings/.contentsettings.xml");//$NON-NLS-1$
		} catch (Exception e) {
			System.exit(0);
		}

		// print all Elements
		//a.printTree(iter);

		// add Element
		Map attMap = new Hashtable();
		attMap.put("path", "hogepath");//$NON-NLS-1$ //$NON-NLS-2$
		attMap.put("fDocument-type", "documenthogehoge");//$NON-NLS-1$ //$NON-NLS-2$
		a.addElementUnderRoot("file", attMap);//$NON-NLS-1$

		try {
			a.writeDocument(System.out);
		} catch (Exception e) {
			System.err.println(e.toString());
		}

	}

	//never used
	//private DOMParser parser;
	private Document fDocument;
	private Node root;

	private String settingsFileName;


	public SimpleNodeOperator(Document doc) throws CreateContentSettingsFailureException {

		if (doc == null)
			throw new CreateContentSettingsFailureException("Document doc==null");//$NON-NLS-1$
		fDocument = doc;
		root = fDocument.getLastChild();
		if (root == null)
			throw new CreateContentSettingsFailureException("Node root==null");//$NON-NLS-1$
	}

	public SimpleNodeOperator(String fullPath) throws ReadContentSettingsFailureException {
		this.settingsFileName = fullPath;
		createObjectOfDocument();
	}

	// add attribute(attName=attValue) of ele without checking overlapping of
	// another attributes of ele.
	// if overlapping ,override
	protected Node addAttributeAt(Element ele, String attName, String attValue) {
		Attr att = fDocument.createAttribute(attName);
		att.setValue(attValue);
		if (ele != null)
			ele.setAttributeNode(att);
		return ele;
	}

	protected Node addElementUnder(Node parent, String tagName, Map attMap) {
		if (parent == null || tagName == null)
			return null;
		Element e = fDocument.createElement(tagName);
		if (attMap != null) {
			if (!attMap.isEmpty()) {
				Set attKeys = attMap.keySet();
				Iterator iter = attKeys.iterator();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					e.setAttribute(key, (String) attMap.get(key));
				}
			}
		}
		parent.appendChild(e);
		return e;
	}

	protected final Node addElementUnderRoot(String tagName) {
		return addElementUnder(root, tagName, null);
	}

	// add element with attMap as attribute without checking overlapping.
	protected final Node addElementUnderRoot(String tagName, Map attMap) {
		return addElementUnder(root, tagName, attMap);
	}

	private void createObjectOfDocument() throws ReadContentSettingsFailureException {
		try {
			fDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(settingsFileName));
		} catch (SAXException e) {
			Logger.logException("exception parsing" + settingsFileName, e); //$NON-NLS-1$
		} catch (IOException e) {
			Logger.logException("I/O exception parsing" + settingsFileName, e); //$NON-NLS-1$
		} catch (ParserConfigurationException e) {
			Logger.logException("exception accessing DOMImplementation", e); //$NON-NLS-1$
		} catch (FactoryConfigurationError e) {
			Logger.logException("exception accessing DOMImplementation", e); //$NON-NLS-1$
		}
		//get the root of the XML fDocument
		root = fDocument.getLastChild();
		if (root == null) {
			throw new ReadContentSettingsFailureException("Error: Node root==null");//$NON-NLS-1$
		}
	}

	protected Map getAttributesOf(Node node) {
		if (!node.hasAttributes())
			return null;
		Map map = new HashMap();
		NamedNodeMap attrs = node.getAttributes();
		int size = attrs.getLength();
		for (int i = 0; i < size; i++) {
			Attr attr = (Attr) attrs.item(i);
			map.put(attr.getName(), attr.getValue());
		}
		return (map);
	}

	private Node getElementWithAttribute(Node first, String attName, String attValue) {
		Node navpoint = first;
		while (navpoint != null) {
			if (navpoint.getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap m = navpoint.getAttributes();
				if (m == null)
					continue;
				if (m.getNamedItem(attName) != null) {
					if (attValue.equals(((Attr) m.getNamedItem(attName)).getNodeValue()))
						return navpoint;
				}
				NodeList childNodes = navpoint.getChildNodes();
				if (childNodes != null && childNodes.getLength() > 0) {
					Node holdNode = getElementWithAttribute(navpoint.getFirstChild(), attName, attValue);
					if (holdNode != null) {
						return holdNode;
					}
				}
			}
			navpoint = navpoint.getNextSibling();
		}
		return null;
	}


	// return a (first) Element with attr(attName=attValue) it if exists,
	// otherwise return null
	protected Node getElementWithAttribute(String attName, String attValue) {
		if (attName == null || attValue == null || !fDocument.hasChildNodes())
			return null;
		return getElementWithAttribute(fDocument.getFirstChild(), attName, attValue);
	}

	// retrun Element which has nodeName as Node Name
	protected Node getElementWithNodeName(String nodeName) {
		if (nodeName == null)
			return null;
		NodeList nodes = fDocument.getElementsByTagName(nodeName);
		if (nodes.getLength() > 0) {
			return nodes.item(0);
		}
		return null;
	}

	public void printTree(NodeIterator iter) {
		Node n;
		while ((n = iter.nextNode()) != null) {
			System.out.println(n.getNodeName() + ":");//$NON-NLS-1$
			NamedNodeMap m = n.getAttributes();
			if (m == null)
				continue;
			for (int i = 0; i < m.getLength(); i++) {
				String attName = m.item(i).getNodeName();
				System.out.print(" " + attName + "=" + m.item(i).getNodeValue());//$NON-NLS-1$ //$NON-NLS-2$
			}
			System.out.println("");//$NON-NLS-1$
		}
	}


	// remove attribute(attName) at ele.
	protected Attr removeAttributeAt(Element ele, String attName) {
		if (ele == null || attName == null)
			return null;
		Attr att = ele.getAttributeNode(attName);
		ele.removeAttribute(attName);
		return att;
	}

	protected Element removeElementWith(String nodeName) {
		NodeList nodes = fDocument.getElementsByTagName(nodeName);
		for (int i = 0; i < nodes.getLength(); i++) {
			nodes.item(i).getParentNode().removeChild(nodes.item(i));
		}
		return null;
	}

	// remove a (first) Element with attr(attName=attValue) and return it if
	// exists, otherwise return null
	protected Element removeElementWith(String attName, String attValue) {
		if (fDocument.hasChildNodes()) {
			Node element = getElementWithAttribute(attName, attValue);
			if (element != null && element.getNodeType() == Node.ELEMENT_NODE) {
				element.getParentNode().removeChild(element);
				return (Element) element;
			}
		}
		return null;

	}

	// update attribute(attName=newValue) at ele if both ele and attribute of
	// ele exist
	protected void updateAttributeAt(Element ele, String attName, String newValue) {
		Attr att = null;
		if (ele != null)
			if ((att = ele.getAttributeNode(attName)) != null)
				att.setValue(newValue);
	}

	protected void writeDocument(OutputStream out) throws WriteContentSettingsFailureException {
		XMLDocumentWriter writer = new XMLDocumentWriter(out);
		try {
			writer.serialize(fDocument);
		} finally {
			writer.close();
		}
	}


}
