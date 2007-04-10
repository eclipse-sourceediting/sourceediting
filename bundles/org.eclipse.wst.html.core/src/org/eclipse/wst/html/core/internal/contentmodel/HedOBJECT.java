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
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * OBJECT.
 */
final class HedOBJECT extends HTMLElemDeclImpl {

	/**
	 */
	public HedOBJECT(ElementCollection collection) {
		super(HTML40Namespace.ElementName.OBJECT, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_PARAM_CONTAINER;
		layoutType = LAYOUT_OBJECT;
		indentChild = true;
	}

	/**
	 * %attrs;
	 * %reserved; ... empty.
	 * (declare (declare) #IMPLIED)
	 * (classid %URI; #IMPLIED)
	 * (codebase %URI; #IMPLIED)
	 * (data %URI; #IMPLIED)
	 * (type %ContentType; #IMPLIED)
	 * (codetype %ContentType; #IMPLIED)
	 * (archive CDATA #IMPLIED)
	 * (standby %Text; #IMPLIED)
	 * (height %Length; #IMPLIED)
	 * (width %Length; #IMPLIED)
	 * (usemap %URI; #IMPLIED)
	 * (name CDATA #IMPLIED)
	 * (tabindex NUMBER #IMPLIED)
	 * (align %IAlign; #IMPLIED) ... should be defined locally.
	 * (border %Pixels; #IMPLIED)
	 * (hspace %Pixels; #IMPLIED)
	 * (vspace %Pixels; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		// %reserved; ... empty.

		String[] names = {HTML40Namespace.ATTR_NAME_DECLARE, HTML40Namespace.ATTR_NAME_CLASSID, HTML40Namespace.ATTR_NAME_CODEBASE, HTML40Namespace.ATTR_NAME_DATA, HTML40Namespace.ATTR_NAME_TYPE, HTML40Namespace.ATTR_NAME_CODETYPE, HTML40Namespace.ATTR_NAME_ARCHIVE, HTML40Namespace.ATTR_NAME_STANDBY, HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_WIDTH, HTML40Namespace.ATTR_NAME_USEMAP, HTML40Namespace.ATTR_NAME_NAME, HTML40Namespace.ATTR_NAME_TABINDEX, HTML40Namespace.ATTR_NAME_BORDER, HTML40Namespace.ATTR_NAME_HSPACE, HTML40Namespace.ATTR_NAME_VSPACE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
		// %align; ... should be defined locally.
		HTMLAttrDeclImpl attr = AttributeCollection.createAlignForImage();
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALIGN, attr);
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.PRE};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
