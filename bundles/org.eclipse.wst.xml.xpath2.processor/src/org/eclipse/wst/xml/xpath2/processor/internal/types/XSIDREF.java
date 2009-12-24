/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) bug 228223 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;

/*
 * Implements the xs:IDREF data type.
 * 
 * @since 1.1
 */
public class XSIDREF extends XSNCName {

	private static final String XS_IDREF = "xs:IDREF";

	public XSIDREF(String x) {
		super(x);
	}

	public XSIDREF() {
		super();
	}

	@Override
	public String string_type() {
		return XS_IDREF;
	}
	
	@Override
	public String type_name() {
		return "IDREF";
	}
	
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		rs.add(new XSIDREF(aat.string_value()));

		return rs;
	}

}
