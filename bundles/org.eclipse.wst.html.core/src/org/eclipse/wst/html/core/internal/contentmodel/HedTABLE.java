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
 * TABLE.
 */
final class HedTABLE extends HTMLElemDeclImpl {

	/**
	 */
	public HedTABLE(ElementCollection collection) {
		super(HTML40Namespace.ElementName.TABLE, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_TABLE;
		layoutType = LAYOUT_BLOCK;
		indentChild = true;
	}

	/**
	 * TABLE.
	 * %attrs;
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
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		// %reserved;
		// ... %reserved; is empty in the current DTD.

		String[] names = {HTML40Namespace.ATTR_NAME_SUMMARY, HTML40Namespace.ATTR_NAME_WIDTH, HTML40Namespace.ATTR_NAME_BORDER, HTML40Namespace.ATTR_NAME_FRAME, HTML40Namespace.ATTR_NAME_RULES, HTML40Namespace.ATTR_NAME_CELLSPACING, HTML40Namespace.ATTR_NAME_CELLPADDING, HTML40Namespace.ATTR_NAME_BGCOLOR, HTML40Namespace.ATTR_NAME_DATAPAGESIZE, HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_BACKGROUND, HTML40Namespace.ATTR_NAME_BORDERCOLOR // D205514
		};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// align (local)
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.ENUM);
		String[] alignValues = {HTML40Namespace.ATTR_VALUE_LEFT, HTML40Namespace.ATTR_VALUE_CENTER, HTML40Namespace.ATTR_VALUE_RIGHT};
		atype.setEnumValues(alignValues);
		HTMLAttrDeclImpl adec = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_ALIGN, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALIGN, adec);
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.DIR, HTML40Namespace.ElementName.MENU};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
