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
 * for FRAMESET.
 * ((FRAMESET | FRAME)+ & NOFRAMES?)
 */
final class CtdFrameset extends ComplexTypeDefinition {

	/**
	 * @param elementCollection ElementCollection
	 */
	public CtdFrameset(ElementCollection elementCollection) {
		super(elementCollection);
		primaryCandidateName = HTML40Namespace.ElementName.FRAME;
	}

	/**
	 * ((FRAMESET | FRAME)+ & NOFRAMES?).
	 * --> ((FRAMESET | FRAME)+ & (NOFRAMES)?)
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		// ( & )
		content = new CMGroupImpl(CMGroup.ALL, 1, 1);

		// ( | )+
		CMGroupImpl group = new CMGroupImpl(CMGroup.CHOICE, 1, CMContentImpl.UNBOUNDED);
		if (group == null)
			return;
		content.appendChild(group);

		// FRAMESET
		CMNode dec = collection.getNamedItem(HTML40Namespace.ElementName.FRAMESET);
		if (dec != null)
			group.appendChild(dec);
		// FRAME
		dec = collection.getNamedItem(HTML40Namespace.ElementName.FRAME);
		if (dec != null)
			group.appendChild(dec);

		// ( )?
		group = new CMGroupImpl(CMGroup.SEQUENCE, 0, 1);
		if (group == null)
			return;
		content.appendChild(group);

		// NOFRAMES
		dec = collection.getNamedItem(HTML40Namespace.ElementName.NOFRAMES);
		if (dec != null)
			group.appendChild(dec);
	}

	/**
	 * ((FRAMESET | FRAME)+ & NOFRAMES?)
	 * @return int; Should be one of ANY, EMPTY, ELEMENT, MIXED, PCDATA, CDATA,
	 * those are defined in CMElementDeclaration.
	 */
	public int getContentType() {
		return CMElementDeclaration.ELEMENT;
	}

	/**
	 * Name of complex type definition.
	 * Each singleton must know its own name.
	 * All names should be defined in
	 * {@link <code>ComplexTypeDefinitionFactory</code>} as constants.<br>
	 * @return java.lang.String
	 */
	public String getTypeName() {
		return ComplexTypeDefinitionFactory.CTYPE_FRAMESET;
	}
}
