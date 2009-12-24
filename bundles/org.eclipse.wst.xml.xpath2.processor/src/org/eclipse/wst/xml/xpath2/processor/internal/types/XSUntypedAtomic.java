/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver - bug 262765 - corrected implementation of XSUntypedAtomic 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;

/**
 * A representation of the UntypedAtomic datatype which is used to represent
 * untyped atomic nodes.
 */
public class XSUntypedAtomic extends CtrType {
	private static final String XS_UNTYPED_ATOMIC = "xs:untypedAtomic";
	private String _value;

	
	public XSUntypedAtomic() {
		this(null);
	}
	
	/**
	 * Initialises using the supplied String
	 * 
	 * @param x
	 *            The String representation of the value of the untyped atomic
	 *            node
	 */
	public XSUntypedAtomic(String x) {
		_value = x;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:untypedAtomic" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return XS_UNTYPED_ATOMIC;
	}

	/**
	 * Retrieves a String representation of the value of this untyped atomic
	 * node
	 * 
	 * @return String representation of the value of this untyped atomic node
	 */
	@Override
	public String string_value() {
		return _value;
	}

	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		rs.add(new XSUntypedAtomic(aat.string_value()));

		return rs;
	}

	@Override
	public String type_name() {
		return "untypedAtomic";
	}
}
