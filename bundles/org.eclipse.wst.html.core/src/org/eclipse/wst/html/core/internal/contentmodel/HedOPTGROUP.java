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
 * OPTGROUP.
 */
final class HedOPTGROUP extends HTMLElemDeclImpl {

	/**
	 */
	public HedOPTGROUP(ElementCollection collection) {
		super(HTML40Namespace.ElementName.OPTGROUP, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_OPTION_CONTAINER;
		layoutType = LAYOUT_HIDDEN;
		indentChild = true;
	}

	/**
	 * %attrs;
	 * (disabled (disabled) #IMPLIED)
	 * (label %Text; #REQUIRED) ... should be defined locally.
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		// (disabled (disabled) #IMPLIED)
		String[] names = {HTML40Namespace.ATTR_NAME_DISABLED};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// (label %Text; #REQUIRED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.TEXT);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_LABEL, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_LABEL, attr);
	}
}
