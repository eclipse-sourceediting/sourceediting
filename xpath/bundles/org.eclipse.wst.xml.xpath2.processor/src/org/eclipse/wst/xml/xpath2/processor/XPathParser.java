/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Bug 338494    - prohibiting xpath expressions starting with / or // to be parsed.
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

import org.eclipse.wst.xml.xpath2.processor.ast.XPath;

/**
 * This is an interface class for the XPath parser.
 */
public interface XPathParser {

	/**
	 * Constructor for the XPath parser interface.
	 * 
	 * @param xpath
	 *            is the input XPath to be parsed.
	 * @throws XPathParserException
	 *             XPath parser exception.
	 * @return The parsed XPath.
	 */
	public XPath parse(String xpath) throws XPathParserException;
	
	/**
	 * Constructor for the XPath parser interface.
	 * 
	 * @param xpath
	 *            is the input XPath to be parsed.
	 * @param isRootlessAccess
	 *            if 'true' then PsychoPath engine can't parse xpath expressions starting with / or //.
	 * @throws XPathParserException
	 *             XPath parser exception.
	 * @return The parsed XPath.
	 * @since 2.0
	 */
	public XPath parse(String xpath, boolean isRootlessAccess) throws XPathParserException;
}
