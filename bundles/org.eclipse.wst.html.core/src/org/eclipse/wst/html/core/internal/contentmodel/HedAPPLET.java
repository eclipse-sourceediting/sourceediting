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


/**
 * APPLET.
 */
final class HedAPPLET extends HTMLElemDeclImpl {

	/**
	 */
	public HedAPPLET(ElementCollection collection) {
		super(HTML40Namespace.ElementName.APPLET, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_PARAM_CONTAINER;
		layoutType = LAYOUT_OBJECT;
		indentChild = true;
	}

	/**
	 * %coreattrs;
	 * (codebase %URI; #IMPLIED)
	 * (archive CDATA #IMPLIED)
	 * (code CDATA #IMPLIED)
	 * (object CDATA #IMPLIED)
	 * (alt %Text; #IMPLIED) ... should be defined locally.
	 * (name CDATA #IMPLIED)
	 * (width %Length; #REQUIRED)
	 * (height %Length; #REQUIRED)
	 * (align %IAlign; #IMPLIED)
	 * (hspace %Pixels; #IMPLIED)
	 * (vspace %Pixels; #IMPLIED)
	 * (mayscript (mayscript) #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %coreattrs;
		attributeCollection.getCore(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_CODEBASE, HTML40Namespace.ATTR_NAME_ARCHIVE, HTML40Namespace.ATTR_NAME_CODE, HTML40Namespace.ATTR_NAME_OBJECT, HTML40Namespace.ATTR_NAME_ALT, HTML40Namespace.ATTR_NAME_NAME, HTML40Namespace.ATTR_NAME_WIDTH, HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_HSPACE, HTML40Namespace.ATTR_NAME_VSPACE, HTML40Namespace.ATTR_NAME_MAYSCRIPT};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// %align; ... should be defined locally.
		HTMLAttrDeclImpl attr = AttributeCollection.createAlignForImage();
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALIGN, attr);
	}
}
