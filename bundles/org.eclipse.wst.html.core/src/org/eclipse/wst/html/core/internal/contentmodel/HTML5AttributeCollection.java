/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
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
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

public class HTML5AttributeCollection extends AttributeCollection implements HTML50Namespace {
	/** html5 core attribs */
	private static final String[] CORE = {ATTR_NAME_ACCESSKEY, ATTR_NAME_CLASS, ATTR_NAME_CONTENT_EDITABLE, ATTR_NAME_CONTEXT_MENU, ATTR_NAME_DIR, ATTR_NAME_DRAGGABLE, ATTR_NAME_HIDDEN, ATTR_NAME_ID, ATTR_NAME_LANG, ATTR_NAME_SPELLCHECK,ATTR_NAME_STYLE, ATTR_NAME_TABINDEX, ATTR_NAME_TITLE};
	/** events for HTML5. */
	private static final String[] EVENTS = {ATTR_NAME_ONABORT, ATTR_NAME_ONBLUR, ATTR_NAME_ONCAN_PLAY, ATTR_NAME_ONCAN_PLAY_THROUGH, ATTR_NAME_ONCHANGE, ATTR_NAME_ONCLICK, ATTR_NAME_ONCONTEXT_MENU, ATTR_NAME_ONDBLCLICK, ATTR_NAME_ONDRAG, ATTR_NAME_ONDRAG_END, ATTR_NAME_ONDRAG_ENTER, ATTR_NAME_ONDRAG_LEAVE, 
		ATTR_NAME_ONDRAG_OVER, ATTR_NAME_ONDRAG_START, ATTR_NAME_ONDROP, ATTR_NAME_ONDURATION_CHANGE, ATTR_NAME_ONEMPTIED, ATTR_NAME_ONENDED, ATTR_NAME_ONERROR, ATTR_NAME_ONFOCUS, ATTR_NAME_ONFORM_CHANGE, ATTR_NAME_ONFORM_INPUT, ATTR_NAME_ONINVALID,ATTR_NAME_ONKEYPRESS, ATTR_NAME_ONKEYDOWN, ATTR_NAME_ONKEYUP, 
		ATTR_NAME_ONLOAD, ATTR_NAME_ONLOAD_START, ATTR_NAME_ONLOADED_DATA, ATTR_NAME_ONLOADED_METADATA, ATTR_NAME_ONMOUSEDOWN, ATTR_NAME_ONMOUSEUP, ATTR_NAME_ONMOUSEOVER, ATTR_NAME_ONMOUSEMOVE, ATTR_NAME_ONMOUSEOUT, ATTR_NAME_ONMOUSE_WHEEL, ATTR_NAME_ONPAUSE, ATTR_NAME_ONPLAY, ATTR_NAME_ONPLAYING, ATTR_NAME_ONPROGRESS,
		ATTR_NAME_ONRATE_CHANGE, ATTR_NAME_ONREADY_STATE_CHANGE, ATTR_NAME_ONSCROLL, ATTR_NAME_ONSEEKED, ATTR_NAME_ONSEEKING, ATTR_NAME_ONSELECT, ATTR_NAME_ONSHOW, ATTR_NAME_ONSTALLED, ATTR_NAME_ONSUBMIT, ATTR_NAME_ONSUSPEND, ATTR_NAME_ONTIME_UPDATE, ATTR_NAME_ONVOLUME_UPDATE, ATTR_NAME_ONWAITING};

