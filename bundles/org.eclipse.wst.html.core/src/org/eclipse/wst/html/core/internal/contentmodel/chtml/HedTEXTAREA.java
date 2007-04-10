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

/**
 * TEXTAREA.
 */
final class HedTEXTAREA extends HedPcdata {

	/**
	 * TEXTAREA should keep spaces in its source.
	 */
	public HedTEXTAREA(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.TEXTAREA, collection);
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

		String[] names = {CHTMLNamespace.ATTR_NAME_NAME, CHTMLNamespace.ATTR_NAME_ROWS, CHTMLNamespace.ATTR_NAME_COLS, CHTMLNamespace.ATTR_NAME_ISTYLE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
