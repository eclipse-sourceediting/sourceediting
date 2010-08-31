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

import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;


public class CtdHeadingContainer extends ComplexTypeDefinition {

	/**
	 * @param elementCollection ElementCollection
	 */
	public CtdHeadingContainer(ElementCollection elementCollection) {
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
		content = new CMGroupImpl(CMGroup.CHOICE, 1, CMContentImpl.UNBOUNDED);
		// H1|H2|H3|H4|H5|H6
		 collection.getHeading(content);
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
		return ComplexTypeDefinitionFactory.CTYPE_HEADING_CONTAINER;
	}

}
