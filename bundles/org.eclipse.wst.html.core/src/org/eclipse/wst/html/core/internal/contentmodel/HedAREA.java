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
 * AREA.
 */
final class HedAREA extends HedEmpty {

	/**
	 */
	public HedAREA(ElementCollection collection) {
		super(HTML40Namespace.ElementName.AREA, collection);
		// LAYOUT_HIDDEN.
		// Because, AREA is GROUP_HIDDEN in the C++DOM/DTDParser.cpp.
		layoutType = LAYOUT_HIDDEN;
	}

	/**
	 * AREA.
	 * %attrs;
	 * (shape %Shape; rect)
	 * (coords %Coords; #IMPLIED)
	 * (href %URI; #IMPLIED)
	 * (target %FrameTarget; #IMPLIED)
	 * (nohref (nohref) #IMPLIED)
	 * (alt %Text; #REQUIRED)
	 * (tabindex NUMBER #IMPLIED)
	 * (accesskey %Character; #IMPLIED)
	 * (onfocus %Script; #IMPLIED)
	 * (onblur %Script; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal
		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_SHAPE, HTML40Namespace.ATTR_NAME_COORDS, HTML40Namespace.ATTR_NAME_HREF, HTML40Namespace.ATTR_NAME_TARGET, HTML40Namespace.ATTR_NAME_NOHREF, HTML40Namespace.ATTR_NAME_ALT, HTML40Namespace.ATTR_NAME_TABINDEX, HTML40Namespace.ATTR_NAME_ACCESSKEY, HTML40Namespace.ATTR_NAME_ONFOCUS, HTML40Namespace.ATTR_NAME_ONBLUR};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
