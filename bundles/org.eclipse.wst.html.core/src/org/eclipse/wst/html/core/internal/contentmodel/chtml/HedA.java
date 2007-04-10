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
 * A.
 */
final class HedA extends HedInlineContainer {

	/**
	 */
	public HedA(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.A, collection);
		// CORRECT_EMPTY - GROUP_COMPACT
		correctionType = CORRECT_EMPTY;
	}

	/**
	 * %attrs;
	 * (charset %Charset; #IMPLIED)
	 * (type %ContentType; #IMPLIED)
	 * (name CDATA #IMPLIED)
	 * (href %URI; #IMPLIED)
	 * (hreflang %LanguageCode; #IMPLIED)
	 * (target %FrameTarget; #IMPLIED)
	 * (rel %LinkTypes; #IMPLIED)
	 * (rev %LinkTypes; #IMPLIED)
	 * (accesskey %Character; #IMPLIED)
	 * (directkey %Character; #IMPLIED)
	 * (shape %Shape; rect)
	 * (coords %Coords; #IMPLIED)
	 * (tabindex NUMBER #IMPLIED)
	 * (onfocus %Script; #IMPLIED)
	 * (onblur %Script; #IMPLIED) 
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {CHTMLNamespace.ATTR_NAME_NAME, CHTMLNamespace.ATTR_NAME_HREF,};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	/**
	 * Exclusion.
	 * <code>A</code> has the exclusion.
	 * It is <code>A</code> itself.
	 */
	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null;

		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		CMNode a = elementCollection.getNamedItem(CHTMLNamespace.ElementName.A);
		if (a != null)
			exclusion.appendChild(a);

		return exclusion;
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {CHTMLNamespace.ElementName.A,};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
