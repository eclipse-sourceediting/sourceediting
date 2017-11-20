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

/**
 * for FIGURE.
 */
final class CtdFigure extends ComplexTypeDefinition {

	/**
	 * @param elementCollection ElementCollection
	 */
	public CtdFigure(ElementCollection elementCollection) {
		super(elementCollection);
		primaryCandidateName = HTML50Namespace.ElementName.FIGCAPTION;
	}

	/**
	 * (FIGCAPTION)+.
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		// ( | )+
		content = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		
		CMGroupImpl group1 = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		if (group1 != null){
			content.appendChild(group1);
		}
		// FIGCAPTION, FLOW
		CMNode dec = collection.getNamedItem(HTML50Namespace.ElementName.FIGCAPTION);
		if (dec != null)
			group1.appendChild(dec);
		CMGroupImpl flowgroup = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		group1.appendChild(flowgroup);
		collection.getFlow(flowgroup);
		
		CMGroupImpl group2 = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		if (group2 != null){
			content.appendChild(group2);
		}
		// FLOW , FIGCAPTION
		CMGroupImpl flowgroup2 = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		group2.appendChild(flowgroup2);
		collection.getFlow(flowgroup2);
		CMNode dec1 = collection.getNamedItem(HTML50Namespace.ElementName.FIGCAPTION);
		if (dec1 != null)
			group2.appendChild(dec1);
		//FLOW
		CMGroupImpl group3 = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		if (group3 != null){
			content.appendChild(group3);
			
		}
		collection.getFlow(group3);

	}

	/**
	 * (FIGCAPTION)+.
	 * @return int; Should be one of ANY, EMPTY, ELEMENT, MIXED, PCDATA, CDATA,
	 * those are defined in CMElementDeclaration.
	 */
	public int getContentType() {
		return CMElementDeclaration.ELEMENT;
	}

	/**
	 * @return java.lang.String
	 */
	public String getTypeName() {
		return ComplexTypeDefinitionFactory.CTYPE_FIGURE;
	}
}
