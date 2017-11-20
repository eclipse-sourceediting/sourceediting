package org.eclipse.wst.xml.xpath2.processor.internal.types.xerces;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.validation.ValidationState;
import org.eclipse.wst.xml.xpath2.api.Item;
import org.eclipse.wst.xml.xpath2.api.typesystem.PrimitiveType;

public class SimpleXercesType extends SimpleXercesTypeDefinition implements PrimitiveType {

	private final XSSimpleType simpleType;

	public SimpleXercesType(XSSimpleType ad) {
		super(ad);
		this.simpleType = ad;
	}

	public short getPrimitiveKind() {
		return simpleType.getPrimitiveKind();
	}

	public boolean validate(String content) {
		boolean isValueValid = true;
		try {
			ValidatedInfo validatedInfo = new ValidatedInfo();
			ValidationContext validationState = new ValidationState();
     		
			// attempt to validate the value with the simpleType
			simpleType.validate(content, validationState, validatedInfo);
	    } 
		catch(InvalidDatatypeValueException ex){
			isValueValid = false;
	    }
		return isValueValid;
	}

	public boolean isEqual(Object value1, Object value2) {
		return simpleType.isEqual(value1, value2);
	}

	public boolean isIDType() {
		return simpleType.isIDType();
	}

	public boolean validateNative(Object content) {

		return false;
	}

	public Item construct(Object content) {
		throw new RuntimeException("construct not supported for Xerces types");
	}

	public Class getInterfaceClass() {
		throw new RuntimeException("getInterfaceClass not supported for Xerces types");
	}

	public Class getNativeType() {
		throw new RuntimeException("getNativeType not supported for Xerces types");
	}

	
}
