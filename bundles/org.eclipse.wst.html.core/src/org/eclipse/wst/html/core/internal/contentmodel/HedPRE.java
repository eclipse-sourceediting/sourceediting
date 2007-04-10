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
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * PRE.
 */
final class HedPRE extends HedInlineContainer {

	/**
	 * PRE element should keep spaces in its source.
	 */
	public HedPRE(ElementCollection collection) {
		super(HTML40Namespace.ElementName.PRE, collection);
		// CORRECT_EMPTY - GROUP_COMPACT
		correctionType = CORRECT_EMPTY;

		keepSpaces = true;
	}

	/**
	 * %attrs;
	 * (width NUMBER #IMPLIED) ... should be defined locally.
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		// (width NUMBER #IMPLIED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_WIDTH, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_WIDTH, attr);
	}

	/**
	 * Exclusion.
	 * <code>PRE</code> has the exclusion.
	 * It is <code>%pre.exclusion;</code>.
	 * %pre.exclusion is:
	 * IMG | OBJECT | APPLET | BIG | SMALL | SUB | SUP | FONT | BASEFONT
	 */
	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null;

		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		String[] names = {HTML40Namespace.ElementName.IMG, HTML40Namespace.ElementName.OBJECT, HTML40Namespace.ElementName.APPLET, HTML40Namespace.ElementName.BIG, HTML40Namespace.ElementName.SMALL, HTML40Namespace.ElementName.SUB, HTML40Namespace.ElementName.SUP, HTML40Namespace.ElementName.FONT, HTML40Namespace.ElementName.BASEFONT};
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
