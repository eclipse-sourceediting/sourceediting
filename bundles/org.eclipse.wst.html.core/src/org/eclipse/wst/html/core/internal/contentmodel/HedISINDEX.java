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
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * ISINDEX.
 */
final class HedISINDEX extends HedEmpty {

	/**
	 */
	public HedISINDEX(ElementCollection collection) {
		super(HTML40Namespace.ElementName.ISINDEX, collection);
		layoutType = LAYOUT_BLOCK;
	}

	/**
	 * ISINDEX.
	 * %coreattrs;
	 * %i18n;
	 * (prompt %Text; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %coreattrs;
		attributeCollection.getCore(attributes);
		// %i18n;
		attributeCollection.getI18n(attributes);

		HTMLAttributeDeclaration attr = attributeCollection.getDeclaration(HTML40Namespace.ATTR_NAME_PROMPT);
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_PROMPT, attr);
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.BUTTON, HTML40Namespace.ElementName.DIR, HTML40Namespace.ElementName.MENU};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
