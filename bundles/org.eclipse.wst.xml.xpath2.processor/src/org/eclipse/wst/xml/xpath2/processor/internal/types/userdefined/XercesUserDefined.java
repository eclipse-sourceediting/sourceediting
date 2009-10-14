/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation 
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.internal.types.userdefined;

import org.apache.xerces.xs.XSObject;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.CtrType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSString;

public class XercesUserDefined extends CtrType {

	private XSObject typeInfo;
	private String value;
	
	public XercesUserDefined(XSObject typeInfo) {
		this.typeInfo = typeInfo;
	}
	

	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
			ResultSequence rs = ResultSequenceFactory.create_new();

			if (arg.empty())
				return rs;


			//AnyAtomicType aat = (AnyAtomicType) arg.first();
			AnyType aat = arg.first();			

			rs.add(new XSString(aat.string_value()));

			return rs;
	}


	@Override
	public String string_type() {
		return null;
	}

	@Override
	public String string_value() {
		return value;
	}
	
	@Override
	public String type_name() {
		return typeInfo.getName();
	}

}
