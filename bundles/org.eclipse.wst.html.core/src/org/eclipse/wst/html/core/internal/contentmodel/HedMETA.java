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
 * META.
 */
final class HedMETA extends HedEmpty {

	/**
	 */
	public HedMETA(ElementCollection collection) {
		super(HTML40Namespace.ElementName.META, collection);
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
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_NAME, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_NAME, attr);

		String[] names = {HTML40Namespace.ATTR_NAME_HTTP_EQUIV, HTML40Namespace.ATTR_NAME_CONTENT, HTML40Namespace.ATTR_NAME_SCHEME};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
