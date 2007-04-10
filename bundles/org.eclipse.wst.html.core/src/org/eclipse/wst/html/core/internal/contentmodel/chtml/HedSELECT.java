/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import java.util.Arrays;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 * SELECT.
 */
final class HedSELECT extends HTMLElemDeclImpl {

	/**
	 */
	public HedSELECT(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.SELECT, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_SELECT;
		layoutType = LAYOUT_OBJECT;
		indentChild = true;
	}

	/**
	 * %attrs;
	 * %reserved;
	 * (name CDATA #IMPLIED)
	 * (size NUMBER #IMPLIED) ... should be defined locally.
	 * (multiple (multiple) #IMPLIED)
	 * (disabled (disabled) #IMPLIED)
	 * (tabindex NUMBER #IMPLIED)
	 * (onfocus %Script; #IMPLIED)
	 * (onblur %Script; #IMPLIED)
	 * (onchange %Script; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		// (size NUMBER #IMPLIED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(CHTMLNamespace.ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_SIZE, attr);

		String[] names = {CHTMLNamespace.ATTR_NAME_NAME, CHTMLNamespace.ATTR_NAME_MULTIPLE,};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
