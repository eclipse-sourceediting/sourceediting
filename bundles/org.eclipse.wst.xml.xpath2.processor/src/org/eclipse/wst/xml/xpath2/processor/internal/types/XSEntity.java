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

/**
 * Built in Data Type for xs:Entity
 * 
 * @author dcarver
 * @since 1.1
 */
public class XSEntity extends XSNCName {
	
	private static final String XS_ENTITY = "xs:ENTITY";

	public XSEntity() {
		super();
	}
	
	public XSEntity(String value) {
		super(value);
	}
	
	@Override
	public String string_type() {
		return XS_ENTITY;
	}
	
	@Override
	public String type_name() {
		return "ENTITY";
	}

	/**
	 * Creates a new ResultSequence consisting of the ENTITY within
	 * the supplied ResultSequence.  The specification says that this
	 * is relaxed from the XML Schema requirement.  The ENTITY does
	 * not have to be located or expanded during construction and
	 * evaluation for casting.
	 * 
	 * @param arg
	 *            The ResultSequence from which to extract the ENTITY
	 * @return New ResultSequence consisting of the ENTITY supplied
	 * @throws DynamicError
	 */

	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();
		

		rs.add(new XSEntity(aat.string_value()));

		return rs;

	}
}
