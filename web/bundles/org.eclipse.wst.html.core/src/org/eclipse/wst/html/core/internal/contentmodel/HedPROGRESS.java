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

import java.util.Arrays;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class HedPROGRESS extends HedInlineContainer {

	public HedPROGRESS(ElementCollection collection) {
		super(HTML50Namespace.ElementName.PROGRESS, collection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.core.internal.contentmodel.HTMLElemDeclImpl#createAttributeDeclarations()
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();
		attributeCollection.getAttrs(attributes);

		String[] names = { HTML40Namespace.ATTR_NAME_VALUE, HTML50Namespace.ATTR_NAME_MAX, HTML50Namespace.ATTR_NAME_FORM };
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion;
		if (elementCollection == null)
			return null;

		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		CMNode progress = elementCollection.getNamedItem(HTML50Namespace.ElementName.PROGRESS);
		if (progress != null) {
			exclusion.appendChild(progress);
		}
		return exclusion;
	}

	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors == null) {
			String[] names = { HTML50Namespace.ElementName.PROGRESS };
			prohibitedAncestors = elementCollection.getDeclarations(names);
		}
		return prohibitedAncestors;
	}
}
