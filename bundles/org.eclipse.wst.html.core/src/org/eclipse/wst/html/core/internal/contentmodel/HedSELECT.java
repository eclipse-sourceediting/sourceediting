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
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Arrays;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * SELECT.
 */
final class HedSELECT extends HTMLElemDeclImpl {

	/**
	 */
	public HedSELECT(ElementCollection collection) {
		super(HTML40Namespace.ElementName.SELECT, collection);
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
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_SIZE, attr);

		String[] names = {HTML40Namespace.ATTR_NAME_NAME, HTML40Namespace.ATTR_NAME_MULTIPLE, HTML40Namespace.ATTR_NAME_DISABLED, HTML40Namespace.ATTR_NAME_TABINDEX, HTML40Namespace.ATTR_NAME_ONFOCUS, HTML40Namespace.ATTR_NAME_ONBLUR, HTML40Namespace.ATTR_NAME_ONCHANGE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.BUTTON};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
