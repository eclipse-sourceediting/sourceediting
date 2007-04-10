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

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;



/**
 * BR.
 */
final class HedBR extends HedEmpty {

	/**
	 */
	public HedBR(ElementCollection collection) {
		super(HTML40Namespace.ElementName.BR, collection);
		// LAYOUT_BREAK.
		// Because, BR is GROUP_BREAK in the C++DOM/DTDParser.cpp.
		layoutType = LAYOUT_BREAK;
	}

	/**
	 * BR.
	 * %coreattrs;
	 * (clear (left | all | right | none) none)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %coreattrs;
		attributeCollection.getCore(attributes);
		// clear
		HTMLAttributeDeclaration attr = attributeCollection.getDeclaration(HTML40Namespace.ATTR_NAME_CLEAR);
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_CLEAR, attr);
	}
}
