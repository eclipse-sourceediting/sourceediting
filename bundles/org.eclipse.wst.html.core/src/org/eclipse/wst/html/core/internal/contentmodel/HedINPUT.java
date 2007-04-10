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
 * INPUT.
 */
final class HedINPUT extends HedEmpty {

	/**
	 */
	public HedINPUT(ElementCollection collection) {
		super(HTML40Namespace.ElementName.INPUT, collection);
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * INPUT.
	 * %attrs;
	 * (type %InputType; TEXT) ... should be defined locally.
	 * (name CDATA #IMPLIED)
	 * (value CDATA #IMPLIED)
	 * (checked (checked) #IMPLIED)
	 * (disabled (disabled) #IMPLIED)
	 * (readonly (readonly) #IMPLIED)
	 * (size CDATA #IMPLIED) ... should be defined locally.
	 * (maxlength NUMBER #IMPLIED)
	 * (src %URI; #IMPLIED)
	 * (alt CDATA #IMPLIED) ... should be defined locally.
	 * (usemap %URI; #IMPLIED)
	 * (ismap (ismap) #IMPLIED)
	 * (tabindex NUMBER #IMPLIED)
	 * (accesskey %Character; #IMPLIED)
	 * (onfocus %Script; #IMPLIED)
	 * (onblur %Script; #IMPLIED)
	 * (onselect %Script; #IMPLIED)
	 * (onchange %Script; #IMPLIED)
	 * (accept %ContentTypes; #IMPLIED)
	 * (align %IAlign; #IMPLIED) ... should be defined locally.
	 * (istyle CDATA #IMPLIED)
	 * <<D215684
	 * (width CDATA; #IMPLIED)
	 * (height CDATA; #IMPLIED)
	 * (border CDATA; #IMPLIED)
	 * D215684
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		HTMLCMDataTypeImpl atype = null;
		HTMLAttrDeclImpl attr = null;
		// (type %InputType; TEXT) ... should be defined locally.
		// NOTE: %InputType is ENUM;
		// (text | password | checkbox | radio | submit | reset |
		//  file | hidden | image | button)
		atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] values = {HTML40Namespace.ATTR_VALUE_TEXT, HTML40Namespace.ATTR_VALUE_PASSWORD, HTML40Namespace.ATTR_VALUE_CHECKBOX, HTML40Namespace.ATTR_VALUE_RADIO, HTML40Namespace.ATTR_VALUE_SUBMIT, HTML40Namespace.ATTR_VALUE_RESET, HTML40Namespace.ATTR_VALUE_FILE, HTML40Namespace.ATTR_VALUE_HIDDEN, HTML40Namespace.ATTR_VALUE_IMAGE, HTML40Namespace.ATTR_VALUE_BUTTON};
		atype.setEnumValues(values);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, HTML40Namespace.ATTR_VALUE_TEXT);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_TYPE, attr);

		// (size CDATA #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_SIZE, attr);

		// (alt CDATA #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_ALT, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALT, attr);

		// (align %IAlign; #IMPLIED) ... should be defined locally.
		attr = AttributeCollection.createAlignForImage();
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALIGN, attr);

		// the rest.
		String[] names = {HTML40Namespace.ATTR_NAME_NAME, HTML40Namespace.ATTR_NAME_VALUE, HTML40Namespace.ATTR_NAME_CHECKED, HTML40Namespace.ATTR_NAME_DISABLED, HTML40Namespace.ATTR_NAME_READONLY, HTML40Namespace.ATTR_NAME_SIZE, HTML40Namespace.ATTR_NAME_MAXLENGTH, HTML40Namespace.ATTR_NAME_SRC, HTML40Namespace.ATTR_NAME_ALT, HTML40Namespace.ATTR_NAME_USEMAP, HTML40Namespace.ATTR_NAME_ISMAP, HTML40Namespace.ATTR_NAME_TABINDEX, HTML40Namespace.ATTR_NAME_ACCESSKEY, HTML40Namespace.ATTR_NAME_ONFOCUS, HTML40Namespace.ATTR_NAME_ONBLUR, HTML40Namespace.ATTR_NAME_ONSELECT, HTML40Namespace.ATTR_NAME_ONCHANGE, HTML40Namespace.ATTR_NAME_ACCEPT, HTML40Namespace.ATTR_NAME_ALIGN, HTML40Namespace.ATTR_NAME_ISTYLE,
		//<<D215684
					HTML40Namespace.ATTR_NAME_WIDTH, HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_BORDER
		//<D215684
		};
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
