/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath.core.util;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class XSLTXPathHelper {

	/**
	 * Use an XPath string to select a single node. XPath namespace prefixes are
	 * resolved from the context node, which may not be what you want (see the
	 * next method).
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @return The first node found that matches the XPath, or null.
	 * 
	 * @throws TransformerException
	 */
	public static Node selectSingleNode(Node contextNode, String str)
			throws TransformerException {
		return selectSingleNode(contextNode, str, contextNode);
	}

	/**
	 * Use an XPath string to select a single node. XPath namespace prefixes are
	 * resolved from the namespaceNode.
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @param namespaceNode
	 *            The node from which prefixes in the XPath will be resolved to
	 *            namespaces.
	 * @return The first node found that matches the XPath, or null.
	 * 
	 * @throws TransformerException
	 */
	public static Node selectSingleNode(Node contextNode, String str,
			Node namespaceNode) throws TransformerException {

		// Have the XObject return its result as a NodeSetDTM.
		NodeIterator nl = selectNodeIterator(contextNode, str, namespaceNode);

		// Return the first node, or null
		return nl.nextNode();
	}

	/**
	 * Use an XPath string to select a nodelist. XPath namespace prefixes are
	 * resolved from the contextNode.
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @return A NodeIterator, should never be null.
	 * 
	 * @throws TransformerException
	 */
	public static NodeIterator selectNodeIterator(Node contextNode, String str)
			throws TransformerException {
		return selectNodeIterator(contextNode, str, contextNode);
	}

	/**
	 * Use an XPath string to select a nodelist. XPath namespace prefixes are
	 * resolved from the namespaceNode.
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @param namespaceNode
	 *            The node from which prefixes in the XPath will be resolved to
	 *            namespaces.
	 * @return A NodeIterator, should never be null.
	 * 
	 * @throws TransformerException
	 */
	public static NodeIterator selectNodeIterator(Node contextNode, String str,
			Node namespaceNode) throws TransformerException {

		// Execute the XPath, and have it return the result
		XObject list = eval(contextNode, str, namespaceNode);

		// Have the XObject return its result as a NodeSetDTM.
		return list.nodeset();
	}

	/**
	 * Use an XPath string to select a nodelist. XPath namespace prefixes are
	 * resolved from the contextNode.
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @return A NodeIterator, should never be null.
	 * 
	 * @throws TransformerException
	 */
	public static NodeList selectNodeList(Node contextNode, String str)
			throws TransformerException {
		return selectNodeList(contextNode, str, contextNode);
	}

	/**
	 * Use an XPath string to select a nodelist. XPath namespace prefixes are
	 * resolved from the namespaceNode.
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @param namespaceNode
	 *            The node from which prefixes in the XPath will be resolved to
	 *            namespaces.
	 * @return A NodeIterator, should never be null.
	 * 
	 * @throws TransformerException
	 */
	public static NodeList selectNodeList(Node contextNode, String str,
			Node namespaceNode) throws TransformerException {

		// Execute the XPath, and have it return the result
		XObject list = eval(contextNode, str, namespaceNode);

		// Return a NodeList.
		return list.nodelist();
	}

	/**
	 * Evaluate XPath string to an XObject. Using this method, XPath namespace
	 * prefixes will be resolved from the namespaceNode.
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @return An XObject, which can be used to obtain a string, number,
	 *         nodelist, etc, should never be null.
	 * @see org.apache.xpath.objects.XObject
	 * @see org.apache.xpath.objects.XNull
	 * @see org.apache.xpath.objects.XBoolean
	 * @see org.apache.xpath.objects.XNumber
	 * @see org.apache.xpath.objects.XString
	 * @see org.apache.xpath.objects.XRTreeFrag
	 * 
	 * @throws TransformerException
	 */
	public static XObject eval(Node contextNode, String str)
			throws TransformerException {
		return eval(contextNode, str, contextNode);
	}

	/**
	 * Evaluate XPath string to an XObject. XPath namespace prefixes are
	 * resolved from the namespaceNode. The implementation of this is a little
	 * slow, since it creates a number of objects each time it is called. This
	 * could be optimized to keep the same objects around, but then
	 * thread-safety issues would arise.
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @param namespaceNode
	 *            The node from which prefixes in the XPath will be resolved to
	 *            namespaces.
	 * @return An XObject, which can be used to obtain a string, number,
	 *         nodelist, etc, should never be null.
	 * @see org.apache.xpath.objects.XObject
	 * @see org.apache.xpath.objects.XNull
	 * @see org.apache.xpath.objects.XBoolean
	 * @see org.apache.xpath.objects.XNumber
	 * @see org.apache.xpath.objects.XString
	 * @see org.apache.xpath.objects.XRTreeFrag
	 * 
	 * @throws TransformerException
	 * 
	 */
	public static XObject eval(Node contextNode, String str, Node namespaceNode)
			throws TransformerException {

		// Since we don't have a XML Parser involved here, install some default
		// support
		// for things like namespaces, etc.
		// (Changed from: XPathContext xpathSupport = new XPathContext();
		// because XPathContext is weak in a number of areas... perhaps
		// XPathContext should be done away with.)
		XPathContext xpathSupport = new XPathContext();

		// Create an object to resolve namespace prefixes.
		// XPath namespaces are resolved from the input context node's document
		// element
		// if it is a root node, or else the current context node (for lack of a
		// better
		// resolution space, given the simplicity of this sample code).
		PrefixResolverDefault prefixResolver = new PrefixResolverDefault(
				(namespaceNode.getNodeType() == Node.DOCUMENT_NODE) ? ((Document) namespaceNode)
						.getDocumentElement()
						: namespaceNode);

		// Create the XPath object.
		XPath xpath = new XPath(str, null, prefixResolver, XPath.SELECT, null,
				getFunctionTable());

		// Execute the XPath, and have it return the result
		// return xpath.execute(xpathSupport, contextNode, prefixResolver);
		int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);

		return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
	}

	/**
	 * Evaluate XPath string to an XObject. XPath namespace prefixes are
	 * resolved from the namespaceNode. The implementation of this is a little
	 * slow, since it creates a number of objects each time it is called. This
	 * could be optimized to keep the same objects around, but then
	 * thread-safety issues would arise.
	 * 
	 * @param contextNode
	 *            The node to start searching from.
	 * @param str
	 *            A valid XPath string.
	 * @param prefixResolver
	 *            Will be called if the parser encounters namespace prefixes, to
	 *            resolve the prefixes to URLs.
	 * @return An XObject, which can be used to obtain a string, number,
	 *         nodelist, etc, should never be null.
	 * @see org.apache.xpath.objects.XObject
	 * @see org.apache.xpath.objects.XNull
	 * @see org.apache.xpath.objects.XBoolean
	 * @see org.apache.xpath.objects.XNumber
	 * @see org.apache.xpath.objects.XString
	 * @see org.apache.xpath.objects.XRTreeFrag
	 * 
	 * @throws TransformerException
	 */
	public static XObject eval(Node contextNode, String str,
			PrefixResolver prefixResolver) throws TransformerException {

		// Since we don't have a XML Parser involved here, install some default
		// support
		// for things like namespaces, etc.
		// (Changed from: XPathContext xpathSupport = new XPathContext();
		// because XPathContext is weak in a number of areas... perhaps
		// XPathContext should be done away with.)
		// Create the XPath object.
		XPath xpath = new XPath(str, null, prefixResolver, XPath.SELECT, null,
				getFunctionTable());

		// Execute the XPath, and have it return the result
		XPathContext xpathSupport = new XPathContext();
		int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);

		return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
	}

	public static void compile(String expression)
			throws XPathExpressionException {
		try {
			new XPath(expression, null, null, org.apache.xpath.XPath.SELECT,
					null, getFunctionTable());
		} catch (javax.xml.transform.TransformerException te) {
			throw new XPathExpressionException(te);
		}

	}

	protected static FunctionTable getFunctionTable() {
		FunctionTable functionTable = new FunctionTable();
		functionTable.installFunction("key",
				org.apache.xalan.templates.FuncKey.class);
		functionTable.installFunction("format-number",
				org.apache.xalan.templates.FuncFormatNumb.class);
		functionTable.installFunction("document",
				org.apache.xalan.templates.FuncDocument.class);
		functionTable.installFunction("element-available",
				org.apache.xpath.functions.FuncExtElementAvailable.class);
		functionTable.installFunction("function-available",
				org.apache.xpath.functions.FuncExtFunctionAvailable.class);
		functionTable.installFunction("current",
				org.apache.xpath.functions.FuncCurrent.class);
		functionTable.installFunction("unparsed-entity-string",
				org.apache.xpath.functions.FuncUnparsedEntityURI.class);
		functionTable.installFunction("generate-id",
				org.apache.xpath.functions.FuncGenerateId.class);
		functionTable.installFunction("system-property",
				org.apache.xpath.functions.FuncSystemProperty.class);
		return functionTable;
	}

	/**
	 * Returns a XPath expression given a DOM Node.
	 * 
	 * @param node
	 *            The DOM Node to create the XPath expression.
	 * @since 1.0
	 */
	public static String calculateXPathToNode(Node node) {
		Node xpathNode = node;
		StringBuffer sb = new StringBuffer();
		while (xpathNode != null) {
			switch (xpathNode.getNodeType()) {
			case Node.ATTRIBUTE_NODE:
				sb.insert(0, xpathNode.getNodeName());
				sb.insert(0, "/@"); //$NON-NLS-1$
				xpathNode = ((Attr) xpathNode).getOwnerElement();
				break;
			case Node.ELEMENT_NODE:
				Node sibling = xpathNode;
				int position = 1;
				while ((sibling = sibling.getPreviousSibling()) != null) {
					if (sibling.getNodeType() == Node.ELEMENT_NODE
							&& sibling.getNodeName().equals(xpathNode.getNodeName())) {
						++position;
					}
				}
				if (position > 1) {
					sb.insert(0, "[" + position + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					sibling = xpathNode.getNextSibling();
					boolean following = false;
					while (sibling != null) {
						if (sibling.getNodeType() == Node.ELEMENT_NODE
								&& sibling.getNodeName().equals(
										xpathNode.getNodeName())) {
							following = true;
							break;
						}
						sibling = sibling.getNextSibling();
					}
					if (following) {
						sb.insert(0, "[1]"); //$NON-NLS-1$
					}
				}
				sb.insert(0, xpathNode.getNodeName());
				sb.insert(0, "/"); //$NON-NLS-1$
				xpathNode = xpathNode.getParentNode();
				break;
			default:
				xpathNode = xpathNode.getParentNode();
			}
		}
		return sb.toString();
	}

}
