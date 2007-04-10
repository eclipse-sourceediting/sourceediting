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
 * HR.
 */
final class HedHR extends HedEmpty {

	/**
	 */
	public HedHR(ElementCollection collection) {
		super(HTML40Namespace.ElementName.HR, collection);
		layoutType = LAYOUT_BLOCK;
	}

	/**
	 * %attrs
	 * (align (left|center|right) #IMPLIED) ... should be defined locally.
	 * (noshade (noshade) #IMPLIED)
	 * (size %Pixels; #IMPLIED)
	 * (width %Length; #IMPLIED)
	 * (color %Color; #IMPLIED) ... D205514
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		// (align (left|center|right) #IMPLIED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] values = {HTML40Namespace.ATTR_VALUE_LEFT, HTML40Namespace.ATTR_VALUE_CENTER, HTML40Namespace.ATTR_VALUE_RIGHT};
		atype.setEnumValues(values);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALIGN, attr);

		// the rest.
		String[] names = {HTML40Namespace.ATTR_NAME_NOSHADE, HTML40Namespace.ATTR_NAME_SIZE, HTML40Namespace.ATTR_NAME_WIDTH, HTML40Namespace.ATTR_NAME_COLOR // D205514
		};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.DIR, HTML40Namespace.ElementName.MENU};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
