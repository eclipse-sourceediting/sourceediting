/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.contentmodel;



import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.html.core.HTML40Namespace;

/**
 * %fontstyle;
 */
final class HedFontStyle extends HedInlineContainer {

	/**
	 */
	public HedFontStyle(String elementName, ElementCollection collection) {
		super(elementName, collection);
		if (elementName.equalsIgnoreCase(HTML40Namespace.ElementName.BIG) || elementName.equalsIgnoreCase(HTML40Namespace.ElementName.SMALL)) {
			correctionType = CORRECT_EMPTY;
		}
		else { // B, I, U, ...
			correctionType = CORRECT_DUPLICATED;
		}
	}

	/**
	 * %attrs;
	 * @see com.ibm.sed.contentmodel.html.HTMLElemDeclImpl
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		String myName = getElementName();
		if (!myName.equalsIgnoreCase(HTML40Namespace.ElementName.BIG) && !myName.equalsIgnoreCase(HTML40Namespace.ElementName.SMALL))
			return EMPTY_MAP;
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.PRE};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}