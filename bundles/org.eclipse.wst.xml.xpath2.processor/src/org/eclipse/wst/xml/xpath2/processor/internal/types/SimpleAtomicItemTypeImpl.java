package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.api.AtomicItemType;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;

public class SimpleAtomicItemTypeImpl implements AtomicItemType {

	private TypeDefinition typeDefinition;

	public SimpleAtomicItemTypeImpl(TypeDefinition typeDefinition) {
		super();
		this.typeDefinition = typeDefinition;
	}

	public short getOccurrence() {
		return OCCURRENCE_ONE;
	}

	public TypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

}
