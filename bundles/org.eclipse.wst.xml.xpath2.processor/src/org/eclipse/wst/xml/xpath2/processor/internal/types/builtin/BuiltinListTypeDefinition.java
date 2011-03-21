package org.eclipse.wst.xml.xpath2.processor.internal.types.builtin;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.wst.xml.xpath2.api.typesystem.SimpleTypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;

public class BuiltinListTypeDefinition extends BuiltinTypeDefinition implements SimpleTypeDefinition {
	
	private final BuiltinAtomicTypeDefinition itemType;

	public BuiltinListTypeDefinition(QName name, BuiltinTypeDefinition baseType, BuiltinAtomicTypeDefinition itemType) {
		super(name, null, Collection.class, baseType);
		this.itemType = itemType;
	}

	public BuiltinListTypeDefinition(String name, BuiltinTypeDefinition baseType, BuiltinAtomicTypeDefinition itemType) {
		super(name, null, Collection.class, baseType);
		this.itemType = itemType;
	}

	public boolean isAbstract() {
		return false;
	}

	public short getVariety() {
		// TODO Auto-generated method stub
		return 0;
	}

	public SimpleTypeDefinition getPrimitiveType() {
		// TODO Auto-generated method stub
		return null;
	}

	public short getBuiltInKind() {
		// TODO Auto-generated method stub
		return 0;
	}

	public TypeDefinition getItemType() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getMemberTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public short getOrdered() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getFinite() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getBounded() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getNumeric() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
