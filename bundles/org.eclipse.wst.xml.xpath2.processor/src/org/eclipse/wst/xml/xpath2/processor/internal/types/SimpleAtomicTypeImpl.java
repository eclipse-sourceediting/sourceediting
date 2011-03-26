package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.api.AtomicItemType;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;

public class SimpleAtomicTypeImpl implements AtomicItemType {

	private TypeDefinition typeDefinition;

	public SimpleAtomicTypeImpl(TypeDefinition typeDefinition) {
		super();
		this.typeDefinition = typeDefinition;
	}

	public int getOccurrence() {
		return OCCURRENCE_ONE;
	}

	public TypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

}
