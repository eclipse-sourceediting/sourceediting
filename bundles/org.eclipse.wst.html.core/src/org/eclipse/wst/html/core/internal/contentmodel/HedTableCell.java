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
import java.util.Iterator;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;


/**
 * (TH | TD).
 */
final class HedTableCell extends HedFlowContainer {

	private static String[] terminators = {HTML40Namespace.ElementName.TR, HTML40Namespace.ElementName.TH, HTML40Namespace.ElementName.TD};

	/**
	 */
	public HedTableCell(String elementName, ElementCollection collection) {
		super(elementName, collection);
		layoutType = LAYOUT_BLOCK;
		omitType = OMIT_END;
	}

	/**
	 * %attrs;
	 * (abbr %Text; #IMPLIED)
	 * (axis CDATA #IMPLIED)
	 * (headers IDREFS #IMPLIED)
	 * (scope %Scope; #IMPLIED)
	 * (rowspan NUMBER 1)
	 * (colspan NUMBER 1)
	 * %cellhalign;
	 * %cellvalign;
	 * (nowrap (nowrap) #IMPLIED)
	 * (bgcolor %Color; #IMPLIED)
	 * (width %Length; #IMPLIED)
	 * (height %Length; #IMPLIED)
	 * (background %URI; #IMPLIED)
	 * (bordercolor %Color #IMPLIED) ... D205514
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		// %cellhalign;
		attributeCollection.getCellhalign(attributes);
		// %cellvalign;
		attributeCollection.getCellvalign(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_ABBR, HTML40Namespace.ATTR_NAME_AXIS, HTML40Namespace.ATTR_NAME_HEADERS, HTML40Namespace.ATTR_NAME_SCOPE, HTML40Namespace.ATTR_NAME_ROWSPAN, HTML40Namespace.ATTR_NAME_COLSPAN, HTML40Namespace.ATTR_NAME_NOWRAP, HTML40Namespace.ATTR_NAME_BGCOLOR, HTML40Namespace.ATTR_NAME_WIDTH, HTML40Namespace.ATTR_NAME_HEIGHT, HTML40Namespace.ATTR_NAME_BACKGROUND, HTML40Namespace.ATTR_NAME_BORDERCOLOR // D205514
		};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	/**
	 * TH and TD have terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
