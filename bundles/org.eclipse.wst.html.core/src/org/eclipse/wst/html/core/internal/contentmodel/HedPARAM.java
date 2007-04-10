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
 * PARAM.
 */
final class HedPARAM extends HedEmpty {

	/**
	 */
	public HedPARAM(ElementCollection collection) {
		super(HTML40Namespace.ElementName.PARAM, collection);
		layoutType = LAYOUT_HIDDEN;
	}

	/**
	 * PARAM.
	 * (id ID #IMPLIED)
	 * (name CDATA #REQUIRED) ... should be defined locally.
	 * (value CDATA #IMPLIED)
	 * (valuetype (DATA|REF|OBJECT) DATA)
	 * (type %ContentType; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		String[] names = {HTML40Namespace.ATTR_NAME_ID, HTML40Namespace.ATTR_NAME_VALUE, HTML40Namespace.ATTR_NAME_VALUETYPE, HTML40Namespace.ATTR_NAME_TYPE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// (name CDATA #REQUIRED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_NAME, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_NAME, attr);
	}
}
