/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



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
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal
		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		//different sets of attributes for html 4 & 5
		attributeCollection.createAttributeDeclarations(HTML40Namespace.ElementName.AREA, attributes);
	
	}
}
