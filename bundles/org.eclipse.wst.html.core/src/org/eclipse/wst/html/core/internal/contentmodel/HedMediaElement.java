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



import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 * EMBED.
 */
class HedMediaElement extends HTMLElemDeclImpl {

	/**
	 */
	public HedMediaElement(String elementName, ElementCollection collection) {
		super(elementName, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_MEDIA_ELEMENT;
		layoutType = LAYOUT_OBJECT;
		indentChild = true;
	}

	/**
	 * MediaElement
	 * %attrs;
	 * (src %URI; #REQUIRED): should be defined locally.
	 * (preload %CDATA; #IMPLIED) 
	 * (autoplay %ENUM; #IMPLIED) 
	 * (loop %ENUM; #IMPLIED)
	 * (controls %MediaType; #IMPLIED)
	 * Global attributes
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

	
		// (src %URI; #REQUIRED): should be defined locally.
		HTMLCMDataTypeImpl atype = null;
		HTMLAttrDeclImpl attr = null;
		atype = new HTMLCMDataTypeImpl(CMDataType.URI);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_SRC, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_SRC, attr);

		//(preload %CDATA; #IMPLIED) ENUM
		// (none | metadata | auto)
		atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] values = {HTML40Namespace.ATTR_VALUE_NONE, HTML50Namespace.ATTR_VALUE_METADATA, HTML40Namespace.ATTR_VALUE_AUTO, HTML50Namespace.ATTR_VALUE_EMPTY};
		atype.setEnumValues(values);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, HTML40Namespace.ATTR_VALUE_AUTO);
		attr = new HTMLAttrDeclImpl(HTML50Namespace.ATTR_NAME_PRELOAD, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML50Namespace.ATTR_NAME_PRELOAD, attr);

		// (autoplay (boolean) #IMPLIED)
		atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] autoPlayValues = {HTML50Namespace.ATTR_NAME_AUTOPLAY};
		atype.setEnumValues(autoPlayValues);
		attr = new HTMLAttrDeclImpl(HTML50Namespace.ATTR_NAME_AUTOPLAY, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML50Namespace.ATTR_NAME_AUTOPLAY, attr);

		// (loop (boolean) #IMPLIED)
		atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] loopValues = {HTML50Namespace.ATTR_NAME_LOOP};
		atype.setEnumValues(loopValues);
		attr = new HTMLAttrDeclImpl(HTML50Namespace.ATTR_NAME_LOOP, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML50Namespace.ATTR_NAME_LOOP, attr);

		// (controls (boolean) #IMPLIED)
		atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] controlValues = {HTML50Namespace.ATTR_NAME_CONTROLS};
		atype.setEnumValues(controlValues);
		attr = new HTMLAttrDeclImpl(HTML50Namespace.ATTR_NAME_CONTROLS, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML50Namespace.ATTR_NAME_CONTROLS, attr);

		
		// global attributes
		attributeCollection.getAttrs(attributes);
	}
}

