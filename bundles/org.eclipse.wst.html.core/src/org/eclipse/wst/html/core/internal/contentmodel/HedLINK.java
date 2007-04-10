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

/**
 * LINK.
 */
final class HedLINK extends HedEmpty {

	/**
	 */
	public HedLINK(ElementCollection collection) {
		super(HTML40Namespace.ElementName.LINK, collection);
		layoutType = LAYOUT_HIDDEN;
	}

	/**
	 * LINK.
	 * %attrs;
	 * (charset %Charset; #IMPLIED)
	 * (href %URI; #IMPLIED)
	 * (hreflang %LanguageCode; #IMPLIED)
	 * (type %ContentType; #IMPLIED): should be defined locally.
	 * (rel %LinkTypes; #IMPLIED)
	 * (rev %LinkTypes; #IMPLIED)
	 * (media %MediaDesc; #IMPLIED)
	 * (target %FrameTarget; #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_CHARSET, HTML40Namespace.ATTR_NAME_HREF, HTML40Namespace.ATTR_NAME_HREFLANG, HTML40Namespace.ATTR_NAME_REL, HTML40Namespace.ATTR_NAME_REV, HTML40Namespace.ATTR_NAME_MEDIA, HTML40Namespace.ATTR_NAME_TARGET};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		// (type %ContentType; #IMPLIED)
		HTMLCMDataTypeImpl atype = new HTMLCMDataTypeImpl(HTMLCMDataType.CONTENT_TYPE);
		HTMLAttrDeclImpl attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_TYPE, attr);
	}
}
