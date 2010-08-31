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


public class CtdMediaElement extends ComplexTypeDefinition {

	/**
	 * @param elementCollection ElementCollection
	 */
	public CtdMediaElement(ElementCollection elementCollection) {
		super(elementCollection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.core.internal.contentmodel.ComplexTypeDefinition#createContent()
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;
		// ( )*
		content = new CMGroupImpl(CMGroup.SEQUENCE,0, 1);
		//source
		CMNode source = collection.getNamedItem(HTML50Namespace.ElementName.SOURCE);
		if (source != null)
			content.appendChild(source);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.core.internal.contentmodel.ComplexTypeDefinition#getContentType()
	 */
	public int getContentType() {
		return CMElementDeclaration.ELEMENT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.html.core.internal.contentmodel.ComplexTypeDefinition#getTypeName()
	 */
	public String getTypeName() {
		return ComplexTypeDefinitionFactory.CTYPE_MEDIA_ELEMENT;
	}

}
