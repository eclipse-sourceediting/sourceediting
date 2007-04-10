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

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * IMG.
 */
final class HedIMG extends HedEmpty {

	/**
	 */
	public HedIMG(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.IMG, collection);
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * IMG.
	 * %attrs;
	 * (src %URI; #REQUIRED): should be defined locally.
	 * (alt %Text; #REQUIRED)
	 * (longdesc %URI; #IMPLIED)
	 * (name CDATA #IMPLIED)
	 * (height %Length; #IMPLIED)
	 * (width %Length; #IMPLIED)
	 * (usemap %URI; #IMPLIED)
	 * (ismap (ismap) #IMPLIED)
	 * (align %IAlign; #IMPLIED): should be defined locally.
	 * (border %Pixels; #IMPLIED)
	 * (hspace %Pixels; #IMPLIED)
	 * (vspace %Pixels; #IMPLIED)
	 * (mapfile %URI; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		// (src %URI; #REQUIRED): should be defined locally.
		HTMLCMDataTypeImpl atype = null;
		HTMLAttrDeclImpl attr = null;
		atype = new HTMLCMDataTypeImpl(CMDataType.URI);
		attr = new HTMLAttrDeclImpl(CHTMLNamespace.ATTR_NAME_SRC, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_SRC, attr);

		String[] names = {CHTMLNamespace.ATTR_NAME_ALT, CHTMLNamespace.ATTR_NAME_NAME, CHTMLNamespace.ATTR_NAME_HEIGHT, CHTMLNamespace.ATTR_NAME_WIDTH, CHTMLNamespace.ATTR_NAME_BORDER, CHTMLNamespace.ATTR_NAME_HSPACE, CHTMLNamespace.ATTR_NAME_VSPACE,};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// align (local); should be defined locally.
		attr = AttributeCollection.createAlignForImage();
		attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_ALIGN, attr);
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {CHTMLNamespace.ElementName.PRE};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
