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
package org.eclipse.wst.html.core.internal.contentmodel;



import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;

/**
 * Complex type definition for containers of <code>%flow;</code>.
 */
final class CtdFlowContainer extends ComplexTypeDefinition {

	/**
	 */
	public CtdFlowContainer(ElementCollection elementCollection) {
		super(elementCollection);
	}

	/**
	 * (%flow;)*
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		content = new CMGroupImpl(CMGroup.CHOICE, 0, CMContentImpl.UNBOUNDED);
		collection.getFlow(content);
	}

	public int getContentType() {
		return CMElementDeclaration.MIXED;
	}

	public String getTypeName() {
		return ComplexTypeDefinitionFactory.CTYPE_FLOW_CONTAINER;
	}
}
