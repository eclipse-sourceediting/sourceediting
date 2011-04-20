/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - bug 226245 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.core.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.wst.xml.xpath2.api.XPath2Expression;
import org.eclipse.wst.xml.xpath2.processor.Engine;
import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.util.DynamicContextBuilder;
import org.eclipse.wst.xml.xpath2.processor.util.StaticContextBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @since 1.0
 */
public class XPath20Helper {

	public XPath20Helper() {
	}
	
	public static class XPath2Engine implements SimpleXPathEngine {

		private NamespaceContext namespaceContext;
		private StaticContextBuilder staticContextBuilder = new StaticContextBuilder() {
			@Override
			public NamespaceContext getNamespaceContext() {
				return namespaceContext;
			}
		};
		private XPath2Expression xPathExpression;

		public void parse(String expression) throws XPathExpressionException {
			xPathExpression = null;
			try {
				xPathExpression = new Engine().parseExpression(expression, staticContextBuilder);
			} catch (StaticError se) {
				throw new XPathExpressionException(se.getMessage() + " (" + se.code() + ")");
			}
		}

		public boolean isValid() {
			return xPathExpression != null;
		}

		public NodeList execute(Node contextNode) {
			DynamicContextBuilder dynContext = new DynamicContextBuilder(staticContextBuilder);
			 org.eclipse.wst.xml.xpath2.api.ResultSequence rs = xPathExpression.evaluate(dynContext, new Object[] { contextNode });
			 return new NodeListImpl(rs);
		}

		public void setNamespaceContext(NamespaceContext namespaceContext) {
			this.namespaceContext = namespaceContext;
//			if (namespaceContext != null) {
//				namespaceContext.dc.add_namespace("xs", "http://www.w3.org/2001/XMLSchema");
		}
	}

}
