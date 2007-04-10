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
 * MARQUEE.
 */
final class HedMARQUEE extends HedFlowContainer {

	/**
	 */
	public HedMARQUEE(ElementCollection collection) {
		super(HTML40Namespace.ElementName.MARQUEE, collection);
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * %attrs;
	 * (behavior (scroll|slide|alternate) scroll)
	 * (bgcolor %Color; #IMPLIED)
	 * (direction (left|right|up|down) left)
	 * (height CDATA #IMPLIED) ... should be defined locally.
	 * (hspace NUMBER #IMPLIED) ... should be defined locally.
	 * (loop CDATA #IMPLIED)
	 * (scrollamount NUMBER #IMPLIED)
	 * (scrolldelay NUMBER #IMPLIED)
	 * (vspace NUMBER #IMPLIED) ... should be defined locally.
	 * (width CDATA #IMPLIED) ... should be defined locally.
	 * (truespeed (truespeed) #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_BEHAVIOR, HTML40Namespace.ATTR_NAME_BGCOLOR, HTML40Namespace.ATTR_NAME_DIRECTION, HTML40Namespace.ATTR_NAME_LOOP, HTML40Namespace.ATTR_NAME_SCROLLAMOUNT, HTML40Namespace.ATTR_NAME_SCROLLDELAY, HTML40Namespace.ATTR_NAME_TRUESPEED};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		HTMLCMDataTypeImpl atype = null;
		HTMLAttrDeclImpl attr = null;
		// (height CDATA #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_HEIGHT, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_HEIGHT, attr);

		// (width CDATA #IMPLIED) ... should be defined locally.
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_WIDTH, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_WIDTH, attr);

		// (hspace NUMBER #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_HSPACE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_HSPACE, attr);

		// (vspace NUMBER #IMPLIED) ... should be defined locally.
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_VSPACE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_VSPACE, attr);
	}
}
