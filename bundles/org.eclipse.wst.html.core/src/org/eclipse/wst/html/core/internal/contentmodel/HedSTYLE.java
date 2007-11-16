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



import java.util.Arrays;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;

/**
 * STYLE.
 */
final class HedSTYLE extends HTMLElemDeclImpl {

	/**
	 */
	public HedSTYLE(ElementCollection collection) {
		super(HTML40Namespace.ElementName.STYLE, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_CDATA;
		layoutType = LAYOUT_HIDDEN;
	}

	/**
	 * STYLE
	 * %i18n;
	 * (type %ContentType; #REQUIRED) ... should be defined locally.
	 * (media %MediaDesc; #IMPLIED)
	 * (title %Text; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %i18n;
		attributeCollection.getI18n(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_MEDIA, HTML40Namespace.ATTR_NAME_TITLE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
		// (type %ContentType; #REQUIRED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, "text/css"); //$NON-NLS-1$
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_TYPE, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_TYPE, attr);
	}

	/**
	 * Content.<br>
	 * <code>STYLE</code> is CDATA content type.  So, it always returns <code>null</code>.
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