	protected HTMLAttrDeclImpl create(String attrName) {
		HTMLAttrDeclImpl attr = null;
		HTMLCMDataTypeImpl atype = null;
		if (attrName.equalsIgnoreCase(ATTR_NAME_AUTOFOCUS)) {
			// (disabled (disabled) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			// boolean attribute must have the same value as its name.
			String[] values = {ATTR_NAME_AUTOFOCUS};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_AUTOFOCUS, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CONTENT_EDITABLE)) {
			// (contenteditable (EMPTY|TRUE|FALSE|INHERIT) TRUE)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_EMPTY, ATTR_VALUE_TRUE, ATTR_VALUE_FALSE, ATTR_VALUE_INHERIT};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_TRUE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CONTENT_EDITABLE, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CHALLENGE)) {
			// (challenge CDATA; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CHALLENGE, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_CONTEXT_MENU)) {
			// (contextmenu, CDATA, IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CONTEXT_MENU, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_DRAGGABLE)) {
			// (draggable (TRUE|FALSE|AUTO) TRUE)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TRUE, ATTR_VALUE_FALSE, ATTR_VALUE_AUTO};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_FALSE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DRAGGABLE, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_FORM)) {
			// (form CDATA; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORM, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_KEYTYPE)) {
			// (keytype CDATA; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_KEYTYPE, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_LOW)) {
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LOW, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_HIGH)) {
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HIGH, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_OPTIMUM)) {
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_OPTIMUM, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MIN)) {
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MIN, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_MAX)) {
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MAX, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_OPEN)) {
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			atype.setEnumValues(new String[] { ATTR_NAME_OPEN });
			attr = new HTMLAttrDeclImpl(ATTR_NAME_OPEN, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_PUBDATE)) {
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			atype.setEnumValues(new String[] { ATTR_NAME_PUBDATE });
			attr = new HTMLAttrDeclImpl(ATTR_NAME_PUBDATE, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_SPELLCHECK)) {
			// (spellcheck (EMPTY|TRUE|FALSE) TRUE)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_EMPTY, ATTR_VALUE_TRUE, ATTR_VALUE_FALSE};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_FALSE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SPELLCHECK, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONABORT)) {
			// (onabort %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONABORT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONCAN_PLAY)) {
			// (oncanplay %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONCAN_PLAY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONCAN_PLAY_THROUGH)) {
			// (oncanplaythrough %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONCAN_PLAY_THROUGH, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONCHANGE)) {
			// (onchange %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONCHANGE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONCONTEXT_MENU)) {
			// (onacontextmenu %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONCONTEXT_MENU, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDRAG)) {
			// (onadrag %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDRAG, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDRAG_END)) {
			// (ondragend %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDRAG_END, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDRAG_ENTER)) {
			// (ondragenter %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDRAG_ENTER, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDRAG_LEAVE)) {
			// (ondragleave %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDRAG_LEAVE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDRAG_OVER)) {
			// (ondragover %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDRAG_OVER, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDRAG_START)) {
			// (ondragstart %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDRAG_START, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDROP)) {
			// (ondrop %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDROP, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONDURATION_CHANGE)) {
			// (ondurationchange %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONDURATION_CHANGE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONEMPTIED)) {
			// (onemptied %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONEMPTIED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONENDED)) {
			// (onended %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONENDED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONERROR)) {
			// (onerror %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONERROR, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONFOCUS)) {
			// (onfocus %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONFOCUS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONFORM_CHANGE)) {
			// (onformchange %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONFORM_CHANGE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONFORM_INPUT)) {
			// (onforminput %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONFORM_INPUT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONINPUT)) {
			// (oninput %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONINPUT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONINVALID)) {
			// (oninvalid %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONINVALID, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONLOAD)) {
			// (onload %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONLOAD, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONLOAD_START)) {
			// (onloadstart %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONLOAD_START, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONLOADED_DATA)) {
			// (onloadeddata %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONLOADED_DATA, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONLOADED_METADATA)) {
			// (onloadedmetadata %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONLOADED_METADATA, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONMOUSE_WHEEL)) {
			// (onmousewheel %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONMOUSE_WHEEL, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONPLAY)) {
			// (onplay %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONPLAY, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONPLAYING)) {
			// (onplaying %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONPLAYING, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONPAUSE)) {
			// (onpause %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONPAUSE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONPROGRESS)) {
			// (onprogress %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONPROGRESS, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONRATE_CHANGE)) {
			// (onratechange %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONRATE_CHANGE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONREADY_STATE_CHANGE)) {
			// (onreadystatechange %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONREADY_STATE_CHANGE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSCROLL)) {
			// (onscroll %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSCROLL, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSEEKED)) {
			// (onseeked %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSEEKED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSEEKING)) {
			// (onseeking %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSEEKING, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSELECT)) {
			// (onselect %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSELECT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSHOW)) {
			// (onshow %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSHOW, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSTALLED)) {
			// (onstalled %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSTALLED, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSUBMIT)) {
			// (onsubmit %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSUBMIT, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONSUSPEND)) {
			// (onsuspend %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONSUSPEND, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONTIME_UPDATE)) {
			// (ontimeupdate %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONTIME_UPDATE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONVOLUME_UPDATE)) {
			// (onvolumeupdate %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONVOLUME_UPDATE, atype, CMAttributeDeclaration.OPTIONAL);

		}
		else if (attrName.equalsIgnoreCase(ATTR_NAME_ONWAITING)) {
			// (onwaiting %Script; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.SCRIPT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ONWAITING, atype, CMAttributeDeclaration.OPTIONAL);
		}
		else {
			attr = super.create(attrName);
		}
		return attr;
	}

	public void getAttrs(CMNamedNodeMapImpl declarations) {
		// %coreattrs;
		getCore(declarations);
		// %events;
		getEvents(declarations);
	}

	public void getCore(CMNamedNodeMapImpl declarations) {
		Iterator names = Arrays.asList(CORE).iterator();
		getDeclarations(declarations, names);
	}

	public void getEvents(CMNamedNodeMapImpl declarations) {
		Iterator names = Arrays.asList(EVENTS).iterator();
		getDeclarations(declarations, names);
	}
	
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
		 * (accept %ContentTypes; #IMPLIED)
		 * (width CDATA; #IMPLIED)
		 * (height CDATA; #IMPLIED)
		 * (autocomplete Boolean; #IMPLIED)
		 * (autofocus Boolean; #IMPLIED)
		 * (form CDATA; #IMPLIED)
		 * (formaction)
		 * (formenctype)
		 * (formmethod)
		 * (formnovalidate)
		 * (formtarget)
		 * (list)
		 * (max)
		 * (min)
		 * (multiple)
		 * (pattern)
		 * (placeholder CDATA #IMPLIED)
		 * (required)
		 * (step)
		 * discouraged tags :- 
		 * (usemap %URI; #IMPLIED)
		 * (ismap (ismap) #IMPLIED)
		 */
		if (elementName.equals(HTML40Namespace.ElementName.INPUT)){
			HTMLCMDataTypeImpl atype = null;
			HTMLAttrDeclImpl attr = null;
			// (type %InputType; TEXT) ... should be defined locally.
			// NOTE: %InputType is ENUM;
			// (text | password | checkbox | radio | submit | reset |
			//  file | hidden | image | button
			//  color| date | time | datetime | datetime-local | month | week| email| 
			//  number | range | search | tel)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_TEXT, ATTR_VALUE_PASSWORD, ATTR_VALUE_CHECKBOX, ATTR_VALUE_RADIO, ATTR_VALUE_SUBMIT, ATTR_VALUE_RESET, ATTR_VALUE_FILE, ATTR_VALUE_HIDDEN, ATTR_VALUE_IMAGE, ATTR_VALUE_BUTTON,
					 ATTR_VALUE_COLOR, ATTR_VALUE_DATE, ATTR_VALUE_DATETIME, ATTR_VALUE_DATETIME_LOCAL, ATTR_VALUE_EMAIL, ATTR_VALUE_MONTH, ATTR_VALUE_NUMBER_STRING, ATTR_VALUE_RANGE, ATTR_VALUE_SEARCH, ATTR_VALUE_TEL, ATTR_VALUE_TIME};
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
			
			
			// (type %autocomeplete; ) ... should be defined locally.
			// NOTE: %autocomeplete is ENUM;
			// (on | off)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] autoCompleteValues = {ATTR_VALUE_ON, ATTR_VALUE_OFF};
			atype.setEnumValues(autoCompleteValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_AUTOCOMPLETE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_AUTOCOMPLETE, attr);

			
			// (form CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORM, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORM, attr);
			
			
			// (formaction URI #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMACTION, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMACTION, attr);

			
			// (type %formmethod; GET) ... should be defined locally.
			// NOTE: %formmethod is ENUM;
			// (GET|POST|PUT|DELETE)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] formMethodValues = {ATTR_VALUE_GET, ATTR_VALUE_POST, ATTR_VALUE_PUT, ATTR_VALUE_DELETE};
			atype.setEnumValues(formMethodValues);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_GET);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMMETHOD, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMMETHOD, attr);

			// (type %formenctype; GET) ... should be defined locally.
			// NOTE: %formenctype is ENUM;
			// (application/x-www-form-urlencoded| multipart/form-data| text/plain)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] formEncTypeValues = {ATTR_VALUE_WWW_FORM_URLENCODED, ATTR_VALUE_FORM_DATA, ATTR_VALUE_PLAIN};
			atype.setEnumValues(formEncTypeValues);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_WWW_FORM_URLENCODED);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMENCTYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMENCTYPE, attr);

			// (formtarget BROWSEING CONTEXT #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.BROWSING_CONTEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMTARGET, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMTARGET, attr);
			
			// (formtnovalidate  #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			// boolean attribute must have the same value as its name.
			String[] formNoValidateValues = {ATTR_NAME_FORMNOVALIDATE};
			atype.setEnumValues(formNoValidateValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMNOVALIDATE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMNOVALIDATE, attr);

		
			// (list ID #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.ID);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LIST, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_LIST, attr);

			// (min CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MIN, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_MIN, attr);

			// (max CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MAX, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_MAX, attr);

			// (maxlength NUMBER #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MAXLENGTH, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_MAXLENGTH, attr);

			// (multiple  #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			// boolean attribute must have the same value as its name.
			String[] multipleValues = {ATTR_NAME_MULTIPLE};
			atype.setEnumValues(multipleValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MULTIPLE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_MULTIPLE, attr);

		
			// (step CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_STEP, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_STEP, attr);

			// (placeholder CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_PLACEHOLDER, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_PLACEHOLDER, attr);

			// (pattern CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_PATTERN, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_PATTERN, attr);

			// (required  #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			// boolean attribute must have the same value as its name.
			String[] requiredValues = {ATTR_NAME_REQUIRED};
			atype.setEnumValues(requiredValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_REQUIRED, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_REQUIRED, attr);

			String[] names = {ATTR_NAME_NAME, ATTR_NAME_VALUE, ATTR_NAME_CHECKED, ATTR_NAME_DISABLED, ATTR_NAME_READONLY, ATTR_NAME_SIZE, ATTR_NAME_MAXLENGTH, ATTR_NAME_SRC, ATTR_NAME_ALT, ATTR_NAME_ACCEPT, //<<D215684
						ATTR_NAME_WIDTH, ATTR_NAME_HEIGHT,			//<D215684
				//html5
						ATTR_NAME_AUTOFOCUS
			};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			//discouraged
			// (ismap (ismap) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] ismapValues = {ATTR_NAME_ISMAP};
			atype.setEnumValues(ismapValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ISMAP, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_ISMAP, attr);
			
			// (usemap %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_USEMAP, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_USEMAP, attr);

			
			getDeclarations(attributes, Arrays.asList(names).iterator());
		}
		/* (href %URI; #IMPLIED)
		 * (hreflang %LanguageCode; #IMPLIED)
		 * (type %ContentType; #IMPLIED): should be defined locally.
		 * (rel %LinkTypes; #IMPLIED)
		 * (media %MediaDesc; #IMPLIED)
		 * // discouraged
		 * (charset %Charset; #IMPLIED)
		 * (rev %LinkTypes; #IMPLIED)
		 * (target %FrameTarget; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.LINK)){
			String[] names = { ATTR_NAME_TYPE, ATTR_NAME_HREF, ATTR_NAME_HREFLANG, ATTR_NAME_REL,  ATTR_NAME_MEDIA};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// (sizes %Pixels; #IMPLIED)
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_SIZES, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_SIZES, attr);
			
			//discouraged
			// (charset %Charset; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CHARSET);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CHARSET, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_CHARSET, attr);
			
			// (rev %LinkTypes; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LINK_TYPES);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_REV, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_REV, attr);
			
			// (target %FrameTarget; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.FRAME_TARGET);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TARGET, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_TARGET, attr);
			
		}
		/* (type %ContentType; #IMPLIED)
		 * (href %URI; #IMPLIED)
		 * (hreflang %LanguageCode; #IMPLIED)
		 * (target %FrameTarget; #IMPLIED)
		 * (rel %LinkTypes; #IMPLIED)
		 * (media %media_desc; #IMPLIED
		 * //discouraged
		 * (charset %Charset; #IMPLIED)
		 * (name CDATA #IMPLIED)
		 * (rev %LinkTypes; #IMPLIED)
		 * (directkey %Character; #IMPLIED)
		 * (shape %Shape; rect)
		 * (coords %Coords; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.A)){
			String[] names = { ATTR_NAME_MEDIA, ATTR_NAME_TYPE, ATTR_NAME_HREF, ATTR_NAME_HREFLANG, ATTR_NAME_REL, ATTR_NAME_TARGET};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			
			
			//discouraged
			// (charset %Charset; #IMPLIED)
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CHARSET);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_CHARSET, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_CHARSET, attr);
			
			// (rev %LinkTypes; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LINK_TYPES);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_REV, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_REV, attr);
			
			// (directkey %Character; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CHARACTER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DIRECTKEY, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_DIRECTKEY, attr);
			
			// (shape %Shape; rect): %Shape; is (rect|circle|poly|default).
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_RECT, ATTR_VALUE_CIRCLE, ATTR_VALUE_POLY, ATTR_VALUE_DEFAULT};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_RECT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SHAPE, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_SHAPE, attr);
			
			// (coords %Coords; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COORDS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COORDS, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_COORDS, attr);
			
			// (name CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_NAME, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_NAME, attr);
	
		}
		/*
		 * (shape %Shape; rect)
		 * (coords %Coords; #IMPLIED)
		 * (href %URI; #IMPLIED)
		 * (target %FrameTarget; #IMPLIED)
		 * (alt %Text; #REQUIRED)
		 * (media %media_desc; #IMPLIED)
		 * (rel %LinkTypes; #IMPLIED)
		 * (type %ContentType; #IMPLIED)
		 * //disocuraged
		 * (nohref (nohref) #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.AREA)){
			// (media %MediaDesc; #IMPLIED)
			String[] names = {ATTR_NAME_TYPE, ATTR_NAME_MEDIA, ATTR_NAME_SHAPE, ATTR_NAME_COORDS, ATTR_NAME_HREF, ATTR_NAME_HREFLANG, ATTR_NAME_TARGET, ATTR_NAME_ALT, ATTR_NAME_REL};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			
			// (nohref (nohref) #IMPLIED)
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_NOHREF};
			atype.setEnumValues(values);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_NOHREF, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_NOHREF, attr);
		
		}
		/*
		 *  %globalattrs;
		 * (http-equiv NAME #IMPLIED)
		 * (name NAME #IMPLIED) ... should be defined locally.
		 * (content CDATA #REQUIRED)
		 * (charset %Charset; #IMPLIED)
		 *  //discouraged
		 * (scheme CDATA #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.META)){
			// globalattrs;
			getAttrs(attributes);

			// (name NAME #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.NAME);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_NAME, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_NAME, attr);

			// (content CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CONTENT, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_CONTENT, attr);
			
			String[] names = {ATTR_NAME_HTTP_EQUIV, ATTR_NAME_CHARSET};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			
			// discouraged
			// (scheme CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCHEME, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_SCHEME, attr);
			
		}
		/*
		 * (src %URI; #REQUIRED): should be defined locally.
		 * (alt %Text; #REQUIRED)
		 * (usemap %URI; #IMPLIED)
		 * (ismap (ismap) #IMPLIED)
		 *  // discouraged
		 * (longdesc %URI; #IMPLIED)
		 * (name CDATA #IMPLIED)
		 * (height %Length; #IMPLIED)
		 * (width %Length; #IMPLIED)
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
		 * (name CDATA #REQUIRED) ... should be defined locally.
		 * (value CDATA #IMPLIED)
		 * global attributes
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.PARAM)){
			
			// (name CDATA #REQUIRED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_NAME, atype, CMAttributeDeclaration.REQUIRED);
			attributes.putNamedItem(ATTR_NAME_NAME, attr);
			
			// (value CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VALUE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_VALUE, attr);
			
			// gloabl attrs
			getAttrs(attributes);
		}
		/*
		 * (autofocus Boolean; #IMPLIED)
		 * (form CDATA; #IMPLIED)
		 * (placeholder CDATA #IMPLIED)
		 * (name CDATA #IMPLIED)
		 * (rows NUMBER #REQUIRED)
		 * (cols NUMBER #REQUIRED)
		 * (disabled (disabled) #IMPLIED)
		 * (readonly (readonly) #IMPLIED)
		 * (maxlength NUMBER; #IMPLIED)
		 * (wrap ENUM; #IMPLIED)
		 *  //discouraged
		 * (istyle CDATA #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.TEXTAREA)){
			String[] names = {ATTR_NAME_MAXLENGTH, ATTR_NAME_FORM, ATTR_NAME_AUTOFOCUS, ATTR_NAME_NAME, ATTR_NAME_ROWS, ATTR_NAME_COLS, ATTR_NAME_DISABLED, ATTR_NAME_READONLY};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			
			// (placeholder CDATA #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_PLACEHOLDER, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_PLACEHOLDER, attr);
			
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_SOFT, ATTR_VALUE_HARD};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_SOFT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_WRAP, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_WRAP, attr);
			
			// discouraged
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ISTYLE, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_ISTYLE, attr);
		}
		/*
		 * (charset %Charset; #IMPLIED)
		 * (type %ContentType; #REQUIRED) ... should be defined locally.
		 * (asynch boolean #IMPLIED)
		 * (src %URI; #IMPLIED)
		 * (defer (defer) #IMPLIED)
		 *  // discouraged
		 * (language CDATA #IMPLIED)
		 * (event CDATA #IMPLIED)
		 * (for %URI; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.SCRIPT)){
			String[] names = {ATTR_NAME_CHARSET,  ATTR_NAME_SRC, ATTR_NAME_DEFER};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// (type %ContentType; #REQUIRED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, "text/javascript"); //$NON-NLS-1$
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.REQUIRED);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);
			
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_ASYNC};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_ASYNC, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_ASYNC, attr);
			
			
			// discouraged
			// (language %CDATA; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LANGUAGE, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_LANGUAGE, attr);
			
			// (event CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_EVENT, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_EVENT, attr);
			
			// (for %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FOR, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_FOR, attr);
		
		}
		/*
		 *  %attrs;
		 * (type %ContentType; #REQUIRED) ... should be defined locally.
		 * (media %MediaDesc; #IMPLIED)
		 * (scoped boolean; #implied)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.STYLE)){
			// %i18n;
			getAttrs(attributes);

			String[] names = {ATTR_NAME_MEDIA};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			// (type %ContentType; #REQUIRED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, "text/css"); //$NON-NLS-1$
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.REQUIRED);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);
			
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_SCOPED};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCOPED, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_SCOPED, attr);
		}
		/*
		 * %reserved;
		 * (name CDATA #IMPLIED)
		 * (size NUMBER #IMPLIED) ... should be defined locally.
		 * (multiple (multiple) #IMPLIED)
		 * (disabled (disabled) #IMPLIED)
		 * (autofocus Boolean; #IMPLIED)
		 * (form CDATA; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.SELECT)){
			// (size NUMBER #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_SIZE, attr);

			String[] names = {ATTR_NAME_FORM, ATTR_NAME_AUTOFOCUS,ATTR_NAME_NAME, ATTR_NAME_MULTIPLE, ATTR_NAME_DISABLED, ATTR_NAME_TABINDEX, ATTR_NAME_ONFOCUS, ATTR_NAME_ONBLUR, ATTR_NAME_ONCHANGE};
			getDeclarations(attributes, Arrays.asList(names).iterator());
		
		}
		/*
		 * (value NUMBER #IMPLIED) ... should be defined locally.
		 *  //discouraged
		 *  (type %LIStyle; #IMPLIED) ... should be defined locally.
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.LI)){
			// (type %LIStyle; #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LI_STYLE);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);

			// (value NUMBER #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VALUE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_VALUE, attr);
		
		}
		/*
		 * (start NUMBER #IMPLIED)
		 * (reversed BOOLEAN; IMPLIED)
		 *   //discouraged
		 * (type %OLStyle; #IMPLIED) ... should be defined locally.
		 * (compact (compact) #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.OL)){

			String[] names = { ATTR_NAME_START};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_NAME_REVERSED};
			atype.setEnumValues(values);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_REVERSED, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_REVERSED, attr);
			
			//discouraged 
			// (type %OLStyle; #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.OL_STYLE);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);
			
			// (compact (compact) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] compactValues = {ATTR_NAME_COMPACT};
			atype.setEnumValues(compactValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COMPACT, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_COMPACT, attr);
		}
		/**
		 * %attrs;
		 * (src %URI; #IMPLIED)
		 * (srcdoc %CONTENT_TYPE; #IMPLIED)
		 * (seamless BOOLEAN; #IMPLIED)
		 * (sandbox CDATA; #IMPLED)
		 * (height %Length; #IMPLIED)
		 * (width %Length; #IMPLIED)
		 * (name CDATA #IMPLIED)
		 * //discouraged
		 * (longdesc %URI; #IMPLIED)
		 * (frameborder (1|0) 1)
		 * (marginwidth %Pixels; #IMPLIED)
		 * (marginheight %Pixels; #IMPLIED)
		 * (scrolling (yes|no|auto) auto)
		 * (align %IAlign; #IMPLIED) ... should be defined locally.
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.IFRAME)){
			// %attrs;
			getAttrs(attributes);
			
			//srcdoc
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_SRCDOC, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_SRCDOC, attr);

			// (seamless (seamless) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] compactValues = {ATTR_NAME_SEAMLESS};
			atype.setEnumValues(compactValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SEAMLESS, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_SEAMLESS, attr);
		
			//sandbox
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SANDBOX, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_SANDBOX, attr);
			
			
			String[] names = { ATTR_NAME_NAME, ATTR_NAME_SRC, ATTR_NAME_HEIGHT, ATTR_NAME_WIDTH};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			//discouraged
			// (marginwidth %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MARGINWIDTH, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_MARGINWIDTH, attr);
			
			// (marginheight %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_MARGINHEIGHT, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_MARGINHEIGHT, attr);
			
			// (scrolling (yes|no|auto) auto)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_YES, ATTR_VALUE_NO, ATTR_VALUE_AUTO};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_AUTO);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_SCROLLING, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_SCROLLING, attr);

			// (frameborder (1|0) 1)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] frameValues = {ATTR_VALUE_1, ATTR_VALUE_0};
			atype.setEnumValues(frameValues);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_1);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FRAMEBORDER, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_FRAMEBORDER, attr);
			
			// (longdesc %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LONGDESC, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_LONGDESC, attr);
			
			
			// align
			attr = AttributeCollection.createAlignForImage();
			if (attr != null)
				attr.obsolete(true);
				attributes.putNamedItem(ATTR_NAME_ALIGN, attr);
		}
		/*
		 * (%attrs)
		 * (manisfest %URI; #IMPLIED)
		 * (xmlns %URI; #IMPLIED)
		 * //discouraged
		 * (version CDATA #FIXED '%HTML.Version;)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.HTML)){
			// %attrs;
			getAttrs(attributes);
			// (manisfest %URI; #IMPLIED)
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_MANIFEST, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_MANIFEST, attr);
			
			// (version CDATA #FIXED '%HTML.Version;)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_FIXED, ATTR_VALUE_VERSION_TRANSITIONAL);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_VERSION, atype, CMAttributeDeclaration.FIXED);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_VERSION, attr);
			
			// (xmlns CDATA #FIXED '%xmlns;)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_XMLNS, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_XMLNS, attr);
		}
		/*
		 * (type enum; (context | toolbar | list))
		 * (label %Text; #IMPLIED)
		 *  //discouraged
		 * (compact (compact) #IMPLIED)
	 	 */
		else if (elementName.equals(HTML40Namespace.ElementName.MENU)){
			// (type %menuType; list) ... should be defined locally is ENUM.
			//  (context | toolbar | list)
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_CONTEXT, ATTR_VALUE_TOOLBAR, ATTR_VALUE_LIST};
			atype.setEnumValues(values);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_LIST);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);

			// (label %Text; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_LABEL, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_LABEL, attr);

			// (compact (compact) #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] compactValues = {ATTR_NAME_COMPACT};
			atype.setEnumValues(compactValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_COMPACT, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_COMPACT, attr);
		}
		/*
		 * (type %button; TEXT) ... should be defined locally.
		 * (name CDATA #IMPLIED)
		 * (value CDATA #IMPLIED)
		 * (disabled (disabled) #IMPLIED)
		 * (autofocus Boolean; #IMPLIED)
		 * (form CDATA; #IMPLIED)
		 * (formaction)
		 * (formenctype)
		 * (formmethod)
		 * (formnovalidate)
		 * (formtarget)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.BUTTON)){
			// (type (button|submit|reset) submit) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_BUTTON, ATTR_VALUE_SUBMIT, ATTR_VALUE_RESET};
			atype.setEnumValues(values);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_TYPE, attr);
			

			String[] names = {ATTR_NAME_NAME, ATTR_NAME_VALUE, ATTR_NAME_DISABLED,
				//html5
						ATTR_NAME_AUTOFOCUS
			};
			getDeclarations(attributes, Arrays.asList(names).iterator());
			
			// (form CDATA #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORM, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORM, attr);
			
			
			// (formaction URI #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMACTION, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMACTION, attr);

			
			// (type %formmethod; GET) ... should be defined locally.
			// NOTE: %formmethod is ENUM;
			// (GET|POST|PUT|DELETE)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] formMethodValues = {ATTR_VALUE_GET, ATTR_VALUE_POST, ATTR_VALUE_PUT, ATTR_VALUE_DELETE};
			atype.setEnumValues(formMethodValues);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_GET);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMMETHOD, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMMETHOD, attr);

			// (type %formenctype; GET) ... should be defined locally.
			// NOTE: %formenctype is ENUM;
			// (application/x-www-form-urlencoded| multipart/form-data| text/plain)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] formEncTypeValues = {ATTR_VALUE_WWW_FORM_URLENCODED, ATTR_VALUE_FORM_DATA, ATTR_VALUE_PLAIN};
			atype.setEnumValues(formEncTypeValues);
			atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, ATTR_VALUE_WWW_FORM_URLENCODED);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMENCTYPE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMENCTYPE, attr);

			// (formtarget BROWSEING CONTEXT #IMPLIED) ... should be defined locally.
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.BROWSING_CONTEXT);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMTARGET, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMTARGET, attr);
			
			// (formtnovalidate  #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			// boolean attribute must have the same value as its name.
			String[] formNoValidateValues = {ATTR_NAME_FORMNOVALIDATE};
			atype.setEnumValues(formNoValidateValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FORMNOVALIDATE, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORMNOVALIDATE, attr);

		}
		/*
		 * (name CDATA #IMPLIED)
		 * (disabled (disabled) #IMPLIED)
		 * (form CDATA; #IMPLIED)
		 */
		else if (elementName.equals(HTML40Namespace.ElementName.FIELDSET)){
			// (form CDATA #IMPLIED) ... should be defined locally.
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_FORM, atype, CMAttributeDeclaration.OPTIONAL);
			attributes.putNamedItem(ATTR_NAME_FORM, attr);
			
			String[] names = {ATTR_NAME_NAME, ATTR_NAME_DISABLED };
			getDeclarations(attributes, Arrays.asList(names).iterator());
				
		}
		/*
		 * (summary %Text; #IMPLIED)
		 *  //discouraged
		 * %reserved;
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

			String[] names = {ATTR_NAME_SUMMARY};
			getDeclarations(attributes, Arrays.asList(names).iterator());

			// align (local)
			HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] alignValues = {ATTR_VALUE_LEFT, ATTR_VALUE_CENTER, ATTR_VALUE_RIGHT};
			atype.setEnumValues(alignValues);
			HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_ALIGN, attr);
			
			// (width %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_WIDTH, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_WIDTH, attr);
			
			// (border %Pixels; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.PIXELS);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BORDER, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_BORDER, attr);
			
			// (frame %TFrame; #IMPLIED)
			// %TFrame; is
			// (void|above|below|hsides|lhs|rhs|vsides|box|border).
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] values = {ATTR_VALUE_VOID, ATTR_VALUE_ABOVE, ATTR_VALUE_BELOW, ATTR_VALUE_HSIDES, ATTR_VALUE_LHS, ATTR_VALUE_RHS, ATTR_VALUE_VSIDES, ATTR_VALUE_BOX, ATTR_VALUE_BORDER};
			atype.setEnumValues(values);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_FRAME, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_FRAME, attr);
			
			// (rules %TRules; #IMPLIED)
			// %TRules; is (none | groups | rows | cols | all).
			atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
			String[] ruleValues = {ATTR_VALUE_NONE, ATTR_VALUE_GROUPS, ATTR_VALUE_ROWS, ATTR_VALUE_COLS, ATTR_VALUE_ALL};
			atype.setEnumValues(ruleValues);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_RULES, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_RULES, attr);
			
			// (cellspacing %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CELLSPACING, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_CELLSPACING, attr);
			
			// (cellpadding %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_CELLPADDING, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_CELLPADDING, attr);
			
			// (bgcolor %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BGCOLOR, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_BGCOLOR, attr);
			
			// (datapagesize CDATA #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_DATAPAGESIZE, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_DATAPAGESIZE, attr);
			
			// (height %Length; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.LENGTH);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_HEIGHT, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_HEIGHT, attr);
			
			// (background %URI; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(CMDataType.URI);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BACKGROUND, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_BACKGROUND, attr);
			
			// (bordercolor, %Color; #IMPLIED)
			atype = new HTMLCMDataTypeImpl(HTMLCMDataType.COLOR);
			attr = new HTMLAttrDeclImpl(ATTR_NAME_BORDERCOLOR, atype, CMAttributeDeclaration.OPTIONAL);
			attr.obsolete(true);
			attributes.putNamedItem(ATTR_NAME_BORDERCOLOR, attr);
			
			
		}
	}
	
	public static String[] getGlobalAttributeList(){
		return CORE;
	}
	
	public static String[] getGlobalEventList(){
		return EVENTS;
	}
}
