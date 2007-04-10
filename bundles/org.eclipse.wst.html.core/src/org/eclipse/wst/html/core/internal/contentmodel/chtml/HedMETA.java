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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import java.util.Arrays;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 * META.
 */
final class HedMETA extends HedEmpty {

	/**
	 */
	public HedMETA(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.META, collection);
		layoutType = LAYOUT_HIDDEN;
	}

	/**
	 * META.
	 * %i18n;
	 * (http-equiv NAME #IMPLIED)
	 * (name NAME #IMPLIED) ... should be defined locally.
	 * (content CDATA #REQUIRED)
	 * (scheme CDATA #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %i18n;
		attributeCollection.getI18n(attributes);

		// (name NAME #IMPLIED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.NAME);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(CHTMLNamespace.ATTR_NAME_NAME, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_NAME, attr);

		// 249493
		atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		attr = new HTMLAttrDeclImpl(CHTMLNamespace.ATTR_NAME_CONTENT, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_CONTENT, attr);

		String[] names = {CHTMLNamespace.ATTR_NAME_HTTP_EQUIV,
		//		CHTMLNamespace.ATTR_NAME_CONTENT
		};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
