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

/**
 * COL.
 */
final class HedCOL extends HedEmpty {

	/**
	 */
	public HedCOL(ElementCollection collection) {
		super(HTML40Namespace.ElementName.COL, collection);
		// LAYOUT_BLOCK.
		// Because, COL is GROUP_BLOCK in the C++DOM/DTDParser.cpp.
		layoutType = LAYOUT_BLOCK;
	}

	/**
	 * COL.
	 * %attrs;
	 * (span NUMBER 1)
	 * (width %MultiLength; #IMPLIED) ... should be defined locally.
	 * %cellhalign;
	 * %cellvalign;
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		// (span NUMBER 1)
		String[] names = {HTML40Namespace.ATTR_NAME_SPAN};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// (width %MultiLength; #IMPLIED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.MULTI_LENGTH);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_WIDTH, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_WIDTH, attr);

		// %cellhalign;
		attributeCollection.getCellhalign(attributes);
		// %cellvalign;
		attributeCollection.getCellvalign(attributes);
	}
}
