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



import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 * SOURCE.
 */
final class HedSOURCE extends HTMLElemDeclImpl {

	private static String[] terminators = {HTML50Namespace.ElementName.SOURCE};

	/**
	 */
	public HedSOURCE(ElementCollection collection) {
		super(HTML50Namespace.ElementName.SOURCE, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_CDATA;
		layoutType = LAYOUT_HIDDEN;
	}

	/**
	 * SOURCE
	 * %attrs;
	 * (src %URI; #REQUIRED): should be defined locally.
	 * (type %ContentType; #IMPLIED) 
	 * (media %MediaType; #IMPLIED) 
	 * Global attributes
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

	
		// (src %URI; #REQUIRED): should be defined locally.
		HTMLCMDataTypeImpl atype = null;
		HTMLAttrDeclImpl attr = null;
		atype = new HTMLCMDataTypeImpl(CMDataType.URI);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_SRC, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_SRC, attr);

		// (type %ContentType; #IMPLIED) 
		atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_TYPE, attr);
		

		// (media %MediaType; #IMPLIED) 
		atype = new HTMLCMDataTypeImpl(HTMLCMDataType.MEDIA_TYPE);
		atype.setImpliedValue(CMDataType.IMPLIED_VALUE_DEFAULT, HTML40Namespace.ATTR_VALUE_ALL);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_MEDIA, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_MEDIA, attr);
		
		
		// global attributes
		attributeCollection.getAttrs(attributes);
	}
	
	/**
	 * SOURCE has terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
