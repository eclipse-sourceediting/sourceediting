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


/**
 * LEGEND.
 */
final class HedLEGEND extends HedInlineContainer {

	/**
	 */
	public HedLEGEND(ElementCollection collection) {
		super(HTML40Namespace.ElementName.LEGEND, collection);
	}

	/**
	 * (accesskey %Character; #IMPLIED)
	 * (align %LAlign; #IMPLIED) ... shuld be defined locally.
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_ACCESSKEY};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// align
		HTMLAttrDeclImpl attr = AttributeCollection.createAlignForLegend();
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALIGN, attr);
	}
}
