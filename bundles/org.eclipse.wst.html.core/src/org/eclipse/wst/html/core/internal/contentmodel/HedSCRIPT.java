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
	 * (charset %Charset; #IMPLIED)
	 * (type %ContentType; #REQUIRED) ... should be defined locally.
	 * (language CDATA #IMPLIED)
	 * (src %URI; #IMPLIED)
	 * (defer (defer) #IMPLIED)
	 * (event CDATA #IMPLIED)
	 * (for %URI; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		String[] names = {HTML40Namespace.ATTR_NAME_CHARSET, HTML40Namespace.ATTR_NAME_LANGUAGE, HTML40Namespace.ATTR_NAME_SRC, HTML40Namespace.ATTR_NAME_DEFER, HTML40Namespace.ATTR_NAME_EVENT, HTML40Namespace.ATTR_NAME_FOR};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// (type %ContentType; #REQUIRED) ... should be defined locally.
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, "text/javascript"); //$NON-NLS-1$
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_TYPE, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_TYPE, attr);
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
