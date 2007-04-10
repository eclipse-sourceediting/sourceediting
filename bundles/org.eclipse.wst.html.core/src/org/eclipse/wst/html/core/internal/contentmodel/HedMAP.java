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

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;



/**
 * MAP.
 */
final class HedMAP extends HTMLElemDeclImpl {

	/**
	 */
	public HedMAP(ElementCollection collection) {
		super(HTML40Namespace.ElementName.MAP, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_MAP;
		layoutType = LAYOUT_OBJECT;
		indentChild = true;
	}

	/**
	 * %attrs;
	 * (name CDATA #REQUIRED) ... should be defined locally
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		// (name CDATA #REQUIRED) ... should be defined locally
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_NAME, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_NAME, attr);
	}
}
