/*******************************************************************************
 * Copyright (c) 2009, 2010 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation 
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.testsuite.userdefined;

import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.CtrType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSString;
import org.eclipse.wst.xml.xpath2.processor.internal.types.xerces.XercesTypeDefinition;

public class XercesUserDefined extends CtrType {

	private XSTypeDefinition typeInfo;
	private String value;
	
	public XercesUserDefined(XSTypeDefinition typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
			ResultSequence rs = ResultSequenceFactory.create_new();

			if (arg.empty())
				return rs;


			//AnyAtomicType aat = (AnyAtomicType) arg.first();
			AnyType aat = arg.first();			

			rs.add(new XSString(aat.string_value()));

			return rs;
	}

	public String string_type() {
		return null;
	}

	public String string_value() {
		return value;
	}
	
	public String type_name() {
		return typeInfo.getName();
	}

	public TypeDefinition getTypeDefinition() {
		return XercesTypeDefinition.createTypeDefinition((XSTypeDefinition)typeInfo);
	}
}
