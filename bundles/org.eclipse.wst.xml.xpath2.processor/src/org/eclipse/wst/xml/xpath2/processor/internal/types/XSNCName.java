/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;

/**
 * A representation of the NCName datatype
 */
public class XSNCName extends XSString {
	private static final String XS_NC_NAME = "xs:NCName";

	/**
	 * Initialises using the supplied String
	 * 
	 * @param x
	 *            String to be stored
	 */
	public XSNCName(String x) {
		super(x);
	}

	/**
	 * Initialises to null
	 */
	public XSNCName() {
		this(null);
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:NCName" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return XS_NC_NAME;
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "NCName" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "NCName";
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable NCName within
	 * the supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which to extract the NCName
	 * @return New ResultSequence consisting of the NCName supplied
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		rs.add(new XSNCName(aat.string_value()));

		return rs;
	}
}
