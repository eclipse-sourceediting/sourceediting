/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 * COMMAND.
 */
final class HedCOMMAND extends HTMLElemDeclImpl {

	/**
	 */
	public HedCOMMAND(ElementCollection collection) {
		super(HTML50Namespace.ElementName.COMMAND, collection);
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * COMMAND.
	 * %attrs;
	 * // (type %CommandTYPE; command | checkbox|radio) 
	 * // (label %CDATA; #REQUIRED) 
	 * // (icon %URI; #OPTIONAL) 
	 * // (disabled %BOOLEAN; #OPTIONAL) 
	 * // (checked %BOOLEAN; #OPTIONAL) 
	 * // (radiogroup %TEXT; #OPTIONAL) 
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
		// (type %CommandTYPE; command | checkbox|radio) 
		// NOTE: %InputType is ENUM;
		atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] values = {HTML50Namespace.ATTR_VALUE_COMMAND, HTML40Namespace.ATTR_VALUE_CHECKBOX, HTML40Namespace.ATTR_VALUE_RADIO};
		atype.setEnumValues(values);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, HTML50Namespace.ATTR_VALUE_COMMAND);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_TYPE, attr);
		
		// (label %CDATA; #REQUIRED) 
		atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_LABEL, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_LABEL, attr);

		// (icon %URI; #OPTIONAL) 
		atype = new HTMLCMDataTypeImpl(CMDataType.URI);
		attr = new HTMLAttrDeclImpl(HTML50Namespace.ATTR_NAME_ICON, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML50Namespace.ATTR_NAME_ICON, attr);

		
		// (disabled %BOOLEAN; #OPTIONAL) 
		atype = new HTMLCMDataTypeImpl(HTMLCMDataType.BOOLEAN);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, HTML40Namespace.ATTR_VALUE_FALSE);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_DISABLED, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_DISABLED, attr);

		// (checked %BOOLEAN; #OPTIONAL) 
		atype = new HTMLCMDataTypeImpl(HTMLCMDataType.BOOLEAN);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, HTML40Namespace.ATTR_VALUE_FALSE);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_CHECKED, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_CHECKED, attr);

		// (radiogroup %TEXT; #OPTIONAL) 
		atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
		attr = new HTMLAttrDeclImpl(HTML50Namespace.ATTR_NAME_RADIOGROUP, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML50Namespace.ATTR_NAME_RADIOGROUP, attr);

	}

}
