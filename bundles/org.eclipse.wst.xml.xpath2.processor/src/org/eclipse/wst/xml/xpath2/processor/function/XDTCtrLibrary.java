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
 * Data Types control library support.
 */
public class XDTCtrLibrary extends ConstructorFL {
	/**
	 * Path to W3.org data types specification.
	 */
	public static final String XDT_NS = "http://www.w3.org/2004/10/xpath-datatypes";
	/**
	 * Constructor for XDTCtrLibrary.
	 */
	public XDTCtrLibrary() {
		super(XDT_NS);

		// add types here
		add_type(new XDTYearMonthDuration());
		add_type(new XDTDayTimeDuration());
	}
}
