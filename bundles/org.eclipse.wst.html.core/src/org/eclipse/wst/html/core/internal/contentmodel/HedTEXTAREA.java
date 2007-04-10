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
 * TEXTAREA.
 */
final class HedTEXTAREA extends HedPcdata {

	/**
	 * TEXTAREA should keep spaces in its source.
	 */
	public HedTEXTAREA(ElementCollection collection) {
		super(HTML40Namespace.ElementName.TEXTAREA, collection);
		layoutType = LAYOUT_OBJECT;

		keepSpaces = true;
	}

	/**
	 * TEXTAREA.
	 * %attrs;
	 * %reserved; ... empty
	 * (name CDATA #IMPLIED)
	 * (rows NUMBER #REQUIRED)
	 * (cols NUMBER #REQUIRED)
	 * (disabled (disabled) #IMPLIED)
	 * (readonly (readonly) #IMPLIED)
	 * (tabindex NUMBER #IMPLIED)
	 * (accesskey %Character; #IMPLIED)
	 * (onfocus %Script; #IMPLIED)
	 * (onblur %Script; #IMPLIED)
	 * (onselect %Script; #IMPLIED)
	 * (onchange %Script; #IMPLIED)
	 * (istyle CDATA #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal
		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_NAME, HTML40Namespace.ATTR_NAME_ROWS, HTML40Namespace.ATTR_NAME_COLS, HTML40Namespace.ATTR_NAME_DISABLED, HTML40Namespace.ATTR_NAME_READONLY, HTML40Namespace.ATTR_NAME_TABINDEX, HTML40Namespace.ATTR_NAME_ACCESSKEY, HTML40Namespace.ATTR_NAME_ONFOCUS, HTML40Namespace.ATTR_NAME_ONBLUR, HTML40Namespace.ATTR_NAME_ONSELECT, HTML40Namespace.ATTR_NAME_ONCHANGE, HTML40Namespace.ATTR_NAME_ISTYLE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
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
