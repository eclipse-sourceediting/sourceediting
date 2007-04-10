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
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * HR.
 */
final class HedHR extends HedEmpty {

	/**
	 */
	public HedHR(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.HR, collection);
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
		String[] values = {CHTMLNamespace.ATTR_VALUE_LEFT, CHTMLNamespace.ATTR_VALUE_CENTER, CHTMLNamespace.ATTR_VALUE_RIGHT};
		atype.setEnumValues(values);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(CHTMLNamespace.ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_ALIGN, attr);

		// the rest.
		String[] names = {CHTMLNamespace.ATTR_NAME_NOSHADE, CHTMLNamespace.ATTR_NAME_SIZE, CHTMLNamespace.ATTR_NAME_WIDTH,};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {CHTMLNamespace.ElementName.DIR, CHTMLNamespace.ElementName.MENU};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
