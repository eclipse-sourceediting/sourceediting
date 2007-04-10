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
 * OPTION.
 */
final class HedOPTION extends HedPcdata {

	private static String[] terminators = {HTML40Namespace.ElementName.OPTION};

	/**
	 */
	public HedOPTION(ElementCollection collection) {
		super(HTML40Namespace.ElementName.OPTION, collection);
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

		String[] names = {HTML40Namespace.ATTR_NAME_SELECTED, HTML40Namespace.ATTR_NAME_DISABLED, HTML40Namespace.ATTR_NAME_LABEL, HTML40Namespace.ATTR_NAME_VALUE};
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
