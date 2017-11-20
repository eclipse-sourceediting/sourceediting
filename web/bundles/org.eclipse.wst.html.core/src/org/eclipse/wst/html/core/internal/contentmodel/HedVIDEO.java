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

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;




/**
 * VIDEO.
 */
final class HedVIDEO extends HedMediaElement {

	
	public HedVIDEO(ElementCollection collection) {
		super(HTML50Namespace.ElementName.VIDEO, collection);
	}

	/**
	 * MediaElement
	 * %attrs;
	 * (src %URI; #REQUIRED): should be defined locally.
	 * (preload %CDATA; #IMPLIED) 
	 * (autoplay %ENUM; #IMPLIED) 
	 * (loop %ENUM; #IMPLIED)
	 * (controls %MediaType; #IMPLIED)
	 * (poster %URI; OPTIONAL)
	 * (height %Length; #IMPLIED)
	 * (width %Length; #IMPLIED)
	 * Global attributes
	 */
	protected void createAttributeDeclarations() {
		super.createAttributeDeclarations();
		
		// (poster %URI; #optional): should be defined locally.
		
		HTMLCMDataTypeImpl atype = null;
		HTMLAttrDeclImpl attr = null;
		atype = new HTMLCMDataTypeImpl(CMDataType.URI);
		attr = new HTMLAttrDeclImpl(HTML50Namespace.ATTR_NAME_POSTER, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML50Namespace.ATTR_NAME_POSTER, attr);
		
		// height , width
		String[] names = {HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_WIDTH};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());


		
	}
}

