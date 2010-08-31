/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
import java.util.Iterator;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Factory for attribute declarations.
 */
class AttributeCollection extends CMNamedNodeMapImpl implements HTML40Namespace {

	/** bodycolors. */
	private static final String[] BODYCOLORS = {ATTR_NAME_BGCOLOR, ATTR_NAME_TEXT, ATTR_NAME_LINK, ATTR_NAME_VLINK, ATTR_NAME_ALINK};
	/** coreattrs. */
	private static final String[] CORE = {ATTR_NAME_ID, ATTR_NAME_CLASS, ATTR_NAME_STYLE, ATTR_NAME_TITLE};
	/** events. */
	private static final String[] EVENTS = {ATTR_NAME_ONCLICK, ATTR_NAME_ONDBLCLICK, ATTR_NAME_ONMOUSEDOWN, ATTR_NAME_ONMOUSEUP, ATTR_NAME_ONMOUSEOVER, ATTR_NAME_ONMOUSEMOVE, ATTR_NAME_ONMOUSEOUT, ATTR_NAME_ONKEYPRESS, ATTR_NAME_ONKEYDOWN, ATTR_NAME_ONKEYUP, ATTR_NAME_ONHELP};
	/** i18n. lang, dir */
	private static final String[] I18N = {ATTR_NAME_LANG, ATTR_NAME_DIR};
	/** cellhaligh. */
	private static final String[] CELLHALIGN = {ATTR_NAME_CHAR, ATTR_NAME_CHAROFF};

	/**
	 * constructor.
	 */
	public AttributeCollection() {
		super();
	}

