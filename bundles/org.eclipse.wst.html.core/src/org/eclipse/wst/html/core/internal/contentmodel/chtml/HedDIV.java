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



import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * DIV.
 */
final class HedDIV extends HedFlowContainer {

	/**
	 */
	public HedDIV(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.DIV, collection);
		layoutType = LAYOUT_BLOCK;
	}

	/**
	 * %attrs;
	 * %align;
	 * %reserved;
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		// %align;
		HTMLAttrDeclImpl attr = AttributeCollection.createAlignForParagraph();
		if (attr != null)
			attributes.putNamedItem(CHTMLNamespace.ATTR_NAME_ALIGN, attr);
		// %reserved; ... empty
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {CHTMLNamespace.ElementName.DIR, CHTMLNamespace.ElementName.MENU};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
