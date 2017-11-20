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



import java.util.Arrays;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * MENU/DIR.
 */
final class HedMENU extends HedListItemContainer {

	/**
	 */
	public HedMENU(String elementName, ElementCollection collection) {
		super(elementName, collection);
	}

	/**
	 * MENU/DIR.
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
		attributeCollection.createAttributeDeclarations(HTML40Namespace.ElementName.MENU, attributes);
	}

	/**
	 * Exclusion.
	 * <code>MENU/DIR</code> has the exclusion.
	 * It is <code>%block;</code>.
	 * %block; is:
	 * P | %heading; | %list; | %preformatted; | DL | DIV | CENTER |
	 * NOSCRIPT | NOFRAMES | BLOCKQUOTE | FORM | ISINDEX | HR |
	 * TABLE | FIELDSET | ADDRESS.
	 * %heading; is: H1 | H2 | H3 | H4 | H5 | H6.
	 * %list; is : UL | OL | DIR | MENU.
	 * %preformatted; is PRE.
	 */
	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null;

		// %block;
		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		String[] names = {HTML40Namespace.ElementName.P, HTML40Namespace.ElementName.H1, HTML40Namespace.ElementName.H2, HTML40Namespace.ElementName.H3, HTML40Namespace.ElementName.H4, HTML40Namespace.ElementName.H5, HTML40Namespace.ElementName.H6, HTML40Namespace.ElementName.UL, HTML40Namespace.ElementName.OL, HTML40Namespace.ElementName.DIR, HTML40Namespace.ElementName.MENU, HTML40Namespace.ElementName.PRE, HTML40Namespace.ElementName.DL, HTML40Namespace.ElementName.DIV, HTML40Namespace.ElementName.CENTER, HTML40Namespace.ElementName.NOSCRIPT, HTML40Namespace.ElementName.NOFRAMES, HTML40Namespace.ElementName.BLOCKQUOTE, HTML40Namespace.ElementName.FORM, HTML40Namespace.ElementName.ISINDEX, HTML40Namespace.ElementName.HR, HTML40Namespace.ElementName.TABLE, HTML40Namespace.ElementName.FIELDSET, HTML40Namespace.ElementName.ADDRESS};
		elementCollection.getDeclarations(exclusion, Arrays.asList(names).iterator());
		return exclusion;
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.DIR, HTML40Namespace.ElementName.MENU};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
