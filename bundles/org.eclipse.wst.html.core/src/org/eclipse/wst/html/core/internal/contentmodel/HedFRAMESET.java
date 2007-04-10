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

/**
 * FRAMESET.
 */
final class HedFRAMESET extends HTMLElemDeclImpl {

	/**
	 */
	public HedFRAMESET(ElementCollection collection) {
		super(HTML40Namespace.ElementName.FRAMESET, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_FRAMESET;
		layoutType = LAYOUT_HIDDEN;
		indentChild = true;
	}

	/**
	 * %coreattrs;
	 * (rows %MultiLengths; #IMPLIED) ... should be defined locally.
	 * (cols %MultiLengths; #IMPLIED) ... should be defined locally.
	 * (onload %Script; #IMPLIED)
	 * (onunload %Script; #IMPLIED)
	 * (frameborder (yes|no) #IMPLIED) ... should be defined locally.
	 * (border %Pixels; #IMPLIED)
	 * (bordercolor %Color #IMPLIED) ... D205514
	 * (framespacing CDATA #IMPLIED) ... D215684
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getCore(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_ONLOAD, HTML40Namespace.ATTR_NAME_ONUNLOAD, HTML40Namespace.ATTR_NAME_BORDER, HTML40Namespace.ATTR_NAME_BORDERCOLOR, // D205514
					HTML40Namespace.ATTR_NAME_FRAMESPACING // D215684
		};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		HTMLCMDataTypeImpl atype = null;
		HTMLAttrDeclImpl attr = null;
		// (rows %MultiLengths; #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(HTMLCMDataType.MULTI_LENGTH);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_ROWS, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ROWS, attr);

		// (cols %MultiLengths; #IMPLIED) ... should be defined locally.
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_COLS, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_COLS, attr);

		// (frameborder (yes|no) #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] values = {HTML40Namespace.ATTR_VALUE_YES, HTML40Namespace.ATTR_VALUE_NO};
		atype.setEnumValues(values);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_FRAMEBORDER, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_FRAMEBORDER, attr);
	}
}
