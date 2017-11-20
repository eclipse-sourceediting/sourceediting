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
 * INPUT.
 */
final class HedINPUT extends HedEmpty {

	/**
	 */
	public HedINPUT(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.INPUT, collection);
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
		String[] values = {CHTMLNamespace.ATTR_VALUE_TEXT, CHTMLNamespace.ATTR_VALUE_PASSWORD, CHTMLNamespace.ATTR_VALUE_CHECKBOX, CHTMLNamespace.ATTR_VALUE_RADIO, CHTMLNamespace.ATTR_VALUE_SUBMIT, CHTMLNamespace.ATTR_VALUE_RESET, CHTMLNamespace.ATTR_VALUE_HIDDEN,};
		atype.setEnumValues(values);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, CHTMLNamespace.ATTR_VALUE_TEXT);
		attr = new HTMLAttrDeclImpl(CHTMLNamespace.ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_TYPE, attr);

		// (size CDATA #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		attr = new HTMLAttrDeclImpl(CHTMLNamespace.ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_SIZE, attr);

		// (alt CDATA #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		attr = new HTMLAttrDeclImpl(CHTMLNamespace.ATTR_NAME_ALT, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_ALT, attr);

		// (align %IAlign; #IMPLIED) ... should be defined locally.
		attr = AttributeCollection.createAlignForImage();
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_ALIGN, attr);

		// the rest.
		String[] names = {CHTMLNamespace.ATTR_NAME_NAME, CHTMLNamespace.ATTR_NAME_VALUE, CHTMLNamespace.ATTR_NAME_CHECKED, CHTMLNamespace.ATTR_NAME_SIZE, CHTMLNamespace.ATTR_NAME_MAXLENGTH, CHTMLNamespace.ATTR_NAME_SRC, CHTMLNamespace.ATTR_NAME_ALT, CHTMLNamespace.ATTR_NAME_ALIGN, CHTMLNamespace.ATTR_NAME_ISTYLE,
		//<<D215684
					CHTMLNamespace.ATTR_NAME_WIDTH, CHTMLNamespace.ATTR_NAME_HEIGHT, CHTMLNamespace.ATTR_NAME_BORDER
		//<D215684
		};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
