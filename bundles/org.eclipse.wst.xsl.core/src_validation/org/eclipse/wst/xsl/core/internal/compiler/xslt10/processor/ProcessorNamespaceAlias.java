/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 224197 - initial API and implementation
 *                    based on work from Apache Xalan 2.7.0
 *******************************************************************************/

/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: ProcessorNamespaceAlias.java,v 1.2 2008/03/28 02:38:16 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.processor;

import org.apache.xalan.res.XSLTErrorResources;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.NamespaceAlias;
import org.xml.sax.Attributes;

/**
 * TransformerFactory for xsl:namespace-alias markup. A stylesheet can use the
 * xsl:namespace-alias element to declare that one namespace URI is an alias for
 * another namespace URI.
 * 
 * <pre>
 * &lt;!ELEMENT xsl:namespace-alias EMPTY&gt;
 * &lt;!ATTLIST xsl:namespace-alias
 *   stylesheet-prefix CDATA #REQUIRED
 *   result-prefix CDATA #REQUIRED
 * &gt;
 * </pre>
 * 
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a
 *      href="http://www.w3.org/TR/xslt#literal-result-element">literal-result-element
 *      in XSLT Specification</a>
 */
public class ProcessorNamespaceAlias extends XSLTElementProcessor {
	static final long serialVersionUID = -6309867839007018964L;

	/**
	 * Receive notification of the start of an xsl:namespace-alias element.
	 * 
	 * @param handler
	 *            The calling StylesheetHandler/TemplatesBuilder.
	 * @param uri
	 *            The Namespace URI, or the empty string if the element has no
	 *            Namespace URI or if Namespace processing is not being
	 *            performed.
	 * @param localName
	 *            The local name (without prefix), or the empty string if
	 *            Namespace processing is not being performed.
	 * @param rawName
	 *            The raw XML 1.0 name (with prefix), or the empty string if raw
	 *            names are not available.
	 * @param attributes
	 *            The attributes attached to the element. If there are no
	 *            attributes, it shall be an empty Attributes object.
	 */
	@Override
	public void startElement(StylesheetHandler handler, String uri,
			String localName, String rawName, Attributes attributes)
			throws org.xml.sax.SAXException {
		final String resultNS;
		NamespaceAlias na = new NamespaceAlias(handler.nextUid());

		setPropertiesFromAttributes(handler, rawName, attributes, na);
		String prefix = na.getStylesheetPrefix();
		if (prefix.equals("#default")) {
			prefix = "";
			na.setStylesheetPrefix(prefix);
		}
		String stylesheetNS = handler.getNamespaceForPrefix(prefix);
		na.setStylesheetNamespace(stylesheetNS);
		prefix = na.getResultPrefix();
		if (prefix.equals("#default")) {
			prefix = "";
			na.setResultPrefix(prefix);
			resultNS = handler.getNamespaceForPrefix(prefix);
			if (null == resultNS)
				handler
						.error(
								XSLTErrorResources.ER_INVALID_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX_FOR_DEFAULT,
								null, null);
		} else {
			resultNS = handler.getNamespaceForPrefix(prefix);
			if (null == resultNS)
				handler
						.error(
								XSLTErrorResources.ER_INVALID_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX,
								new Object[] { prefix }, null);
		}

		na.setResultNamespace(resultNS);
		handler.getStylesheet().setNamespaceAlias(na);
		handler.getStylesheet().appendChild(na);
	}
}
