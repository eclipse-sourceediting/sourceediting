package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.api.AtomicItemType;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;

public class SimpleAtomicItemTypeImpl implements AtomicItemType {

	private final short occurrence;
	private final TypeDefinition typeDefinition;

	public SimpleAtomicItemTypeImpl(TypeDefinition typeDefinition) {
		super();
		this.typeDefinition = typeDefinition;
		this.occurrence = OCCURRENCE_ONE;
	}

	public SimpleAtomicItemTypeImpl(TypeDefinition typeDefinition, short occurrence) {
		super();
		this.typeDefinition = typeDefinition;
		this.occurrence = occurrence;
	}

	public short getOccurrence() {
		return this.occurrence;
	}

	public TypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

}
