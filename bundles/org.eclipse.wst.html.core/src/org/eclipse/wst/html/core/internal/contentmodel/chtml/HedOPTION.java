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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import java.util.Arrays;
import java.util.Iterator;

/**
 * OPTION.
 */
final class HedOPTION extends HedPcdata {

	private static String[] terminators = {CHTMLNamespace.ElementName.OPTION};

	/**
	 */
	public HedOPTION(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.OPTION, collection);
		layoutType = LAYOUT_HIDDEN;
		omitType = OMIT_END;
	}

	/**
	 * OPTION.
	 * %attrs;
	 * (selected (selected) #IMPLIED)
	 * (disabled (disabled) #IMPLIED)
	 * (label %Text; #IMPLIED)
	 * (value CDATA #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {CHTMLNamespace.ATTR_NAME_SELECTED, CHTMLNamespace.ATTR_NAME_VALUE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}

	/**
	 * OPTION has terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