	/**
	 * Create an attribute declaration.
	 * 
	 * @param attrName
	 *            java.lang.String
	 */
	protected HTMLAttrDeclImpl create(String attrName) {
		HTMLAttrDeclImpl attr = null;
		HTMLCMDataTypeImpl atype = null;

		if (attrName.equalsIgnoreCase(ATTR_NAME_ABBR)) {
			// (abbr %Text; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ABBR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ACCEPT)) {
			// (accept %ContentTypes; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ACCEPT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ACCEPT_CHARSET)) {
			// (accept-charset %Charsets;; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CHARSETS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ACCEPT_CHARSET, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ACTION)) {
			// (action %URI #REQUIRED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ACTION, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ARCHIVE)) {
			// (archive CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ARCHIVE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ACCESSKEY)) {
			// (accesskey %Character; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CHARACTER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ACCESSKEY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ALINK)) {
			// (alink %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ALINK, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ALT)) {
			// (alt %Text; #REQUIRED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ALT, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_AUTOSTART)) {
			// (autostart (true|false) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_AUTOSTART, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_AUTOPLAY)) {
			// (autoplay (true|false) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_AUTOPLAY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_AUTOSIZE)) {
			// (autosize (true|false) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_AUTOSIZE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_AXIS)) {
			// (axis CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_AXIS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_BACKGROUND)) {
			// (background %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BACKGROUND, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_BEHAVIOR)) {
			// (behavior (scroll|slide|alternate) scroll)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_SCROLL, ATTR_VALUE_SLIDE, ATTR_VALUE_ALTERNATE};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_SCROLL);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BEHAVIOR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_BGCOLOR)) {
			// (bgcolor %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BGCOLOR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_BORDER)) {
			// (border %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BORDER, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CELLSPACING)) {
			// (cellspacing %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CELLSPACING, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CELLPADDING)) {
			// (cellpadding %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CELLPADDING, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CGI)) {
			// (cgi %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CGI, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CHAR)) {
			// (char %Character; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CHARACTER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CHAR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CHAROFF)) {
			// (charoff %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CHAROFF, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CHARSET)) {
			// (charset %Charset; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CHARSET);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CHARSET, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CITE)) {
			// (cite %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CITE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CLASS)) {
			// (class CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CLASS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CLASSID)) {
			// (classid %URI #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CLASSID, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CMD)) {
			// (cmd CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CMD, atype, CMAttributeDeclaration.OPTIONAL);

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
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CODE)) {
			// (code CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CODE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CODEBASE)) {
			// (codebase %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CODEBASE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CODETYPE)) {
			// (codetype %CotentType; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CODETYPE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_COLOR)) {
			// (color %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COLOR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_COMPACT)) {
			// (compact (compact) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_COMPACT};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COMPACT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_COLS)) {
			// (cols NUMBER #REQUIRED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COLS, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_COLSPAN)) {
			// (colspan NUMBER 1)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_1);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COLSPAN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CONTENT)) {
			// (content CDATA #REQUIRED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CONTENT, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CONTROLLER)) {
			// (controller (true|false) true)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_TRUE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CONTROLLER, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_COORDS)) {
			// (coords %Coords; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COORDS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COORDS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DATA)) {
			// (data %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DATA, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DATETIME)) {
			// (datetime %Datetime; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.DATETIME);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DATETIME, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DATAPAGESIZE)) {
			// (datapagesize CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DATAPAGESIZE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DECLARE)) {
			// (declare (declare) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_DECLARE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DECLARE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DEFER)) {
			// (defer (defer) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_DEFER};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DEFER, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DIR)) {
			// (dir (ltr|rtl) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_LTR, ATTR_VALUE_RTL};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DIR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DIRECTION)) {
			// (direction (left|right|up|down) left)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_LEFT, ATTR_VALUE_RIGHT, ATTR_VALUE_UP, ATTR_VALUE_DOWN};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DIRECTION, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DIRECTKEY)) {
			// (directkey %Character; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CHARACTER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DIRECTKEY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DISABLED)) {
			// (disabled (disabled) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			// boolean attribute must have the same value as its name.
			String[] values = {ATTR_NAME_DISABLED};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DISABLED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ENCTYPE)) {
			// (enctype %ContentType; "application/x-www-form-urlencoded")
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_WWW_FORM_URLENCODED);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ENCTYPE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ERRMSG)) {
			// (errmsg CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ERRMSG, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_EVENT)) {
			// (event CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_EVENT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_FACE)) {
			// (face CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FACE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_FILE)) {
			// (file %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FILE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_FOR)) {
			// (for %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FOR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_FRAME)) {
			// (frame %TFrame; #IMPLIED)
			// %TFrame; is
			// (void|above|below|hsides|lhs|rhs|vsides|box|border).
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_VOID, ATTR_VALUE_ABOVE, ATTR_VALUE_BELOW, ATTR_VALUE_HSIDES, ATTR_VALUE_LHS, ATTR_VALUE_RHS, ATTR_VALUE_VSIDES, ATTR_VALUE_BOX, ATTR_VALUE_BORDER};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FRAME, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_FRAMEBORDER)) {
			// (frameborder (1|0) 1)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_1, ATTR_VALUE_0};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_1);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FRAMEBORDER, atype, CMAttributeDeclaration.OPTIONAL);

			// <<D215684
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_FRAMESPACING)) {
			// (framespacing CDATA; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FRAMESPACING, atype, CMAttributeDeclaration.OPTIONAL);
			// D215684
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HEADERS)) {
			// (HEADERS IDREFS; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.IDREFS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HEADERS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HEIGHT)) {
			// (height %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HEIGHT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HIDDEN)) {
			// (hidden CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HIDDEN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HREF)) {
			// (href %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HREF, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HREFLANG)) {
			// (hreflang %LanguageCode; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LANGUAGE_CODE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HREFLANG, atype, CMAttributeDeclaration.OPTIONAL);

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
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ID)) {
			// (id ID #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ID);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ID, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ISMAP)) {
			// (ismap (ismap) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_ISMAP};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ISMAP, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ISTYLE)) {
			// (istyle CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ISTYLE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_LABEL)) {
			// (label %Text; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LABEL, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_LANG)) {
			// (lang %LanguageCode; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LANGUAGE_CODE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LANG, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_LANGUAGE)) {
			// (language %CDATA; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LANGUAGE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_LINK)) {
			// (link %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LINK, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_LONGDESC)) {
			// (longdesc %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LONGDESC, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_LOOP)) {
			// (loop CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LOOP, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MACRO)) {
			// (macro CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MACRO, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MAPFILE)) {
			// (mapfile %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MAPFILE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MARGINWIDTH)) {
			// (marginwidth %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MARGINWIDTH, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MARGINHEIGHT)) {
			// (marginheight %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MARGINHEIGHT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MAXLENGTH)) {
			// (maxlength NUMBER #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MAXLENGTH, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MAYSCRIPT)) {
			// (mayscript (mayscript) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_MAYSCRIPT};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MAYSCRIPT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MEDIA)) {
			// (media %MediaDesc; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.MEDIA_DESC);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MEDIA, atype, CMAttributeDeclaration.OPTIONAL);

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
		else if (attrName.equalsIgnoreCase(ATTR_NAME_NOHREF)) {
			// (nohref (nohref) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_NOHREF};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_NOHREF, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_NORESIZE)) {
			// (noresize (noresize) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_NORESIZE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_NORESIZE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_NOSHADE)) {
			// (noshade (noshade) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_NOSHADE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_NOSHADE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_NOWRAP)) {
			// (nowrap (nowrap) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_NOWRAP};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_NOWRAP, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_OBJECT)) {
			// (object CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_OBJECT};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_OBJECT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONBLUR)) {
			// (onblur %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONBLUR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONCLICK)) {
			// (onclick %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONCLICK, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONCHANGE)) {
			// (onchange %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONCHANGE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDBLCLICK)) {
			// (ondblclick %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDBLCLICK, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONFOCUS)) {
			// (onfocus %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONFOCUS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONHELP)) {
			// (onhelp %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONHELP, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONKEYPRESS)) {
			// (onkeypress %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONKEYPRESS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONKEYDOWN)) {
			// (onkeydown %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONKEYDOWN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONKEYUP)) {
			// (onkyeup %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONKEYUP, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONLOAD)) {
			// (onload %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONLOAD, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONMOUSEDOWN)) {
			// (onmousedown %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONMOUSEDOWN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONMOUSEUP)) {
			// (onmouseup %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONMOUSEUP, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONMOUSEOVER)) {
			// (onmouseover %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONMOUSEOVER, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONMOUSEMOVE)) {
			// (onmousemove %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONMOUSEMOVE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONMOUSEOUT)) {
			// (onmouseout %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONMOUSEOUT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONRESET)) {
			// (onreset %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONRESET, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSELECT)) {
			// (onselect %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSELECT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSUBMIT)) {
			// (onsubmit %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSUBMIT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONUNLOAD)) {
			// (onunload %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONUNLOAD, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_PALETTE)) {
			// (palette CDATA; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_PALETTE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_PANEL)) {
			// (panel CDATA; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_PANEL, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_PLAYCOUNT)) {
			// (playcount NUMBER; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_PLAYCOUNT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_PROFILE)) {
			// (profile %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_PROFILE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_PROMPT)) {
			// (prompt %Text; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_PROMPT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_READONLY)) {
			// (readonly (readonly) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_READONLY};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_READONLY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_REPEAT)) {
			// (repeat CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_REPEAT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_REL)) {
			// (rel %LinkTypes; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LINK_TYPES);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_REL, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_REV)) {
			// (rev %LinkTypes; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LINK_TYPES);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_REV, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ROWS)) {
			// (rows NUMBER #REQUIRED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ROWS, atype, CMAttributeDeclaration.REQUIRED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ROWSPAN)) {
			// (rowspan NUMBER 1)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_1);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ROWSPAN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_RULES)) {
			// (rules %TRules; #IMPLIED)
			// %TRules; is (none | groups | rows | cols | all).
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_NONE, ATTR_VALUE_GROUPS, ATTR_VALUE_ROWS, ATTR_VALUE_COLS, ATTR_VALUE_ALL};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_RULES, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SCALE)) {
			// (scale CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCALE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SCHEME)) {
			// (scheme CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCHEME, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SELECTED)) {
			// (selected (selected) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_SELECTED};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SELECTED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SCOPE)) {
			// (SCOPE %Scope; #IMPLIED)
			// %Scope; is (row|col|rowgroup|colgroup)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_ROW, ATTR_VALUE_COL, ATTR_VALUE_ROWGROUP, ATTR_VALUE_COLGROUP};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCOPE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SCROLLAMOUNT)) {
			// (scrollamount NUMBER #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCROLLAMOUNT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SCROLLDELAY)) {
			// (scrolldelay NUMBER #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCROLLDELAY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SCROLLING)) {
			// (scrolling (yes|no|auto) auto)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_YES, ATTR_VALUE_NO, ATTR_VALUE_AUTO};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_AUTO);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCROLLING, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SHAPE)) {
			// (shape %Shape; rect): %Shape; is (rect|circle|poly|default).
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_RECT, ATTR_VALUE_CIRCLE, ATTR_VALUE_POLY, ATTR_VALUE_DEFAULT};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_RECT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SHAPE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SHOWCONTROLS)) {
			// (showcontrols (true|false) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SHOWCONTROLS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SIZE)) {
			// (size %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SIZEFMT)) {
			// (sizefmt CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SIZEFMT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SPAN)) {
			// (span NUMBER 1)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_1);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SPAN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SRC)) {
			// (src %URI; #IMPLIED)
			// NOTE: "src" attributes are defined in several elements.
			// The definition of IMG is different from others.
			// So, it should be locally defined.
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SRC, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_START)) {
			// (start NUMBER #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_START, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_STANDBY)) {
			// (standby %Text; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_STANDBY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_STYLE)) {
			// (style %StyleSheet; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.STYLE_SHEET);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_STYLE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SUMMARY)) {
			// (summary %Text; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SUMMARY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TABINDEX)) {
			// (tabindex NUMBER #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TABINDEX, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TARGET)) {
			// (target %FrameTarget; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.FRAME_TARGET);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TARGET, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TEXT)) {
			// (text %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TEXT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TEXTFOCUS)) {
			// (textfocus CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TEXTFOCUS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TITLE)) {
			// (title %Text; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TITLE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TIMEFMT)) {
			// (timefmt CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TIMEFMT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TRUESPEED)) {
			// (truespeed (truespeed) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_TRUESPEED};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TRUESPEED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TYPE)) {
			// (type %CotentType; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_USEMAP)) {
			// (usemap %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_USEMAP, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VALIGN)) {
			// (valign (top|middle|bottom|baseline) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TOP, ATTR_VALUE_MIDDLE, ATTR_VALUE_BOTTOM, ATTR_VALUE_BASELINE};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VALIGN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VALUE)) {
			// (value CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VALUE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VALUETYPE)) {
			// (valuetype (DATA|REF|OBJECT) DATA)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_DATA, ATTR_VALUE_REF, ATTR_VALUE_OBJECT};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_DATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VALUETYPE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VAR)) {
			// (var CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VAR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VERSION)) {
			// (version CDATA #FIXED '%HTML.Version;)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_FIXED, ATTR_VALUE_VERSION_TRANSITIONAL);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VERSION, atype, CMAttributeDeclaration.FIXED);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VIRTUAL)) {
			// (virtual %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VIRTUAL, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VLINK)) {
			// (vlink %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VLINK, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_VOLUME)) {
			// (volume CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VOLUME, atype, CMAttributeDeclaration.OPTIONAL);

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
		else if (attrName.equalsIgnoreCase(ATTR_NAME_TOPMARGIN)) {
			// (topmargin, CDATA, #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TOPMARGIN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_BOTTOMMARGIN)) {
			// (bottommargin, CDATA, #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BOTTOMMARGIN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_LEFTMARGIN)) {
			// (leftmargin, CDATA, #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LEFTMARGIN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_RIGHTMARGIN)) {
			// (rightmargin, CDATA, #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_RIGHTMARGIN, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_BORDERCOLOR)) {
			// (bordercolor, %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BORDERCOLOR, atype, CMAttributeDeclaration.OPTIONAL);
			// D205514

		}
		else {
			// unknown attribute; maybe error.
			// should warn.
			attr = null;
		}

		return attr;
	}

	/**
	 * Get align attribute which has %CAlign; as values.. At this time
	 * (4/19/2001), it is identical to %LAlign;.
	 * 
	 */
	public static final HTMLAttrDeclImpl createAlignForCaption() {
		// align (local)
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		// set enum values
		String[] values = {ATTR_VALUE_TOP, ATTR_VALUE_BOTTOM, ATTR_VALUE_LEFT, ATTR_VALUE_RIGHT};
		atype.setEnumValues(values);

		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		return attr;
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
	 * Get align attribute which has %LAlign; as values..
	 */
	public static final HTMLAttrDeclImpl createAlignForLegend() {
		// align (local)
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		// set enum values
		String[] values = {ATTR_VALUE_TOP, ATTR_VALUE_BOTTOM, ATTR_VALUE_LEFT, ATTR_VALUE_RIGHT};
		atype.setEnumValues(values);

		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		return attr;
	}

	/**
	 * Create an attribute declaration for <code>align</code> in several
	 * elements, like <code>P</code>, <code>DIV</code>. The values are
	 * different from attributes those have the same name in other elements (<code>IMG</code>
	 * and <code>TABLE</code>). So, it can't treat as global attributes.
	 * <strong>NOTE: These attribute declaration has no owner CMDocument
	 * instance.</strong>
	 */
	public static final HTMLAttrDeclImpl createAlignForParagraph() {
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		// set enum values: left|center|right|justify
		String[] values = {ATTR_VALUE_LEFT, ATTR_VALUE_CENTER, ATTR_VALUE_RIGHT, ATTR_VALUE_JUSTIFY};
		atype.setEnumValues(values);

		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		return attr;
	}

	/**
	 * Get %attrs; declarations. %attrs; consists of %coreattrs;, %i18n, and
	 * %events;.
	 */
	public void getAttrs(CMNamedNodeMapImpl declarations) {
		// %coreattrs;
		Iterator names = Arrays.asList(CORE).iterator();
		getDeclarations(declarations, names);
		// %i18n;
		names = Arrays.asList(I18N).iterator();
		getDeclarations(declarations, names);
		// %events;
		names = Arrays.asList(EVENTS).iterator();
		getDeclarations(declarations, names);
	}

	/**
	 * Get %bodycolors; declarations.
	 */
	public void getBodycolors(CMNamedNodeMapImpl declarations) {
		Iterator names = Arrays.asList(BODYCOLORS).iterator();
		getDeclarations(declarations, names);
	}

	/**
	 * Get %cellhalign; declarations. %cellhaligh; consists of: - (align
	 * (left|center|right|justify|char) #IMPLIED) - (char %Character;
	 * #IMPLIED) - (charoff %Length; #IMPLIED)
	 */
	public void getCellhalign(CMNamedNodeMapImpl declarations) {
		// (align (left|center|right|justify|char) #IMPLIED) should be defined
		// locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		// set enum values
		String[] values = {ATTR_VALUE_LEFT, ATTR_VALUE_CENTER, ATTR_VALUE_RIGHT, ATTR_VALUE_JUSTIFY, ATTR_VALUE_CHAR};
		atype.setEnumValues(values);

		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		declarations.putNamedItem(ATTR_NAME_ALIGN, attr);

		// the rest.
		Iterator names = Arrays.asList(CELLHALIGN).iterator();
		getDeclarations(declarations, names);
	}

	/**
	 * Get %cellvalign; declarations. %cellhaligh; is: - (valign
	 * (top|middle|bottom|baseline) #IMPLIED)
	 */
	public void getCellvalign(CMNamedNodeMapImpl declarations) {
		HTMLAttributeDeclaration dec = getDeclaration(ATTR_NAME_VALIGN);
		if (dec != null)
			declarations.putNamedItem(ATTR_NAME_VALIGN, dec);
	}

	/**
	 * Get %coreattrs; declarations.
	 */
	public void getCore(CMNamedNodeMapImpl declarations) {
		Iterator names = Arrays.asList(CORE).iterator();
		getDeclarations(declarations, names);
	}

	/**
	 * Get a global attribute declaration.
	 * 
	 * @param attrName
	 *            java.lang.String
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
	 * 
	 * @param names
	 *            java.util.Iterator
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
		Iterator names = Arrays.asList(EVENTS).iterator();
		getDeclarations(declarations, names);
	}

	/**
	 * Get %i18n; declarations.
	 */
	public void getI18n(CMNamedNodeMapImpl declarations) {
		Iterator names = Arrays.asList(I18N).iterator();
		getDeclarations(declarations, names);
	}
	
	/**
	 * create declarations.
	 */
	public void createAttributeDeclarations(String elementName, CMNamedNodeMapImpl attributes) {
		 /* (type %InputType; TEXT) ... should be defined locally.
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
		if (elementName.equals(HTML40Namespace.ElementName.INPUT)){
			HTMLCMDataTypeImpl atype = null;
			HTMLAttrDeclImpl attr = null;
			// (type %InputType; TEXT) ... should be defined locally.
			// NOTE: %InputType is ENUM;
			// (text | password | checkbox | radio | submit | reset |
			//  file | hidden | image | button)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TEXT, ATTR_VALUE_PASSWORD, ATTR_VALUE_CHECKBOX, ATTR_VALUE_RADIO, ATTR_VALUE_SUBMIT, ATTR_VALUE_RESET, ATTR_VALUE_FILE, ATTR_VALUE_HIDDEN, ATTR_VALUE_IMAGE, ATTR_VALUE_BUTTON};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);

			// (size CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_SIZE, attr);

			// (alt CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ALT, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_ALT, attr);

			// (align %IAlign; #IMPLIED) ... should be defined locally.
			attr = AttributeCollection.createAlignForImage();
			attributes.putNamedItem(ATTR_NAME_ALIGN, attr);

			// the rest.
			String[] names = {ATTR_NAME_NAME, ATTR_NAME_VALUE, ATTR_NAME_CHECKED, ATTR_NAME_DISABLED, ATTR_NAME_READONLY, ATTR_NAME_SIZE, ATTR_NAME_MAXLENGTH, ATTR_NAME_SRC, ATTR_NAME_ALT, ATTR_NAME_USEMAP, ATTR_NAME_ISMAP, ATTR_NAME_TABINDEX, ATTR_NAME_ACCESSKEY, ATTR_NAME_ONFOCUS, ATTR_NAME_ONBLUR, ATTR_NAME_ONSELECT, ATTR_NAME_ONCHANGE, ATTR_NAME_ACCEPT, ATTR_NAME_ALIGN, ATTR_NAME_ISTYLE,
			//<<D215684
						ATTR_NAME_WIDTH, ATTR_NAME_HEIGHT, ATTR_NAME_BORDER
			//<D215684
			};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		}
		 /* (charset %Charset; #IMPLIED)
		 * (href %URI; #IMPLIED)
		 * (hreflang %LanguageCode; #IMPLIED)
		 * (type %ContentType; #IMPLIED): should be defined locally.
		 * (rel %LinkTypes; #IMPLIED)
		 * (rev %LinkTypes; #IMPLIED)
		 * (media %MediaDesc; #IMPLIED)
		 * (target %FrameTarget; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.LINK)){
			String[] names = {ATTR_NAME_CHARSET, ATTR_NAME_HREF, ATTR_NAME_HREFLANG, ATTR_NAME_REL, ATTR_NAME_REV, ATTR_NAME_MEDIA, ATTR_NAME_TARGET};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// (type %ContentType; #IMPLIED)
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);
		
		}
		/* (charset %Charset; #IMPLIED)
		 * (type %ContentType; #IMPLIED)
		 * (name CDATA #IMPLIED)
		 * (href %URI; #IMPLIED)
		 * (hreflang %LanguageCode; #IMPLIED)
		 * (target %FrameTarget; #IMPLIED)
		 * (rel %LinkTypes; #IMPLIED)
		 * (rev %LinkTypes; #IMPLIED)
		 * (accesskey %Character; #IMPLIED)
		 * (directkey %Character; #IMPLIED)
		 * (shape %Shape; rect)
		 * (coords %Coords; #IMPLIED)
		 * (tabindex NUMBER #IMPLIED)
		 * (onfocus %Script; #IMPLIED)
		 * (onblur %Script; #IMPLIED) 
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.A)){
			String[] names = {ATTR_NAME_CHARSET, ATTR_NAME_TYPE, ATTR_NAME_NAME, ATTR_NAME_HREF, ATTR_NAME_HREFLANG, ATTR_NAME_TARGET, ATTR_NAME_REL, ATTR_NAME_REV, ATTR_NAME_ACCESSKEY, ATTR_NAME_DIRECTKEY, ATTR_NAME_SHAPE, ATTR_NAME_COORDS, ATTR_NAME_TABINDEX, ATTR_NAME_ONFOCUS, ATTR_NAME_ONBLUR};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		
		}
		/*
		 * (shape %Shape; rect)
		 * (coords %Coords; #IMPLIED)
		 * (href %URI; #IMPLIED)
		 * (target %FrameTarget; #IMPLIED)
		 * (nohref (nohref) #IMPLIED)
		 * (alt %Text; #REQUIRED)
		 * (tabindex NUMBER #IMPLIED)
		 * (accesskey %Character; #IMPLIED)
		 * (onfocus %Script; #IMPLIED)
		 * (onblur %Script; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.AREA)){
			String[] names = {ATTR_NAME_SHAPE, ATTR_NAME_COORDS, ATTR_NAME_HREF, ATTR_NAME_TARGET, ATTR_NAME_NOHREF, ATTR_NAME_ALT, ATTR_NAME_TABINDEX, ATTR_NAME_ACCESSKEY, ATTR_NAME_ONFOCUS, ATTR_NAME_ONBLUR};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		}
		/*
		 *  %i18n;
		 * (http-equiv NAME #IMPLIED)
		 * (name NAME #IMPLIED) ... should be defined locally.
		 * (content CDATA #REQUIRED)
		 * (scheme CDATA #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.META)){
			// %i18n;
			getI18n(attributes);

			// (name NAME #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.NAME);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_NAME, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_NAME, attr);

			String[] names = {ATTR_NAME_HTTP_EQUIV, ATTR_NAME_CONTENT, ATTR_NAME_SCHEME};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		}
		/*
		 * (src %URI; #REQUIRED): should be defined locally.
		 * (alt %Text; #REQUIRED)
		 * (longdesc %URI; #IMPLIED)
		 * (name CDATA #IMPLIED)
		 * (height %Length; #IMPLIED)
		 * (width %Length; #IMPLIED)
		 * (usemap %URI; #IMPLIED)
		 * (ismap (ismap) #IMPLIED)
		 * (align %IAlign; #IMPLIED): should be defined locally.
		 * (border %Pixels; #IMPLIED)
		 * (hspace %Pixels; #IMPLIED)
		 * (vspace %Pixels; #IMPLIED)
		 * (mapfile %URI; #IMPLIED)
	 
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.IMG)){
			// (src %URI; #REQUIRED): should be defined locally.
			HTMLCMDataTypeImpl atype = null;
			HTMLAttrDeclImpl attr = null;
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SRC, atype, CMAttributeDeclaration.REQUIRED);
			attributes.putNamedItem(ATTR_NAME_SRC, attr);

			String[] names = {ATTR_NAME_ALT, ATTR_NAME_LONGDESC, ATTR_NAME_NAME, ATTR_NAME_HEIGHT, ATTR_NAME_WIDTH, ATTR_NAME_USEMAP, ATTR_NAME_ISMAP, ATTR_NAME_BORDER, ATTR_NAME_HSPACE, ATTR_NAME_VSPACE, ATTR_NAME_MAPFILE};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// align (local); should be defined locally.
			attr = AttributeCollection.createAlignForImage();
			attributes.putNamedItem(ATTR_NAME_ALIGN, attr);
		
		}
		/*
		 * (id ID #IMPLIED)
		 * (name CDATA #REQUIRED) ... should be defined locally.
		 * (value CDATA #IMPLIED)
		 * (valuetype (DATA|REF|OBJECT) DATA)
		 * (type %ContentType; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.PARAM)){
			String[] names = {ATTR_NAME_ID, ATTR_NAME_VALUE, ATTR_NAME_VALUETYPE, ATTR_NAME_TYPE};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// (name CDATA #REQUIRED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_NAME, atype, CMAttributeDeclaration.REQUIRED);
			attributes.putNamedItem(ATTR_NAME_NAME, attr);
		}
		/*
		 * %reserved; ... empty
		 * (name CDATA #IMPLIED)
		 * (rows NUMBER #REQUIRED)
		 * (cols NUMBER #REQUIRED)
		 * (disabled (disabled) #IMPLIED)
		 * (readonly (readonly) #IMPLIED)
		 * (tabindex NUMBER #IMPLIED)
		 * (accesskey %Character; #IMPLIED)
		 * (onfocus %Script; #IMPLIED)
		 * (onblur %Script; #IMPLIED)
		 * (onselect %Script; #IMPLIED)
		 * (onchange %Script; #IMPLIED)
		 * (istyle CDATA #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.TEXTAREA)){
			String[] names = {ATTR_NAME_NAME, ATTR_NAME_ROWS, ATTR_NAME_COLS, ATTR_NAME_DISABLED, ATTR_NAME_READONLY, ATTR_NAME_TABINDEX, ATTR_NAME_ACCESSKEY, ATTR_NAME_ONFOCUS, ATTR_NAME_ONBLUR, ATTR_NAME_ONSELECT, ATTR_NAME_ONCHANGE, ATTR_NAME_ISTYLE};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		}
		/*
		 * (charset %Charset; #IMPLIED)
		 * (type %ContentType; #REQUIRED) ... should be defined locally.
		 * (language CDATA #IMPLIED)
		 * (src %URI; #IMPLIED)
		 * (defer (defer) #IMPLIED)
		 * (event CDATA #IMPLIED)
		 * (for %URI; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.SCRIPT)){
			String[] names = {ATTR_NAME_CHARSET, ATTR_NAME_LANGUAGE, ATTR_NAME_SRC, ATTR_NAME_DEFER, ATTR_NAME_EVENT, ATTR_NAME_FOR};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// (type %ContentType; #REQUIRED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, "text/javascript"); //$NON-NLS-1$
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.REQUIRED);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);
		
		}
		/*
		 *  %i18n;
		 * (type %ContentType; #REQUIRED) ... should be defined locally.
		 * (media %MediaDesc; #IMPLIED)
		 * (title %Text; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.STYLE)){
			// %i18n;
			getI18n(attributes);

			String[] names = {ATTR_NAME_MEDIA, ATTR_NAME_TITLE};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			// (type %ContentType; #REQUIRED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, "text/css"); //$NON-NLS-1$
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.REQUIRED);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);
		
		}
		/*
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
		else if (elementName.equals(HTML40Namespace.ElementName.SELECT)){
			// (size NUMBER #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_SIZE, attr);

			String[] names = {ATTR_NAME_NAME, ATTR_NAME_MULTIPLE, ATTR_NAME_DISABLED, ATTR_NAME_TABINDEX, ATTR_NAME_ONFOCUS, ATTR_NAME_ONBLUR, ATTR_NAME_ONCHANGE};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		
		}
		/*
		 * 	(type %LIStyle; #IMPLIED) ... should be defined locally.
		 * (value NUMBER #IMPLIED) ... should be defined locally.
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.LI)){
			// (type %LIStyle; #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LI_STYLE);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);

			// (value NUMBER #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VALUE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_VALUE, attr);
		
		}
		/*
		 * (type %OLStyle; #IMPLIED) ... should be defined locally.
		 * (compact (compact) #IMPLIED)
		 * (start NUMBER #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.OL)){
			// (type %OLStyle; #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.OL_STYLE);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);

			// the rest.
			String[] names = {ATTR_NAME_COMPACT, ATTR_NAME_START};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		
		}
		/**
		 * %coreattrs;
		 * (longdesc %URI; #IMPLIED)
		 * (name CDATA #IMPLIED)
		 * (src %URI; #IMPLIED)
		 * (frameborder (1|0) 1)
		 * (marginwidth %Pixels; #IMPLIED)
		 * (marginheight %Pixels; #IMPLIED)
		 * (scrolling (yes|no|auto) auto)
		 * (align %IAlign; #IMPLIED) ... should be defined locally.
		 * (height %Length; #IMPLIED)
		 * (width %Length; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.IFRAME)){
			// %coreattrs;
			getCore(attributes);

			String[] names = {ATTR_NAME_LONGDESC, ATTR_NAME_NAME, ATTR_NAME_SRC, ATTR_NAME_FRAMEBORDER, ATTR_NAME_MARGINWIDTH, ATTR_NAME_MARGINHEIGHT, ATTR_NAME_SCROLLING, ATTR_NAME_HEIGHT, ATTR_NAME_WIDTH};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// align
			HTMLAttrDeclImpl attr = AttributeCollection.createAlignForImage();
			if (attr != null)
				attributes.putNamedItem(ATTR_NAME_ALIGN, attr);
		
		}
		/*
		 * %i18n attrs
		 * %version
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.HTML)){
			// %i18n;
			getI18n(attributes);
			// version
			HTMLAttributeDeclaration adec = getDeclaration(ATTR_NAME_VERSION);
			if (adec != null)
				attributes.putNamedItem(ATTR_NAME_VERSION, adec);
		
		}
		/*
		 * (compact (compact) #IMPLIED)
	 	 */
		else if (elementName.equals(HTML40Namespace.ElementName.MENU)){
			String[] names = {ATTR_NAME_COMPACT};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		
		}
		/*
		 * %reserved; ... empty.
		 * (name CDATA #IMPLIED)
		 * (value CDATA #IMPLIED)
		 * (type (button|submit|reset) submit) ... should be defined locally.
		 * (disabled (disabled) #IMPLIED)
		 * (tabindex NUMBER #IMPLIED)
		 * (accesskey %Character; #IMPLIED)
		 * (onfocus %Script; #IMPLIED)
		 * (onblur %Script; #IMPLIED)
	 	 */
		else if (elementName.equals(HTML40Namespace.ElementName.BUTTON)){
			String[] names = {ATTR_NAME_NAME, ATTR_NAME_VALUE, ATTR_NAME_DISABLED, ATTR_NAME_TABINDEX, ATTR_NAME_ACCESSKEY, ATTR_NAME_ONFOCUS, ATTR_NAME_ONBLUR};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// (type (button|submit|reset) submit) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_BUTTON, ATTR_VALUE_SUBMIT, ATTR_VALUE_RESET};
			atype.setEnumValues(values);

			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);
		
		}
		/*
		 * %reserved;
		 * (summary %Text; #IMPLIED)
		 * (width %Length; #IMPLIED)
		 * (border %Pixels; #IMPLIED)
		 * (frame %TFrame; #IMPLIED)
		 * (rules %TRules; #IMPLIED)
		 * (cellspacing %Length; #IMPLIED)
		 * (cellpadding %Length; #IMPLIED)
		 * (align %TAlign; #IMPLIED)
		 * (bgcolor %Color; #IMPLIED)
		 * (datapagesize CDATA #IMPLIED)
		 * (height %Pixels; #IMPLIED)
		 * (background %URI; #IMPLIED)
		 * (bordercolor %Color #IMPLIED) ... D205514
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.TABLE)){
			// %reserved;
			// ... %reserved; is empty in the current DTD.

			String[] names = {ATTR_NAME_SUMMARY, ATTR_NAME_WIDTH, ATTR_NAME_BORDER, ATTR_NAME_FRAME, ATTR_NAME_RULES, ATTR_NAME_CELLSPACING, ATTR_NAME_CELLPADDING, ATTR_NAME_BGCOLOR, ATTR_NAME_DATAPAGESIZE, ATTR_NAME_HEIGHT, ATTR_NAME_BACKGROUND, ATTR_NAME_BORDERCOLOR // D205514
			};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// align (local)
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] alignValues = {ATTR_VALUE_LEFT, ATTR_VALUE_CENTER, ATTR_VALUE_RIGHT};
			atype.setEnumValues(alignValues);
			HTMLAttrDeclImpl adec = new HTMLAttrDeclImpl(ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_ALIGN, adec);
			
		}
	}
}
