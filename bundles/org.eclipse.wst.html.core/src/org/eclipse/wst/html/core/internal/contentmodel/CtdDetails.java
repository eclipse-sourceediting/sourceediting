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
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class CtdDetails extends ComplexTypeDefinition {

	public CtdDetails(ElementCollection elementCollection) {
		super(elementCollection);
		primaryCandidateName = HTML50Namespace.ElementName.SUMMARY;
	}

	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		content = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		// summary
		CMNode dec = collection.getNamedItem(HTML50Namespace.ElementName.SUMMARY);
		if (dec != null)
			content.appendChild(dec);

		CMGroupImpl group = new CMGroupImpl(CMGroup.CHOICE, 1, CMContentImpl.UNBOUNDED);
		content.appendChild(group);
		collection.getFlow(group);
	}

	public int getContentType() {
		return CMElementDeclaration.ELEMENT;
	}

	public String getTypeName() {
		return ComplexTypeDefinitionFactory.CTYPE_DETAILS_CONTAINER;
	}

}
