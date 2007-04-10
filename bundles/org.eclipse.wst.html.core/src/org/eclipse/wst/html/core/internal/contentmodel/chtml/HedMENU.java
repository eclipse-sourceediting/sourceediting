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
	 * (compact (compact) #IMPLIED)
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
	 * <br>
	 */
	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null;

		// %block;
		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		String[] names = {CHTMLNamespace.ElementName.P, CHTMLNamespace.ElementName.H1, CHTMLNamespace.ElementName.H2, CHTMLNamespace.ElementName.H3, CHTMLNamespace.ElementName.H4, CHTMLNamespace.ElementName.H5, CHTMLNamespace.ElementName.H6, CHTMLNamespace.ElementName.UL, CHTMLNamespace.ElementName.OL, CHTMLNamespace.ElementName.DIR, CHTMLNamespace.ElementName.MENU, CHTMLNamespace.ElementName.PRE, CHTMLNamespace.ElementName.DL, CHTMLNamespace.ElementName.DIV, CHTMLNamespace.ElementName.CENTER, CHTMLNamespace.ElementName.BLOCKQUOTE, CHTMLNamespace.ElementName.FORM, CHTMLNamespace.ElementName.HR, CHTMLNamespace.ElementName.ADDRESS};
		elementCollection.getDeclarations(exclusion, Arrays.asList(names).iterator());
		return exclusion;
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
