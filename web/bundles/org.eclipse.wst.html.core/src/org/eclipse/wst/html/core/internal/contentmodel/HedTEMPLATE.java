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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

/**
 * "template" : https://html.spec.whatwg.org/#the-template-element
 * 
 * It's an unusual requirement of treating the contained text as content
 * rather than children while considering it renderable, but for now, opt to
 * treat them as children for the sake of editing.
 */
final class HedTEMPLATE extends HTMLElemDeclImpl {

	private CMContent fContent = null;

	public HedTEMPLATE(ElementCollection collection) {
		super(HTML50Namespace.ElementName.TEMPLATE, collection);
		layoutType = LAYOUT_BLOCK;
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
		attributeCollection.createAttributeDeclarations(HTML50Namespace.ElementName.TEMPLATE, attributes);
	}
	
	@Override
	public CMContent getContent() {
		if (fContent == null ) {
			ComplexTypeDefinitionFactory factory = ComplexTypeDefinitionFactory.getInstance();
			String[] possibleTypes = new String[] {
				ComplexTypeDefinitionFactory.CTYPE_FLOW_CONTAINER,
				ComplexTypeDefinitionFactory.CTYPE_LI_CONTAINER,
				ComplexTypeDefinitionFactory.CTYPE_DATALIST,
				ComplexTypeDefinitionFactory.CTYPE_RUBY,
				ComplexTypeDefinitionFactory.CTYPE_MEDIA_ELEMENT,
				ComplexTypeDefinitionFactory.CTYPE_COLUMN_GROUP,
				ComplexTypeDefinitionFactory.CTYPE_TABLE,
				ComplexTypeDefinitionFactory.CTYPE_TR_CONTAINER,
				ComplexTypeDefinitionFactory.CTYPE_FIELDSET,
				ComplexTypeDefinitionFactory.CTYPE_SELECT
			};
			CMGroupImpl group = new CMGroupImpl(CMGroup.ALL, 0, CMContentImpl.UNBOUNDED);
			Set<CMNode> possibleChildren = new HashSet<>();
			for (int i = 0; i < possibleTypes.length; i++) {
				ComplexTypeDefinition def = factory.createTypeDefinition(possibleTypes[i], elementCollection);
				CMGroup content = def.getContent();
				CMNodeList childNodes = content.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					possibleChildren.add(childNodes.item(j));
				}
			}
			for (CMNode child: possibleChildren) {
				group.appendChild(child);
			}
			fContent = group;
		}
		return fContent;
	}

	@Override
	public int getContentType() {
		return CMElementDeclaration.MIXED;
	}
}
