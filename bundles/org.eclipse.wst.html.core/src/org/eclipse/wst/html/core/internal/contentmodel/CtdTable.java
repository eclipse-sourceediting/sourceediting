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
 * for TABLE.
 * (CAPTION?, (COL*|COLGROUP*), THEAD?, TFOOT?, TBODY+, TR+)
 */
final class CtdTable extends ComplexTypeDefinition {

	/**
	 * @param elementCollection ElementCollection
	 */
	public CtdTable(ElementCollection elementCollection) {
		super(elementCollection);
		primaryCandidateName = HTML40Namespace.ElementName.TBODY;
	}

	/**
	 * (CAPTION?, (COL*|COLGROUP*), THEAD?, TFOOT?, TBODY+)
	 * --> ((CAPTION)?, ((COL)* | (COLGROUP)*), (THEAD)?, (TFOOT)?, (TBODY)+, (TR)+)
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		// ( , , , ,)
		content = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);

		// (CAPTION)?
		//     ( )?
		CMGroupImpl wrap = new CMGroupImpl(CMGroup.SEQUENCE, 0, 1);
		if (wrap == null)
			return;
		content.appendChild(wrap);
		//     CAPTION
		CMNode dec = collection.getNamedItem(HTML40Namespace.ElementName.CAPTION);
		if (dec != null)
			wrap.appendChild(dec);

		// ((COL)* | (COLGROUP)*)
		//     ( | )
		CMGroupImpl group = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		if (group == null)
			return;
		content.appendChild(group);
		//         (COL)*
		wrap = new CMGroupImpl(CMGroup.SEQUENCE, 0, CMContentImpl.UNBOUNDED);
		if (wrap == null)
			return;
		group.appendChild(wrap);
		dec = collection.getNamedItem(HTML40Namespace.ElementName.COL);
		if (dec != null)
			wrap.appendChild(dec);
		//         (COLGROUP)*
		wrap = new CMGroupImpl(CMGroup.SEQUENCE, 0, CMContentImpl.UNBOUNDED);
		if (wrap == null)
			return;
		group.appendChild(wrap);
		dec = collection.getNamedItem(HTML40Namespace.ElementName.COLGROUP);
		if (dec != null)
			wrap.appendChild(dec);

		// (THEAD)?
		wrap = new CMGroupImpl(CMGroup.SEQUENCE, 0, 1);
		if (wrap == null)
			return;
		content.appendChild(wrap);
		dec = collection.getNamedItem(HTML40Namespace.ElementName.THEAD);
		if (dec != null)
			wrap.appendChild(dec);

		// (TFOOT)?
		wrap = new CMGroupImpl(CMGroup.SEQUENCE, 0, 1);
		if (wrap == null)
			return;
		content.appendChild(wrap);
		dec = collection.getNamedItem(HTML40Namespace.ElementName.TFOOT);
		if (dec != null)
			wrap.appendChild(dec);

		// (TBODY)+
		// TBODY has optional start and end tags
		wrap = new CMGroupImpl(CMGroup.SEQUENCE, 0, CMContentImpl.UNBOUNDED);
		if (wrap == null)
			return;
		content.appendChild(wrap);
		dec = collection.getNamedItem(HTML40Namespace.ElementName.TBODY);
		if (dec != null)
			wrap.appendChild(dec);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=96101
		// TBODY start and end tag are optional, so TR should be allowed here
		// (TR)+
		wrap = new CMGroupImpl(CMGroup.SEQUENCE, 1, CMContentImpl.UNBOUNDED);
		if (wrap == null)
			return;
		content.appendChild(wrap);
		dec = collection.getNamedItem(HTML40Namespace.ElementName.TR);
		if (dec != null)
			wrap.appendChild(dec);
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
		return ComplexTypeDefinitionFactory.CTYPE_TABLE;
	}
}
