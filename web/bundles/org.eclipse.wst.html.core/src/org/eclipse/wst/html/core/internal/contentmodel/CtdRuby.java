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
 * for RUBY.
 */
final class CtdRuby extends ComplexTypeDefinition {

	/**
	 * @param elementCollection ElementCollection
	 */
	public CtdRuby(ElementCollection elementCollection) {
		super(elementCollection);
		primaryCandidateName = HTML50Namespace.ElementName.RT;
	}

	/**
	 * (RT)+.
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

	
		content = new CMGroupImpl(CMGroup.SEQUENCE, 1, CMContentImpl.UNBOUNDED);
		
		CMGroupImpl phraseGroup = new CMGroupImpl(CMGroup.CHOICE, 0, CMContentImpl.UNBOUNDED);
		if (phraseGroup == null)
			return;
		content.appendChild(phraseGroup);
		collection.getPhrase(phraseGroup);
		
		CMGroupImpl rtrpgroup = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		if (rtrpgroup == null)
			return;
		content.appendChild(rtrpgroup);
		
		//RT
		CMNode dec = collection.getNamedItem(HTML50Namespace.ElementName.RT);
		if (dec != null)
			rtrpgroup.appendChild(dec);
		
		CMGroupImpl rpgroup = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		if (rpgroup == null)
			return;
		rtrpgroup.appendChild(rpgroup);
		
		// RP
		dec = collection.getNamedItem(HTML50Namespace.ElementName.RP);
		if (dec != null)
			rpgroup.appendChild(dec);
		// RT
		dec = collection.getNamedItem(HTML50Namespace.ElementName.RT);
		if (dec != null)
			rpgroup.appendChild(dec);
		dec = collection.getNamedItem(HTML50Namespace.ElementName.RP);
		if (dec != null)
			rpgroup.appendChild(dec);
	}

	/**
	 * (RT)+.
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
		return ComplexTypeDefinitionFactory.CTYPE_RUBY;
	}
}
