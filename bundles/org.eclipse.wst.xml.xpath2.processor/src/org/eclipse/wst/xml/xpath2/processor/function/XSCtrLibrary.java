/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.function;

import org.eclipse.wst.xml.xpath2.processor.types.*;

/**
 * XML Schema control library support.
 */
public class XSCtrLibrary extends ConstructorFL {
	/**
	 * Path to W3.org XML Schema specification.
	 */
	public static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

	/**
	 * Constructor for XSCtrLibrary.
	 */
	public XSCtrLibrary() {
		super(XML_SCHEMA_NS);

		// add types here
		add_type(new XSString());
		add_type(new XSBoolean());

		// numeric
		add_type(new XSDecimal());
		add_type(new XSFloat());
		add_type(new XSDouble());
		add_type(new XSInteger());

		// date
		add_type(new XSDateTime());
		add_type(new XSDate());
		add_type(new XSTime());
		add_type(new XSGYearMonth());
		add_type(new XSGYear());
		add_type(new XSGMonthDay());
		add_type(new XSGMonth());
		add_type(new XSGDay());

		add_type(new QName());
		add_type(new XSNCName());
		add_type(new XSAnyURI());
	}
}
