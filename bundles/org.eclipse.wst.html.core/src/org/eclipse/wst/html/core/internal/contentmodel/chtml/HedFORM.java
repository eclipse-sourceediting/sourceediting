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
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * FORM.
 */
final class HedFORM extends HedFlowContainer {

	/**
	 */
	public HedFORM(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.FORM, collection);
		layoutType = LAYOUT_BLOCK;
	}

	/**
	 * %attrs;
	 * (action %URI; #REQUIRED)
	 * (method (GET|POST) GET)
	 * (enctype %ContentType; "application/x-www-form-urlencoded")
	 * (accept %ContentTypes; #IMPLIED)
	 * (name CDATA #IMPLIED)
	 * (onsubmit %Script; #IMPLIED)
	 * (onreset %Script; #IMPLIED)
	 * (target %FrameTarget; #IMPLIED)
	 * (accept-charset %Charsets; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {CHTMLNamespace.ATTR_NAME_ACTION, CHTMLNamespace.ATTR_NAME_METHOD, CHTMLNamespace.ATTR_NAME_ENCTYPE, CHTMLNamespace.ATTR_NAME_NAME,};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	/**
	 * Exclusion.
	 * <code>FORM</code> has the exclusion.
	 * It is <code>FORM</code> itself.
	 */
	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null; // fatal

		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		CMNode form = elementCollection.getNamedItem(CHTMLNamespace.ElementName.FORM);
		if (form != null)
			exclusion.appendChild(form);

		return exclusion;
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {CHTMLNamespace.ElementName.DIR, CHTMLNamespace.ElementName.FORM, CHTMLNamespace.ElementName.MENU};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
