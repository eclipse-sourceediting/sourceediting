/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;

/**
 * SCRIPT.
 */
final class HedSCRIPT extends HTMLElemDeclImpl {

	/**
	 */
	public HedSCRIPT(ElementCollection collection) {
		super(HTML40Namespace.ElementName.SCRIPT, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_CDATA;
		layoutType = LAYOUT_OBJECT;
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

		//different sets of attributes for html 4 & 5
		attributeCollection.createAttributeDeclarations(HTML40Namespace.ElementName.SCRIPT, attributes);
	
	}

	/**
	 * <code>SCRIPT</code> is CDATA type.
	 * So, the method always returns <code>null</code>.
	 * <br>
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMContent
	 */
	public CMContent getContent() {
		return null;
	}

	/**
	 * CDATA content.<br>
	 * @return int
	 */
	public int getContentType() {
		return CMElementDeclaration.CDATA;
	}
}
