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
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class HedDETAILS extends HTMLElemDeclImpl {

	public HedDETAILS(ElementCollection collection) {
		super(HTML50Namespace.ElementName.DETAILS, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_DETAILS_CONTAINER;
	}

	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal
		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		CMNode node = attributeCollection.getDeclaration(HTML50Namespace.ATTR_NAME_OPEN);
		if (node != null)
			attributes.putNamedItem(HTML50Namespace.ATTR_NAME_OPEN, node);
	}

}
