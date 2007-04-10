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



final class HedBGSOUND extends HedEmpty {

	public HedBGSOUND(ElementCollection collection) {
		super(HTML40Namespace.ElementName.BGSOUND, collection);
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * (src, CDATA, #IMPLIED)
	 * (loop, CDATA, #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return;
		if (attributeCollection == null)
			return; // fatal
		attributes = new CMNamedNodeMapImpl();
		// src
		HTMLAttributeDeclaration attr = attributeCollection.getDeclaration(HTML40Namespace.ATTR_NAME_SRC);
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_SRC, attr);
		// loop
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, HTML40Namespace.ATTR_VALUE_INFINITE);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_LOOP, atype, CMAttributeDeclaration.OPTIONAL);
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_LOOP, attr);
	}
}
