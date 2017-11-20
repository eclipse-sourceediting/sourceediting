/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class HedHEADER extends HedFlowContainer {

	public HedHEADER(String name, ElementCollection collection) {
		super(name, collection);
	}

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
	 * <code>HEADER</code> has the exclusion.
	 * It is <code>HEADER</code> and <code>FOOTER</code>.
	 */
	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null; // fatal

		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		CMNode node = elementCollection.getNamedItem(HTML50Namespace.ElementName.HEADER);
		if (node != null)
			exclusion.appendChild(node);
		node = elementCollection.getNamedItem(HTML50Namespace.ElementName.FOOTER);
		if (node != null)
			exclusion.appendChild(node);

		return exclusion;
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML50Namespace.ElementName.HEADER, HTML50Namespace.ElementName.FOOTER};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
