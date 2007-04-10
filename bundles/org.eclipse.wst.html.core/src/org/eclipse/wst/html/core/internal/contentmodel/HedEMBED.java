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
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 * EMBED.
 */
final class HedEMBED extends HTMLElemDeclImpl {

	/**
	 */
	public HedEMBED(ElementCollection collection) {
		super(HTML40Namespace.ElementName.EMBED, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_EMBED;
		layoutType = LAYOUT_OBJECT;
		omitType = OMIT_END_DEFAULT;
	}

	/**
	 * %coreattrs;
	 * %events;
	 * (src %URI; #REQUIRED) ... should be defined locally.
	 * (height %Length; #IMPLIED)
	 * (width %Length; #IMPLIED)
	 * (align %IAlign; #IMPLIED) ... should be defined locally.
	 * (hspace %Pixels; #IMPLIED)
	 * (vspace %Pixels; #IMPLIED)
	 * (loop CDATA #IMPLIED)
	 * (hidden CDATA #IMPLIED)
	 * (volume CDATA #IMPLIED)
	 * (autostart (true|false) #IMPLIED)
	 * (autoplay (true|false) #IMPLIED)
	 * (autosize (true|false) #IMPLIED)
	 * (controller (true|false) true)
	 * (scale CDATA #IMPLIED)
	 * (showcontrols (true|false) #IMPLIED)
	 * (playcount NUMBER #IMPLIED)
	 * (repeat CDATA #IMPLIED)
	 * (panel CDATA #IMPLIED)
	 * (text CDATA #IMPLIED)
	 * (palette CDATA #IMPLIED)
	 * (textfocus CDATA #IMPLIED)
	 * (type CDATA #IMPLIED) ... should be defined locally.
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %coreattrs;
		attributeCollection.getCore(attributes);
		// %events;
		attributeCollection.getEvents(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_WIDTH, HTML40Namespace.ATTR_NAME_HSPACE, HTML40Namespace.ATTR_NAME_VSPACE, HTML40Namespace.ATTR_NAME_LOOP, HTML40Namespace.ATTR_NAME_HIDDEN, HTML40Namespace.ATTR_NAME_VOLUME, HTML40Namespace.ATTR_NAME_AUTOSTART, HTML40Namespace.ATTR_NAME_AUTOPLAY, HTML40Namespace.ATTR_NAME_AUTOSIZE, HTML40Namespace.ATTR_NAME_CONTROLLER, HTML40Namespace.ATTR_NAME_SCALE, HTML40Namespace.ATTR_NAME_SHOWCONTROLS, HTML40Namespace.ATTR_NAME_PLAYCOUNT, HTML40Namespace.ATTR_NAME_REPEAT, HTML40Namespace.ATTR_NAME_PANEL, HTML40Namespace.ATTR_NAME_TEXT, HTML40Namespace.ATTR_NAME_PALETTE, HTML40Namespace.ATTR_NAME_TEXTFOCUS};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());

		HTMLCMDataTypeImpl atype = null;
		HTMLAttrDeclImpl attr = null;
		// (src %URI; #REQUIRED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.URI);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_SRC, atype, CMAttributeDeclaration.REQUIRED);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_SRC, attr);

		// (align %IAlign; #IMPLIED) ... should be defined locally.
		attr = AttributeCollection.createAlignForImage();
		if (attr != null)
			attributes.putNamedItem(HTML40Namespace.ATTR_NAME_ALIGN, attr);

		// (type CDATA #IMPLIED) ... should be defined locally.
		atype = new HTMLCMDataTypeImpl(CMDataType.CDATA);
		attr = new HTMLAttrDeclImpl(HTML40Namespace.ATTR_NAME_TYPE, atype, CMAttributeDeclaration.OPTIONAL);
		attributes.putNamedItem(HTML40Namespace.ATTR_NAME_TYPE, attr);
	}
}
