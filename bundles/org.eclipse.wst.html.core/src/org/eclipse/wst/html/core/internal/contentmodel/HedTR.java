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
 * TR.
 */
final class HedTR extends HTMLElemDeclImpl {

	private static String[] terminators = {HTML40Namespace.ElementName.TR};

	/**
	 */
	public HedTR(ElementCollection collection) {
		super(HTML40Namespace.ElementName.TR, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_TCELL_CONTAINER;
		layoutType = LAYOUT_BLOCK;
		omitType = OMIT_END;
		indentChild = true;
	}

	/**
	 * %attrs;
	 * %cellhalign;
	 * %cellvalign;
	 * (bgcolor %Color; #IMPLIED)
	 * (background %URI; #IMPLIED)
	 * (bordercolor %Color #IMPLIED) ... D205514
	 * (height %Length #IMPLIED) bug2246
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

		String[] names = {HTML40Namespace.ATTR_NAME_BGCOLOR, HTML40Namespace.ATTR_NAME_BACKGROUND, HTML40Namespace.ATTR_NAME_BORDERCOLOR, HTML40Namespace.ATTR_NAME_HEIGHT // D205514
		};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	/**
	 * TR has terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
