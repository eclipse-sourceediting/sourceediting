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


/**
 * FRAME.
 */
final class HedFRAME extends HedEmpty {

	/**
	 */
	public HedFRAME(ElementCollection collection) {
		super(HTML40Namespace.ElementName.FRAME, collection);
		// LAYOUT_HIDDEN.
		// Because, FRAME is GROUP_HIDDEN in the C++DOM/DTDParser.cpp.
		layoutType = LAYOUT_HIDDEN;
	}

	/**
	 * FRAME.
	 * %coreattrs;
	 * (longdesc %URI; #IMPLIED)
	 * (name CDATA #IMPLIED)
	 * (src %URI; #IMPLIED)
	 * (frameborder (1|0) 1)
	 * (marginwidth %Pixels; #IMPLIED)
	 * (marginheight %Pixels; #IMPLIED)
	 * (noresize (noresize) #IMPLIED)
	 * (scrolling (yes|no|auto) auto)
	 * (bordercolor %Color #IMPLIED) ... D205514
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %coreattrs;
		attributeCollection.getCore(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_LONGDESC, HTML40Namespace.ATTR_NAME_NAME, HTML40Namespace.ATTR_NAME_SRC, HTML40Namespace.ATTR_NAME_FRAMEBORDER, HTML40Namespace.ATTR_NAME_MARGINWIDTH, HTML40Namespace.ATTR_NAME_MARGINHEIGHT, HTML40Namespace.ATTR_NAME_NORESIZE, HTML40Namespace.ATTR_NAME_SCROLLING, HTML40Namespace.ATTR_NAME_BORDERCOLOR // D20554
		};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
