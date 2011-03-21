/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) bug 228223 - initial API and implementation
 *     Mukul Gandhi - bug 334842 - improving support for the data types Name, NCName, ENTITY, 
 *                                 ID, IDREF and NMTOKEN.
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;

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

	public String string_type() {
		return XS_IDREF;
	}
	
	public String type_name() {
		return "IDREF";
	}
	
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();
		String strValue = aat.string_value();
		
		if (!isConstraintSatisfied(strValue)) {
			// invalid input
			DynamicError.throw_type_error();
		}

		rs.add(new XSIDREF(strValue));

		return rs;
	}

	public TypeDefinition getTypeDefinition() {
		return BuiltinTypeLibrary.XS_IDREF;
	}
	
}
