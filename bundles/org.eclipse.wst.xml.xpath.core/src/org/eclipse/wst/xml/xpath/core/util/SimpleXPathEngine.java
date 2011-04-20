package org.eclipse.wst.xml.xpath.core.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface SimpleXPathEngine {
	void parse(String expression) throws XPathExpressionException;
	boolean isValid();
	NodeList execute(Node contextNode) throws XPathExpressionException;
	void setNamespaceContext(NamespaceContext namespaceContext);
}
