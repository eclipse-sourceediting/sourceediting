package org.eclipse.wst.xml.xpath2.processor.internal.types.xerces;

import java.util.LinkedList;
import java.util.List;

import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.api.typesystem.ComplexTypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.SimpleTypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SimpleXercesTypeDefinition extends XercesTypeDefinition implements SimpleTypeDefinition {

	private final XSSimpleTypeDefinition simpleTypeDefinition;

	public SimpleXercesTypeDefinition(XSSimpleTypeDefinition ad) {
		super(ad);
		this.simpleTypeDefinition = ad;
	}

	public short getVariety() {
		return simpleTypeDefinition.getVariety();
	}

	public SimpleTypeDefinition getPrimitiveType() {
		return createTypeDefinition(simpleTypeDefinition.getPrimitiveType());
	}

	public short getBuiltInKind() {
		return simpleTypeDefinition.getBuiltInKind();
	}

	public TypeDefinition getItemType() {
		return createTypeDefinition(simpleTypeDefinition.getItemType());
	}

	public List/*<SimpleTypeDefinition>*/ getMemberTypes() {
		XSObjectList xsMemberTypes = simpleTypeDefinition.getMemberTypes();
		List/*<SimpleTypeDefinition>*/ memberTypes = new LinkedList/*<SimpleTypeDefinition>*/();
		for (int i = 0; i < xsMemberTypes.getLength(); i++) {
			memberTypes.add(createTypeDefinition((XSSimpleTypeDefinition) xsMemberTypes.item(i)));
		}
		return memberTypes;
	}

	public short getOrdered() {
		return simpleTypeDefinition.getOrdered();
	}

	public boolean getFinite() {
		return simpleTypeDefinition.getFinite();
	}

	public boolean getBounded() {
		return simpleTypeDefinition.getBounded();
	}

	public boolean getNumeric() {
		return simpleTypeDefinition.getNumeric();
	}

	public Class getNativeType() {
		return Node.class;
	}

}
