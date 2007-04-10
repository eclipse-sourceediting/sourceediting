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
 * FONT.
 */
final class HedFONT extends HedInlineContainer {

	/**
	 */
	public HedFONT(ElementCollection collection) {
		super(HTML40Namespace.ElementName.FONT, collection);
		// CORRECT_EMPTY - GROUP_COMPACT
		correctionType = CORRECT_EMPTY;
	}

	/**
	 * %coreattrs;
	 * %i18n;
	 * (size CDATA #IMPLIED) ... should be defined locally.
	 * (color %Color; #IMPLIED)
	 * (face CDATA #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %coreattrs;
		attributeCollection.getCore(attributes);
		// %i18n;
		attributeCollection.getI18n(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_COLOR, HTML40Namespace.ATTR_NAME_FACE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// (size CDATA #IMPLIED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_SIZE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_SIZE, attr);
	}
}
