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
 * for FIELDSET.
 */
final class CtdFieldset extends ComplexTypeDefinition {

	/**
	 * @param elementCollection ElementCollection
	 */
	public CtdFieldset(ElementCollection elementCollection) {
		super(elementCollection);
		primaryCandidateName = HTML40Namespace.ElementName.LEGEND;
	}

	/**
	 * (#PCDATA, LEGEND, (%flow;)*)
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		// ( , , )
		content = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		// #PCDATA
		// ...??

		// LEGEND
		CMNode dec = collection.getNamedItem(HTML40Namespace.ElementName.LEGEND);
		if (dec != null)
			content.appendChild(dec);
		// (%flow;)*
		CMGroupImpl flows = new CMGroupImpl(CMGroup.CHOICE, 0, CMContentImpl.UNBOUNDED);
		if (flows == null)
			return;
		collection.getFlow(flows);
		content.appendChild(flows);
	}

	/**
	 * (#PCDATA, LEGEND, (%flow;)*)
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
		return ComplexTypeDefinitionFactory.CTYPE_FIELDSET;
	}
}
