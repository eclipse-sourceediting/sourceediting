/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) bug 282223 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;

public class XSNotation extends CtrType {

	public String string_type() {
		return "xs:NOTATION";
	}

	public String string_value() {
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

}
