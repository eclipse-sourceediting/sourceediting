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
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class HedMETER extends HedInlineContainer {

	public HedMETER(ElementCollection collection) {
		super(HTML50Namespace.ElementName.METER, collection);
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

		String[] names = { HTML50Namespace.ATTR_NAME_MIN, HTML50Namespace.ATTR_NAME_MAX, HTML50Namespace.ATTR_NAME_LOW, HTML50Namespace.ATTR_NAME_HIGH, HTML50Namespace.ATTR_NAME_OPTIMUM, HTML50Namespace.ATTR_NAME_FORM };
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(CMDataType.NUMBER);
		HTMLAttrDeclImpl decl = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_VALUE, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_VALUE, decl);
	}

	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null;

		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		CMNode meter = elementCollection.getNamedItem(HTML50Namespace.ElementName.METER);
		if (meter != null)
			exclusion.appendChild(meter);

		return exclusion;
	}

	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML50Namespace.ElementName.METER};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}

}
