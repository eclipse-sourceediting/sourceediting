package org.eclipse.wst.xml.xpath2.processor.testsuite.userdefined;

import org.apache.xerces.xs.XSObject;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.QName;

public class XercesQNameUserDefined extends QName {

	private XSObject typeInfo;
	
	public XercesQNameUserDefined(XSObject typeInfo) {
		this.typeInfo = typeInfo;
	}

	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;


		//AnyAtomicType aat = (AnyAtomicType) arg.first();
		AnyType aat = arg.first();
		
		QName qname = QName.parse_QName(aat.string_value());
		
		rs.add(qname);

		return rs;

		
	}
	
	@Override
	public String type_name() {
		return typeInfo.getName();
	}
}
