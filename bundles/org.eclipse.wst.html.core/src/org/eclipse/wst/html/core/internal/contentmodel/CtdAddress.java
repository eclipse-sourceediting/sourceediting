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



import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * for ADDRESS.
 */
final class CtdAddress extends ComplexTypeDefinition {

	/**
	 * @param elementCollection ElementCollection
	 */
	public CtdAddress(ElementCollection elementCollection) {
		super(elementCollection);
	}

	/**
	 * ((%inline) | P)*.
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		// ( | )*
		content = new CMGroupImpl(CMGroup.CHOICE, 0, CMContentImpl.UNBOUNDED);
		// (%inline)
		CMGroupImpl inlines = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		if (inlines == null)
			return;
		collection.getInline(inlines);
		content.appendChild(inlines);
		// P
		CMNode p = collection.getNamedItem(HTML40Namespace.ElementName.P);
		if (p != null)
			content.appendChild(p);
	}

	/**
	 * ((%inline) | P)*.
	 * Because %inline; contains #PCDATA, the type is MIXED.
	 * @return int; Should be one of ANY, EMPTY, ELEMENT, MIXED, PCDATA, CDATA,
	 * those are defined in CMElementDeclaration.
	 */
	public int getContentType() {
		return CMElementDeclaration.MIXED;
	}

	/**
	 * @return java.lang.String
	 */
	public String getTypeName() {
		return ComplexTypeDefinitionFactory.CTYPE_ADDRESS;
	}
}
