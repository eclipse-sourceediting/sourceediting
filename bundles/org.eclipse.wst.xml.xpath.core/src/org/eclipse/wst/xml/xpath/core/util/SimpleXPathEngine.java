/*******************************************************************************
 * Copyright (c) 2011 Jesper Steen Moller and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moller - initial API and implementation
 *     Jesper Steen Moller - bug 343804 - Updated API information
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.core.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @since 1.2
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface SimpleXPathEngine {
	void parse(String expression) throws XPathExpressionException;
	boolean isValid();
	NodeList execute(Node contextNode) throws XPathExpressionException;
	void setNamespaceContext(NamespaceContext namespaceContext);
}
