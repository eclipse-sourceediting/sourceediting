/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) bug 282223 - initial API and implementation
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;

public class XSNotation extends CtrType {

	private static final String XS_NOTATION = "xs:NOTATION";

	public String string_type() {
		return XS_NOTATION;
	}

	public String getStringValue() {
		return null;
	}

	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		if (arg.empty())
			DynamicError.throw_type_error();
		throw new DynamicError("XPST0080", "Can't Cast to NOTATION");
	}

	public String type_name() {
		return "NOTATION";
	}

	public TypeDefinition getTypeDefinition() {
		return BuiltinTypeLibrary.XS_NOTATION;
	}

	public Object getNativeValue() {
		return getStringValue();
	}
	
}
