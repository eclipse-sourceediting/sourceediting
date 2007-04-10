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



import java.util.Iterator;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLAttributeDeclaration;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Factory for attribute declarations.
 */
final class AttributeCollection extends CMNamedNodeMapImpl implements CHTMLNamespace {
	/**
	 * constructor.
	 */
	public AttributeCollection() {
		super();
	}

	/**
	 * Create an attribute declaration.
	 * @param attrName java.lang.String
	 */
	private HTMLAttrDeclImpl create(String attrName) {
		HTMLAttrDeclImpl attr = null;
		HTMLCMDataTypeImpl atype = null;

		if (attrName.equalsIgnoreCase(ATTR_NAME_ACTION)) {
			// (action %URI #REQUIRED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ACTION, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ALT)) {
			// (alt %Text; #REQUIRED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ALT, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_BORDER)) {
			// (border %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BORDER, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CHECKED)) {
			// (checked (checked) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_CHECKED};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CHECKED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CLEAR)) {
			// (clear (left | all | right | none) none)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_LEFT, ATTR_VALUE_ALL, ATTR_VALUE_RIGHT, ATTR_VALUE_NONE};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_NONE);

			attr = new HTMLAttrDeclImpl(ATTR_NAME_CLEAR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_COLS)) {
			// (cols NUMBER #REQUIRED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COLS, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ENCTYPE)) {
			// (enctype %ContentType; "application/x-www-form-urlencoded")
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_WWW_FORM_URLENCODED);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ENCTYPE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HEIGHT)) {
			// (height %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HEIGHT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HREF)) {
			// (href %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HREF, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HSPACE)) {
			// (hspace %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HSPACE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HTTP_EQUIV)) {
			// (http-equiv NAME #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.NAME);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HTTP_EQUIV, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MAXLENGTH)) {
			// (maxlength NUMBER #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MAXLENGTH, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_METHOD)) {
			// (method (GET|POST) GET)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_GET, ATTR_VALUE_POST};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_GET);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_METHOD, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MULTIPLE)) {
			// (multiple (multiple) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_MULTIPLE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MULTIPLE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_NAME)) {
			// (name CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_NAME, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_NOSHADE)) {
			// (noshade (noshade) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_NOSHADE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_NOSHADE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ROWS)) {
			// (rows NUMBER #REQUIRED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ROWS, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SELECTED)) {
			// (selected (selected) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_SELECTED};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SELECTED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SIZE)) {
			// (size %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SRC)) {
			// (src %URI; #IMPLIED)
			// NOTE: "src" attributes are defined in several elements.
			//		 The definition of IMG is different from others.
			//		 So, it should be locally defined.
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SRC, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TYPE)) {
			// (type %CotentType; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VALUE)) {
			// (value CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VALUE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VERSION)) {
			// (version CDATA #FIXED '%HTML.Version;)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_FIXED, ATTR_VALUE_VERSION_TRANSITIONAL);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VERSION, atype, CMAttributeDeclaration.FIXED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_WIDTH)) {
			// (width %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_WIDTH, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VSPACE)) {
			// (vspace %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VSPACE, atype, CMAttributeDeclaration.OPTIONAL);

			// <<D205514
		}
		else {
			// unknown attribute; maybe error.
			// should warn.
			attr = null;
		}

		return attr;
	}

	/**
	 * Get %bodycolors; declarations.
	 */
	public void getBodycolors(CMNamedNodeMapImpl declarations) {
	}

	/**
	 * Get align attribute which has %IAlign; as values..
	 */
	public static final HTMLAttrDeclImpl createAlignForImage() {
		// align (local)
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		// set enum values
		String[] values = {ATTR_VALUE_TOP, ATTR_VALUE_MIDDLE, ATTR_VALUE_BOTTOM, ATTR_VALUE_LEFT, ATTR_VALUE_RIGHT};
		atype.setEnumValues(values);

		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		return attr;
	}

	/**
	 * Create an attribute declaration for <code>align</code>
	 * in several elements, like <code>P</code>, <code>DIV</code>.
	 * The values are different from attributes those have the same name
	 * in other elements (<code>IMG</code> and <code>TABLE</code>).
	 * So, it can't treat as global attributes.
	 * <strong>NOTE: These attribute declaration has
	 * no owner CMDocument instance.</strong>
	 */
	public static final HTMLAttrDeclImpl createAlignForParagraph() {
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		// set enum values: left|center|right|justify
		String[] values = {ATTR_VALUE_LEFT, ATTR_VALUE_CENTER, ATTR_VALUE_RIGHT,};
		atype.setEnumValues(values);

		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		return attr;
	}

	/**
	 * Get %attrs; declarations.
	 * %attrs; consists of %coreattrs;, %i18n, and %events;.
	 */
	public void getAttrs(CMNamedNodeMapImpl declarations) {
	}

	/**
	 * Get %coreattrs; declarations.
	 */
	public void getCore(CMNamedNodeMapImpl declarations) {
	}

	/**
	 * Get a global attribute declaration.
	 * @param attrName java.lang.String
	 */
	public HTMLAttributeDeclaration getDeclaration(String attrName) {
		CMNode cmnode = getNamedItem(attrName);
		if (cmnode != null)
			return (HTMLAttributeDeclaration) cmnode; // already exists.

		HTMLAttrDeclImpl dec = create(attrName);
		if (dec != null)
			putNamedItem(attrName, dec);

		return dec;
	}

	/**
	 * Get declarations which are specified by names.
	 * @param names java.util.Iterator
	 */
	public void getDeclarations(CMNamedNodeMapImpl declarations, Iterator names) {
		while (names.hasNext()) {
			String attrName = (String) names.next();
			HTMLAttributeDeclaration dec = getDeclaration(attrName);
			if (dec != null)
				declarations.putNamedItem(attrName, dec);
		}
	}

	/**
	 * Get %events; declarations.
	 */
	public void getEvents(CMNamedNodeMapImpl declarations) {
	}

	/**
	 * Get %i18n; declarations.
	 */
	public void getI18n(CMNamedNodeMapImpl declarations) {
	}
}
