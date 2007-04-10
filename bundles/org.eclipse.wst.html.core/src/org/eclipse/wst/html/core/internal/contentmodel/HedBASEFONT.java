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
 * BASEFONT.
 */
final class HedBASEFONT extends HedEmpty {

	/**
	 */
	public HedBASEFONT(ElementCollection collection) {
		super(HTML40Namespace.ElementName.BASEFONT, collection);
		// LAYOUT_OBJECT - GROUP_NOWRAP.
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * BASEFONT.
	 * (id ID #IMPLIED)
	 * (size CDATA #REQUIRED) ... should be localy defined.
	 * (color %Color; #IMPLIED)
	 * (face CDATA #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		String[] names = {HTML40Namespace.ATTR_NAME_ID, HTML40Namespace.ATTR_NAME_COLOR, HTML40Namespace.ATTR_NAME_FACE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// (size CDATA #REQUIRED) ... should be localy defined.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_SIZE, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_SIZE, attr);
	}
}
