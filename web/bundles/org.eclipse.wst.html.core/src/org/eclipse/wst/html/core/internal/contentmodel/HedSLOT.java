/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;

/**
 * SCRIPT.
 */
final class HedSLOT extends HTMLElemDeclImpl {

	/**
	 */
	public HedSLOT(ElementCollection collection) {
		super(HTML50Namespace.ElementName.SLOT, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_CDATA;
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * SCRIPT.
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		attributeCollection.createAttributeDeclarations(HTML50Namespace.ElementName.SLOT, attributes);
	
	}

	public CMContent getContent() {
		return null;
	}

	public int getContentType() {
		return CMElementDeclaration.CDATA;
	}
}
