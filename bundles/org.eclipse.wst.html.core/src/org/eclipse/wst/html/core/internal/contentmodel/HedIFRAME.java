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
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * IFRAME.
 */
final class HedIFRAME extends HedFlowContainer {

	/**
	 */
	public HedIFRAME(ElementCollection collection) {
		super(HTML40Namespace.ElementName.IFRAME, collection);
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * %coreattrs;
	 * (longdesc %URI; #IMPLIED)
	 * (name CDATA #IMPLIED)
	 * (src %URI; #IMPLIED)
	 * (frameborder (1|0) 1)
	 * (marginwidth %Pixels; #IMPLIED)
	 * (marginheight %Pixels; #IMPLIED)
	 * (scrolling (yes|no|auto) auto)
	 * (align %IAlign; #IMPLIED) ... should be defined locally.
	 * (height %Length; #IMPLIED)
	 * (width %Length; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %coreattrs;
		attributeCollection.getCore(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_LONGDESC, HTML40Namespace.ATTR_NAME_NAME, HTML40Namespace.ATTR_NAME_SRC, HTML40Namespace.ATTR_NAME_FRAMEBORDER, HTML40Namespace.ATTR_NAME_MARGINWIDTH, HTML40Namespace.ATTR_NAME_MARGINHEIGHT, HTML40Namespace.ATTR_NAME_SCROLLING, HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_WIDTH};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// align
		HTMLAttrDeclImpl attr = AttributeCollection.createAlignForImage();
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALIGN, attr);
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.BUTTON};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
